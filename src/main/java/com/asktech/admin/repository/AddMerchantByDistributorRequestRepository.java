package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.model.AddMerchantByDistributorRequest;

public interface AddMerchantByDistributorRequestRepository extends JpaRepository<AddMerchantByDistributorRequest,Long>{

	AddMerchantByDistributorRequest  findByDistributorID(String distributorID);

	// findAllByDistributorIDAndFromDateUpToDateAndStatus is not working as expected, fix this query
	@Query(value = "SELECT * FROM addmerchantby_distributorrequest_details  WHERE DATE(created) BETWEEN  :fromDate and :upToDate AND distributor_id =:distributorID AND status =:status order by id asc", nativeQuery=true)
	List<AddMerchantByDistributorRequest> findAllByDistributorIDAndFromDateUpToDateAndStatus(@Param("distributorID") String distributorID,@Param("fromDate") String fromDate,@Param("upToDate") String upToDate,@Param("status") String status);
	@Query(value = "SELECT * FROM addmerchantby_distributorrequest_details  WHERE distributor_id =:distributorID AND status =:status order by id asc", nativeQuery=true)
	List<AddMerchantByDistributorRequest> findByDistributorIDAndStatus(@Param("distributorID") String distributorID,@Param("status") String status);
	@Query(value = "SELECT * FROM addmerchantby_distributorrequest_details  WHERE distributor_id =:distributorID  order by id asc", nativeQuery=true)
	List<AddMerchantByDistributorRequest> findAllByDistributorID(@Param("distributorID") String distributorID);

	AddMerchantByDistributorRequest findByDistributorIDAndUuid(String distributorID, String addMerchantRequestUuid);

	
	
	
	//group by distributor_id
}
