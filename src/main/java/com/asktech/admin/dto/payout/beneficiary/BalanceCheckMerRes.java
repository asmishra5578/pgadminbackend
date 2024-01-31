package com.asktech.admin.dto.payout.beneficiary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceCheckMerRes {
    private String status;
    private String message;
    private String walletBalance;
    private String lastUpdatedDate;
    private String merchantId;
}