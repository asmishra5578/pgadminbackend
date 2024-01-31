package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.asktech.admin.customInterface.IAdminPGAllDetails;
import com.asktech.admin.model.PGConfigurationDetails;


public interface PGConfigurationDetailsRepository  extends JpaRepository<PGConfigurationDetails, String>{
	
	PGConfigurationDetails findByPgUuid(String pgUuid);

	List<PGConfigurationDetails> findAllByPgUuid(String pgUuid);
	
	
	
	@Query(value = "select * from pgconfiguration_details where pg_uuid= :pgUuid and date(created) between :start_date and :end_date ",nativeQuery = true)
	PGConfigurationDetails findByPgUuidWithDate(@Param("pgUuid") String pgUuid, @Param("start_date") String start_date, @Param("end_date") String end_date);

	PGConfigurationDetails findByPgName(String pgName);
	
	@Query(value = "select * from pgconfiguration_details where pg_name= :pgName and date(created) between :start_date and :end_date ",nativeQuery = true)
	PGConfigurationDetails findByPgNameWithDate(@Param("pgName") String pgName, @Param("start_date") String start_date, @Param("end_date") String end_date);
	
	
	
	
	@Query(value = "SELECT  a.created ,a.updated, a.pg_uuid, a.pg_app_id, a.pg_name, a.status pg_status,a.pg_daily_limit, b.created service_created, b.updated service_updated, b.pg_services, b.status service_status, b.default_service, b.priority, b.thresold_day, b.thresold_month, b.thresold_week, b.thresold_year, b.thresold_3month, b.thresold_6month "
			+ "FROM pgconfiguration_details a, pgservice_details b "
			+ "WHERE a.pg_uuid = b.pg_id;",
			nativeQuery = true)
	List<IAdminPGAllDetails> getAllPgDetails();

	
	
	
	
	
	
	@Query(value = "select * from pgconfiguration_details where date(created) between :start_date and :end_date ",nativeQuery = true)
	List<PGConfigurationDetails> getPgListDateWise(@Param("start_date") String start_date, @Param("end_date") String end_date);

	@Query(value = "select pg_uuid from pgconfiguration_details where pg_api= :pg_api", nativeQuery = true)
	String findByPgApi(@Param("pg_api") String pg_api);
	
	@Query(value = "select pg_uuid from pgconfiguration_details where pg_app_id= :pgAppId", nativeQuery = true)
	String findByPgAppId(@Param("pgAppId") String pgAppId);
	@Query(value = "select pg_uuid from pgconfiguration_details where pg_secret= :pgSecretKey", nativeQuery = true)
	String findByPgSecret(@Param("pgSecretKey") String pgSecretKey);
	@Query(value = "select pg_uuid from pgconfiguration_details where pg_salt_key= :pgSaltKey", nativeQuery = true)
	String findByPgSaltKey(@Param("pgSaltKey") String pgSaltKey);
	@Query(value = "select pg_uuid from pgconfiguration_details where pg_secret_id= :pgSecretId", nativeQuery = true)
	String findByPgSecretId(@Param("pgSecretId") String pgSecretId);
	@Query(value = "select pg_uuid from pgconfiguration_details where pg_secret= :pgSecretKey", nativeQuery = true)
	String findByPgSecretKey(@Param("pgSecretKey") String pgSecretKey);

	PGConfigurationDetails findByPgUuidAndStatus(String pgId, String string);
	
}
