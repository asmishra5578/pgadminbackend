package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.model.TicketComplaintDetails;

public interface TicketComplaintDetailsRepository extends JpaRepository<TicketComplaintDetails, String>{

	TicketComplaintDetails findByComplaintId(String complaintId);
	List<TicketComplaintDetails> getByComplaintId(String complaintId);
	List<TicketComplaintDetails> findAllByCreatedBy(String uuid);
	List<TicketComplaintDetails> findAllByUpdatedBy(String uuid);
	List<TicketComplaintDetails>  findAllByPendingWith(String uuid);
	
	@Query(value = "select tc.* "
			+ "from ticket_complaint_details tc "
			+ "where tc.status= :status "
			+ "and date(tc.created) between :start_date and :end_date ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintByStatusWithDate(@Param("start_date") String start_date, @Param("end_date") String end_date ,@Param("status") String status);
	
	@Query(value = "select tc.* "
			+ "from ticket_complaint_details tc "
			+ "where tc.status= :status ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintByStatus(@Param("status") String status);
	
	@Query(value = "select tc.* from ticket_complaint_details tc where tc.comm_type= :ticketType and tc.comm_sub_type= :ticketSubType ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintByTypeAndSubType(@Param("ticketType") String ticketType, @Param("ticketSubType") String ticketSubType);
	
	@Query(value = "select tc.* from ticket_complaint_details tc where tc.complaint_id= :ticketId and tc.comm_type= :ticketType and tc.comm_sub_type= :ticketSubType and tc.status= :status ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintByIdAndTypeAndSubTypeAndStatus(@Param("ticketId")String ticketId, @Param("ticketType") String ticketType, @Param("ticketSubType") String ticketSubType,@Param("status") String status);
	
	@Query(value = "select tc.* from ticket_complaint_details tc where tc.complaint_id= :ticketId and tc.status= :status ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintByIdAndStatus(@Param("ticketId")String ticketId,@Param("status") String status);
	
	@Query(value = "select tc.* from ticket_complaint_details tc where tc.comm_type= :ticketType and tc.comm_sub_type= :ticketSubType and tc.status= :status ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintByTypeAndSubTypeAndStatus(@Param("ticketType") String ticketType, @Param("ticketSubType") String ticketSubType,@Param("status") String status);
	
	@Query(value = "select tc.* from ticket_complaint_details tc where tc.comm_type= :ticketType and tc.status= :status ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintByTypeAndStatus(@Param("ticketType") String ticketType,@Param("status") String status);
	
	@Query(value = "select tc.* from ticket_complaint_details tc where tc.comm_sub_type= :ticketSubType and tc.status= :status ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintBySubTypeAndStatus(@Param("ticketSubType") String ticketSubType,@Param("status") String status);
	
	@Query(value = "select tc.* "
			+ "from ticket_complaint_details tc "
			+ "where tc.comm_type= :ticketType ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintByType(@Param("ticketType") String ticketType);
	
	@Query(value = "select tc.* "
			+ "from ticket_complaint_details tc "
			+ "where tc.comm_sub_type= :ticketSubType ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintBySubType(@Param("ticketSubType") String ticketSubType);
	
	@Query(value = "select tc.* "
			+ "from ticket_complaint_details tc "
			+ "where date(tc.created) between :start_date and :end_date ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintByDate(@Param("start_date") String start_date, @Param("end_date") String end_date);
	
	@Query(value = "select tc.* "
			+ "from ticket_complaint_details tc "
			+ "where tc.complaintId= :complaintid "
			+ "and date(tc.created) = :start_date and :end_date ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintById(@Param("complaintid") String complaintid, @Param("start_date") String start_date, @Param("end_date") String end_date);
	
	@Query(value = "select tc.* "
			+ "from ticket_complaint_details tc "
			+ "where tc.complaintId= :complaintid and tc.status= :status"
			+ "and date(tc.created) = :start_date and :end_date ",
			nativeQuery = true)
	List<TicketComplaintDetails> getComplaintByIdAndStatus(@Param("status") String status, @Param("complaintid") String complaintid, @Param("start_date") String start_date, @Param("end_date") String end_date);
	
	
}
