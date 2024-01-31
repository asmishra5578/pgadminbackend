package com.asktech.admin.repository.seam;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.seam.CustomerRequest;

public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, String>{

	CustomerRequest findAllByUuid(String uuid);
	CustomerRequest findAllBySessionToken(String sessionToken);
}
