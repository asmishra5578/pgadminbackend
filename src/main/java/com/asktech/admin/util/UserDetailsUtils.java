package com.asktech.admin.util;

import com.asktech.admin.customInterface.IUserDetails;
import com.asktech.admin.dto.report.UserDetailsReport;

public  class UserDetailsUtils {

	public static UserDetailsReport updateUserDetails(IUserDetails iUserDetails) {
		
		UserDetailsReport userDetailsReport = new UserDetailsReport();
		
		userDetailsReport.setCustomerName(iUserDetails.getCustomerName());
		userDetailsReport.setEmailId(iUserDetails.getEmailId());
		userDetailsReport.setPhoneNumber(iUserDetails.getPhoneNumber());
		userDetailsReport.setCardNumber(Utility.maskCardNumber(SecurityUtils.decryptSaveDataKMS(iUserDetails.getCardNumber())));
		//userDetailsReport.setPaymentCode(SecurityUtils.decryptSaveDataKMS(iUserDetails.getPaymentCode()));
		userDetailsReport.setPaymentCode(iUserDetails.getPaymentCode());
		userDetailsReport.setVpaUpi(Utility.maskUpiCode(SecurityUtils.decryptSaveDataKMS(iUserDetails.getVpaUpi())));
		return userDetailsReport;
	}
}
