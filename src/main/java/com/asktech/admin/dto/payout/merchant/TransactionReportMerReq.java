package com.asktech.admin.dto.payout.merchant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionReportMerReq {
	private String fromDate;
	private String toDate;
}