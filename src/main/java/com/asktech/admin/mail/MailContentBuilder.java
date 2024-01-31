package com.asktech.admin.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.MerchantKycDetails;
import com.asktech.admin.model.RefundDetails;
import com.asktech.admin.model.TransactionDetails;
import com.asktech.admin.model.UserDetails;
import com.asktech.admin.security.Encryption;

@Service
public class MailContentBuilder {

	private TemplateEngine templateEngine;

	@Autowired
	public MailContentBuilder(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	
	public String forgetPasswordOTP(String mailText) {
		Context context = new Context();		
		context.setVariable("mailText", mailText);
		return templateEngine.process("/emailCommunication/forgetPassowrd", context);
	}

	public String createMerchant(MerchantDetails merchantDetails) {
		Context context = new Context();		
		context.setVariable("merchantName", merchantDetails.getMerchantName());
		context.setVariable("merchantEmail", merchantDetails.getMerchantEmail());
		context.setVariable("password", Encryption.getDecryptedPasswordKMS(merchantDetails.getPassword()));
		return templateEngine.process("/emailCommunication/welcome", context);
	}
	
	public String merchantRefundStatus(RefundDetails refundDetails) {
		Context context = new Context();		
		context.setVariable("merchantId", refundDetails.getMerchantId());
		context.setVariable("refundStatus", refundDetails.getStatus());
		context.setVariable("message", refundDetails.getRefundMsg());
		return templateEngine.process("/emailCommunication/welcome", context);
	}
	
	public String merchantKycStatus(MerchantKycDetails merchantKycDetails) {
		Context context = new Context();		
		context.setVariable("merchantName", merchantKycDetails.getMerchantLegalName());
		context.setVariable("kycStatus", merchantKycDetails.getMerchantKycStatus());
		context.setVariable("message", merchantKycDetails.getKycComment());
		return templateEngine.process("/emailCommunication/welcome", context);
	}


	public String createComplaint(String mailText) {
		Context context = new Context();		
		context.setVariable("mailText", mailText);
		return templateEngine.process("/emailCommunication/createComplaint", context);
	}
	
	public String createMerchantTransaction(UserDetails userDetails , MerchantDetails merchantDetails , TransactionDetails transactionDetails) {
		
		Context context = new Context();		
		context.setVariable("merchantName", merchantDetails.getMerchantName());
		context.setVariable("orderId", transactionDetails.getMerchantOrderId());
		context.setVariable("transactionId", transactionDetails.getOrderID());
		context.setVariable("transactionTime", transactionDetails.getTxtPGTime());
		context.setVariable("amount", transactionDetails.getAmount());
		context.setVariable("custName",userDetails.getCustomerName());
		context.setVariable("custEmail",userDetails.getEmailId());
		context.setVariable("custPhone",userDetails.getPhoneNumber());
		context.setVariable("paymentMode",transactionDetails.getPaymentMode());
		return templateEngine.process("/emailCommunication/txn_status_Merchant", context);
	}


	public String createCustomerTransaction(UserDetails userDetails, TransactionDetails transactionDetails) {
		Context context = new Context();		
		
		context.setVariable("orderId", transactionDetails.getMerchantOrderId());
		context.setVariable("transactionId", transactionDetails.getOrderID());
		context.setVariable("transactionTime", transactionDetails.getTxtPGTime());
		context.setVariable("amount", transactionDetails.getAmount());
		context.setVariable("custName",userDetails.getCustomerName());
		context.setVariable("custEmail",userDetails.getEmailId());
		context.setVariable("custPhone",userDetails.getPhoneNumber());
		context.setVariable("paymentMode",transactionDetails.getPaymentMode());
		return templateEngine.process("/emailCommunication/txn_status_customer", context);
	}


	public String createCustomerMail(String link) {
		Context context = new Context();		
		
		context.setVariable("mailText", link);
		
		return templateEngine.process("emailCommunication/customerpaymentLink", context);
	}


	public String createLoginOtp(String userName, String otp) {
		Context context = new Context();				
		context.setVariable("otp", otp);		
		return templateEngine.process("emailCommunication/custLoginOtp", context);
	}
	
}
