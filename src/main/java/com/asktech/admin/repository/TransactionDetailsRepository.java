package com.asktech.admin.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.customInterface.IHourandCountStatus;
import com.asktech.admin.customInterface.IHourandStatusWise;
import com.asktech.admin.customInterface.ILastTrxMercList;
import com.asktech.admin.customInterface.IMerchantCompoundReport;
import com.asktech.admin.customInterface.IMerchantTransaction;
import com.asktech.admin.customInterface.IMerchantWisePgWiseSum;
import com.asktech.admin.customInterface.IMinuteandCountStatus;
import com.asktech.admin.customInterface.IPaymentOptionAmt;
import com.asktech.admin.customInterface.IStatusCountTrx;
import com.asktech.admin.customInterface.ISumHourandStatusWise;
import com.asktech.admin.customInterface.Ipayment_modeTrxAmount;
import com.asktech.admin.customInterface.StatusAndMinute;
import com.asktech.admin.customInterface.payout.IPgTypeAndCountByStatusAndDate;
import com.asktech.admin.model.TransactionDetails;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, String> {

	@Query(value = "select * from transaction_details where orderid = :orderid", nativeQuery = true)
	List<TransactionDetails> findByOrderID(@Param("orderid") String orderid);


	
	//List<TransactionDetails> findByOrderIDs(@Param("oderIdsListInString2") List<String> oderIdsListInString2);
	//@Query(value = "SELECT * FROM transaction_details where orderid in :oderIdsListInString2 ;", nativeQuery = true)
	//@Query(value = "SELECT td FROM transaction_details td where td.orderid IN (:oderIdsListInString2) ", nativeQuery = true)
	List<TransactionDetails> findByorderIDIn(List<String> oderIdsListInString2);
	@Query(value = "select * from transaction_details where orderid = :orderid", nativeQuery = true)
	TransactionDetails getOneByOrderID(@Param("orderid") String orderid);
	@Query(value = "select * from transaction_details where orderid = :orderid", nativeQuery = true)
	List<TransactionDetails> getByOrderID(@Param("orderid") String orderid);
	TransactionDetails findByOrderIDAndStatus(String string, String string2);

	List<TransactionDetails> findBymerchantOrderIdIn(List<String> oderIdsListInString2);
	@Query(value = "select sum(amount)/100 amt from transaction_details where date(created) between :start_date and :end_date ", nativeQuery = true)
	Ipayment_modeTrxAmount getAllTxnDateWise(@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select sum(amount) cnt from transaction_details where status = 'Cancelled' and date(created) between :start_date and :end_date  ", nativeQuery = true)
	String getAllCancelledTxnDateWise(@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select status,count(1) cnt, sum(amount)/100 amt from transaction_details where upper(payment_option)= upper(:payment_option) and date(created) between :start_date and :end_date  and status ='SUCCESS';", nativeQuery = true)
	Ipayment_modeTrxAmount getAllUPITxnDateWise(@Param("payment_option") String payment_option, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

			@Query(value = "select status,count(1) cnt, sum(amount)/100 amt from transaction_details where upper(payment_option)= upper(:payment_option) and date(created) between :start_date and :end_date  and status ='SUCCESS';", nativeQuery = true)
			Ipayment_modeTrxAmount getAllWalletTxnDateWise(@Param("payment_option") String payment_option,
			@Param("start_date") String start_date, @Param("end_date") String end_date);

			@Query(value = "select status,count(1) cnt, sum(amount)/100 amt from transaction_details where upper(payment_option)= upper(:payment_option) and date(created) between :start_date and :end_date  and status ='SUCCESS';", nativeQuery = true)
			Ipayment_modeTrxAmount getAllNBTxnDateWise(@Param("payment_option") String payment_option, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

			@Query(value = "select status,count(1) cnt, sum(amount)/100 amt from transaction_details where upper(payment_option)= upper(:payment_option) and date(created) between :start_date and :end_date  and status ='SUCCESS';", nativeQuery = true)
			Ipayment_modeTrxAmount getAllCardTxnDateWise(@Param("payment_option") String payment_option, @Param("start_date") String start_date,
			@Param("end_date") String end_date);
			@Query(value = "SELECT payment_option, sum(amount)/100 amt, count(1) cnt from transaction_details where date(created) between :start_date and :end_date and status = 'SUCCESS' group by payment_option;", nativeQuery = true)
			List<IPaymentOptionAmt> getAllSumByPaymentOption(@Param("start_date") String start_date,@Param("end_date") String end_date);
	@Query(value = "select sum(amount) amt from transaction_details where status = 'SUCCESS' and date(created) between :start_date and :end_date ", nativeQuery = true)
	String getAllHitTxnDateWise(@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select sum(amount) amt from transaction_details where status = 'Captured' and date(created) between :start_date and :end_date ", nativeQuery = true)
	String getAllCapturedTxnDateWise(@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select sum(amount) cnt from transaction_details where status = 'SUCCESS' ", nativeQuery = true)
	String getAllTxnWithSuccessStatus(@Param("status") String status);

	@Query(value = "select sum(amount) cnt from transaction_details where status = 'Captured' ", nativeQuery = true)
	String getAllTxnWithCapturedStatus(@Param("status") String status);

	@Query(value = "select sum(amount) cnt from transaction_details where status = 'Cancelled' ", nativeQuery = true)
	String getAllTxnWithCancelledStatus(@Param("status") String status);

	@Query(value = "select sum(amount) cnt from transaction_details where status = 'FAILED' ", nativeQuery = true)
	String getAllTxnWithFailedStatus(@Param("status") String status);

	@Query(value = "select sum(amount) amt from transaction_details where upper(payment_mode)= upper(:payment_mode) and date(created) between :start_date and :end_date ", nativeQuery = true)
	String getAllUPITxnModeDateWise(@Param("payment_mode") String payment_mode, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

	@Query(value = "select sum(amount) amt from transaction_details where upper(payment_mode)= upper(:payment_mode) and date(created) between :start_date and :end_date ", nativeQuery = true)
	String getAllWalletTxnModeDateWise(@Param("payment_mode") String payment_mode,
			@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select sum(amount) amt from transaction_details where upper(payment_mode)= upper(:payment_mode) and date(created) between :start_date and :end_date ", nativeQuery = true)
	String getAllNBTxnModeDateWise(@Param("payment_mode") String payment_mode, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

	@Query(value = "select sum(amount) amt from transaction_details where upper(payment_mode)= upper(:payment_mode) and date(created) between :start_date and :end_date ", nativeQuery = true)
	String getAllCardTxnModeDateWise(@Param("payment_mode") String payment_mode, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

	List<TransactionDetails> findAllByMerchantId(String id);

	@Query(value = "select tr.id, tr.created, tr.updated, tr.amount, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where merchant_id= :merchant_id order by tr.id desc limit 100", nativeQuery = true)
	List<TransactionDetails> findAllTopByMerchantId(@Param("merchant_id") String merchant_id);

	@Query(value = "select tr.id, tr.created, tr.updated, tr.amount, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where merchant_id= :merchant_id and created >=DATE_ADD(CURDATE(), INTERVAL -3 DAY)", nativeQuery = true)
	public List<TransactionDetails> findLast3DaysTransaction(@Param("merchant_id") String merchant_id);

	@Query(value = "select *  from transaction_details tr order by id desc limit 100", nativeQuery = true)
	public List<TransactionDetails> getTrxTop100();


	@Query(value = "select tr.* "
			+ "from transaction_details tr "
			+ "where tr.merchant_id= :merchant_id "
			+ "and date(tr.created) between :dateFrom and :dateTo ", nativeQuery = true)
	public List<TransactionDetails> getTransactionDateRange(@Param("merchant_id") String merchant_id,
			@Param("dateFrom") String dateFrom, @Param("dateTo") String dateTo);

	@Query(value = "select tr.* "
			+ "from transaction_details tr "
			+ "where tr.merchant_id= :merchant_id "
			+ "and date(tr.created) = :dateFrom  ", nativeQuery = true)
	public List<TransactionDetails> getTransactionDate(@Param("merchant_id") String merchant_id,
			@Param("dateFrom") String dateFrom);

	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and date(a.created) = curdate()-1 "
			+ "and b.merchantid= :merchant_id "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getYesterdayTrDetails(@Param("merchant_id") String merchant_id);

	@Query(value = "select a.merchant_id as merchantId, status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and date(a.created) = curdate() "
			+ "and b.merchantid= :merchant_id "
			+ "group by a.merchant_id, a.status", nativeQuery = true)
	List<IMerchantTransaction> getTodayTrDetails(@Param("merchant_id") String merchant_id);

	@Query(value = "select sum(a.amount)/100 amt from transaction_details a where date(a.created) = date(curdate()) and a.merchant_id= :merchantid", nativeQuery = true)
	String getTodayTr(@Param("merchantid") String merchantid);

	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount  "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and MONTH(a.created) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "and YEAR(a.created) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "and b.merchantid= :merchant_id "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getLastMonthTrDetails(@Param("merchant_id") String merchant_id);

	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and MONTH(a.created) = MONTH(CURRENT_DATE()) "
			+ "and YEAR(a.created) = YEAR(CURRENT_DATE()) "
			+ "and b.merchantid= :merchant_id "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getCurrMonthTrDetails(@Param("merchant_id") String merchant_id);

	List<TransactionDetails> findAllByMerchantOrderIdAndMerchantId(String string, String merchantID);

	List<TransactionDetails> findAllByMerchantOrderIdAndMerchantIdAndStatus(String string, String merchantID,
			String status);

	List<TransactionDetails> findByPgOrderID(String txtId);

	Page<TransactionDetails> findByMerchantIdContaining(String merchantID, Pageable paging);

	Page<TransactionDetails> findByMerchantIdAndStatusContaining(String merchantID, String status, Pageable paging);

	List<TransactionDetails> findByMerchantIdAndStatus(String merchantID, String status);

	@Query(value = "SELECT "
			+ "    B.merchant_name merchantName, "
			+ "    SUM(CASE WHEN (upper(payment_option)='UPI') THEN ROUND(AMOUNT/100,2) ELSE 0 END) AS UPI, "
			+ "    SUM(CASE WHEN (upper(payment_option)='WALLET') THEN ROUND(AMOUNT/100,2)  ELSE 0 END) AS WALLET, "
			+ "    SUM(CASE WHEN (upper(payment_option)='CARD') THEN ROUND(AMOUNT/100,2)  ELSE 0 END) AS CARD, "
			+ "    SUM(CASE WHEN (upper(payment_option)='NB') THEN ROUND(AMOUNT/100,2)  ELSE 0 END) AS NB,   "
			+ "    SUM(CASE WHEN (upper(payment_option)='UPI_QR') THEN ROUND(AMOUNT/100,2)  ELSE 0 END) AS UPIQR "
			+ "    from transaction_details A, merchant_details B "
			+ "where A.MERCHANT_ID = B.merchantid "
			+ "AND DATE(A.created) = CURDATE() "
			+ "and A.status in('PAYMENT_SUCCESSFUL','SUCCESS','TXN_SUCCESS') "
			+ "GROUP BY B.merchant_name", nativeQuery = true)
	List<IMerchantCompoundReport> getDailyReportsMerchants();

	// @Query(value = ":query" )
	// List<Object> getData(@Param("query") String query);

	@Query(value = "select tr.id, tr.created, tr.updated, tr.amount,tr.email_id, tr.merchant_alerturl, tr.order_note, tr.recon_status, tr.source, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where merchant_order_id= :merchant_order_id ", nativeQuery = true)
	List<TransactionDetails> findAllByMerchantOrderId(@Param("merchant_order_id") String merchant_order_id);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(payment_option)= upper(:payment_option) and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByPaymentOption(@Param("payment_option") String payment_option,
			@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(payment_option)= upper(:payment_option) limit 5000", nativeQuery = true)
	List<TransactionDetails> findAllByPaymentOptionWithOutDate(@Param("payment_option") String payment_option);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByPgType(@Param("pgType") String pgType, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) limit 5000 ", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeWithOutDate(@Param("pgType") String pgType);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and upper(payment_option)= upper(:payment_option) and tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id and tr.id = :trId ", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndPayOptionAndMerOrAndTrIdAndPgId(
			@Param("payment_option") String payment_option,
			@Param("pgType") String pgType, @Param("merchant_order_id") String merchant_order_id,
			@Param("pg_id") String pg_id,
			@Param("trId") String trId);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and tr.merchant_id= :merchant_id and tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id and tr.id = :trId ", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndMerIdAndMerOrAndTrIdAndPgId(
			@Param("pgType") String pgType, @Param("merchant_order_id") String merchant_order_id,
			@Param("pg_id") String pg_id,
			@Param("trId") String trId);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id and tr.id = :trId ", nativeQuery = true)
	List<TransactionDetails> findAllByMerOrAndTrIdAndPgId(
			@Param("merchant_order_id") String merchant_order_id, @Param("pg_id") String pg_id,
			@Param("trId") String trId);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(payment_option)= upper(:payment_option) and tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id ", nativeQuery = true)
	List<TransactionDetails> findAllByPayOptionAndMerOrAndPgId(@Param("payment_option") String payment_option,
			@Param("merchant_order_id") String merchant_order_id, @Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and tr.merchant_order_id= :merchant_order_id and tr.id = :trId ", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndMerOrAndTrId(
			@Param("pgType") String pgType, @Param("merchant_order_id") String merchant_order_id,
			@Param("trId") String trId);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and tr.merchant_order_id= :merchant_order_id ", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndMerOrAndTrIdAndPgId(
			@Param("pgType") String pgType, @Param("merchant_order_id") String merchant_order_id);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and upper(payment_option)= upper(:payment_option) and tr.merchant_id= :merchant_id and tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id  ", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndPayOptionAndMerIdAndMerOrAndPgId(
			@Param("merchant_id") String merchant_id, @Param("payment_option") String payment_option,
			@Param("pgType") String pgType, @Param("merchant_order_id") String merchant_order_id,
			@Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and upper(payment_option)= upper(:payment_option) and tr.merchant_id= :merchant_id and tr.merchant_order_id= :merchant_order_id and tr.id = :trId", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndPayOptionAndMerIdAndMerOrAndTrId(
			@Param("merchant_id") String merchant_id, @Param("payment_option") String payment_option,
			@Param("pgType") String pgType, @Param("merchant_order_id") String merchant_order_id,
			@Param("trId") String trId);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and upper(payment_option)= upper(:payment_option) and tr.merchant_id= :merchant_id and tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id and tr.id = :trId ", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndMerIdAndMerOrAndPgId(@Param("merchant_id") String merchant_id,
			@Param("pgType") String pgType, @Param("merchant_order_id") String merchant_order_id,
			@Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id  and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByMerIdAndMerOrAndPgId(@Param("merchant_id") String merchant_id,
			@Param("merchant_order_id") String merchant_order_id, @Param("pg_id") String pg_id,
			@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(payment_option)= upper(:payment_option) and tr.merchant_id= :merchant_id and tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id ", nativeQuery = true)
	List<TransactionDetails> findAllByPayOptionAndMerIdAndMerOrAndPgId(@Param("merchant_id") String merchant_id,
			@Param("payment_option") String payment_option,
			@Param("merchant_order_id") String merchant_order_id, @Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and upper(payment_option)= upper(:payment_option) and tr.merchant_id= :merchant_id and tr.pg_id = :pg_id", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndPayOptionAndMerIdAndPgId(@Param("merchant_id") String merchant_id,
			@Param("payment_option") String payment_option,
			@Param("pgType") String pgType, @Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id ", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndMerOrAndPgId(
			@Param("pgType") String pgType, @Param("merchant_order_id") String merchant_order_id,
			@Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and upper(payment_option)= upper(:payment_option) and tr.merchant_id= :merchant_id and tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id and tr.id = :trId  ", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndPayOptionAndMerIdAndMerOrAndTrIdAndPgId(
			@Param("merchant_id") String merchant_id, @Param("payment_option") String payment_option,
			@Param("pgType") String pgType, @Param("merchant_order_id") String merchant_order_id,
			@Param("pg_id") String pg_id,
			@Param("trId") String trId);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_order_id= :merchant_order_id and tr.pg_id = :pg_id ", nativeQuery = true)
	List<TransactionDetails> findAllByMerOrAndPgId(
			@Param("merchant_order_id") String merchant_order_id, @Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_order_id= :merchant_order_id and tr.id = :trId ", nativeQuery = true)
	List<TransactionDetails> findAllByMerOrAndTrId(
			@Param("merchant_order_id") String merchant_order_id,
			@Param("trId") String trId);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and upper(payment_option)= upper(:payment_option) and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByPgTypeAndPayOption(@Param("pgType") String pgType,
			@Param("payment_option") String payment_option, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

			@Query(value = "select * from transaction_details where txt_msg like '% :trx_msg %' and date(created) between :start_date and :end_date", nativeQuery = true)
	List<TransactionDetails> findAllByTrx_msg_Like(@Param("trx_msg") String trx_msg, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

			@Query(value = "select * from transaction_details where status = ':status' and date(created) between :start_date and :end_date", nativeQuery = true)
			List<TransactionDetails> findAllByStatusDateWise(@Param("status") String status, @Param("start_date") String start_date,
					@Param("end_date") String end_date);
	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and upper(payment_option)= upper(:payment_option) ", nativeQuery = true)
	List<TransactionDetails> findAllByMerIdAndPayOption(@Param("merchant_id") String merchant_id,
			@Param("payment_option") String payment_option);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and upper(pg_type)= upper(:pgType) ", nativeQuery = true)
	List<TransactionDetails> findAllByMerIdAndPgType(@Param("merchant_id") String merchant_id,
			@Param("pgType") String pgType);

	@Query(value = "select *"
			+ " from transaction_details tr where date(created) between :start_date and :end_date limit 10000", nativeQuery = true)
	List<TransactionDetails> findAllByTrDate(@Param("start_date") String start_date,
			@Param("end_date") String end_date);

	@Query(value = "select tr.id, tr.created, tr.updated, tr.amount, tr.email_id, tr.merchant_alerturl, tr.order_note, tr.recon_status, tr.source, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where date(created) between :start_date and :end_date", nativeQuery = true)
	List<TransactionDetails> findAllByTrDatewithoutlimit(@Param("start_date") String start_date,
			@Param("end_date") String end_date);

	@Query(value = "select tr.id, tr.created, tr.updated, tr.amount,tr.email_id, tr.merchant_alerturl, tr.order_note, tr.recon_status, tr.source, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByMerchantId(@Param("merchant_id") String merchant_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.pg_id = :pg_id and date(created) between :start_date and :end_date", nativeQuery = true)
	List<TransactionDetails> getTransactionByPgId(@Param("pg_id") String pg_id, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.pg_id = :pg_id", nativeQuery = true)
	List<TransactionDetails> getTransactionByPgIdWithOutDate(@Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.id = :trId and date(created) between :start_date and :end_date", nativeQuery = true)
	List<TransactionDetails> getTransactionByTrId(@Param("trId") String trId, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.id = :trId", nativeQuery = true)
	List<TransactionDetails> getTransactionByTrIdWithOutDate(@Param("trId") String trId);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and tr.merchant_order_id= :merchant_order_id  ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByMerIdAndMerOrId(@Param("merchant_id") String merchant_id,
			@Param("merchant_order_id") String merchant_order_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and tr.payment_option= :payment_option and tr.merchant_order_id= :merchant_order_id ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByMerIdAndPayOptAndMerOrd(@Param("merchant_id") String merchant_id,
			@Param("payment_option") String payment_option, @Param("merchant_order_id") String merchant_order_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and upper(pg_type)= upper(:pgType) and tr.merchant_order_id= :merchant_order_id ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByMerIdAndPgTyAndMerOr(@Param("merchant_id") String merchant_id,
			@Param("pgType") String pgType, @Param("merchant_order_id") String merchant_order_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and upper(pg_type)= upper(:pgType) and tr.payment_option= :payment_option and tr.merchant_order_id= :merchant_order_id ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByMerIdAndPgTyAndPayOpAndMerOr(@Param("merchant_id") String merchant_id,
			@Param("pgType") String pgType, @Param("payment_option") String payment_option,
			@Param("merchant_order_id") String merchant_order_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and upper(pg_type)= upper(:pgType) and tr.payment_option= :payment_option  ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByMerIdAndPgTyAndPayOp(@Param("merchant_id") String merchant_id,
			@Param("pgType") String pgType, @Param("payment_option") String payment_option);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and tr.id= :trId ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByMerIdAndTrId(@Param("merchant_id") String merchant_id,
			@Param("trId") String trId);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and tr.pg_id= :pg_id  ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByMerIdAndPgId(@Param("merchant_id") String merchant_id,
			@Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and tr.id= :trId and tr.pg_id= :pg_id  ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByMerIdAndTrIdAndPgId(@Param("merchant_id") String merchant_id,
			@Param("trId") String trId, @Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and tr.merchant_order_id= :merchant_order_id and tr.id= :trId and tr.pg_id= :pg_id ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByMerIdAndMerOrIdAndTrIdAndPgId(@Param("merchant_id") String merchant_id,
			@Param("merchant_order_id") String merchant_order_id, @Param("trId") String trId,
			@Param("pg_id") String pg_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.pg_id= :pg_id and tr.id= :trId and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByTrIdAndPgId(@Param("trId") String trId, @Param("pg_id") String pg_id,
			@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.id= :trId and upper(pg_type)= upper(:pgType) and tr.pg_id= :pg_id and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByPgTyAndTrIdAndPgId(@Param("pgType") String pgType,
			@Param("trId") String trId, @Param("pg_id") String pg_id, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.pg_id= :pg_id and upper(pg_type)= upper(:pgType) and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByPgTyAndPgId(@Param("pgType") String pgType, @Param("pg_id") String pg_id,
			@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where upper(pg_type)= upper(:pgType) and tr.payment_option= :payment_option and tr.id= :trId and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByPgTyAndPayOpAndTrId(@Param("pgType") String pgType,
			@Param("payment_option") String payment_option, @Param("trId") String trId,
			@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.payment_option= :payment_option and upper(pg_type)= upper(:pgType) and tr.pg_id= :pg_id and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByPgTyAndPayOpAndPgId(@Param("pgType") String pgType,
			@Param("payment_option") String payment_option, @Param("pg_id") String pg_id,
			@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.payment_option= :payment_option and tr.merchant_order_id= :merchant_order_id  ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByPayOpAndMerOr(@Param("payment_option") String payment_option,
			@Param("merchant_order_id") String merchant_order_id);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.payment_option= :payment_option and tr.id= :trId and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByPayOpAndTrId(@Param("payment_option") String payment_option,
			@Param("trId") String trId, @Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.payment_option= :payment_option and tr.pg_id= :pg_id  and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByPayOpAndPgId(@Param("payment_option") String payment_option,
			@Param("pg_id") String pg_id, @Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select *"
			+ " from transaction_details tr where tr.payment_option= :payment_option and tr.id= :trId and tr.pg_id= :pg_id and date(created) between :start_date and :end_date ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByPayOpAndTrIdAndPgId(@Param("payment_option") String payment_option,
			@Param("trId") String trId, @Param("pg_id") String pg_id, @Param("start_date") String start_date,
			@Param("end_date") String end_date);

	@Query(value = "select tr.id, tr.created, tr.updated, tr.amount,tr.email_id, tr.merchant_alerturl, tr.order_note, tr.recon_status, tr.source, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where tr.payment_option= :payment_option and tr.id= :trId and tr.pg_id= :pg_id ", nativeQuery = true)
	List<TransactionDetails> findAllByTrDateByPayOpAndTrIdAndPgId(@Param("payment_option") String payment_option,
			@Param("trId") String trId, @Param("pg_id") String pg_id);

	@Query(value = "select tr.id, tr.created, tr.updated, tr.amount,tr.email_id, tr.merchant_alerturl, tr.order_note, tr.recon_status, tr.source, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where merchant_id= :merchant_id order by tr.amount desc limit 100", nativeQuery = true)
	List<TransactionDetails> findAllTopTxnByMerchantId(@Param("merchant_id") String merchant_id);

	@Query(value = "select tr.id, tr.created, tr.updated, tr.amount,tr.email_id, tr.merchant_alerturl, tr.order_note, tr.recon_status, tr.source, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where date(created) = curdate() order by tr.amount desc ", nativeQuery = true)
	List<TransactionDetails> findAllTopMerchantTxn();

	@Query(value = "select *"
			+ " from transaction_details tr where  date(created) >= :start_date  ", nativeQuery = true)
	List<TransactionDetails> getDataStartDate(@Param("start_date") String start_date);

	@Query(value = "select *"
			+ " from transaction_details tr where  date(created) <= :end_date ", nativeQuery = true)
	List<TransactionDetails> getDataEndDate(@Param("end_date") String end_date);

	@Query(value = "update transaction_details set status = 'refunded' where merchant_id: =merchantId and merchant_order_id: =orderId ", nativeQuery = true)
	public void changeTransactionStatusForRefund(@Param("merchantId") String merchantId,
			@Param("orderId") String orderId);

	/** @author abhimanyu-kumar **/
	@Query(value = "FROM TransactionDetails t WHERE  created between :start_date and :end_date")
	List<TransactionDetails> findByDate(@Param("start_date") Date start_date, @Param("end_date") Date end_date);

	@Query(value = "SELECT * FROM transaction_details  where  created  >=DATE_ADD(CURDATE(), INTERVAL -3 MONTH)  order by id asc", nativeQuery = true)
	List<TransactionDetails> getPrevious3MonthsTransactionReport();

	@Query(value = "SELECT * FROM transaction_details  WHERE DATE(created) BETWEEN  :fromDate and :upToDate order by id asc", nativeQuery = true)
	List<TransactionDetails> getTransactionfutdTransactionReport(@Param("fromDate") String fromDate,
			@Param("upToDate") String upToDate);

	// @Query(value = "SELECT * FROM transaction_details WHERE pg_type =:pgName
	// group by pg_type order by id asc", nativeQuery=true)
	@Query(value = "SELECT * FROM transaction_details  WHERE pg_type like %:pgName%  order by id asc", nativeQuery = true)
	List<TransactionDetails> getTransactionpgnamelikeTransactionReport(@Param("pgName") String pgName);

	@Query(value = "SELECT * FROM transaction_details  WHERE DATE(created) = CURDATE()  order by id asc", nativeQuery = true)
	List<TransactionDetails> getTransactionTodayTransactionReport();

	@Query(value = "SELECT * FROM transaction_details  WHERE  merchant_id = :merchantId", nativeQuery = true)
	List<TransactionDetails> getTransactionMerchantIDWiseTransactionReport(@Param("merchantId") String merchantId);

	@Query(value = "SELECT * FROM transaction_details  WHERE  merchant_id = :merchantId and merchant_order_id= :orderId ", nativeQuery = true)
	List<TransactionDetails> getTransactionMerchantIDAndOrderIDTransactionReport(@Param("merchantId") String merchantId,
			@Param("orderId") String orderId);

	@Query(value = "SELECT *  FROM transaction_details  WHERE pg_type = :pgName and DATE(created) BETWEEN  :start_date and  :end_date", nativeQuery = true)
	List<TransactionDetails> findByPgNameWithDate(@Param("pgName") String pgName,
			@Param("start_date") String start_date, @Param("end_date") String end_date);

	// Reamove group by pg_type and watch output, no of results should increase

	/****/
	// @Query(value = "SELECT r.merchant_id, m.merchant_name, m.company_name, r.cnt,
	// r.totalAmt, r.pg_type FROM (SELECT *, count(1) cnt, SUM(amount) / 100
	// totalAmt FROM transaction_details WHERE status = 'PENDING' AND DATE(created)
	// = '2022-10-28' GROUP BY pg_type , merchant_id) r, merchant_details m WHERE
	// m.merchantid = r.merchant_id" , nativeQuery = true)
	@Query(value = "SELECT count(1) cnt, SUM(amount) / 100 totalAmt FROM transaction_details WHERE status = :status AND DATE(created) = :start_date GROUP BY pg_type , merchant_id", nativeQuery = true)
	List<TransactionDetails> findAllByStatusAndDateAndMerchantID(@Param("start_date") String start_date,
			@Param("status") String status);

	@Query(value = "select r.merchant_id merchantId, m.merchant_name merchantName, m.company_name companyName , r.pg_type pgType, r.totalAmt totalAmt, r.cnt cnt, p.totalCnt totalCnt, round(((r.cnt/p.totalCnt) *100),2) ratio from (select pg_type, merchant_id, count(1) cnt, round(SUM(amount) / 100,2) totalAmt  from transaction_details where status = :status AND DATE(created) between :fromDate and :endDate GROUP BY pg_type , merchant_id) r , (select pg_type, merchant_id, count(1)totalCnt from transaction_details where DATE(created) between :fromDate and :endDate group by pg_type, merchant_id ) p, merchant_details m where r.merchant_id = p.merchant_id and m.merchantid = r.merchant_id;", nativeQuery = true)
	List<IMerchantWisePgWiseSum> getByMerchantWisePgWiseSumQuery(@Param("fromDate") String fromDate,
			@Param("endDate") String endDate, @Param("status") String status);

			@Query(value = "SELECT  pg_type,  count(1) cnt, SUM(amount) / 100 totalAmt FROM transaction_details WHERE status = :status and DATE(created) between :fromDate and :endDate GROUP BY pg_type;", nativeQuery = true)
			List<IPgTypeAndCountByStatusAndDate> getPgTypeAndCountByStatusAndDate(@Param("fromDate") String fromDate,
			@Param("endDate") String endDate, @Param("status") String status);

			@Query(value = "select hour(created) as Hour, count(1) as Count from transaction_details where date(created) = :fromDate and status = :status group by  hour(created);", nativeQuery = true)
			List<IHourandCountStatus> getHourandCountStatusAndDate(@Param("fromDate") String fromDate,
		    @Param("status") String status);

			@Query(value = "select minute(created) as minutes, count(1) as Count from transaction_details where created between NOW() - INTERVAL 60 MINUTE AND NOW() - INTERVAL 5 MINUTE and status = :status  group by  minute(created) order by minutes desc;", nativeQuery = true)
			List<IMinuteandCountStatus> getMinuteandCountByStatus(@Param("status") String status);
			
			// @Query(value = "select data.status, data.Count, data.amountSum, data.Count / (sum(data.Count) over ()) * 100 statusPercent from (select status, count(1) as Count, sum(amount)/100 amountSum from transaction_details where date(created) between :startDate and :endDate group by status) as data;", nativeQuery = true)
			// List<IStatusCountTrx> getStatusCount(@Param("startDate") String startDate,
			// @Param("endDate") String endDate);
			@Query(value = "select status, sum(amount)/100 amountSum, count(1) count from transaction_details where date(created) between :startDate and :endDate group by status", nativeQuery = true)
			List<IStatusCountTrx> getStatusCount(@Param("startDate") String startDate,
			@Param("endDate") String endDate);

			@Query(value = "SELECT merchant_id,max(created) last_trxn, amount/100 amt FROM transaction_details where date(created)=:startDate  group by merchant_id", nativeQuery = true)
			List<ILastTrxMercList> getLastTrxMerchList(@Param("startDate") String startDate);

			@Query(value = "SELECT MINUTE(created) AS minutes, status, COUNT(1) AS cnt, sum(amount)/100 total FROM transaction_details WHERE created BETWEEN NOW() - INTERVAL 60 MINUTE AND NOW() - INTERVAL 5 MINUTE  GROUP BY MINUTE(created), status ORDER BY  status,MINUTE(created) DESC", nativeQuery = true)
			List<StatusAndMinute> getStatusAndMinuteWiseCount();


			@Query(value = "select hour(created) as Hour,status, count(1) as Count from transaction_details where date(created) = :fromDate  group by status ,hour(created) ", nativeQuery = true)
			List<IHourandStatusWise> getHourandStatusWiseCountAndDate(@Param("fromDate") String fromDate);
			@Query(value = "SELECT status, hour(created) as Hour ,count(1) AS Count, sum(amount)/100 as amount FROM transaction_details where  date(created) = curdate() group by status, hour(created) order by id desc;", nativeQuery = true)
			List<ISumHourandStatusWise> getHourandStatusWiseCountAndDateAndSum();

			@Query(value = "select *"
			+ " from transaction_details tr where tr.merchant_id= :merchant_id and date(created) between :start_date and :end_date", nativeQuery = true)
			List<TransactionDetails> findAllByTrDateByMerchantId(@Param("merchant_id") String merchant_id, @Param("start_date") String start_date, @Param("end_date") String end_date);



			TransactionDetails findByorderID(String orderId);
}
