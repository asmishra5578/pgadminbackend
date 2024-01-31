package com.asktech.admin.reports.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class ReportGenerateRequestDTO implements Serializable{

	private static final long serialVersionUID = -1708708905711610112L;
	
	private String merchantId;
	private String toDate;
	private String fromDate;
	private String fileName;
	

}
