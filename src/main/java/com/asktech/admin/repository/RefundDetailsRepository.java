package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.model.RefundDetails;

public interface RefundDetailsRepository extends JpaRepository<RefundDetails, String> {

	RefundDetails findByMerchantIdAndMerchantOrderIdAndStatus(String merchantId, String orderId, String string);


	@Query(value = "select sum(amount) amt from refund_details where date(created) between :start_date and :end_date ",
			nativeQuery = true)
	String getAllRefundDateWise(@Param("start_date") String start_date, @Param("end_date") String end_date);
	
	
	@Query(value = "select * from refund_details where date(created) between :start_date and :end_date ",
			nativeQuery = true)
	List<RefundDetails> getAllRefundDetail(@Param("start_date") String start_date, @Param("end_date") String end_date);
	
	
	@Query(value = "select * from refund_details where id= :refundid ",
			nativeQuery = true)
	List<RefundDetails> getAllRefundDetailByRefundId(@Param("refundid") String refundid);
	
	@Query(value = "select * from refund_details where merchant_order_id= :merchantOrderId ",
			nativeQuery = true)
	List<RefundDetails> getAllRefundByMerchantOrderId(@Param("merchantOrderId") String merchantOrderId);
	
	@Query(value = "select * from refund_details where and status= :status ",
			nativeQuery = true)
	List<RefundDetails> getAllRefundByStatus(@Param("status") String status);
	
	@Query(value = "select * from refund_details where merchant_order_id= :merchantOrderId  and status= :status ",
			nativeQuery = true)
	List<RefundDetails> getAllRefundByStatusOrMerchantOrderId(@Param("merchantOrderId") String merchantid, @Param("status") String status);
	
}
