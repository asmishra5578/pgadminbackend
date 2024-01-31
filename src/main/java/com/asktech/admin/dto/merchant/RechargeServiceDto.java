package com.asktech.admin.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RechargeServiceDto {
	private String merchantId;
	private String commissionType;
	private String commissionValue;
}
