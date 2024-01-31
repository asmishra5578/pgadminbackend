package com.asktech.admin.dto.admin;

import java.io.Serializable;
import java.util.List;

import com.asktech.admin.model.RechargeRequestDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindAllResponseRechargeRequestDetails implements Serializable{

	/**
	 * @author abhimanyu
	 */
	private static final long serialVersionUID = -7397086916813103005L;
	private int status;
	private String message;
	private List<RechargeRequestDetails> listOfRechargeRequestDetails;

}
