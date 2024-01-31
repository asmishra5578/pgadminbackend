package com.asktech.admin.dto.admin;

import com.asktech.admin.dto.payout.beneficiary.TransactionChangeResponce;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransactionChangeResponceListDto {
	
	private TransactionChangeResponce successDataTransactionChangeResponce;
	private TransactionChangeResponce failedDataTransactionChangeResponce;
	private TransactionChangeResponce pendingDataTransactionChangeResponce;
	private TransactionChangeResponce refundDataTransactionChangeResponce;

}
