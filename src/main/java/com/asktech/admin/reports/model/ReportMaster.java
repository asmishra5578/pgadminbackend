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
@Table(name="report_master")
public class ReportMaster extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String createdBy;
	private String updatedBy;
	private String reportName;
	private String reportType;
	private String reportExportType;
	private String headerNames;
	@Column(columnDefinition = "TEXT")  
	private String reportQuery;
	@Column(columnDefinition = "TEXT")
	private String additionalPartQuery;
	@Column(columnDefinition = "TEXT")
	private String queryFilter;
	private String userType;
	private String reportStatus;
	private String parameters;
	private String mandatoryParameter;
	private String mandatoryPrType;
	
	
}
