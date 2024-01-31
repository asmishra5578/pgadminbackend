package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.asktech.admin.model.DistributorMerchantDetails;

@Repository
public interface DistributorMerchantAssociationDetailsRepository extends JpaRepository<DistributorMerchantDetails, Long>{

	DistributorMerchantDetails findByDistributorIDAndMerchantID(String distributorID,String merchantID);
	
	/*
	 * @Query(value =
	 * "SELECT distributor_id FROM distributor_merchant_association_details  WHERE merchant_id =:merchantID"
	 * , nativeQuery=true) String findByMerchantID(@Param(value = "merchantID")
	 * String merchantID);
	 */
	@Query(value = "SELECT distributor_id FROM distributor_merchant_association_details  WHERE merchant_id =:merchantID", nativeQuery=true)
	String findByMerchantID(@Param(value = "merchantID") String merchantID);
	
	
	@Query(value = "SELECT merchant_id FROM distributor_merchant_association_details  WHERE distributor_id =:distributorID", nativeQuery=true)
	List<String> findAllByDistributorID(@Param(value = "distributorID") String distributorID);

	DistributorMerchantDetails findByDistributorID(String distributorID);


}
