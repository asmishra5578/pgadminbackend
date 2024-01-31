package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.model.MerchantKycDetails;

public interface MerchantKycDetailsRepository extends JpaRepository<MerchantKycDetails, String>{

	MerchantKycDetails findByMerchantID(String merchantId);
	
	List<MerchantKycDetails> findAllByMerchantID(String merchantId);
	
	@Query(value = "select * from merchant_kyc_details where date(created) between :start_date and :end_date ",
			nativeQuery = true)
	List<MerchantKycDetails> getKycDateWise(@Param("start_date") String start_date, @Param("end_date") String end_date);
	
	@Query(value = "select * from merchant_kyc_details where merchant_kyc_status= :status ",
			nativeQuery = true)
	List<MerchantKycDetails> getKycStatusWise(@Param("status") String status);
}
