package com.asktech.admin.dto.seam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrLinkdto {
	private String appid;
	private String custName;
	private String custPhone;
	private String custEmail;
	private String custAmount;
	private String linkExpiry;
	private String orderNote;
	private String returnUrl;
	private String secretKey;
	private String orderId;
	private String source;
}
