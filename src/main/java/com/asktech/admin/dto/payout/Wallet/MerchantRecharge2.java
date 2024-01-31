package com.asktech.admin.dto.payout.Wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRecharge2 {
    private long id;
    private String amount;
    private String utrid;
    private String merchantId;

    private String commission;
    private String referenceId;
    private String rechargeId;
    private String bankName;
    private String referenceName;
    private String walletId;
    private String mainWalletId;
    private String note1;
    private String note2;
    private String note3;
    private String status;
    private String rechargeAgent;
    private String rechargeAgentName;
}
