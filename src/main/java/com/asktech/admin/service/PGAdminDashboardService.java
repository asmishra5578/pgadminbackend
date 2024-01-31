package com.asktech.admin.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.admin.AddOrUpdateBankListRequest;
import com.asktech.admin.dto.admin.AddOrUpdateWalletListRequest;
import com.asktech.admin.dto.admin.UpdateLimitPolicyRequestDto;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.MerchantPGServices;
import com.asktech.admin.model.PGConfigurationDetails;
import com.asktech.admin.model.seam.BankList;
import com.asktech.admin.model.seam.WalletList;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.MerchantPGServicesRepository;
import com.asktech.admin.repository.PGConfigurationDetailsRepository;
import com.asktech.admin.repository.seam.BankListRepository;
import com.asktech.admin.repository.seam.WalletListRepository;
import com.asktech.admin.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

@SuppressWarnings("deprecation")
@Service
public class PGAdminDashboardService implements ErrorValues {

    static Logger logger = LoggerFactory.getLogger(PGAdminDashboardService.class);
    @Autowired
    BankListRepository bankListRepository;
    @Autowired
    MerchantDetailsRepository merchantDetailsRepository;
    @Autowired
    WalletListRepository walletListRepository;
    @Autowired
    PGConfigurationDetailsRepository pgConfigurationDetailsRepository;
    @Autowired
    MerchantPGServicesRepository merchantPGServicesRepository;

