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
/**@author abhimanyu-kumar*/
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "recharge_request_details")
public class RechargeRequestDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String createdBy;
	private String updatedBy;
	private String merchantID;
	private String distributorID;
	private String uuid;
	private long amount;
	private String notes;// you can use clob
	@Column(name = "utr")
	private String utr;
	private String created;
	private String updated;
	private String status;
	private String approval;
	private String info1;
	private String info2;
	private String info3;
	private String info4;
	private String info5;

}
