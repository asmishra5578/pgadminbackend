package com.asktech.admin.controller;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.dto.ticket.TicketDetailsResponse;
import com.asktech.admin.dto.ticket.TicketUpdateRequest;
import com.asktech.admin.dto.utility.SuccessResponseDto;
import com.asktech.admin.enums.ComplaintStatus;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.enums.SuccessCode;
import com.asktech.admin.enums.TicketStatus;
import com.asktech.admin.exception.JWTException;
import com.asktech.admin.exception.SessionExpiredException;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.TicketComplaintDetails;
import com.asktech.admin.model.TicketComplaintSubType;
import com.asktech.admin.model.TicketComplaintType;
import com.asktech.admin.model.UserAdminDetails;
import com.asktech.admin.service.PGGatewayAdminService;
import com.asktech.admin.service.TicketingService;
import com.asktech.admin.util.JwtUserValidator;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import com.asktech.admin.util.Validator;

@RestController
public class TicketingController implements ErrorValues {

    @Autowired
    TicketingService ticketingService;
    @Autowired
    private JwtUserValidator jwtUserValidator;
    @Autowired
    PGGatewayAdminService pgGatewayAdminService;
    static Logger logger = LoggerFactory.getLogger(TicketingController.class);

    @PostMapping(value = "/api/admin/createComplaintType")
    @ApiOperation(value = "Create API for create Complaint Type ", authorizations = {
            @Authorization(value = "apiKey") })
    public ResponseEntity<?> createComplaintType(@RequestParam("uuid") String uuid,
            @RequestParam("complaintType") String complaintType) throws UserException, JWTException,
            SessionExpiredException, ValidationExceptions {

        UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
        logger.info("User Validation done :: " + userAdminDetails.getEmailId());

        TicketComplaintType ticketComplaintType = ticketingService.createComplaintType(uuid, complaintType);
        SuccessResponseDto sdto = new SuccessResponseDto();
        sdto.getMsg().add("Request Processed Successfully!");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("complaintDetail", ticketComplaintType);
        return ResponseEntity.ok().body(sdto);
    }

    @PostMapping(value = "/api/admin/createComplaintSubType")
    @ApiOperation(value = "Create API for create Complaint Sub Type ", authorizations = {
            @Authorization(value = "apiKey") })
    public ResponseEntity<?> createComplaintSubType(@RequestParam("uuid") String uuid,
            @RequestParam("complaintType") String complaintType,
            @RequestParam("complaintSubType") String complaintSubType) throws UserException, JWTException,
            SessionExpiredException, ValidationExceptions {

        UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
        logger.info("User Validation done :: " + userAdminDetails.getEmailId());

        TicketComplaintSubType ticketComplaintType = ticketingService.createComplaintSubType(uuid, complaintType,
                complaintSubType);
        SuccessResponseDto sdto = new SuccessResponseDto();
        sdto.getMsg().add("Request Processed Successfully!");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("complaintDetail", ticketComplaintType);
        return ResponseEntity.ok().body(sdto);
    }

    @PutMapping(value = "/api/admin/updateComplaintType")
    @ApiOperation(value = "Create API for update Complaint Type ", authorizations = {
            @Authorization(value = "apiKey") })
    public ResponseEntity<?> updateComplaintType(@RequestParam("uuid") String uuid,
            @RequestParam("complaintType") String complaintType,
            @RequestParam("status") String status) throws UserException, JWTException,
            SessionExpiredException, ValidationExceptions {

        UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
        logger.info("User Validation done :: " + userAdminDetails.getEmailId());

        if (!Validator.containsEnum(ComplaintStatus.class, status)) {
            throw new ValidationExceptions(TICKET_STATUS, FormValidationExceptionEnums.COMPLAIN_STATUS);
        }

        TicketComplaintType ticketComplaintType = ticketingService.updateComplaintType(uuid, complaintType, status);
        SuccessResponseDto sdto = new SuccessResponseDto();
        sdto.getMsg().add("Request Processed Successfully!");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("complaintDetail", ticketComplaintType);
        return ResponseEntity.ok().body(sdto);
    }

    @PutMapping(value = "/api/admin/updateComplaintSubType")
    @ApiOperation(value = "Create API for update Complaint Type ", authorizations = {
            @Authorization(value = "apiKey") })
    public ResponseEntity<?> updateComplaintSubType(@RequestParam("uuid") String uuid,
            @RequestParam("complaintType") String complaintType,
            @RequestParam("subType") String subType,
            @RequestParam("status") String status) throws UserException, JWTException,
            SessionExpiredException, ValidationExceptions {

        UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
        logger.info("User Validation done :: " + userAdminDetails.getEmailId());

        if (!Validator.containsEnum(ComplaintStatus.class, status)) {
            throw new ValidationExceptions(TICKET_STATUS, FormValidationExceptionEnums.COMPLAIN_STATUS);
        }

        TicketComplaintSubType ticketComplaintType = ticketingService.updateComplaintSubType(uuid, complaintType,
                subType, status);
        SuccessResponseDto sdto = new SuccessResponseDto();
        sdto.getMsg().add("Request Processed Successfully!");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("complaintDetail", ticketComplaintType);
        return ResponseEntity.ok().body(sdto);
    }

