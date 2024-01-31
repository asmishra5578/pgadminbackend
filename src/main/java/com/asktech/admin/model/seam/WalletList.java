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
public class WalletList {
	public WalletList(String walletname2, String paymentcodepg2, String pgname2, String paymentcode2, String status2,
			String merchantId2) {
		this.walletname = walletname2;
		this.paymentcodepg = paymentcodepg2;
		this.pgname = pgname2;
		this.paymentcode = paymentcode2;
		this.status = status2;
		this.merchantId = merchantId2;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String walletname;
	private String paymentcodepg;
	private String pgname;
	private String pgId;
	private String paymentcode;
	private String status;
	private String merchantId;
}
