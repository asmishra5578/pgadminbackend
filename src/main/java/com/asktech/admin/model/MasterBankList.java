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
	        @UniqueConstraint(columnNames={"bank_name", "bank_code" , "pg_bank_code","pg_id"})
	)
@Entity
public class MasterBankList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long id;
	@Column(name="bank_name")
	private String bankName;
	@Column(name="bank_code")
	private String bankCode;
	@Column(name="pg_bank_code")
	private String pgBankCode;	
	@Column(name="pg_id")
	private String pgId;
	@Column(name="pg_name")
	private String pgName;
}
