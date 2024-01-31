package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.asktech.admin.model.LetzpayTransactionDetails;
import java.lang.String;

public interface LetzpayTransactionDetailsRepository extends JpaRepository<LetzpayTransactionDetails, String>{

	@Query(value = "select * "
			+ " from letzpay_transaction_details tr "
			+ " where created between (NOW() - INTERVAL 40000 MINUTE) and (NOW() - INTERVAL 5 MINUTE) order by id ",
			nativeQuery = true)
	List<LetzpayTransactionDetails> getAllRecords();

}
