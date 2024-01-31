package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.MerchantBankDetails;
import com.asktech.admin.model.ServiceWisePaymentThresold;

public interface ServiceWisePaymentThresoldRepository extends JpaRepository<ServiceWisePaymentThresold, String>{

	ServiceWisePaymentThresold findByMerchantIdAndService(String merchantId, String service);
	
	ServiceWisePaymentThresold findByMerchantId(String merchantId);
}
