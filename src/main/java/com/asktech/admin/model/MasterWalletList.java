package com.asktech.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
	    uniqueConstraints=
	        @UniqueConstraint(columnNames={"wallet_name", "wallet_code" , "pg_wallet_code"})
	)
@Entity
public class MasterWalletList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long id;
	@Column(name="wallet_name")
	private String walletName;
	@Column(name="wallet_code")
	private String walletCode;
	@Column(name="pg_wallet_code")
	private String pgWalletCode;
	@Column(name="pg_id")
	private String pgId;
	@Column(name="pg_name")
	private String pgName;
}
