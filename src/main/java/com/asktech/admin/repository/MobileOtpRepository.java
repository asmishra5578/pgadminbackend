package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.MobileOtp;

public interface MobileOtpRepository extends JpaRepository<MobileOtp, String>{

	MobileOtp findBymobileNo(String phoneNumber);

	MobileOtp findByOtpAndMobileNo(int otp, String userNameOrEmail);

	MobileOtp findByOtpAndUserName(int otp, String userNameOrEmail);
	

	MobileOtp findByOtpAndUserNameAndOtpSessionId(int otp, String userNameOrEmail, String sessionId);

}
