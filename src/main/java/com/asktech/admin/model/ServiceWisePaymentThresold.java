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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name="service_wise_payment_thresold")
public class ServiceWisePaymentThresold extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="service")
	private String service;
	@Column(name="pgid")
	private String pgid;
	@Column(name="merchant_id")
	private String merchantId;
	@Column(name="status")
	private String status;
	@Column(name="thresold_value")
	private long thresoldValue;
	@Column(name="createdBy")
	private String createdBy;
	@Column(name="updatedBy")
	private String updatedBy;
	
}
