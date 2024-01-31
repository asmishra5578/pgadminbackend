package com.asktech.admin.dto.admin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateDistributorMerchantAssociationDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6574628815402494537L;
	private String status;
    private String region;
    private String rights;
    private Boolean flagValue;
    private String approval;

}
