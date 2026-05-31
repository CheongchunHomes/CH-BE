package com.chcorp.homes.sign.service;

import com.chcorp.homes.diagnosis.entity.UserProfile;
import com.chcorp.homes.diagnosis.repository.UserProfileRepository;
import com.chcorp.homes.files.dto.response.FileSignedUrlResponseDTO;
import com.chcorp.homes.files.entity.FileAsset;
import com.chcorp.homes.files.entity.FileContentType;
import com.chcorp.homes.files.entity.FileStatus;
import com.chcorp.homes.files.repository.FileAssetRepository;
import com.chcorp.homes.files.service.FileService;
import com.chcorp.homes.files.service.SupabaseStorageClient;
import com.chcorp.homes.properties.entity.Property;
import com.chcorp.homes.properties.repository.PropertyRepository;
import com.chcorp.homes.sign.dto.request.CustomerSignRequestDTO;
import com.chcorp.homes.sign.dto.request.ProviderSignRequestDTO;
import com.chcorp.homes.sign.dto.request.SignCreateRequestDTO;
import com.chcorp.homes.sign.dto.response.BrokerSignImageResponseDTO;
import com.chcorp.homes.sign.dto.response.SignContractResponseDTO;
import com.chcorp.homes.sign.dto.response.SignResponseDTO;
import com.chcorp.homes.sign.entity.SignContract;
import com.chcorp.homes.sign.entity.SignRequest;
import com.chcorp.homes.sign.entity.SignStatus;
import com.chcorp.homes.sign.repository.SignContractRepository;
import com.chcorp.homes.sign.repository.SignRepository;
import com.chcorp.homes.users.entity.PersonalInfo;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.PersonalInfoRepository;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class SignService {
    private static final String BROKER_SIGN_OBJECT_PATH = "common/broker_sign.png";

    private final SignRepository signRepository;
    private final SignContractRepository signContractRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final UserProfileRepository userProfileRepository;
    private final FileAssetRepository fileAssetRepository;
    private final FileService fileService;
    private final SupabaseStorageClient supabaseStorageClient;

    @Transactional(readOnly = true)
    public List<SignResponseDTO> myList(Long currentUserId) {
        validateCurrentUserId(currentUserId);

        return signRepository.findByProviderIdOrCustomerIdOrderByUpdatedAtDesc(currentUserId, currentUserId)
                .stream()
                .map(SignResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SignContractResponseDTO contractDetail(Long currentUserId, Long signId) {
        validateCurrentUserId(currentUserId);

        SignRequest signRequest = getSignRequest(signId);
        validateParticipant(signRequest, currentUserId);

        SignContract signContract = getContractDetailForResponse(signRequest);

        PersonalInfo providerInfo = personalInfoRepository.findByUserId(signRequest.getProvider().getId())
                .orElse(null);
        PersonalInfo customerInfo = personalInfoRepository.findByUserId(signRequest.getCustomer().getId())
                .orElse(null);
        UserProfile providerProfile = userProfileRepository.findByUserId(signRequest.getProvider().getId())
                .orElse(null);
        UserProfile customerProfile = userProfileRepository.findByUserId(signRequest.getCustomer().getId())
                .orElse(null);

        return SignContractResponseDTO.from(
                signRequest,
                signContract,
                providerInfo,
                customerInfo,
                providerProfile,
                customerProfile
        );
    }

    @Transactional(readOnly = true)
    public BrokerSignImageResponseDTO brokerSignImage(Long currentUserId) {
        validateCurrentUserId(currentUserId);

        return new BrokerSignImageResponseDTO(
                createBrokerSignatureSignedUrl(),
                supabaseStorageClient.signedDownloadTtlSeconds()
        );
    }

    @Transactional(readOnly = true)
    public FileSignedUrlResponseDTO completedPdfSignedUrl(Long currentUserId, Long signId) {
        validateCurrentUserId(currentUserId);

        SignRequest signRequest = getSignRequest(signId);
        validateParticipant(signRequest, currentUserId);
        SignContract signContract = signContractRepository.findBySignRequestId(signRequest.getId())
                .orElseThrow(() -> new IllegalStateException("Contract detail does not exist."));

        Long fileId = signContract.getCompletedPdfFileId();
        if (fileId == null) {
            throw new IllegalStateException("Completed PDF file does not exist.");
        }

        return contractFileSignedUrl(currentUserId, signId, fileId);
    }

    @Transactional(readOnly = true)
    public FileSignedUrlResponseDTO contractFileSignedUrl(Long currentUserId, Long signId, Long fileId) {
        validateCurrentUserId(currentUserId);

        SignRequest signRequest = getSignRequest(signId);
        validateParticipant(signRequest, currentUserId);

        SignContract signContract = signContractRepository.findBySignRequestId(signRequest.getId())
                .orElseThrow(() -> new IllegalStateException("Contract detail does not exist."));

        if (fileId == null) {
            throw new IllegalArgumentException("fileId is required.");
        }

        FileContentType requiredContentType = resolveAttachedContractFileContentType(signContract, fileId);
        return createContractFileSignedUrl(fileId, requiredContentType);
    }

    @Transactional
    public SignResponseDTO issue(Long currentUserId, SignCreateRequestDTO request) {
        validateCurrentUserId(currentUserId);
        validateCreateRequest(request);

        User customer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("Customer does not exist."));

        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new NoSuchElementException("Property does not exist."));

        Long providerId = property.getLandlordUserId();
        if (providerId == null) {
            throw new IllegalStateException("Property provider information does not exist.");
        }

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new NoSuchElementException("Provider does not exist."));

        SignRequest signRequest = SignRequest.builder()
                .provider(provider)
                .customer(customer)
                .propertyId(property)
                .status(SignStatus.ISSUED)
                .build();

        return SignResponseDTO.from(signRepository.save(signRequest));
    }

    @Transactional
    public SignResponseDTO providerSign(Long currentUserId, Long signId, ProviderSignRequestDTO request) {
        validateCurrentUserId(currentUserId);
        validateProviderSignRequest(request);

        SignRequest signRequest = getSignRequest(signId);
        validateProvider(signRequest, currentUserId);
        validateStatus(signRequest, SignStatus.ISSUED, "provider-sign");

        FileAsset providerSignature = fileService.validateReadableImageFile(
                currentUserId,
                request.providerSignatureFileId()
        );
        int updated = signRepository.updateStatusIfCurrent(
                signRequest.getId(),
                SignStatus.ISSUED,
                SignStatus.PROVIDER_SIGNED
        );
        if (updated == 0) {
            throw new IllegalStateException("Contract is already processed or cannot be provider-signed.");
        }

        if (signContractRepository.existsBySignRequestId(signRequest.getId())) {
            throw new IllegalStateException("Provider contract detail is already registered.");
        }

        SignRequest updatedSignRequest = getSignRequest(signId);
        SignContract signContract = SignContract.builder()
                .signRequest(updatedSignRequest)
                .build();

        signContract.saveProviderContract(
                request.leaseEndDate(),
                request.contractAmount(),
                request.interimAmount1(),
                request.interimAmount1Date(),
                request.interimAmount2(),
                request.interimAmount2Date(),
                request.balanceAmount(),
                request.balanceDate(),
                request.specialTerms(),
                request.buildingDong(),
                request.unitHo(),
                request.rentedPart(),
                providerSignature.getId(),
                Instant.now()
        );
        signContractRepository.save(signContract);

        return SignResponseDTO.from(getSignRequest(signId));
    }

    @Transactional
    public SignResponseDTO customerSign(Long currentUserId, Long signId, CustomerSignRequestDTO request) {
        validateCurrentUserId(currentUserId);
        validateCustomerSignRequest(request);

        SignRequest signRequest = getSignRequest(signId);
        validateCustomer(signRequest, currentUserId);
        validateStatus(signRequest, SignStatus.PROVIDER_SIGNED, "customer-sign");

        FileAsset customerSignature = fileService.validateReadableImageFile(
                currentUserId,
                request.customerSignatureFileId()
        );
        FileAsset completedPdf = fileService.validateReadableDocumentFile(
                currentUserId,
                request.completedPdfFileId()
        );

        int updated = signRepository.updateStatusIfCurrent(
                signRequest.getId(),
                SignStatus.PROVIDER_SIGNED,
                SignStatus.COMPLETED
        );
        if (updated == 0) {
            throw new IllegalStateException("Contract is already processed or cannot be customer-signed.");
        }

        SignContract signContract = signContractRepository.findBySignRequestId(signRequest.getId())
                .orElseThrow(() -> new IllegalStateException("Provider contract detail does not exist."));

        signContract.saveCustomerSignature(
                customerSignature.getId(),
                completedPdf.getId(),
                Instant.now()
        );
        signContractRepository.save(signContract);

        return SignResponseDTO.from(getSignRequest(signId));
    }

    @Transactional
    public SignResponseDTO cancel(Long currentUserId, Long signId) {
        validateCurrentUserId(currentUserId);

        SignRequest signRequest = getSignRequest(signId);
        validateParticipant(signRequest, currentUserId);

        int updated = signRepository.cancelIfProcessable(signRequest.getId());
        if (updated == 0) {
            throw new IllegalStateException("Contract cannot be canceled.");
        }

        return SignResponseDTO.from(getSignRequest(signId));
    }

    private SignRequest getSignRequest(Long signId) {
        if (signId == null) {
            throw new IllegalArgumentException("signId is required.");
        }

        return signRepository.findById(signId)
                .orElseThrow(() -> new NoSuchElementException("Sign request does not exist."));
    }

    private String createBrokerSignatureSignedUrl() {
        try {
            return supabaseStorageClient.createSignedDownloadUrl(BROKER_SIGN_OBJECT_PATH);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Broker signature file could not be signed.", e);
        }
    }

    private void validateCurrentUserId(Long currentUserId) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("Login is required.");
        }
    }

    private void validateCreateRequest(SignCreateRequestDTO request) {
        if (request == null || request.propertyId() == null) {
            throw new IllegalArgumentException("propertyId is required.");
        }
    }

    private void validateProviderSignRequest(ProviderSignRequestDTO request) {
        if (request == null || request.providerSignatureFileId() == null) {
            throw new IllegalArgumentException("providerSignatureFileId is required.");
        }
    }

    private void validateCustomerSignRequest(CustomerSignRequestDTO request) {
        if (request == null || request.customerSignatureFileId() == null) {
            throw new IllegalArgumentException("customerSignatureFileId is required.");
        }
        if (request.completedPdfFileId() == null) {
            throw new IllegalArgumentException("completedPdfFileId is required.");
        }
    }

    private void validateParticipant(SignRequest signRequest, Long currentUserId) {
        if (!isProvider(signRequest, currentUserId) && !isCustomer(signRequest, currentUserId)) {
            throw new AccessDeniedException("No permission for this sign request.");
        }
    }

    private void validateProvider(SignRequest signRequest, Long currentUserId) {
        if (!isProvider(signRequest, currentUserId)) {
            throw new AccessDeniedException("Only the provider can sign.");
        }
    }

    private void validateCustomer(SignRequest signRequest, Long currentUserId) {
        if (!isCustomer(signRequest, currentUserId)) {
            throw new AccessDeniedException("Only the customer can sign.");
        }
    }

    private void validateStatus(SignRequest signRequest, SignStatus expectedStatus, String actionName) {
        if (signRequest.getStatus() != expectedStatus) {
            throw new IllegalStateException(actionName + " is allowed only in " + expectedStatus + " status.");
        }
    }

    private SignContract getContractDetailForResponse(SignRequest signRequest) {
        SignContract signContract = signContractRepository.findBySignRequestId(signRequest.getId())
                .orElse(null);

        if (signContract == null && requiresContractDetail(signRequest.getStatus())) {
            throw new IllegalStateException("Signed contract detail does not exist.");
        }

        return signContract;
    }

    private boolean requiresContractDetail(SignStatus status) {
        return status == SignStatus.PROVIDER_SIGNED || status == SignStatus.COMPLETED;
    }

    private FileContentType resolveAttachedContractFileContentType(SignContract signContract, Long fileId) {
        if (fileId.equals(signContract.getProviderSignatureFileId())
                || fileId.equals(signContract.getCustomerSignatureFileId())) {
            return FileContentType.IMAGE;
        }

        if (fileId.equals(signContract.getCompletedPdfFileId())) {
            return FileContentType.DOCUMENT;
        }

        throw new AccessDeniedException("Requested file is not attached to this contract.");
    }

    private FileSignedUrlResponseDTO createContractFileSignedUrl(Long fileId, FileContentType requiredContentType) {
        FileAsset fileAsset = fileAssetRepository.findById(fileId)
                .orElseThrow(() -> new NoSuchElementException("Contract file does not exist."));

        if (fileAsset.getStatus() != FileStatus.ACTIVE) {
            throw new IllegalStateException("Contract file is not active.");
        }
        if (fileAsset.getContentType() != requiredContentType) {
            throw new IllegalStateException("Contract file content type is invalid.");
        }

        return new FileSignedUrlResponseDTO(
                fileAsset.getId(),
                supabaseStorageClient.createSignedDownloadUrl(fileAsset.getObjectPath()),
                supabaseStorageClient.signedDownloadTtlSeconds(),
                fileAsset.getContentType(),
                fileAsset.getOriginalFilename(),
                fileAsset.getSizeBytes()
        );
    }

    private boolean isProvider(SignRequest signRequest, Long currentUserId) {
        return signRequest.getProvider().getId().equals(currentUserId);
    }

    private boolean isCustomer(SignRequest signRequest, Long currentUserId) {
        return signRequest.getCustomer().getId().equals(currentUserId);
    }
}
