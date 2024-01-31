package com.asktech.admin.dto.admin;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserSessionDto {
	
	private String uuid;
	private String sessionToken;

}
