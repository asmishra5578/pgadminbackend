package com.asktech.admin.dto.payout.beneficiary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssociateBankDetails {
	private String orderId;
	private String merchantOrderId;
	private String beneficiaryAccountNo;
	private String beneficiaryIFSCCode;
}
