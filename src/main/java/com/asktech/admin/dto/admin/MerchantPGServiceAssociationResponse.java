package com.asktech.admin.dto.admin;

import java.io.Serializable;

import com.asktech.admin.model.MerchantPGServices;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**@author abhimanyu-kumar*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantPGServiceAssociationResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8836115203082916556L;
	private MerchantPGServices merchantPGServices;
	private int status;
	private String message;

}
