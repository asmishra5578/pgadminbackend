package com.asktech.admin.reports.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.asktech.admin.model.AbstractTimeStampAndId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="report_transaction_details")
public class ReportTransactionDetails extends AbstractTimeStampAndId {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String createdBy;
	private String updatedBy;
	private String reportPath;
	private String reportName;
	private String reportExecuteStatus;
	private String reportType;
	@Column(columnDefinition = "TEXT")  
	private String reportQuery;
	private String reportHeader;
	private int reportValidity;
	private String reportExportType;	
	private String reportParam1;
	private String reportParam2;
	private String reportParam3;
	private String reportParam4;
	private String reportParam5;
	private String reportParam6;
	private String folderName;
}
