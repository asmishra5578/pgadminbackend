package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.UserOTPDetails;

public interface UserOTPDetailsRepository extends JpaRepository<UserOTPDetails, String>{

	UserOTPDetails findByUuid(String uuid);

}
