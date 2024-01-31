package com.asktech.admin.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommissionReq {
	private String merchantId;
	private String gateway;
	private String type;
	private String amount;
	private String commissionValue;
	private String unitType;
}
