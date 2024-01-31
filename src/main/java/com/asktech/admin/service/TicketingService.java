package com.asktech.admin.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.ticket.TicketDetailsResponse;
import com.asktech.admin.dto.ticket.TicketUpdateRequest;
import com.asktech.admin.enums.ComplaintStatus;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.TicketStatus;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.mail.MailIntegration;
import com.asktech.admin.model.TicketComplaintDetails;
import com.asktech.admin.model.TicketComplaintSubType;
import com.asktech.admin.model.TicketComplaintType;
import com.asktech.admin.model.TicketTransactionDetails;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.TicketComplaintDetailsRepository;
import com.asktech.admin.repository.TicketComplaintSubTypeRepository;
import com.asktech.admin.repository.TicketComplaintTypeRepository;
import com.asktech.admin.repository.TicketTransactionDetailsRepository;
import com.asktech.admin.repository.UserAdminDetailsRepository;

@Service
public class TicketingService implements ErrorValues {

	@Autowired
	TicketComplaintDetailsRepository ticketComplaintDetailsRepository;
	@Autowired
	TicketComplaintSubTypeRepository ticketComplaintSubTypeRepository;
	@Autowired
	TicketComplaintTypeRepository ticketComplaintTypeRepository;
	@Autowired
	TicketTransactionDetailsRepository ticketTransactionDetailsRepository;
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	@Autowired
	UserAdminDetailsRepository userAdminDetailsRepository;
	@Autowired
	PGGatewayAdminService pgGatewayAdminService;
	@Autowired
	MailIntegration sendMail;

	public TicketComplaintType createComplaintType(String uuid, String complaintType) throws ValidationExceptions {

		if (complaintType.length() == 0) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		TicketComplaintType ticketComplaintType = ticketComplaintTypeRepository.findBycommType(complaintType);
		if (ticketComplaintType != null) {
			throw new ValidationExceptions(COMPLAINT_TYPE_EXISTS, FormValidationExceptionEnums.COMPLAINT_TYPE_EXISTS);
		}
		ticketComplaintType = new TicketComplaintType();
		ticketComplaintType.setCommType(complaintType);
		ticketComplaintType.setCreatedBy(uuid);
		ticketComplaintType.setStatus(ComplaintStatus.ACTIVE.toString());
		ticketComplaintTypeRepository.save(ticketComplaintType);
		return ticketComplaintType;
	}

	public TicketComplaintSubType createComplaintSubType(String uuid, String complaintType, String subType)
			throws ValidationExceptions {

		if (complaintType.length() == 0 || subType.length() == 0) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		TicketComplaintType ticketComplaintType = ticketComplaintTypeRepository.findBycommType(complaintType);
		if (ticketComplaintType == null) {
			throw new ValidationExceptions(COMPLAINT_TYPE_NOT_EXISTS,
					FormValidationExceptionEnums.COMPLAINT_TYPE_NOT_EXISTS);
		}

		TicketComplaintSubType ticketComplaintSubType = ticketComplaintSubTypeRepository
				.findByCommTypeAndCommSubType(complaintType, subType);

		if (ticketComplaintSubType != null) {
			throw new ValidationExceptions(COMPLAINT_TYPE_SUBTYPE_EXISTS,
					FormValidationExceptionEnums.COMPLAINT_TYPE_SUBTYPE_EXISTS);
		}
		ticketComplaintSubType = new TicketComplaintSubType();
		ticketComplaintSubType.setCommSubType(subType);
		ticketComplaintSubType.setCommType(complaintType);
		ticketComplaintSubType.setCreatedBy(uuid);
		ticketComplaintSubType.setStatus(ComplaintStatus.ACTIVE.toString());
		ticketComplaintSubTypeRepository.save(ticketComplaintSubType);

		return ticketComplaintSubType;
	}

	public TicketComplaintType updateComplaintType(String uuid, String complaintType, String status)
			throws ValidationExceptions {

		if (complaintType.length() == 0) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		TicketComplaintType ticketComplaintType = ticketComplaintTypeRepository.findBycommType(complaintType);
		if (ticketComplaintType == null) {
			throw new ValidationExceptions(COMPLAINT_TYPE_NOT_EXISTS,
					FormValidationExceptionEnums.COMPLAINT_TYPE_NOT_EXISTS);
		}

		if (status.equalsIgnoreCase(ComplaintStatus.BLOCKED.toString())) {

			List<TicketComplaintSubType> listTicketComplaintSubType = ticketComplaintSubTypeRepository
					.findBycommType(complaintType);
			for (TicketComplaintSubType ticketComplaintSubType : listTicketComplaintSubType) {
				ticketComplaintSubType.setStatus(status);
				ticketComplaintSubType.setUpdateBy(uuid);
				ticketComplaintSubTypeRepository.save(ticketComplaintSubType);
			}
		}

		ticketComplaintType.setStatus(status);
		ticketComplaintType.setUpdateBy(uuid);
		ticketComplaintTypeRepository.save(ticketComplaintType);

		return ticketComplaintType;
	}

