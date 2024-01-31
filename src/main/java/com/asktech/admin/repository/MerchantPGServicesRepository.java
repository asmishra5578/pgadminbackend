package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.customInterface.IAllMerchantDetailsReport;
import com.asktech.admin.customInterface.IMerchantDetailsReport;
import com.asktech.admin.customInterface.IPGWiseMerchantDetails;
import com.asktech.admin.model.MerchantPGServices;

public interface MerchantPGServicesRepository extends JpaRepository<MerchantPGServices, String>{

	List<MerchantPGServices> findByMerchantIDAndService(String merchantId, String service);

	List<MerchantPGServices> findByMerchantIDAndServiceAndPgID(String merchantId, String service, String pgid);

	List<MerchantPGServices> findByMerchantID(String merchantId);

	List<MerchantPGServices> findByMerchantIDAndPgID(String merchantId, String pgid);

@Query(value = "SELECT * FROM merchantpgservices  where merchantid =:merchantId and service= :service and pgid =:pgid",nativeQuery = true)
List<MerchantPGServices> getByMerchantIDAndServiceAndPgID(@Param("merchantId")String merchantId,@Param("service") String service,@Param("pgid") String pgid);
	
	// Rectify Query Via Active And Blocked State
	List<MerchantPGServices> findByMerchantIDAndPgIDAndService(String merchantId, String pgUuid, String service);
	
	@Query(value = "SELECT service FROM merchantpgservices  where merchantid =:merchantId",nativeQuery = true)
	List<String> findAllServiceByMerchantID(@Param("merchantId") String merchantId);
	List<MerchantPGServices> findAllByMerchantIDAndPgIDAndService(String merchantId, String valueOf, String service);

	List<MerchantPGServices> findAllByPgIDAndService(String pgid, String service);
	//MerchantPGServices findByMerchantIDAndPgIDAndService(String merchantID, String pgID, String service);
	
	
	MerchantPGServices findByMerchantIDAndPgIDAndServiceAndStatus(@Param("merchantId") String merchantId, @Param("pgUuid") String pgUuid,@Param("merchantService") String merchantService,
			@Param("status") String status);
	
	@Query(value = "select a.merchantid merchantId ,a.merchantemail merchantEMail, a.merchant_name merchantName,a.kyc_status kycStatus,a.phone_number phoneNumber, "
			+ "c.service , c.status   "
			+ "from merchant_details a, merchantpgdetails b, merchantpgservices c "
			+ "where a.merchantid =b.merchantid "
			+ "and b.merchantid = c.merchantid "
			+ "and c.merchantid = :merchant_id "
			+ "and c.status = :status "
			+ "group by a.merchantid ,a.merchantemail , a.merchant_name ,a.kyc_status,a.phone_number, "
			+ "c.service , c.status  ",
			nativeQuery = true)
	public List<IMerchantDetailsReport> getMerchantDetailsReport(@Param("merchant_id") String merchant_id , @Param("status") String status) ;


	//merchantpgdetails r
	//merchantpgservices d
	//merchant_details e
	@Query(value = "SELECT e.merchantid, e.uuid,e.merchantemail, e.merchant_name merchantName,e.user_status userStatus, e.appid appId, e.secret_id secretId,e.salt_key saltKey, e.kyc_status kycStatus,e.phone_number phoneNumber,"
			+ "r.merchantpgname pGName, r.merchantpgid pGUuid, r.status pGStatus, d.service serviceType , d.status serviceStatus "
			+ "FROM merchant_details e LEFT JOIN merchantpgdetails r ON e.merchantid=r.merchantid "
			+ "left JOIN merchantpgservices d ON d.merchantid=r.merchantid and d.pgid = r.merchantpgid order by  e.merchantid ,r.merchantpgname",
			nativeQuery = true)
	public List<IAllMerchantDetailsReport> getAllMerchantDetailsReport() ;
	
	
	
	
	
	
	@Query(value = "SELECT count(r.merchantid) "
			+ "FROM merchant_details e JOIN merchantpgdetails r ON e.merchantid=r.merchantid "
			+ "left OUTER JOIN merchantpgservices d ON d.merchantid=r.merchantid and d.pgid = r.id order by  e.merchantid ,r.merchantpgname",
			nativeQuery = true)
	public String getAllMerchantDetailsCount() ;

	MerchantPGServices findByMerchantIDAndServiceAndStatus(String merchantId, String service, String status);

	MerchantPGServices findByMerchantIDAndStatusAndService(String merchantId, String statusUpdate, String service);

	List<MerchantPGServices> findAllByPgID(String valueOf);

	List<MerchantPGServices> findAllByPgIDAndServiceAndStatus(String pgId, String pgServices, String status);

	List<MerchantPGServices> findAllByUpdatePgIdAndService(String pgId, String pgServices);
	@Query(value = "SELECT count(e.merchantid) FROM merchant_details e ",nativeQuery = true)
	long findRowCount();

	List<MerchantPGServices> findAllByMerchantID(String merchantID);

	MerchantPGServices findByMerchantIDAndPgIDAndStatusAndService(String merchantId, String PGID,String status, String service);
	

	@Query(value = "SELECT a.merchantid, a.merchant_name merchantName, a.user_status userStatus, s.service FROM merchant_details a JOIN merchantpgservices s ON a.merchantid = s.merchantid WHERE s.pgid =:pgId ", nativeQuery= true )
	List<IPGWiseMerchantDetails> getAllMerchantByPgId(@Param("pgId") String pgId);

	}
