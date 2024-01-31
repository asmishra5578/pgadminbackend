package com.asktech.admin.dto.admin;

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
public class MerchantKycDetailsRequest {

	private String merchantLegalName;
	private String merchantDBAName;
	private String panCardNumber;
	private String gstId;
	private String webstieUrl;
	private String businessEntityType;
	private String productDescription;
	private String tanNumber;
	private String monthlyExpectedTransactionsCount;
	private String averageTicketSize;
	private String categoryOrSegment;
	private String regName;
    private String regAddress;
    private String regPinCode;
    private String regNumber;
    private String regEmailAddress;
    private String commName;
    private String commAddress;
    private String commPinCode;
    private String commNumber;
    private String commEmailAddress;
    private String alertName;
    private String alertNumber;
    private String alertAddress;
    private String bankAccountHolderName;
    private String bankAccountNumber;
    private String bankIFSC;
    private String bankName;
    private String bankBranchAddress;
    private String bankAccountType;

}
