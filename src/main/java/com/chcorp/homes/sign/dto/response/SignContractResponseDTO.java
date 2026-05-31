package com.chcorp.homes.sign.dto.response;

import com.chcorp.homes.properties.entity.Property;
import com.chcorp.homes.sign.entity.SignContract;
import com.chcorp.homes.sign.entity.SignRequest;
import com.chcorp.homes.sign.entity.SignStatus;
import com.chcorp.homes.diagnosis.entity.UserProfile;
import com.chcorp.homes.users.entity.PersonalInfo;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record SignContractResponseDTO(
        Long signId,
        SignStatus status,
        Instant createdAt,
        Instant updatedAt,
        ContractPropertyDTO property,
        ContractPartyDTO provider,
        ContractPartyDTO customer,
        ContractFormDTO contract
) {
    public static SignContractResponseDTO from(
            SignRequest signRequest,
            SignContract signContract,
            PersonalInfo providerInfo,
            PersonalInfo customerInfo,
            UserProfile providerProfile,
            UserProfile customerProfile
    ) {
        return new SignContractResponseDTO(
                signRequest.getId(),
                signRequest.getStatus(),
                signRequest.getCreatedAt(),
                signRequest.getUpdatedAt(),
                ContractPropertyDTO.from(signRequest.getPropertyId()),
                ContractPartyDTO.from(signRequest.getProvider().getId(), providerInfo, providerProfile),
                ContractPartyDTO.from(signRequest.getCustomer().getId(), customerInfo, customerProfile),
                ContractFormDTO.from(signContract)
        );
    }

    public record ContractPropertyDTO(
            Long propertyId,
            String address,
            Integer depositAmount,
            BigDecimal supplyAreaM2,
            BigDecimal exclusiveAreaM2,
            String buildingUse,
            LocalDate moveInDate
    ) {
        public static ContractPropertyDTO from(Property property) {
            return new ContractPropertyDTO(
                    property.getId(),
                    property.getAddress(),
                    property.getDepositAmount(),
                    property.getSupplyAreaM2(),
                    property.getExclusiveAreaM2(),
                    property.getBuildingUse(),
                    property.getMoveInDate()
            );
        }
    }

    public record ContractPartyDTO(
            Long userId,
            String realName,
            String address,
            String phone,
            LocalDate birthDate
    ) {
        public static ContractPartyDTO from(Long userId, PersonalInfo personalInfo, UserProfile userProfile) {
            LocalDate birthDate = userProfile == null ? null : userProfile.getBirthDate();

            if (personalInfo == null) {
                return new ContractPartyDTO(userId, null, null, null, birthDate);
            }

            return new ContractPartyDTO(
                    userId,
                    personalInfo.getRealName(),
                    personalInfo.getAddress(),
                    personalInfo.getPhone(),
                    birthDate
            );
        }
    }

    public record ContractFormDTO(
            LocalDate leaseEndDate,
            Long contractAmount,
            Long interimAmount1,
            LocalDate interimAmount1Date,
            Long interimAmount2,
            LocalDate interimAmount2Date,
            Long balanceAmount,
            LocalDate balanceDate,
            String specialTerms,
            String buildingDong,
            String unitHo,
            String rentedPart,
            Long providerSignatureFileId,
            Long customerSignatureFileId,
            Long completedPdfFileId,
            Instant providerSignedAt,
            Instant customerSignedAt
    ) {
        public static ContractFormDTO from(SignContract signContract) {
            if (signContract == null) {
                return null;
            }

            return new ContractFormDTO(
                    signContract.getLeaseEndDate(),
                    signContract.getContractAmount(),
                    signContract.getInterimAmount1(),
                    signContract.getInterimAmount1Date(),
                    signContract.getInterimAmount2(),
                    signContract.getInterimAmount2Date(),
                    signContract.getBalanceAmount(),
                    signContract.getBalanceDate(),
                    signContract.getSpecialTerms(),
                    signContract.getBuildingDong(),
                    signContract.getUnitHo(),
                    signContract.getRentedPart(),
                    signContract.getProviderSignatureFileId(),
                    signContract.getCustomerSignatureFileId(),
                    signContract.getCompletedPdfFileId(),
                    signContract.getProviderSignedAt(),
                    signContract.getCustomerSignedAt()
            );
        }
    }
}
