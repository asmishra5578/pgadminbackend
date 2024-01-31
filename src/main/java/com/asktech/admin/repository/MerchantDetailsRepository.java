package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.model.MerchantDetails;
public interface MerchantDetailsRepository extends JpaRepository<MerchantDetails, String>{

	//MerchantDetails findByMerchantName(String merchantName);

	MerchantDetails findByAppID(String encryptCardNumberOrExpOrCvv);
	
	MerchantDetails findByAppIDAndSecretId(String encryptCardNumberOrExpOrCvv, String secretkey);

	MerchantDetails findByMerchantEmail(String userNameOrEmail);

	MerchantDetails findByuuid(String uuid);	

	MerchantDetails findByMerchantID(String merchantId);
	
	@Query(value="SELECT * FROM merchant_details a where a.merchantid = :merchantId and date(created) between :start_date and :end_date", nativeQuery = true)
	MerchantDetails findByMerchantIDWithDate(@Param("merchantId") String merchantId, @Param("start_date") String start_date, @Param("end_date") String end_date) ;

	@Query(value="SELECT * FROM merchant_details a where a.merchant_name = :merchantName and date(created) between :start_date and :end_date", nativeQuery = true)
	List<MerchantDetails> findByMerchantName(@Param("merchantName") String merchantName, @Param("start_date") String start_date, @Param("end_date") String end_date);

	MerchantDetails findByPhoneNumber(String phoneNumber);
	
	@Query(value = "select * from merchant_details where date(created) between :start_date and :end_date ", nativeQuery = true)
	public List<MerchantDetails> getAllMerchantDetailsReportDateWise(@Param("start_date") String start_date, @Param("end_date") String end_date) ;

	@Query(value = "select count(*) from merchant_details", nativeQuery = true)
	public String findTotalNoOfMerchants();

	@Query(value = "select * from merchant_details  where merchantid in :merchantID", nativeQuery=true )
	List<MerchantDetails> findAllByMerchantID(@Param("merchantID") List<String> merchantID);

	MerchantDetails findByMerchantIDAndUserStatus(String merchantId, String string);

}
