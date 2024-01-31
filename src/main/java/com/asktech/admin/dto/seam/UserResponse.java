package com.asktech.admin.dto.seam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

	private String userName;
	private String userPhone;
	private String userEmail;
	private Integer amount;
	private String uuid;
	private String jwtToken;
}
