package com.asktech.admin.dto.admin;

import java.io.Serializable;

import javax.persistence.Column;

public class UpdateResponseOfPGDetailsRequest implements Serializable{

	/**
	 * @author abhimanyu
	 */
	private static final long serialVersionUID = 8428067838334946978L;
	private long id;
	private String pgUuid;
	private String pgName;
	private String pgApi;
	private String pgAppId;
	
	private String pgSecret;//pgSecretKey
	
	private String pgSecretId;
	
	private String pgSaltKey;
	
	
	private String status;
	
	private String pgDailyLimit;
	
	
	private String createdBy;
	
	private String updatedBy;
	
	
	private String pgMerchantLink;
	
	
	private String pgAddInfo1;
	
	private String pgAddInfo2;
	
	private String pgAddInfo3;
	

}
