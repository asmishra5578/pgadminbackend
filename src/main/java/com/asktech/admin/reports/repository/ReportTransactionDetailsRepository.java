package com.asktech.admin.reports.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.asktech.admin.reports.model.ReportTransactionDetails;

public interface ReportTransactionDetailsRepository extends JpaRepository<ReportTransactionDetails, String>{

	List<ReportTransactionDetails> findAllByReportExecuteStatusAndReportType(String string, String string2);

	ReportTransactionDetails findById(long reportId);

	ReportTransactionDetails findByReportPath(String fileName);
	
	List<ReportTransactionDetails> findAllByReportName(String reportName);
	List<ReportTransactionDetails> findAllByReportNameAndReportParam3(String reportName,String reportParam3);
	
}
