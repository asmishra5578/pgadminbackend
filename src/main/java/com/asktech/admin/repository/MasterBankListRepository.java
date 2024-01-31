package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.asktech.admin.customInterface.IUniqueBankList;
import com.asktech.admin.model.MasterBankList;

public interface MasterBankListRepository extends JpaRepository<MasterBankList, String>{

	MasterBankList findByPgIdAndBankCode(String pgId, String bankCode);
	@Query(value = "select distinct bank_code bankCode,bank_name bankName,pg_bank_code pgBankCode "
			+ " from master_bank_list order by bankCode ",
			nativeQuery = true)
	List<IUniqueBankList> getUniqueBankList();

}
