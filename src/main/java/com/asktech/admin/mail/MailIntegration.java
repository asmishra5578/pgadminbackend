package com.asktech.admin.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.MerchantKycDetails;
import com.asktech.admin.model.RefundDetails;
import com.asktech.admin.model.TransactionDetails;
import com.asktech.admin.model.UserDetails;

@Service
@Async
public class MailIntegration {

	

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	MailContentBuilder mailbuilder;

	@Autowired
	public MailIntegration(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	
	public void sendMailForgotPassword(String recipientAddress, String mailText, String subject) {		
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			
			messageHelper.setFrom("EasyPayment <no-reply@eazypaymentz.com>");
			messageHelper.setTo(recipientAddress);
			messageHelper.setSubject(subject);
			String verification = mailbuilder.forgetPasswordOTP(mailText);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
	}

	
	public void sendMailCreateMerchant(MerchantDetails merchantDetails) {		
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			String recipientAddress = merchantDetails.getMerchantEmail();
			messageHelper.setFrom("EazyPaymentz <no-reply@eazypaymentz.com>");
			messageHelper.setTo(recipientAddress);
			messageHelper.setSubject("Welcome To EazyPaymentz");
			String verification = mailbuilder.createMerchant(merchantDetails);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
	}
	
	public void sendMailForMerchantRefundStatus(MerchantDetails merchantDetails, RefundDetails refundDetails) {		
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			String recipientAddress = merchantDetails.getMerchantEmail();
			messageHelper.setFrom("EazyPaymentz <no-reply@eazypaymentz.com>");
			messageHelper.setTo(recipientAddress);
			messageHelper.setSubject("Welcome To EazyPaymentz");
			String verification = mailbuilder.merchantRefundStatus(refundDetails);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
	}
	
	public void sendMailForMerchantKycStatus(MerchantDetails merchantDetails, MerchantKycDetails merchantKycDetails) {		
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			String recipientAddress = merchantDetails.getMerchantEmail();
			messageHelper.setFrom("EazyPaymentz <no-reply@eazypaymentz.com>");
			messageHelper.setTo(recipientAddress);
			messageHelper.setSubject("Welcome To EazyPaymentz");
			String verification = mailbuilder.merchantKycStatus(merchantKycDetails);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
	}
	
	/*
	public void sendMailUpdateMerchantStatus(String recipientAddress, String mailText, String subject) {		
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			
			messageHelper.setFrom("EasyPayment <no-reply@eazypaymentz.com>");
			messageHelper.setTo(recipientAddress);
			messageHelper.setSubject(subject);
			String verification = mailbuilder.createMerchant(mailText);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
	}*/

	
	public void sendMailCreateComplaint(String recipientAddress, String mailText, String subject) {		
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			
			messageHelper.setFrom("EazyPaymentz <no-reply@eazypaymentz.com>");
			messageHelper.setTo(recipientAddress);
			messageHelper.setSubject(subject);
			String verification = mailbuilder.createComplaint(mailText);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
	}

	
	public void sendMailToCustomer(String recipientAddress, String mailText, String subject) {		
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			
			messageHelper.setFrom("EazyPaymentz <no-reply@eazypaymentz.com>");
			messageHelper.setTo(recipientAddress);
			messageHelper.setSubject(subject);
			String verification = mailbuilder.createComplaint(mailText);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
	}

	
	public void sendTransactionMailMerchant(MerchantDetails merhantDetails, UserDetails userDetails, TransactionDetails transactionDetails) {
		
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			
			messageHelper.setFrom("EazyPaymentz <no-reply@eazypaymentz.com>");
			messageHelper.setTo(merhantDetails.getMerchantEmail());
			messageHelper.setSubject("Transaction Email from EasyPaymentz.com");
			String verification = mailbuilder.createMerchantTransaction(userDetails,merhantDetails,transactionDetails);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
		
	}

	
	public void sendTransactionMailCustomer(UserDetails userDetails, TransactionDetails transactionDetails) {

		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			
			messageHelper.setFrom("EazyPaymentz <no-reply@eazypaymentz.com>");
			messageHelper.setTo(userDetails.getEmailId());
			messageHelper.setSubject("Transaction Email from EasyPaymentz.com");
			String verification = mailbuilder.createCustomerTransaction(userDetails,transactionDetails);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
		
		
	}

	
	public void sendGeneratedMailToCustomer(String userName, String emailId, String phoneNumber , String link) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			
			messageHelper.setFrom("EazyPaymentz <no-reply@eazypaymentz.com>");
			messageHelper.setTo(emailId);
			messageHelper.setSubject("Payment Pending Notification from EasyPaymentz.com");
			String verification = mailbuilder.createCustomerMail(link);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
		
	}

	
	public void sendLoginOtpMail(String userName, String emailId, String phoneNumber , String otp) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			
			messageHelper.setFrom("EazyPaymentz <no-reply@eazypaymentz.com>");
			messageHelper.setTo(emailId);
			messageHelper.setSubject("Login OTP from EasyPaymentz.com");
			String verification = mailbuilder.createLoginOtp(userName,otp);
			messageHelper.setText(verification, true);
		};
		try {
			mailSender.send(messagePreparator);			
		} catch (MailException e) {			
			e.printStackTrace();
		}
		
	}
	
	
	
}
