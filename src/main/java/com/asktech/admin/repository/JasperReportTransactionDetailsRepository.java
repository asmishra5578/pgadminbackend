package com.asktech.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asktech.admin.reports.model.JasperReportTransactionDetails;

@Repository
public interface JasperReportTransactionDetailsRepository extends JpaRepository<JasperReportTransactionDetails, Long>{

	
	Optional<JasperReportTransactionDetails> findById(Long reportId);

	JasperReportTransactionDetails  save(JasperReportTransactionDetails jasperReportTransactionDetails);

	JasperReportTransactionDetails findByReportPath(String fileName);

	//JasperReportTransactionDetails findByFileName(String fileName);

	JasperReportTransactionDetails findByReportName(String fileName);

}
