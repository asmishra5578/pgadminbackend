package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asktech.admin.model.TransactionChangeRequest;

@Repository
public interface TransactionChangeRequestRepo extends JpaRepository<TransactionChangeRequest, Long> {

}
