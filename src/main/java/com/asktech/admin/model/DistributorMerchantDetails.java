package com.asktech.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "distributor_merchant_association_details")
public class DistributorMerchantDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="created_by")
	private String createdBy;
	@Column(name="updated_by")
	private String updatedBy;
	private String uuid;
	
	//Distributor merchant , 1 Distributor can be aassociated with many merchnat, 1 merchnat can be associated with 1 distributor only
	@Column(name="distributor_id")
	private String distributorID;
	
	@Column(name="merchant_id")
	private String merchantID;
	
	
	@Column(name="status")
	private String status; 
	@Column(name="region")
	private String region;
	@Column(name="rights")
	private String rights;
	private Boolean flagValue;
	private String approval;

}
