package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.UserAdminDetails;

public interface UserAdminDetailsRepository extends JpaRepository<UserAdminDetails, String>{

	UserAdminDetails findByUserId(String userNameOrEmail);

	UserAdminDetails findByEmailId(String email);

	UserAdminDetails findByuuid(String uuid);

	UserAdminDetails findByUuid(String uuid);

	
}
