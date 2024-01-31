package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.BusinessAssociateCommissionDetails;

public interface BusinessAssociateCommissionDetailsRepo extends JpaRepository<BusinessAssociateCommissionDetails, String>{

	BusinessAssociateCommissionDetails findByUuidAndMerchantIDAndPaymentTypeAndPaymentSubTypeAndStatus(String busiAssociateuuid,
			String merchantId, String serviceType, String serviceSubType, String string);

	BusinessAssociateCommissionDetails findByUuidAndMerchantIDAndPaymentTypeAndStatus(String busiAssociateuuid, String merchantId,
			String serviceType, String string);

	BusinessAssociateCommissionDetails findByUuidAndId(String busiAssociateuuid, long commId);

	BusinessAssociateCommissionDetails findByUuidAndIdAndStatus(String busiAssociateuuid, Long valueOf, String status);

}
