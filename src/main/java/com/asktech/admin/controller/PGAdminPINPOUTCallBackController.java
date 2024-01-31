package com.asktech.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.callback.PayInPayOutCallBackRequest;
import com.asktech.admin.dto.error.ErrorResponseDto;
import com.asktech.admin.dto.utility.SuccessResponseDto;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.SuccessCode;
import com.asktech.admin.exception.JWTException;
import com.asktech.admin.exception.SessionExpiredException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.service.callbackservice.PGAdminPINPOUTCallBackService;
import com.asktech.admin.util.JwtUserValidator;

@SuppressWarnings("deprecation")
@RestController
public class PGAdminPINPOUTCallBackController implements ErrorValues {

    @Autowired
    PGAdminPINPOUTCallBackService pGAdminPINPOUTCallBackService;
    @Autowired
    JwtUserValidator jwtUserValidator;

    @PutMapping("api/payin/callBack")
    public ResponseEntity<?> payInCallBack(@RequestBody PayInPayOutCallBackRequest payInPayOutCallBackRequest) throws UserException, JWTException, SessionExpiredException {
        ErrorResponseDto erdto = new ErrorResponseDto();
        SuccessResponseDto sdto = new SuccessResponseDto();
        if (StringUtils.isEmpty(payInPayOutCallBackRequest.getAdminUuid())
                || payInPayOutCallBackRequest.getOrderIds().isEmpty()) {
            erdto = new ErrorResponseDto(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
            return ResponseEntity.ok().body(erdto);
        }
        jwtUserValidator.validatebyJwtAdminDetails(payInPayOutCallBackRequest.getAdminUuid());
        pGAdminPINPOUTCallBackService.callAndUpdateCallBackFlag(payInPayOutCallBackRequest);
        sdto.getMsg().add("Call Back send for orderIds");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        return ResponseEntity.ok().body(sdto);
    }

    @PutMapping("api/payout/callBack")
    public ResponseEntity<?> payOutCallBack(@RequestBody PayInPayOutCallBackRequest payInPayOutCallBackRequest) throws UserException, JWTException, SessionExpiredException {
        ErrorResponseDto erdto = new ErrorResponseDto();
        SuccessResponseDto sdto = new SuccessResponseDto();
        if (StringUtils.isEmpty(payInPayOutCallBackRequest.getAdminUuid())
                || payInPayOutCallBackRequest.getOrderIds().isEmpty()) {
            erdto = new ErrorResponseDto(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
            return ResponseEntity.ok().body(erdto);
        }
        jwtUserValidator.validatebyJwtAdminDetails(payInPayOutCallBackRequest.getAdminUuid());
        pGAdminPINPOUTCallBackService.callPayoutServiceForUpdateCallBackFlag(payInPayOutCallBackRequest);
        sdto.getMsg().add("Call Back send for orderIds");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        return ResponseEntity.ok().body(sdto);
    }

}
