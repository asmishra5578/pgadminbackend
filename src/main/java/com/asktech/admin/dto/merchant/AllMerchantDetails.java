package com.asktech.admin.dto.merchant;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllMerchantDetails {
    private String merchantEMail;
    private String phoneNumber;
    private String merchantId;
    private String kycStatus;
    private List<MerchantPgdetails> merchantpgdetails;
    
}