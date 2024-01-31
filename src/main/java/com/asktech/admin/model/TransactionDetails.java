package com.asktech.admin.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "transaction_details")
public class TransactionDetails {
//extends AbstractTimeStampAndId
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created", nullable = false, updatable = false)
	private Date created = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated", nullable = false)
	private Date updated;
	
	
	private long userID;
	private String merchantId;
	private String pgId;
	private int amount;
	private String paymentOption;
	private String orderID;
	private String pgOrderID;
	private String pgType;
	private String status;
	private String paymentMode;
	@Column(columnDefinition = "LONGTEXT")
	private String txtMsg;
	private String txtPGTime;
	private String merchantOrderId;
	private String custOrderId;
	private String merchantReturnURL;
	private String cardNumber;
	private String paymentCode;
	
	@Column(columnDefinition = "LONGTEXT")
	private String vpaUPI;

	private String reconStatus;
	private String source;
	private String merchantAlertURL;
	private String orderNote;
	private String emailId;
	private String callBackFlag;
	private String errorMsg;
	
}
