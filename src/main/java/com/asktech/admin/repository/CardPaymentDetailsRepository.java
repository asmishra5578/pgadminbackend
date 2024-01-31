package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.model.CardPaymentDetails;

public interface CardPaymentDetailsRepository extends JpaRepository<CardPaymentDetails, String>{


	@Query(value = "select sum(order_amount) amt from card_payment_details where date(created) between :start_date and :end_date  ",
			nativeQuery = true)
	String getAllCardPaymentDateWise(@Param("start_date") String start_date, @Param("end_date") String end_date);
	
}
