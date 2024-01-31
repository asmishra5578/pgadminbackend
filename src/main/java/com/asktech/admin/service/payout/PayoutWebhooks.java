package com.asktech.admin.service.payout;

import org.springframework.stereotype.Service;

import com.asktech.admin.constants.payout.Payout;
import com.asktech.admin.dto.paytmwebhook.VanWebhookReq;

import kong.unirest.Unirest;

@Service
public class PayoutWebhooks implements Payout{
	public String vanWebhook(VanWebhookReq dto) {
		System.out.println("Webhook Request");
		String res =  Unirest.post(payoutBaseUrl+"van/webhook/")
				.header("Content-Type", "application/json")
				.body(dto).asString().getBody();
		return res;
	}
	public String nodalWebhook(VanWebhookReq dto) {
		String res =  Unirest.post(payoutBaseUrl+"nodal/webhook/")
				.header("Content-Type", "application/json")
				.body(dto).asString().getBody();
		return res;
	}
}
