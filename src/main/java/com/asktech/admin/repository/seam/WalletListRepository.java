package com.asktech.admin.repository.seam;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.asktech.admin.model.seam.WalletList;

public interface WalletListRepository extends JpaRepository<WalletList, String>{
	
	List<WalletList> findAllByPgnameAndStatus(String pgname, String status);
	WalletList findByPaymentcode(String paymentcode);
	List<WalletList> findByOrderByPgname();
	WalletList findByPaymentcodeAndPgIdAndMerchantId(String walletCode, String pgId, String merchantId);
    WalletList findByPaymentcodepgAndPaymentcodeAndMerchantId(String paymentcodepg, String paymentcode,
            String merchantId);
    List<WalletList> findByPgnameAndMerchantId(String pgname, String merchantId);
}
