package com.asktech.admin.dto.paytmwebhook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class NodalWebhookReqDataExtInfo {

	private String transfer_mode;
	private String externalTransactionId;
}
