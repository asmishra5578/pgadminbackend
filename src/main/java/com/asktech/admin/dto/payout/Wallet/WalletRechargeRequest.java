package com.asktech.admin.dto.payout.Wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WalletRechargeRequest {
    private String amount;
    private String utrid;
    private String merchantId;
    private String commission;
    private String referenceId;
    private String bankName;
    private String referenceName;
    private String note1;
    private String note2;
    private String note3;
    private String rechargeAgent;
    private String rechargeType;
    private String rechargeAgentName;
}
