package com.asktech.admin.dto.admin;

import java.util.List;

import com.asktech.admin.customInterface.IMerchantStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDashboardDet {

	private String totalMerchants;
	private List<IMerchantStatus> totalMerchantStatus;
	private String merchantByAdmin;
	private List<IMerchantStatus> adminMerchantStatus;
}