	public TicketComplaintSubType updateComplaintSubType(String uuid, String complaintType, String subType,
			String subTypeStatus) throws ValidationExceptions {

		if (complaintType.length() == 0 || subType.length() == 0) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		TicketComplaintType ticketComplaintType = ticketComplaintTypeRepository.findBycommType(complaintType);
		if (ticketComplaintType == null) {
			throw new ValidationExceptions(COMPLAINT_TYPE_NOT_EXISTS,
					FormValidationExceptionEnums.COMPLAINT_TYPE_NOT_EXISTS);
		}

		TicketComplaintSubType ticketComplaintSubType = ticketComplaintSubTypeRepository
				.findByCommTypeAndCommSubType(complaintType, subType);

		if (ticketComplaintSubType == null) {
			throw new ValidationExceptions(COMPLAINT_TYPE_SUBTYPE_NOT_EXISTS,
					FormValidationExceptionEnums.COMPLAINT_TYPE_SUBTYPE_NOT_EXISTS);
		}

		ticketComplaintSubType.setStatus(subTypeStatus);
		ticketComplaintSubType.setUpdateBy(uuid);
		ticketComplaintSubTypeRepository.save(ticketComplaintSubType);

		return ticketComplaintSubType;
	}
	public TicketDetailsResponse updateTicketAdmin(String uuid, TicketUpdateRequest ticketUpdateRequest)
			throws ParseException, ValidationExceptions {

		return updateTicket(uuid, ticketUpdateRequest, null);

	}

