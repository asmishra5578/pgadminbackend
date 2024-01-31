package com.asktech.admin.service.callbackservice;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asktech.admin.dto.admin.PayOutCallBackRequest;
import com.asktech.admin.dto.callback.PayInPayOutCallBackRequest;
import com.asktech.admin.model.TransactionDetails;
import com.asktech.admin.repository.TransactionDetailsRepository;

import kong.unirest.Unirest;

@Service
public class PGAdminPINPOUTCallBackService {

    @Autowired
    TransactionDetailsRepository transactionDetailsRepository;
    @Value("${apiPayoutEndPoint.payoutBaseUrl}")
    String payoutBaseUrl;

    static Logger logger = LoggerFactory.getLogger(PGAdminPINPOUTCallBackService.class);

    public void callAndUpdateCallBackFlag(PayInPayOutCallBackRequest payInPayOutCallBackRequest) {
        logger.info("Going to change call Back flag as true......");
        List<String> validOrderIds = new ArrayList<>();
        payInPayOutCallBackRequest.getOrderIds().forEach(o -> {
            if (o.length() > 5) {
                validOrderIds.add(o);
            }
        });
        validOrderIds.forEach(orderId -> {
            TransactionDetails transactionDetails = transactionDetailsRepository.findByorderID(orderId);
            if (transactionDetails != null) {
                transactionDetails.setCallBackFlag("true");
                transactionDetailsRepository.save(transactionDetails);
                logger.info("Call Back flag changed..." + orderId);
            }
        });
    }

    public void callPayoutServiceForUpdateCallBackFlag(PayInPayOutCallBackRequest payInPayOutCallBackRequest) {
        logger.info("Going to change call Back flag as true......");
        PayOutCallBackRequest rdto = new PayOutCallBackRequest();
        payInPayOutCallBackRequest.getOrderIds().forEach(o -> {
            if (o.length() > 5) {
                rdto.getValidOrderIds().add(o);
            }
        });
        logger.info("Calling.. Rest call for update call back flag for payout: "+rdto);
        Unirest.put(payoutBaseUrl + "controller/updateCallBackFlag")
                .header("Content-Type", "application/json").body(rdto).asString();
    }

}
