package com.asktech.admin.dto.payout.beneficiary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyBankAccountResponse {

	private String status;
	private String statusCode;
	private String msg;
}
