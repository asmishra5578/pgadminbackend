package com.asktech.admin.dto.admin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePGDetailsRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3749366505514004249L;
	private String pgName;
	
	private String pgAppId;
	private String pgSecretKey;
	private String pgSaltKey;
	private String pgApi;
	private String pgSecretId;
	
	
	//private String pgDailyLimit;// the value for pgDailyLimit is set by another api endpoint
	//status is set in service-layer and updated by another end-point
	
	
	private String pgAddInfo1;
	private String pgAddInfo2;
	private String pgAddInfo3;
	private String pgMerchantLink;


	
}
