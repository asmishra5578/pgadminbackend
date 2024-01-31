package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.customInterface.IMerchantSettlement;
import com.asktech.admin.customInterface.IMerchantWiseDateWiseSettlement;
import com.asktech.admin.customInterface.ISettlementBalanceReport;
import com.asktech.admin.dto.merchant.MerchantDashBoardBalance;
import com.asktech.admin.model.MerchantBalanceSheet;

public interface MerchantBalanceSheetRepository extends JpaRepository<MerchantBalanceSheet, String> {

	@Query(value = "select bs.*"
			+ " from merchant_balance_sheet bs where merchant_id= :merchant_id and settlement_status='COMPLETED' and created >=DATE_ADD(CURDATE(), INTERVAL -7 DAY)", nativeQuery = true)
	public List<MerchantBalanceSheet> findLast7DaysSettleTransaction(@Param("merchant_id") String merchant_id);

	public List<MerchantBalanceSheet> findAllByMerchantIdAndSettlementStatus(String merchantID2, String string);

	@Query(value = "select created, amount, settle_amount_to_merchant,  merchant_id, merchant_order_id, settlement_status,tr_type, card_number, payment_code, "
			+ "vpaupi from merchant_balance_sheet where settlement_status = :settlement_status and  merchant_id = :merchant_id and pg_status in ('SUCCESS', 'Captured')", nativeQuery = true)
	public List<ISettlementBalanceReport> getSettlementBalanceSheet(
			@Param("settlement_status") String settlement_status, @Param("merchant_id") String merchant_id);

	@Query(value = "SELECT sum(amount) amt FROM merchant_balance_sheet where merchant_id = :merchant_id and settlement_status = 'PENDING' and pg_status in ('SUCCESS', 'Captured')", nativeQuery = true)
	public String getPendingSettlementTotal(@Param("merchant_id") String merchant_id);
	
	@Query(value = "SELECT (SUM(settle_amount_to_merchant)) amt FROM merchant_balance_sheet where merchant_id = :merchant_id and settlement_status = 'SETTLED' group by date(created) order by date(created) desc limit 1", nativeQuery = true)
	public String getSettledTotal(@Param("merchant_id") String merchant_id);

	
	@Query(value = "SELECT tr.* FROM merchant_balance_sheet tr where tr.merchant_id = :merchant_id and settlement_status = 'SETTLED' and pg_status in ('SUCCESS', 'Captured') and date(tr.created) between :dateFrom and :dateTo  ;",
			nativeQuery = true)
	public List<MerchantBalanceSheet> getSettlementDateRange(@Param("merchant_id") String merchant_id , @Param("dateFrom") String dateFrom ,@Param("dateTo") String dateTo );	
	
	
	@Query(value = "SELECT tr.* FROM merchant_balance_sheet tr where tr.merchant_id = :merchant_id and settlement_status = 'SETTLED' and pg_status in ('SUCCESS', 'Captured') and date(tr.created) = :dateFrom;",
			nativeQuery = true)
	public List<MerchantBalanceSheet> getSettlementFrom(@Param("merchant_id") String merchant_id , @Param("dateFrom") String dateFrom);	
	
	
	@Query(value = "select  merchant_id,settlement_status,sum(amount) amount "
			+ "from merchant_balance_sheet where merchant_id=:merchant_id "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	public List<MerchantDashBoardBalance> getDashboardStauts(@Param("merchant_id") String merchant_id);

	public MerchantBalanceSheet findByOrderId(String orderid);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' " + "and date(created) = curdate()-1 "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getLastDaySettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' " + "and date(created) = curdate() "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getCurrDaySettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' " + "and date(created) >= curdate() -7 "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getLast7DaySettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' "
			+ "and MONTH(created) = MONTH(CURRENT_DATE()) " + "and YEAR(created) = YEAR(CURRENT_DATE()) "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getCurrMonthSettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' "
			+ "and MONTH(created) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "and YEAR(created) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getLastMonthSettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' " + "and date(created) >= curdate() -90 "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getLast90DaySettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select * from merchant_balance_sheet "
			+ "where pg_status = 'SUCCESS' "
			+ "and ask_commission is null and pg_commission is null and settle_amount_to_merchant is null  ", nativeQuery = true)
	List<MerchantBalanceSheet> findByPGStatusAndCommission(@Param("pg_status") String pg_status  );
	
