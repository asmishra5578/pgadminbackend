package com.asktech.admin.model.payout;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.asktech.admin.model.AbstractTimeStampAndId;

import lombok.Getter;
import lombok.Setter;
	
@Entity
@Getter
@Setter
public class PayoutApiUserDetails extends AbstractTimeStampAndId {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int sno;
	private String merchantId;
	private String token;
	private String whitelistedip;
	@Column(name="merchant_status" , columnDefinition = "VARCHAR(255) default 'PENDING' ")
	private String merchantStatus;
	@Column(name="wallet_check_status" , columnDefinition = "VARCHAR(255) default 'FALSE' ")
	private String walletCheckStatus;
}
