package com.asktech.admin.dto.payout.Wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WalletRechargeResponse {
    private String amount;
    private String merchantId;

    private String referenceId;
    private String bankName;
    private String mainWalletId;
    private String status;
    private String referenceName;
    private String rechargeAgentName;

}
