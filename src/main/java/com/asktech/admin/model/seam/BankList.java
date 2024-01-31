package com.asktech.admin.model.seam;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BankList {
	public BankList(String bankname2, String bankcode2, String pgBankCode2, String pgName2, String status2,
			String merchantId2) {
		this.bankname = bankname2;
		this.bankcode = bankcode2;
		this.pgBankCode = pgBankCode2;
		this.pgName = pgName2;
		this.status = status2;
		this.merchantId = merchantId2;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	private long id;
	private String bankname;
	private String bankcode;
	private String pgBankCode;
	private String pgName;
	private String pgId;
	private String status;
	private String merchantId;
	
}
