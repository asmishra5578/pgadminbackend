package com.asktech.admin.repository.seam;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.seam.BankListCashfree;

public interface BankListCashfreeRepository extends JpaRepository<BankListCashfree, String>{
	List<BankListCashfree> findAllByBankcodemap(String code);
}
