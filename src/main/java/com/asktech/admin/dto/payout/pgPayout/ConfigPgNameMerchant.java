package com.asktech.admin.dto.payout.pgPayout;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigPgNameMerchant {
    private long id;
    private String merchantId;
    private String pgId;
    private String service;
    private String status;
    private String enableRandomName;
    private String pgName;
}
