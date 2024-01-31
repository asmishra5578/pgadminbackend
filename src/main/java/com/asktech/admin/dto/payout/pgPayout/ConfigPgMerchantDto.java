package com.asktech.admin.dto.payout.pgPayout;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigPgMerchantDto {
    private String merchantId;
    private String pgId;
    private String service;
    private String status;
}
