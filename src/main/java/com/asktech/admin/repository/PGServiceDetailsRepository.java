package com.asktech.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.model.PGServiceDetails;

public interface PGServiceDetailsRepository extends JpaRepository<PGServiceDetails, String>{

	PGServiceDetails findByPgIdAndPgServices(String pgId, String upperCase);

	List<PGServiceDetails> findByPgId(String pgId);

	PGServiceDetails findByPgServicesAndDefaultService(String upperCase, String string);

	List<PGServiceDetails> findAllByDefaultService(String string);

	List<PGServiceDetails> findAllByStatus(String string);

	@Query(value = "select *  "
			+ "from pgservice_details where status='ACTIVE' "
			+ "and pg_services =:pgServices "
			+ "and pg_id <>:pgId "
			+ "order by pg_services,status,priority",
			nativeQuery = true)
	public PGServiceDetails getNextServices(
			@Param("pgServices") String pgServices,
			@Param("pgId") String pgId);

	
	


}
