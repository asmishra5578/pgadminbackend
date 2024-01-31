package com.asktech.admin.reports.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.admin.reports.model.ReportMaster;

public interface ReportMasterRepository extends JpaRepository<ReportMaster, String>{

	
	
	@Query(value = "select :sql_query ",nativeQuery=true)
	public List<Object> reportExecute(@Param("sql_query") String sql_query);

	public ReportMaster findByReportName(String reportName);
	
	
}
