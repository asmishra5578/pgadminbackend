package com.asktech.admin.dto.payout.beneficiary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionChangeResponceList {
    private String orderIds;
    private String status;
    private String comment;
}
