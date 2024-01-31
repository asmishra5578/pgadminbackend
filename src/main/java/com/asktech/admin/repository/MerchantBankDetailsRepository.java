package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.MerchantBankDetails;

public interface MerchantBankDetailsRepository extends JpaRepository<MerchantBankDetails, String>{

	MerchantBankDetails findByMerchantID(String merchantID);

}
