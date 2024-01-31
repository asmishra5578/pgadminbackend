package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.customInterface.IMerchantList;
import com.asktech.admin.customInterface.IMerchantStatus;
import com.asktech.admin.customInterface.IMerchantTransaction;
import com.asktech.admin.customInterface.ITransactionByPgId;

import com.asktech.admin.model.MerchantDetails;

public interface MerchantDetailsAddRepository extends JpaRepository<MerchantDetails, String>{

	@Query(value = "select ms.user_status as status,ms.created_by as adminUUid,count(ms.user_status) as merchantCount from merchant_details ms "
			+ "where created_by= :uuid "
			+ "group by user_status,created_by", nativeQuery = true)
	List<IMerchantStatus> countTotalMerchantDetailsByUserStatusAndCreatedBy(@Param("uuid") String uuid );
	
	@Query(value = "select ms.user_status as status,ms.created_by as adminUUid,count(ms.user_status) as merchantCount from merchant_details ms "
			+ "group by user_status,created_by", nativeQuery = true)
	List<IMerchantStatus> countAllTotalMerchantDetailsByUserStatu();
	
	@Query(value = "select count(*) as merchantCount from merchant_details ms "
			+ "where created_by= :uuid ", nativeQuery = true)
	String countTotalMerchantDetailsByCreatedBy(@Param("uuid") String uuid );
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and date(a.created) = curdate()-1 "
			+ "and b.created_by= :uuid "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getYesterdayTrDetails(@Param("uuid") String uuid  );
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and date(a.created) between :start_date and :end_date "
			+ "and b.created_by= :uuid "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getDateTrDetails(@Param("uuid") String uuid, @Param("start_date") String start_date, @Param("end_date") String end_date );
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and date(a.created) = curdate() "
			+ "and b.created_by= :uuid "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getTodayTrDetails(@Param("uuid") String uuid  );
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount  "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and MONTH(a.created) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "and YEAR(a.created) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "and b.created_by= :uuid "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getLastMonthTrDetails(@Param("uuid") String uuid  );
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and MONTH(a.created) = MONTH(CURRENT_DATE()) "
			+ "and YEAR(a.created) = YEAR(CURRENT_DATE()) "
			+ "and b.created_by= :uuid "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getCurrMonthTrDetails(@Param("uuid") String uuid  );
	
	//@Query(value="SELECT a.merchantemail, a.merchantid, a.merchant_name, a.phone_number, a.user_status, a.kyc_status FROM merchant_details a where a.created_by = :created_by", nativeQuery = true)
	//List<IMerchantList> getCompleteMerchantList(@Param("created_by") String created_by);
	
	@Query(value="select tr.id, tr.created, tr.updated, tr.amount, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where tr.pg_id = :pg_id and date(created) between :start_date and :end_date", nativeQuery = true)
	List<ITransactionByPgId> getTransactionByPgId(@Param("pg_id") String pg_id, @Param("start_date") String start_date, @Param("end_date") String end_date);
	
	@Query(value="select tr.id, tr.created, tr.updated, tr.amount, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where tr.id = :trId and date(created) between :start_date and :end_date", nativeQuery = true)
	List<ITransactionByPgId> getTransactionByTrId(@Param("trId") String trId, @Param("start_date") String start_date, @Param("end_date") String end_date);
	
	@Query(value="select tr.id, tr.created, tr.updated, tr.amount, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where tr.merchant_id = :merchant_id and date(created) between :start_date and :end_date", nativeQuery = true)
	List<ITransactionByPgId> getTransactionByMerchantId(@Param("merchant_id") String merchant_id, @Param("start_date") String start_date, @Param("end_date") String end_date);
	
	
	@Query(value="SELECT a.merchantemail,a.appid,a.secret_id,a.salt_key,a.uuid, a.merchantid, a.merchant_name, a.phone_number, a.user_status, a.kyc_status FROM merchant_details a", nativeQuery = true)
	List<IMerchantList> getCompleteMerchantList();
	
}
