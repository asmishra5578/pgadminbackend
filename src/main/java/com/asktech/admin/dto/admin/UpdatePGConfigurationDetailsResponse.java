package com.asktech.admin.dto.admin;

import java.io.Serializable;
import java.util.List;

import com.asktech.admin.model.MerchantPGDetails;
import com.asktech.admin.model.PGConfigurationDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
/**@author abhimanyu-kumar*/
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatePGConfigurationDetailsResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4814709315860105376L;
	private PGConfigurationDetails pgConfigurationDetails;
	private List<MerchantPGDetails> listOfMerchantPGDetails;

}
