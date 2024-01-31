package com.asktech.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.admin.dto.admin.AddOrUpdateBankListRequest;
import com.asktech.admin.dto.admin.AddOrUpdateWalletListRequest;
import com.asktech.admin.dto.admin.UpdateLimitPolicyRequestDto;
import com.asktech.admin.dto.utility.ErrorResponseDto;
import com.asktech.admin.dto.utility.SuccessResponseDto;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.SuccessCode;
import com.asktech.admin.exception.JWTException;
import com.asktech.admin.exception.SessionExpiredException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.seam.BankList;
import com.asktech.admin.model.seam.WalletList;
import com.asktech.admin.service.PGAdminDashboardService;
import com.asktech.admin.util.JwtUserValidator;
import com.asktech.admin.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;

@SuppressWarnings("deprecation")
@RestController
public class PGAdminDashboardController {

    @Autowired
    PGAdminDashboardService pGAdminDashboardService;
    @Autowired
    JwtUserValidator jwtUserValidator;

    static Logger logger = LoggerFactory.getLogger(PGAdminDashboardController.class);

    @PostMapping("api/paying/add/modify/bank")
    @ApiOperation(value = "Add or modify bank list with merchant")
    public ResponseEntity<?> addOrUpdateBankList(@RequestBody AddOrUpdateBankListRequest addOrUpdateBankListRequest)
            throws ValidationExceptions, JsonProcessingException, UserException, JWTException, SessionExpiredException {
        SuccessResponseDto sdto = new SuccessResponseDto();
        ErrorResponseDto erdto = new ErrorResponseDto();
        if (StringUtils.isEmpty(addOrUpdateBankListRequest.getMerchantId())
                || StringUtils.isEmpty(addOrUpdateBankListRequest.getBankcode())
                || StringUtils.isEmpty(addOrUpdateBankListRequest.getPgBankCode())
                || StringUtils.isEmpty(addOrUpdateBankListRequest.getAdminUuid())) {
            erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
                    "Field can't be empty", false, 100);
            logger.error("Add or Update Bank list failed: ");
            return ResponseEntity.ok().body(erdto);
        }
        jwtUserValidator.validatebyJwtAdminDetails(addOrUpdateBankListRequest.getAdminUuid());
        BankList bankList = pGAdminDashboardService.addOrUpdateBankList(addOrUpdateBankListRequest);
        sdto.getMsg().add("Bank list added or modified");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("Bank", bankList);
        return ResponseEntity.ok().body(sdto);
    }

    @PostMapping("api/paying/add/modify/wallet")
    @ApiOperation(value = "Add or modify Wallet list with merchant")
    public ResponseEntity<?> addOrUpdateWalletList(
            @RequestBody AddOrUpdateWalletListRequest addOrUpdateWalletListRequest)
            throws ValidationExceptions, JsonProcessingException, UserException, JWTException, SessionExpiredException {
        SuccessResponseDto sdto = new SuccessResponseDto();
        ErrorResponseDto erdto = new ErrorResponseDto();
        if (StringUtils.isEmpty(addOrUpdateWalletListRequest.getMerchantId())
                || StringUtils.isEmpty(addOrUpdateWalletListRequest.getPaymentcode())
                || StringUtils.isEmpty(addOrUpdateWalletListRequest.getPaymentcodepg())
                || StringUtils.isEmpty(addOrUpdateWalletListRequest.getAdminUuid())) {
            erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
                    "Field can't be empty", false, 100);
            logger.error("Add or Update Wallet list failed: ");
            return ResponseEntity.ok().body(erdto);
        }
        jwtUserValidator.validatebyJwtAdminDetails(addOrUpdateWalletListRequest.getAdminUuid());
        WalletList walletList = pGAdminDashboardService.addOrUpdateWalletList(addOrUpdateWalletListRequest);
        sdto.getMsg().add("Wallet list added or modified");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("Bank", walletList);
        return ResponseEntity.ok().body(sdto);
    }

    @PutMapping("api/paying/update/limit/policy")
    @ApiOperation(value = "Update limit policy by admin, Merchant, PG and Service wise.")
    public ResponseEntity<?> updateLimitPolicyMerchantPGAndServiceWise(
            @RequestBody UpdateLimitPolicyRequestDto updateLimitPolicyRequestDto)
            throws ValidationExceptions, JsonProcessingException, UserException, JWTException, SessionExpiredException {
        SuccessResponseDto sdto = new SuccessResponseDto();
        ErrorResponseDto erdto = new ErrorResponseDto();
        if (StringUtils.isEmpty(updateLimitPolicyRequestDto.getAdminUuid())
                || StringUtils.isEmpty(updateLimitPolicyRequestDto.getPolicyType())
                || (StringUtils.isEmpty(updateLimitPolicyRequestDto.getDailyLimit())
                        && StringUtils.isEmpty(updateLimitPolicyRequestDto.getMaxLimit())
                        && StringUtils.isEmpty(updateLimitPolicyRequestDto.getMinLimit()))) {
            erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
                    "Field can't be empty", false, 100);
            logger.error("Update limit policy failed: ");
            return ResponseEntity.ok().body(erdto);
        }
        jwtUserValidator.validatebyJwtAdminDetails(updateLimitPolicyRequestDto.getAdminUuid());
        pGAdminDashboardService.updateLimitPolicyMerchantPGAndServiceWise(updateLimitPolicyRequestDto);
        sdto.getMsg().add("Limit Policy updated..");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        return ResponseEntity.ok().body(sdto);
    }

}