	@Query(value = "select * from merchant_balance_sheet "
			+ "where merchant_id in(select merchant_id from merchant_details where created_by  =:adminuuid ) order by id , merchant_id"
			, nativeQuery = true)
	List<MerchantBalanceSheet> findByAdminCommDetailsTotal(@Param("adminuuid") String adminuuid  );
	
	@Query(value = "select * from merchant_balance_sheet "
			+ "where settlement_status ='PENDING' and merchant_id in(select merchant_id from merchant_details where created_by  =:adminuuid ) order by id , merchant_id"
			, nativeQuery = true)
	List<MerchantBalanceSheet> findByAdminMerchantCommissionPendindSettlement(@Param("adminuuid") String adminuuid  );
	
	@Query(value = "select merchant_id merchantId, settlement_status settlementStatus, "
			+ "date(updated) settleMentDate,round(sum(settle_amount_to_merchant)/100,2) settledAmount, "
			+ "date(min(created)) transactionDateStart,date(max(created)) transactionDateEnd "
			+ "from merchant_balance_sheet "
			+ "where date(updated) between :dateFrom and :dateTo  "
			+ "and merchant_id =:merchantId and settlement_status = 'SETTLED' "
			+ "group by merchant_id, settlement_status,date(updated) "
			+ "order by merchant_id,date(updated),settlement_status"
			, nativeQuery = true)
	List<IMerchantWiseDateWiseSettlement> getMerchantWiseSettlementDateWise(@Param("merchantId") String merchantId,
																			@Param("dateFrom") String dateFrom,
																			@Param("dateTo") String dateTo);	

	MerchantBalanceSheet findByOrderIdAndSettlementStatus(@Param("dateTo") String orderId,@Param("string") String string);

	MerchantBalanceSheet findAllByMerchantIdAndMerchantOrderIdAndSettlementStatus(@Param("merchantID") String merchantID,
	@Param("merchantOrderId") String merchantOrderId,@Param("settlementStatus") String settlementStatus);

	Page<MerchantBalanceSheet> findByMerchantIdContaining(@Param("merchantId") String merchantID,@Param("paging")  Pageable paging);
	Page<MerchantBalanceSheet> findByMerchantIdAndSettlementStatusContaining(@Param("merchantId") String merchantID,@Param("merchantId") String status,@Param("paging")  Pageable paging);
	MerchantBalanceSheet findByMerchantIdAndMerchantOrderId(@Param("merchantId")  String merchantID,@Param("orderId")  String orderId);
	MerchantBalanceSheet findByMerchantIdAndMerchantOrderIdAndSettlementStatus(@Param("merchantId") String merchantID, @Param("orderId") String orderId,@Param("status")  String status);

	@Query(value = "SELECT (SUM(settle_amount_to_merchant)) amt FROM merchant_balance_sheet where settlement_status = 'SETTLED' and date(created) between :start_date and :end_date", nativeQuery = true)
	public String getTotalSettled(@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "SELECT sum(amount) cnt FROM merchant_balance_sheet where settlement_status = 'PENDING' and date(created) between :start_date and :end_date", nativeQuery = true)
	public String getTotalUnSettled(@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value ="update merchant_balance_sheet set settlement_status = 'refunded' where merchant_id: =merchantId and merchant_order_id: =orderId ", nativeQuery = true)
    public void changeSettlementStatusForRefund(@Param("merchantId") String merchantId,
			@Param("orderId") String orderId);
}
