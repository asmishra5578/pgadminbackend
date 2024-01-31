package com.asktech.admin.reports.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.asktech.admin.model.AbstractTimeStampAndId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="jasperreport_transaction_details")
public class JasperReportTransactionDetails extends AbstractTimeStampAndId {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String createdBy;
	private String updatedBy;
	
	private String uuid;
	private String reportPath;
	private String reportName;//fileName
	private String fileType;
	private String reportExecuteStatus;
	private String reportType;
	//@Column(columnDefinition = "TEXT")  
	//private String reportQuery;
	
	@Lob
	private byte[] data;
	
	private int reportValidity;// read from yaml file
	private String folderName;// read from yaml file
	
	public JasperReportTransactionDetails() {}
	
}
