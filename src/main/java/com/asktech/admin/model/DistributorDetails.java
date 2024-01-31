package com.asktech.admin.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**@author abhimanyu-kumar*/
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "distributors_details")
public class DistributorDetails extends AbstractTimeStampAndId {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private long id;
	private String distributorID;
	@Column(columnDefinition="LONGTEXT")
	private String appID;
	private String uuid;
	@Column(columnDefinition="LONGTEXT")
	private String secretId;
	private String distributorEMail;
	@Column(columnDefinition = "LONGTEXT")
	private String password;
	private String initialPwdChange;
	
	private String userStatus;
	
	private String phoneNumber;
	private String distributorName;
	private String kycStatus;
	private String createdBy;
	private String updatedBy;
	private String saltKey;
	private String tr_mail_flag;
	private String distributorType;
	private String companyName;
	private String supportEmailId;
	private String supportPhoneNo;
	private String logoUrl;
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	private com.asktech.admin.model.UserSession userSession;
	
	
	
	
	/*
	 * @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch =
	 * FetchType.LAZY, targetEntity = MerchantDetails.class)
	 * 
	 * @JoinTable(name = "distributor_merchant_association_details", joinColumns = {
	 * 
	 * @JoinColumn(name = "distributorID", referencedColumnName = "id") },
	 * inverseJoinColumns = {
	 * 
	 * @JoinColumn(name = "merchantID", referencedColumnName = "id") }) private
	 * Set<MerchantDetails> setOfMerchantDetails;
	 */
	 
	 
}
