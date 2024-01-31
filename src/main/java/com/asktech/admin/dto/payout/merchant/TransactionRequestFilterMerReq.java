package com.asktech.admin.dto.payout.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequestFilterMerReq {
	private String fromDate;
	private String toDate;
	private String orderId;
	private String status;
	private String transactionType;
}
