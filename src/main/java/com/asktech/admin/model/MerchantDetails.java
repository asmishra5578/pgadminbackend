package com.asktech.admin.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="merchant_details")
public class MerchantDetails extends AbstractTimeStampAndId{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String merchantID;
	@Column(columnDefinition="LONGTEXT")
	private String appID;
	private String uuid;
	@Column(columnDefinition="LONGTEXT")
	private String secretId;
	@Column(name="merchantemail")
	private String merchantEmail;
	@Column(columnDefinition="LONGTEXT")
	private String password;
	private String initialPwdChange;
	private String userStatus;
	private String phoneNumber;
	private String merchantName;
	private String kycStatus;
	private String createdBy;
	private String saltKey;
	private String tr_mail_flag;
	private String merchantType;
	private String companyName;
	private String supportEmailId;
	private String supportPhoneNo;
	private String logoUrl;
	private String payinFlag;
	private String payoutFlag;
	private String permenantLink;
	private String maxTicketSize;
	private String minTicketSize;
	private String merchantDailyLimit;
	@Column(columnDefinition = "VARCHAR(255) default 'DISABLE' ")
	private String otpStatus;
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	private UserSession userSession;
	
	//@ManyToOne(cascade = CascadeType.ALL, targetEntity = DistributorDetails.class)
	//private DistributorDetails distributorDetails;
	
	
	 
	
}


