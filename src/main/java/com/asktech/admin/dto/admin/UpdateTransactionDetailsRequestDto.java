package com.asktech.admin.dto.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateTransactionDetailsRequestDto {
	
    private String internalOrderId;
	private String utrid;
	private String referenceId;
	private String transactionStatus;
	private String transactionMessage;
	private String comment;
	private String callBackFlag;

}
