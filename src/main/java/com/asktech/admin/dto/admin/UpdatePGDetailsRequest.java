package com.asktech.admin.dto.admin;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdatePGDetailsRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1609146069948126589L;
	
	private String pgUuid;
	private String pgName;

	private String pgAppId;
	private String pgSecretKey;
	private String pgSaltKey;

	private String pgDailyLimit;
	// status is set in service-layer and updated by another end-point
	private String pgSecretId;
	private String pgApi;
	private String pgMerchantLink;
	
	private String pgAddInfo1;
	private String pgAddInfo2;
	private String pgAddInfo3;

}