	public TicketDetailsResponse updateTicket(String uuid, TicketUpdateRequest ticketUpdateRequest, String pendingWith)
			throws ValidationExceptions {
		if (ticketUpdateRequest.getComplaintId().isEmpty() || ticketUpdateRequest.getStatus().isEmpty()) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE,
					FormValidationExceptionEnums.PLEASE_FILL_THE_MANDATORY_FIELDS);
		}

		TicketComplaintDetails ticketComplaintDetails = ticketComplaintDetailsRepository
				.findByComplaintId(ticketUpdateRequest.getComplaintId());
		if (ticketComplaintDetails == null) {
			throw new ValidationExceptions(COMPLAINT_NOT_FOUND, FormValidationExceptionEnums.COMPLAINT_NOT_FOUND);
		}

		if (ticketComplaintDetails.getStatus().equalsIgnoreCase(TicketStatus.CLOSED.toString())) {
			throw new ValidationExceptions(COMPLAINT_ALREADY_CLOSED,
					FormValidationExceptionEnums.COMPLAINT_ALREADY_CLOSED);
		}

		if (pendingWith == null) {
			ticketComplaintDetails.setPendingWith(ticketComplaintDetails.getCreatedBy());
		} else {
			ticketComplaintDetails.setPendingWith(pendingWith);
		}

		ticketComplaintDetails.setCommCounter(ticketComplaintDetails.getCommCounter() + 1);
		ticketComplaintDetails.setComplaintTest(ticketUpdateRequest.getComplaintText());
		ticketComplaintDetails.setStatus(ticketUpdateRequest.getStatus());
		ticketComplaintDetails.setUpdatedBy(uuid);
		ticketComplaintDetailsRepository.save(ticketComplaintDetails);

		TicketTransactionDetails ticketTransactionDetails = new TicketTransactionDetails();
		ticketTransactionDetails.setComplaintId(ticketComplaintDetails.getComplaintId());
		ticketTransactionDetails.setComplaintTest(ticketComplaintDetails.getComplaintTest());
		ticketTransactionDetails.setStatus(ticketComplaintDetails.getStatus());
		ticketTransactionDetails.setUpdatedBy(uuid);

		ticketTransactionDetailsRepository.save(ticketTransactionDetails);

		return getTicketResponse(ticketComplaintDetails);
	}

	public TicketDetailsResponse getTicketResponse(TicketComplaintDetails ticketComplaintDetails) {

		TicketDetailsResponse ticketDetailsResponse = new TicketDetailsResponse();
		List<TicketTransactionDetails> listTransactionDetails = ticketTransactionDetailsRepository
				.findAllByComplaintIdOrderByIdAsc(ticketComplaintDetails.getComplaintId());

		ticketDetailsResponse.setCommCounter(ticketComplaintDetails.getCommCounter());
		ticketDetailsResponse.setCommSubType(ticketComplaintDetails.getCommSubType());
		ticketDetailsResponse.setCommType(ticketComplaintDetails.getCommType());
		ticketDetailsResponse.setComplaintId(ticketComplaintDetails.getComplaintId());
		ticketDetailsResponse.setStatus(ticketComplaintDetails.getStatus());
		ticketDetailsResponse.setUpdatedBy(ticketComplaintDetails.getUpdatedBy());
		ticketDetailsResponse.setListTicketTransactionDetails(listTransactionDetails);

		return ticketDetailsResponse;
	}

	public List<TicketComplaintDetails> getTicketUpdatedByAdmin(String uuid) {
		return ticketComplaintDetailsRepository.findAllByUpdatedBy(uuid);
	}

	public List<TicketComplaintDetails> getTicketDetailsAdmin(String uuid) {
		return ticketComplaintDetailsRepository.findAllByPendingWith(uuid);
	}

	public List<TicketComplaintDetails> getTicketList() {
		return ticketComplaintDetailsRepository.findAll();
	}

	public List<TicketComplaintDetails> getTicketDetailFilter(String ticketId, String ticketType, String ticketSubType,
			String status, String start_date, String end_date) throws ValidationExceptions {
		if (pgGatewayAdminService.txnParam(start_date) == true && pgGatewayAdminService.txnParam(end_date) == true) {
			pgGatewayAdminService.dateWiseValidation(start_date, end_date);
		}

		List<TicketComplaintDetails> list = new ArrayList<TicketComplaintDetails>();

		if (pgGatewayAdminService.txnParam(ticketId) == true && pgGatewayAdminService.txnParam(ticketType) == true
				&& pgGatewayAdminService.txnParam(ticketSubType) == true
				&& pgGatewayAdminService.txnParam(status) == true) {
			list = ticketComplaintDetailsRepository.getComplaintByIdAndTypeAndSubTypeAndStatus(ticketId, ticketType,
					ticketSubType, status);
		} else if (pgGatewayAdminService.txnParam(ticketId) == true && pgGatewayAdminService.txnParam(status) == true) {
			list = ticketComplaintDetailsRepository.getComplaintByIdAndStatus(ticketId, status);
		} else if (pgGatewayAdminService.txnParam(ticketId) == true) {
			list = ticketComplaintDetailsRepository.getByComplaintId(ticketId);
		} else if (pgGatewayAdminService.txnParam(ticketType) == true
				&& pgGatewayAdminService.txnParam(ticketSubType) == true
				&& pgGatewayAdminService.txnParam(status) == true) {
			list = ticketComplaintDetailsRepository.getComplaintByTypeAndSubTypeAndStatus(ticketType, ticketSubType,
					status);
		} else if (pgGatewayAdminService.txnParam(ticketType) == true
				&& pgGatewayAdminService.txnParam(ticketSubType) == true) {
			list = ticketComplaintDetailsRepository.getComplaintByTypeAndSubType(ticketType, ticketSubType);
		} else if (pgGatewayAdminService.txnParam(ticketType) == true
				&& pgGatewayAdminService.txnParam(status) == true) {
			list = ticketComplaintDetailsRepository.getComplaintByTypeAndStatus(ticketType, status);
		} else if (pgGatewayAdminService.txnParam(ticketSubType) == true
				&& pgGatewayAdminService.txnParam(status) == true) {
			list = ticketComplaintDetailsRepository.getComplaintBySubTypeAndStatus(ticketSubType, status);
		} else if (pgGatewayAdminService.txnParam(ticketType) == true) {
			list = ticketComplaintDetailsRepository.getComplaintByType(ticketType);
		} else if (pgGatewayAdminService.txnParam(ticketSubType) == true) {
			list = ticketComplaintDetailsRepository.getComplaintBySubType(ticketSubType);
		} else if (pgGatewayAdminService.txnParam(status) == true) {
			list = ticketComplaintDetailsRepository.getComplaintByStatus(status);
		} else if (pgGatewayAdminService.txnParam(start_date) == true
				&& pgGatewayAdminService.txnParam(end_date) == true) {
			list = ticketComplaintDetailsRepository.getComplaintByDate(start_date, end_date);
		}

		return list;
	}

	public TicketDetailsResponse getComplaintDetailsUsingComplaintId(String uuid, String complaintId) throws ValidationExceptions {
		TicketComplaintDetails ticketComplaintDetails = ticketComplaintDetailsRepository
				.findByComplaintId(complaintId);
		if (ticketComplaintDetails == null) {
			throw new ValidationExceptions(COMPLAINT_NOT_FOUND, FormValidationExceptionEnums.COMPLAINT_NOT_FOUND);
		}
        return getTicketResponse(ticketComplaintDetails);
	}
}
