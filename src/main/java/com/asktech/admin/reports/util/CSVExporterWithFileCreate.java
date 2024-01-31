package com.asktech.admin.reports.util;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.asktech.admin.reports.dto.ReportData;
import com.opencsv.CSVWriter;

@Service
public class CSVExporterWithFileCreate  {
    
	static Logger logger = LoggerFactory.getLogger(CSVExporterWithFileCreate.class);
    public void exportCSVFiles(List<ReportData> datas , String fileName , String reportHeader, String reportFolderPath) 
    {
    	
    	try {
    		logger.info("Inside CSV wirter ...");
    			logger.info("Path :: "+reportFolderPath+fileName);
		        CSVWriter writer = new CSVWriter(new FileWriter(reportFolderPath+fileName));
		        List<String[]> list = new ArrayList<>();
		        
		        String[] headers = reportHeader.split("\\^");
		        list.add(headers);
		
		        for (ReportData reportData : datas) {
					
					String[] arrOfStr = reportData.getDatas().split("\\^");
					list.add(arrOfStr);
					//writer.writeAll(list);
		        }
		        writer.writeAll(list);
		        logger.info("End CSV wirter ...");
				
			}catch(Exception e) {
				logger.error("Exception in CSV fie generated");
			}
        
    }
}
