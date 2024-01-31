package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.UserSession;

public interface UserSessionRepo extends JpaRepository<UserSession, Long>{

	UserSession findBysessionToken(String sessionToken);

}
