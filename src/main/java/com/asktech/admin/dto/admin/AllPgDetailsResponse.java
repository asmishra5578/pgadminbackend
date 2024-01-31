package com.asktech.admin.dto.admin;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllPgDetailsResponse {
	
	   private String pgname;
	   private String created;
	   private Pgdetails pgdetails;
	   private List<Pgservices> pgservices;
	   
}
