package com.asktech.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "addmerchantby_distributorrequest_details")
public class AddMerchantByDistributorRequest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String uuid;
	@Column(name="created_by")
	private String createdBy;
	@Column(name="updated_by")
	private String updatedBy;
	
	private String merchantName;
	private String phoneNumber;
	private String emailId;
	private String kycStatus;
	private String companyName;
	private String supportEmailId;
	private String supportPhoneNo;
	private String merchantType;
	private String logoUrl;
	
	@Column(name="distributor_id")
	private String distributorID;
	
	private String notes;
	private String reference;
	@Column(name="status")
	private String status; 
	private Boolean flagValue;
	private String approval;
	
	private String info1;
	private String info2;
	private String info3;
	private String info4;
	private String info5;

}
