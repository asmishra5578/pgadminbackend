package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.TicketTransactionDetails;

public interface TicketTransactionDetailsRepository extends JpaRepository<TicketTransactionDetails, String>{
	
	TicketTransactionDetails findAllByComplaintId(String complaintId);

	List<TicketTransactionDetails> findAllByComplaintIdOrderByIdAsc(String complaintId);

}
