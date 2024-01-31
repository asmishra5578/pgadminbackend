package com.asktech.admin.repository.seam;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.asktech.admin.model.seam.BankList;

public interface BankListRepository extends JpaRepository<BankList, String>{
	List<BankList> findAllByBankcode(String code);
	
	List<BankList> findByOrderByPgName();

	BankList findByBankcodeAndPgIdAndMerchantId(String bankCode, String pgId, String merchantId);

    BankList findByBankcodeAndPgBankCodeAndMerchantId(String bankcode, String pgBankCode, String merchantId);

    List<BankList> findByPgNameAndMerchantId(String pgName, String merchantId);
}
