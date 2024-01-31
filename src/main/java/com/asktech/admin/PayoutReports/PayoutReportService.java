package com.asktech.admin.PayoutReports;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asktech.admin.PayoutReports.dto.ReportDownloadTrx;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.reports.dto.ReportRequestDTO;
import com.asktech.admin.reports.model.ReportMaster;
import com.asktech.admin.reports.model.ReportTransactionDetails;

import kong.unirest.GenericType;
import kong.unirest.Unirest;

@Service
public class PayoutReportService {

    @Value("${apiPayoutEndPoint.payoutUrl}")
    String payoutUrl;
    @Value("${apiPayoutEndPoint.payoutBaseUrl}")
    String payoutBaseUrl;

    public List<ReportMaster> getAllReportDetails() {
        kong.unirest.HttpResponse<List<ReportMaster>> responce = Unirest
                .get(payoutBaseUrl + "/report/getAllPayoutReportDetails")
                .header("Content-Type", "application/json")
                .asObject(new GenericType<List<ReportMaster>>() {
                });
                return responce.getBody();
            }

            public String transactionReportService(ReportRequestDTO reportRequestDTO, String uuid)
			throws ValidationExceptions, ParseException, IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

                kong.unirest.HttpResponse<String> responce = Unirest
                .post(payoutBaseUrl + "/report/transactionReport/"+uuid)
                .header("Content-Type", "application/json")
                .body(reportRequestDTO)
               
                .asObject(String.class);
                return responce.getBody();
            }

            public List<ReportTransactionDetails> getAllReportTransactionDownloadLinkList(){
                kong.unirest.HttpResponse<List<ReportTransactionDetails>> responce = Unirest
                .get(payoutBaseUrl + "/report/getAllPayoutReportTransactionDownloadLinkList")
                .header("Content-Type", "application/json")
                .asObject(new GenericType<List<ReportTransactionDetails>>() {
                });
                return responce.getBody();
            }
            public List<ReportDownloadTrx> getAllReportTrList(String reportName){
                kong.unirest.HttpResponse<List<ReportDownloadTrx>> responce = Unirest
                .get(payoutBaseUrl + "/report/getAllReportTrList/"+reportName)
                .header("Content-Type", "application/json")
                .asObject(new GenericType<List<ReportDownloadTrx>>() {
                });
                return responce.getBody();
            }
}
