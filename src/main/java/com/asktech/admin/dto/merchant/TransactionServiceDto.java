package com.asktech.admin.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionServiceDto {
	private String merchantId;
	private String gateway;
	private String transactionType;
	private String slab;
	private String commissionType;
	private String commissionValue;
	
}
