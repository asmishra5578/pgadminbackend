package com.asktech.admin.dto.admin;

import java.io.Serializable;
import java.util.List;

import com.asktech.admin.model.AddMerchantByDistributorRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindAllResponseAddMerchantByDistributorRequest implements Serializable{

	/**
	 * @author abhimanyu
	 */
	private static final long serialVersionUID = 6046855944421159396L;
	private int status;
	private String message;
	private List<AddMerchantByDistributorRequest> listOfAddMerchantByDistributorRequest;

}
