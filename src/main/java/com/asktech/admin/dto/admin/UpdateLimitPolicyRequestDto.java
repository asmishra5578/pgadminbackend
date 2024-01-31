package com.asktech.admin.dto.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateLimitPolicyRequestDto {

    private String adminUuid;
    private String merchantId;
    private String pgId;
    private String service;
    private String policyType;
    private String dailyLimit;
    private String maxLimit;
    private String minLimit;
    
}
