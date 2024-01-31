package com.asktech.admin.dto.admin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateDistributorDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8753368240373132966L;
	
	
	private int status;
	private String message;
	
	private String appID;
    private String secretId;
    private String distributorEMail;
    private String password;// check how it is created and will be updated
    private String initialPwdChange;// what to do ask 
    private String userStatus;
    private String phoneNumber;
    private String distributorName;
    private String kycStatus;
    private String saltKey;
    private String tr_mail_flag;
    private String distributorType;
    private String companyName;
    private String supportEmailId;
    private String supportPhoneNo;
    private String logoUrl;
	private String createdBy;
	private String updatedBy;
    

}

////AppID,SaltKey,SecretId

//"appID": "",
//"secretId": "",
//"saltKey": "",
//"password": "",
//"initialPwdChange": "",
