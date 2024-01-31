package com.asktech.admin.dto.merchant;

import com.asktech.admin.model.RefundDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRefundResponse {

	private RefundDetails refundDetails;
	private String header; 
}
