package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.TicketComplaintSubType;

public interface TicketComplaintSubTypeRepository extends JpaRepository<TicketComplaintSubType, String>{

	List<TicketComplaintSubType> findBycommType(String complaintType);

	TicketComplaintSubType findByCommTypeAndCommSubType(String complaintType, String subType);

	TicketComplaintSubType findByCommTypeAndCommSubTypeAndStatus(String complaintType, String complaintSubType,
			String string);

}
