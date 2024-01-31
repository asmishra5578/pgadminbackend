package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.asktech.admin.model.DistributorDetails;

/**@author abhimanyu-kumar*/
@Repository
public interface DistributorDetailsRepository extends JpaRepository<DistributorDetails, Long>{

	@Query(value = "select * from distributors_details  where distributorid =:distributorID", nativeQuery=true )
	DistributorDetails findByDistributorID(@Param("distributorID")String distributorID);

	//@Query(value = "select * from distributors_details  where distributorid in :listOfdistributorID", nativeQuery=true )
	//List<DistributorDetails> findByDistributorIDInLIST(@Param("listOfdistributorID") List<String> listOfdistributorID);
	
	DistributorDetails findByDistributorEMail(String distributorEMail);

	DistributorDetails findByPhoneNumber(String phoneNumber);

	

}
