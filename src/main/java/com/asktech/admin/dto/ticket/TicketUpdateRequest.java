package com.asktech.admin.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateRequest {
	private String complaintId;	
	private String complaintText;
	private String status;
}
