package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.QRCodePaymentDetails;

public interface QRCodePaymentDetailsRepository extends JpaRepository<QRCodePaymentDetails, String>{

}