    public BankList addOrUpdateBankList(AddOrUpdateBankListRequest addOrUpdateBankListRequest)
            throws ValidationExceptions, JsonProcessingException {
        MerchantDetails merchantDetails = merchantDetailsRepository
                .findByMerchantID(addOrUpdateBankListRequest.getMerchantId());
        if (merchantDetails == null) {
            throw new ValidationExceptions(MERCHANT_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
        }
        BankList bankList = bankListRepository.findByBankcodeAndPgBankCodeAndMerchantId(
                addOrUpdateBankListRequest.getBankcode(), addOrUpdateBankListRequest.getPgBankCode(),
                addOrUpdateBankListRequest.getMerchantId());
        if (bankList == null) {
            logger.info("Bank list not found going for add new Bank: ");
            if (StringUtils.isEmpty(addOrUpdateBankListRequest.getBankname())
                    || StringUtils.isEmpty(addOrUpdateBankListRequest.getBankname())) {
                throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
            }
            List<BankList> bankList2 = bankListRepository.findByPgNameAndMerchantId(
                    addOrUpdateBankListRequest.getPgName(), addOrUpdateBankListRequest.getMerchantId());
            if (!bankList2.isEmpty()) {
                bankList2.forEach(o -> {
                    o.setStatus("DEACTIVE");
                });
                bankListRepository.saveAll(bankList2);
            }
            bankList = new BankList(addOrUpdateBankListRequest.getBankname(), addOrUpdateBankListRequest.getBankcode(),
                    addOrUpdateBankListRequest.getPgBankCode(), addOrUpdateBankListRequest.getPgName(), "ACTIVE",
                    addOrUpdateBankListRequest.getMerchantId());
            bankListRepository.save(bankList);
        } else {
            logger.info("Bank list not found going for update new Bank: " + Utility.convertDTO2JsonString(bankList));
            if (!StringUtils.isEmpty(addOrUpdateBankListRequest.getStatus())) {
                if (!(addOrUpdateBankListRequest.getStatus().equals("ACTIVE")
                        || addOrUpdateBankListRequest.getStatus().equals("DEACTIVE"))) {
                    throw new ValidationExceptions(BANK_LIST_STATUS, FormValidationExceptionEnums.BANK_LIST_STATUS);
                } else {
                    bankList.setStatus(addOrUpdateBankListRequest.getStatus());
                }
            }
            if (!StringUtils.isEmpty(addOrUpdateBankListRequest.getBankname())) {
                bankList.setBankname(addOrUpdateBankListRequest.getBankname());
            }
            if (!StringUtils.isEmpty(addOrUpdateBankListRequest.getPgName())) {
                List<BankList> bankList2 = bankListRepository.findByPgNameAndMerchantId(
                        addOrUpdateBankListRequest.getPgName(), addOrUpdateBankListRequest.getMerchantId());
                if (!bankList2.isEmpty()) {
                    throw new ValidationExceptions(LIST_DEACTIVE_FIRST,
                            FormValidationExceptionEnums.LIST_DEACTIVE_FIRST);
                } else {
                    bankList.setBankname(addOrUpdateBankListRequest.getPgName());
                }
            }
            if (!StringUtils.isEmpty(addOrUpdateBankListRequest.getPgId())) {
                bankList.setPgId(addOrUpdateBankListRequest.getPgId());
            }
            bankListRepository.save(bankList);
        }
        logger.info("Bank list save and Return:: " + Utility.convertDTO2JsonString(bankList));
        return bankList;
    }

    public WalletList addOrUpdateWalletList(AddOrUpdateWalletListRequest addOrUpdateWalletListRequest)
            throws ValidationExceptions, JsonProcessingException {
        MerchantDetails merchantDetails = merchantDetailsRepository
                .findByMerchantID(addOrUpdateWalletListRequest.getMerchantId());
        if (merchantDetails == null) {
            throw new ValidationExceptions(MERCHANT_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
        }
        WalletList walletList = walletListRepository.findByPaymentcodepgAndPaymentcodeAndMerchantId(
                addOrUpdateWalletListRequest.getPaymentcodepg(), addOrUpdateWalletListRequest.getPaymentcode(),
                addOrUpdateWalletListRequest.getMerchantId());
        if (walletList == null) {
            logger.info("Wallet list not found going for add new Wallet: ");
            if (StringUtils.isEmpty(addOrUpdateWalletListRequest.getPgname())
                    || StringUtils.isEmpty(addOrUpdateWalletListRequest.getWalletname())) {
                throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
            }
            List<WalletList> walletList2 = walletListRepository.findByPgnameAndMerchantId(
                    addOrUpdateWalletListRequest.getPgname(), addOrUpdateWalletListRequest.getMerchantId());
            if (!walletList2.isEmpty()) {
                walletList2.forEach(o -> {
                    o.setStatus("DEACTIVE");
                });
                walletListRepository.saveAll(walletList2);
            }
            walletList = new WalletList(addOrUpdateWalletListRequest.getWalletname(),
                    addOrUpdateWalletListRequest.getPaymentcodepg(),
                    addOrUpdateWalletListRequest.getPgname(), addOrUpdateWalletListRequest.getPaymentcode(), "ACTIVE",
                    addOrUpdateWalletListRequest.getMerchantId());
            walletListRepository.save(walletList);
        } else {
            logger.info(
                    "Wallet list not found going for update new Wallet: " + Utility.convertDTO2JsonString(walletList));
            if (!StringUtils.isEmpty(addOrUpdateWalletListRequest.getStatus())) {
                if (!(addOrUpdateWalletListRequest.getStatus().equals("ACTIVE")
                        || addOrUpdateWalletListRequest.getStatus().equals("DEACTIVE"))) {
                    throw new ValidationExceptions(BANK_LIST_STATUS, FormValidationExceptionEnums.BANK_LIST_STATUS);
                } else {
                    walletList.setStatus(addOrUpdateWalletListRequest.getStatus());
                }
            }
            if (!StringUtils.isEmpty(addOrUpdateWalletListRequest.getWalletname())) {
                walletList.setWalletname(addOrUpdateWalletListRequest.getWalletname());
            }
            if (!StringUtils.isEmpty(addOrUpdateWalletListRequest.getPgname())) {
                List<WalletList> walletList2 = walletListRepository.findByPgnameAndMerchantId(
                        addOrUpdateWalletListRequest.getPgname(), addOrUpdateWalletListRequest.getMerchantId());
                if (!walletList2.isEmpty()) {
                    throw new ValidationExceptions(LIST_DEACTIVE_FIRST,
                            FormValidationExceptionEnums.LIST_DEACTIVE_FIRST);
                } else {
                    walletList.setPgname(addOrUpdateWalletListRequest.getPgname());
                }
            }
            if (!StringUtils.isEmpty(addOrUpdateWalletListRequest.getPgId())) {
                walletList.setPgId(addOrUpdateWalletListRequest.getPgId());
            }
            walletListRepository.save(walletList);
        }
        logger.info("Wallet list save and Return:: " + Utility.convertDTO2JsonString(walletList));
        return walletList;
    }

    public void updateLimitPolicyMerchantPGAndServiceWise(UpdateLimitPolicyRequestDto updateLimitPolicyRequestDto)
            throws ValidationExceptions {
        if (updateLimitPolicyRequestDto.getPolicyType().equals("MERCHANT")
                && !StringUtils.isEmpty(updateLimitPolicyRequestDto.getMerchantId())) {
            MerchantDetails merchantDetails = merchantDetailsRepository
                    .findByMerchantIDAndUserStatus(updateLimitPolicyRequestDto.getMerchantId(), "ACTIVE");
            if (merchantDetails == null) {
                throw new ValidationExceptions(MERCHANT_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
            }
            if (!StringUtils.isEmpty(updateLimitPolicyRequestDto.getDailyLimit())) {
                merchantDetails.setMerchantDailyLimit(updateLimitPolicyRequestDto.getDailyLimit());
            }
            if (!StringUtils.isEmpty(updateLimitPolicyRequestDto.getMinLimit())) {
                merchantDetails.setMinTicketSize(updateLimitPolicyRequestDto.getMinLimit());
            }
            if (!StringUtils.isEmpty(updateLimitPolicyRequestDto.getMaxLimit())) {
                merchantDetails.setMaxTicketSize(updateLimitPolicyRequestDto.getMaxLimit());
            }
            merchantDetailsRepository.save(merchantDetails);
        } else if (updateLimitPolicyRequestDto.getPolicyType().equals("PG")
                && !StringUtils.isEmpty(updateLimitPolicyRequestDto.getPgId())) {
            PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
                    .findByPgUuidAndStatus(updateLimitPolicyRequestDto.getPgId(), "ACTIVE");
            if (pgConfigurationDetails == null) {
                throw new ValidationExceptions(PG_NOT_CREATED, FormValidationExceptionEnums.PG_NOT_CREATED);
            }
            if (!StringUtils.isEmpty(updateLimitPolicyRequestDto.getDailyLimit())) {
                pgConfigurationDetails.setPgDailyLimit(updateLimitPolicyRequestDto.getDailyLimit());
            }
            if (!StringUtils.isEmpty(updateLimitPolicyRequestDto.getMinLimit())) {
                pgConfigurationDetails.setPgMinTicketSize(updateLimitPolicyRequestDto.getMinLimit());
            }
            if (!StringUtils.isEmpty(updateLimitPolicyRequestDto.getMaxLimit())) {
                pgConfigurationDetails.setPgMaxTicketSize(updateLimitPolicyRequestDto.getMaxLimit());
            }
            pgConfigurationDetailsRepository.save(pgConfigurationDetails);
        } else if (updateLimitPolicyRequestDto.getPolicyType().equals("SERVICE")
                && !StringUtils.isEmpty(updateLimitPolicyRequestDto.getMerchantId())
                && !StringUtils.isEmpty(updateLimitPolicyRequestDto.getPgId())
                && !StringUtils.isEmpty(updateLimitPolicyRequestDto.getService())) {
            MerchantPGServices merchantPGServices = merchantPGServicesRepository
                    .findByMerchantIDAndPgIDAndStatusAndService(updateLimitPolicyRequestDto.getMerchantId(),
                            updateLimitPolicyRequestDto.getPgId(), "ACTIVE", updateLimitPolicyRequestDto.getService().toUpperCase());
            if (merchantPGServices == null) {
                throw new ValidationExceptions(MERCHANT_PG_SERVICE_NOT_FOUND,
                        FormValidationExceptionEnums.MERCHANT_PG_SERVICE_NOT_FOUND);
            }
            if (!StringUtils.isEmpty(updateLimitPolicyRequestDto.getDailyLimit())) {
                merchantPGServices.setServiceDailyLimit(updateLimitPolicyRequestDto.getDailyLimit());
            }
            if (!StringUtils.isEmpty(updateLimitPolicyRequestDto.getMinLimit())) {
                merchantPGServices.setServiceMinTicketSize(updateLimitPolicyRequestDto.getMinLimit());
            }
            if (!StringUtils.isEmpty(updateLimitPolicyRequestDto.getMaxLimit())) {
                merchantPGServices.setServiceMaxTicketSize(updateLimitPolicyRequestDto.getMaxLimit());
            }
            merchantPGServicesRepository.save(merchantPGServices);
        } else {
            throw new ValidationExceptions(LIMIT_POLICY_ERROR, FormValidationExceptionEnums.LIMIT_POLICY_ERROR);
        }
    }

}
