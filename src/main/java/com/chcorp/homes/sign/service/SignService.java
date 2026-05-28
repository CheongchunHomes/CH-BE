package com.chcorp.homes.sign.service;

import com.chcorp.homes.properties.entity.Property;
import com.chcorp.homes.properties.repository.PropertyRepository;
import com.chcorp.homes.diagnosis.entity.UserProfile;
import com.chcorp.homes.diagnosis.repository.UserProfileRepository;
import com.chcorp.homes.files.service.SupabaseStorageClient;
import com.chcorp.homes.sign.dto.request.SignCreateRequestDTO;
import com.chcorp.homes.sign.dto.response.BrokerSignImageResponseDTO;
import com.chcorp.homes.sign.dto.response.SignContractResponseDTO;
import com.chcorp.homes.sign.dto.response.SignResponseDTO;
import com.chcorp.homes.sign.entity.SignRequest;
import com.chcorp.homes.sign.entity.SignStatus;
import com.chcorp.homes.sign.repository.SignRepository;
import com.chcorp.homes.users.entity.PersonalInfo;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.PersonalInfoRepository;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class SignService {
    private static final String BROKER_SIGN_OBJECT_PATH = "common/broker_sign.png";
    private static final String BROKER_SIGN_FILENAME = "broker_sign.png";
    private static final String BROKER_SIGN_CONTENT_TYPE = "image/png";

    private final SignRepository signRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final UserProfileRepository userProfileRepository;
    private final SupabaseStorageClient supabaseStorageClient;

    @Transactional(readOnly = true)
    public List<SignResponseDTO> myList(Long currentUserId) {
        validateCurrentUserId(currentUserId);

        return signRepository.findByProviderIdOrCustomerIdOrderByUpdatedAtDesc(
                        currentUserId,
                        currentUserId
                )
                .stream()
                .map(SignResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SignContractResponseDTO contractDetail(Long currentUserId, Long signId) {
        validateCurrentUserId(currentUserId);

        SignRequest signRequest = getSignRequest(signId);
        validateParticipant(signRequest, currentUserId);

        PersonalInfo providerInfo = personalInfoRepository.findByUserId(signRequest.getProvider().getId())
                .orElse(null);
        PersonalInfo customerInfo = personalInfoRepository.findByUserId(signRequest.getCustomer().getId())
                .orElse(null);
        UserProfile providerProfile = userProfileRepository.findByUserId(signRequest.getProvider().getId())
                .orElse(null);
        UserProfile customerProfile = userProfileRepository.findByUserId(signRequest.getCustomer().getId())
                .orElse(null);

        return SignContractResponseDTO.from(signRequest, providerInfo, customerInfo, providerProfile, customerProfile);
    }

    @Transactional(readOnly = true)
    public BrokerSignImageResponseDTO brokerSignImage(Long currentUserId) {
        validateCurrentUserId(currentUserId);

        return new BrokerSignImageResponseDTO(
                BROKER_SIGN_OBJECT_PATH,
                supabaseStorageClient.createSignedDownloadUrl(BROKER_SIGN_OBJECT_PATH),
                supabaseStorageClient.signedDownloadTtlSeconds(),
                BROKER_SIGN_CONTENT_TYPE,
                BROKER_SIGN_FILENAME
        );
    }

    @Transactional
    public SignResponseDTO issue(Long currentUserId, SignCreateRequestDTO request) {
        validateCurrentUserId(currentUserId);
        validateCreateRequest(request);

        User customer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("customer를 찾을 수 없습니다."));

        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new NoSuchElementException("매물을 찾을 수 없습니다."));

        Long providerId = property.getLandlordUserId();
        if (providerId == null) {
            throw new IllegalStateException("매물 임대인 정보가 없습니다.");
        }

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new NoSuchElementException("provider를 찾을 수 없습니다."));

        SignRequest signRequest = SignRequest.builder()
                .provider(provider)
                .customer(customer)
                .propertyId(property)
                .status(SignStatus.ISSUED)
                .build();

        return SignResponseDTO.from(signRepository.save(signRequest));
    }

    @Transactional
    public SignResponseDTO approve(Long currentUserId, Long signId) {
        validateCurrentUserId(currentUserId);

        SignRequest signRequest = getSignRequest(signId);
        validateParticipant(signRequest, currentUserId);

        if (signRequest.getStatus() == SignStatus.ISSUED) {
            validateProvider(signRequest, currentUserId);
            signRequest.providerSign();

            return SignResponseDTO.from(signRequest);
        }

        if (signRequest.getStatus() == SignStatus.PROVIDER_SIGNED) {
            validateCustomer(signRequest, currentUserId);
            signRequest.customerSign();

            return SignResponseDTO.from(signRequest);
        }

        throw new IllegalStateException("승인할 수 없는 계약 상태입니다.");
    }

    @Transactional
    public SignResponseDTO cancel(Long currentUserId, Long signId) {
        validateCurrentUserId(currentUserId);

        SignRequest signRequest = getSignRequest(signId);
        validateParticipant(signRequest, currentUserId);
        validateCancelable(signRequest);
        signRequest.cancel();

        return SignResponseDTO.from(signRequest);
    }

    private SignRequest getSignRequest(Long signId) {
        if (signId == null) {
            throw new IllegalArgumentException("signId가 필요합니다.");
        }

        return signRepository.findById(signId)
                .orElseThrow(() -> new NoSuchElementException("계약서를 찾을 수 없습니다."));
    }

    private void validateCurrentUserId(Long currentUserId) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
    }

    private void validateCreateRequest(SignCreateRequestDTO request) {
        if (request == null || request.propertyId() == null) {
            throw new IllegalArgumentException("propertyId가 필요합니다.");
        }
    }

    private void validateParticipant(SignRequest signRequest, Long currentUserId) {
        if (!isProvider(signRequest, currentUserId) && !isCustomer(signRequest, currentUserId)) {
            throw new IllegalStateException("계약서를 처리할 권한이 없습니다.");
        }
    }

    private void validateProvider(SignRequest signRequest, Long currentUserId) {
        if (!isProvider(signRequest, currentUserId)) {
            throw new IllegalStateException("provider만 승인할 수 있습니다.");
        }
    }

    private void validateCustomer(SignRequest signRequest, Long currentUserId) {
        if (!isCustomer(signRequest, currentUserId)) {
            throw new IllegalStateException("customer만 승인할 수 있습니다.");
        }
    }

    private void validateCancelable(SignRequest signRequest) {
        if (signRequest.getStatus() == SignStatus.COMPLETED
                || signRequest.getStatus() == SignStatus.CANCELED) {
            throw new IllegalStateException("취소할 수 없는 계약 상태입니다.");
        }
    }

    private boolean isProvider(SignRequest signRequest, Long currentUserId) {
        return signRequest.getProvider().getId().equals(currentUserId);
    }

    private boolean isCustomer(SignRequest signRequest, Long currentUserId) {
        return signRequest.getCustomer().getId().equals(currentUserId);
    }
}
