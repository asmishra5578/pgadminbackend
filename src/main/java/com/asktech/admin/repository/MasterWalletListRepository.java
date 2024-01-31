package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.asktech.admin.customInterface.IUniqueWalletList;
import com.asktech.admin.model.MasterWalletList;

public interface MasterWalletListRepository extends JpaRepository<MasterWalletList, String>{

	MasterWalletList findByPgIdAndWalletCode(String pgId, String walletCode);
	
	@Query(value = "select distinct wallet_code paymentCode,pg_wallet_code paymentCodePg,wallet_name walletName  "
			+ " from master_wallet_list order by paymentcode ",
			nativeQuery = true)
	List<IUniqueWalletList> getUniqueWalletList();

}
