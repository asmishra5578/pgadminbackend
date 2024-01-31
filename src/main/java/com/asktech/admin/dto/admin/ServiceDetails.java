package com.asktech.admin.dto.admin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDetails {

	   private String defaultService;
	   private long thresholdThreeMonths;
	   private long thresholdYear;
	   private long thresholdDay;
	   private Integer priority;
	   private long thresholdSixMonths;
	   private long thresholdWeek;
	   private long thresholdMonth;
	   
}
