package com.asktech.admin.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
//@Table(name="usersession")
public class UserSession extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;	
	private int sessionStatus;
	private String sessionToken;
	private Date sessionExpiryDate;
	private String userAgent;
	private String ipAddress;
	private Date idealSessionExpiry;
	@OneToOne(cascade = CascadeType.ALL)
	private MerchantDetails user;
	
	@OneToOne(cascade = CascadeType.ALL)
	private UserAdminDetails userAdmin;
}
