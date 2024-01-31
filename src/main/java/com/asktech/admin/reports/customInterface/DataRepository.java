package com.asktech.admin.reports.customInterface;

import java.util.List;

import com.asktech.admin.reports.dto.ReportData;

public interface DataRepository {

	List<ReportData> executeQuery(String query);
	
	
}
