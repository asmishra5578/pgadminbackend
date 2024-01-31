package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.BusinessAssociate;

public interface BusinessAssociateRepository extends JpaRepository<BusinessAssociate, String>{

	BusinessAssociate findByMerchantID(String merchantId);

	BusinessAssociate findByUuid(String busiAssociateuuid);

	BusinessAssociate findByUuidAndMerchantID(String busiAssociateuuid, String merchantId);
	
}
