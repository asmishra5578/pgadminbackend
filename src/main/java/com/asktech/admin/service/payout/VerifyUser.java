package com.asktech.admin.service.payout;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.model.payout.PayoutApiUserDetails;
import com.asktech.admin.repository.payout.PayoutApiUserDetailsRepo;

@Service
public class VerifyUser {

	@Autowired
	PayoutApiUserDetailsRepo payoutApiUserDetailsRepo;
	
	static Logger logger = LoggerFactory.getLogger(VerifyUser.class);
	
	public boolean checkUser(String ipaddress, String merchantid, String secret) throws ValidationExceptions {
		//secret = new String(Base64.getDecoder().decode(secret));
		System.out.println(ipaddress+"|"+merchantid+"|"+secret);
		if(!isValidIPAddress(ipaddress)) {
			throw new ValidationExceptions("Invalid IP", FormValidationExceptionEnums.INVALID_IP);
		}
		if((merchantid == null)  || (merchantid.length() < 8)) {
			System.out.println(merchantid);
			throw new ValidationExceptions("Invalid Merchant Id", FormValidationExceptionEnums.DATA_INVALID);
		}
		if((secret == null) || (secret.length() < 8)) {
			throw new ValidationExceptions("Invalid Secret", FormValidationExceptionEnums.DATA_INVALID);
		}
		
		 PayoutApiUserDetails  payoutdetails = 
				 payoutApiUserDetailsRepo.findAllByMerchantIdAndToken(merchantid, secret);
		if(payoutdetails != null ) {
			logger.info("Ip Details ::"+payoutdetails.getWhitelistedip());
			if(payoutdetails.getWhitelistedip().contains(ipaddress)) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	private static boolean isValidIPAddress(String ip)
    {
 
        // Regex for digit from 0 to 255.
        String zeroTo255
            = "(\\d{1,2}|(0|1)\\"
              + "d{2}|2[0-4]\\d|25[0-5])";
 
        // Regex for a digit from 0 to 255 and
        // followed by a dot, repeat 4 times.
        // this is the regex to validate an IP address.
        String regex
            = zeroTo255 + "\\."
              + zeroTo255 + "\\."
              + zeroTo255 + "\\."
              + zeroTo255;
 
        // Compile the ReGex
        Pattern p = Pattern.compile(regex);
 
        // If the IP address is empty
        // return false
        if (ip == null) {
            return false;
        }
 
        // Pattern class contains matcher() method
        // to find matching between given IP address
        // and regular expression.
        Matcher m = p.matcher(ip);
 
        // Return if the IP address
        // matched the ReGex
        return m.matches();
    }
}
