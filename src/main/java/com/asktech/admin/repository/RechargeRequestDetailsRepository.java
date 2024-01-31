package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.asktech.admin.model.RechargeRequestDetails;

/**@author abhimanyu-kumar*/
public interface RechargeRequestDetailsRepository extends JpaRepository<RechargeRequestDetails, Long>{

	//recharge_request_details


	
	
	RechargeRequestDetails findByDistributorIDAndMerchantID(String distributorID, String merchantID);

	@Query(value = "SELECT * FROM recharge_request_details  WHERE distributorid =:distributorID AND status =:status order by id asc", nativeQuery=true)
	List<RechargeRequestDetails> findAllByDistributorIDAndAndStatus(String distributorID, String status);
	@Query(value = "SELECT * FROM recharge_request_details  WHERE DATE(created) BETWEEN  :fromDate and :upToDate AND distributorid =:distributorID AND status =:status  order by id asc", nativeQuery=true)
	List<RechargeRequestDetails> findAllByDistributorIDAndFromDateUpToDateAndStatus(String distributorID,String fromDate, String upToDate, String status);
	@Query(value = "SELECT * FROM recharge_request_details  WHERE distributorid =:distributorID  order by id asc", nativeQuery=true)
	List<RechargeRequestDetails> findAllByDistributorID(String distributorID);

	RechargeRequestDetails findByDistributorIDAndMerchantIDAndUuid(String distributorID, String merchantID,String rechargeRequestUuid);

}
