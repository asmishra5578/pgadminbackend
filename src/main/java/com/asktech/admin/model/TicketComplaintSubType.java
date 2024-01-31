package com.asktech.admin.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="ticket_complaint_subtype")
public class TicketComplaintSubType  extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String commType;
	private String commSubType;
	private String createdBy;
	private String status;
	private String updateBy;
}