    @PutMapping(value = "/api/admin/updateTicket")
    @ApiOperation(value = "Create API for update ticket status with text msg", authorizations = {
            @Authorization(value = "apiKey") })
    public ResponseEntity<?> updateTicketAdmin(@RequestParam("uuid") String uuid,
            @RequestBody TicketUpdateRequest ticketUpdateRequest) throws UserException, JWTException,
            SessionExpiredException, ValidationExceptions, ParseException {

        UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
        logger.info("User Validation done :: " + userAdminDetails.getEmailId());

        TicketDetailsResponse ticketComplaintType = ticketingService.updateTicketAdmin(uuid, ticketUpdateRequest);
        SuccessResponseDto sdto = new SuccessResponseDto();
        sdto.getMsg().add("Request Processed Successfully!");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("complaintDetail", ticketComplaintType);
        return ResponseEntity.ok().body(sdto);
    }

    @GetMapping(value = "/api/admin/pendingByYou")
    @ApiOperation(value = "Create API for get Complaint details pending by you", authorizations = {
            @Authorization(value = "apiKey") })
    public ResponseEntity<?> getComplaintDetailsAdmin(
            @RequestParam("uuid") String uuid) throws UserException, JWTException,
            SessionExpiredException, ValidationExceptions, ParseException {

        UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
        logger.info("User Validation done :: " + userAdminDetails.getEmailId());

        List<TicketComplaintDetails> ticketComplaintType = ticketingService.getTicketDetailsAdmin(uuid);
        SuccessResponseDto sdto = new SuccessResponseDto();
        sdto.getMsg().add("Request Processed Successfully!");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("complaintDetail", ticketComplaintType);
        return ResponseEntity.ok().body(sdto);
    }

    @GetMapping(value = "/api/admin/updatedByYou")
    @ApiOperation(value = "Create API for get Complaint details updated by you ", authorizations = {
            @Authorization(value = "apiKey") })
    public ResponseEntity<?> getComplaintupdatedByAdmin(
            @RequestParam("uuid") String uuid) throws UserException, JWTException,
            SessionExpiredException, ValidationExceptions, ParseException {

        UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
        logger.info("User Validation done :: " + userAdminDetails.getEmailId());

        List<TicketComplaintDetails> ticketComplaintType = ticketingService.getTicketUpdatedByAdmin(uuid);
        SuccessResponseDto sdto = new SuccessResponseDto();
        sdto.getMsg().add("Request Processed Successfully!");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("complaintDetail", ticketComplaintType);
        return ResponseEntity.ok().body(sdto);
    }

    @GetMapping(value = "/api/admin/allListOfComplaint")
    @ApiOperation(value = "Create API for Get all list of raised Complaint ", authorizations = {
            @Authorization(value = "apiKey") })
    public ResponseEntity<?> getComplaintDetailsList(
            @RequestParam("uuid") String uuid) throws UserException, JWTException,
            SessionExpiredException, ValidationExceptions, ParseException {

        UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
        logger.info("User Validation done :: " + userAdminDetails.getEmailId());

        List<TicketComplaintDetails> ticketComplaintType = ticketingService.getTicketList();
        SuccessResponseDto sdto = new SuccessResponseDto();
        sdto.getMsg().add("Request Processed Successfully!");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("complaintDetail", ticketComplaintType);
        return ResponseEntity.ok().body(sdto);
    }

    @GetMapping(value = "/api/admin/complaintDetails")
    @ApiOperation(value = "Create API for get complaintDetails using complaintId ", authorizations = {
            @Authorization(value = "apiKey") })
    public ResponseEntity<?> getComplaintDetailsUsingComplaintId(
            @RequestParam("uuid") String uuid, @RequestParam("complaintId") String complaintId)
            throws UserException, JWTException,
            SessionExpiredException, ValidationExceptions, ParseException {

        UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
        logger.info("User Validation done :: " + userAdminDetails.getEmailId());

        TicketDetailsResponse ticketComplaintType = ticketingService.getComplaintDetailsUsingComplaintId(uuid,
                complaintId);
        SuccessResponseDto sdto = new SuccessResponseDto();
        sdto.getMsg().add("Request Processed Successfully!");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("complaintDetail", ticketComplaintType);
        return ResponseEntity.ok().body(sdto);
    }

    @GetMapping(value = "/api/admin/complaintDetailFilter")
    @ApiOperation(value = "Create API for Complaint Type ", authorizations = {
            @Authorization(value = "apiKey") })
    public ResponseEntity<?> complaintDetailWithTicketId(
            @RequestParam("uuid") String uuid,
            @RequestParam(value = "complaintId", required = false) String complaintId,
            @RequestParam(value = "complaintType", required = false) String complaintType,
            @RequestParam(value = "complaintSubType", required = false) String complaintSubType,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "start_date", required = false) String start_date,
            @RequestParam(value = "end_date", required = false) String end_date) throws UserException, JWTException,
            SessionExpiredException, ValidationExceptions, ParseException {

        UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
        logger.info("User Validation done :: " + userAdminDetails.getEmailId());

        if (pgGatewayAdminService.txnParam(status) == true) {
            if (!Validator.containsEnum(TicketStatus.class, status)) {
                throw new ValidationExceptions(TICKET_STATUS, FormValidationExceptionEnums.TICKET_STATUS);
            }
        }

        List<TicketComplaintDetails> listfilter = ticketingService.getTicketDetailFilter(complaintId, complaintType,
                complaintSubType, status, start_date, end_date);

        if (listfilter.isEmpty()) {
            throw new ValidationExceptions(TICKET_NOT_FOUND, FormValidationExceptionEnums.TICKET_NOT_FOUND);
        }

        SuccessResponseDto sdto = new SuccessResponseDto();
        sdto.getMsg().add("Request Processed Successfully!");
        sdto.setSuccessCode(SuccessCode.API_SUCCESS);
        sdto.getExtraData().put("complaintDetail", listfilter);
        return ResponseEntity.ok().body(sdto);
    }

}
