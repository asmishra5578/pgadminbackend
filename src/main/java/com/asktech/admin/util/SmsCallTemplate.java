package com.asktech.admin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.asktech.admin.constants.ErrorValues;
import com.asktech.admin.enums.AskTechGateway;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.exception.UserException;
import com.asktech.admin.exception.ValidationExceptions;
import com.fasterxml.jackson.core.JsonProcessingException;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Component
@Async
public class SmsCallTemplate implements  ErrorValues{

	static Logger logger = LoggerFactory.getLogger(SmsCallTemplate.class);
	private static String smsApi = "https://api-alerts.kaleyra.com/v4/?";
	private static String apiKey = "A7e3cefd77660c94c31e05bb5ac2f4909";
	private static String smsSender_Id = "IMONEY";
	private static String apiType = "sms";
	
	
	public void smsSendbyApi(String message, String mobileNo, String senderId) throws UserException, JsonProcessingException, ValidationExceptions {
		HttpResponse<String> dto = (HttpResponse<String>) Unirest.get(smsApi + "api_key=" + apiKey + "&method="+apiType+"&message="+message+"&to="+mobileNo+"&sender="+senderId).asString();
		logger.info("sms using ikontel==> " + Utility.convertDTO2JsonString(dto.getBody()));
		
		if (!dto.isSuccess()) {
			logger.error("sms using ikontel==> "+"Ikontel is down :: "+AskTechGateway.REST_TEMPLATE_NOT_CALL);
			throw new ValidationExceptions(EXCEPTION_IN_SMS_SENDING, FormValidationExceptionEnums.EXCEPTION_IN_SMS_SENDING);
		}
	}
	
}
