package com.asktech.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.admin.customInterface.IPGWiseMerchantDetails;
import com.asktech.admin.customInterface.StatusAndMinute;
import com.asktech.admin.reports.dto.NestedAndMinute;
import com.asktech.admin.reports.dto.StatusAndMinuteDto;
import com.asktech.admin.repository.MerchantPGServicesRepository;
import com.asktech.admin.repository.TransactionDetailsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PGAdminReports {
    @Autowired
    TransactionDetailsRepository transactionDetailsRepository;
    // static Logger logger = LoggerFactory.getLogger(PGAdminReports.class);
    @Autowired
    MerchantPGServicesRepository merchantPGServicesRepository;

    public Object getByMerchantWisePgWiseSum(String start_date, String end_date, String status){
        return transactionDetailsRepository.getByMerchantWisePgWiseSumQuery(start_date, end_date, status);
    }
    public Object getPgTypeAndCountByStatusAndDate(String start_date, String end_date, String status){
        return transactionDetailsRepository.getPgTypeAndCountByStatusAndDate(start_date, end_date, status);
    }
    public Object getHourandCountStatusAndDate(String start_date, String status){
        return transactionDetailsRepository.getHourandCountStatusAndDate(start_date, status);
    }
    public Object getMinuteandCountByStatus(String status){
        return transactionDetailsRepository.getMinuteandCountByStatus( status);
    }
    public Object getStatusCount(String start_date, String end_date){


        
        return transactionDetailsRepository.getStatusCount( start_date,end_date);
    }
    public Object getLastTrxMerchList(String start_date){
        return transactionDetailsRepository.getLastTrxMerchList( start_date);
    }

    // public StatusAndMinuteDto getStatusAndMinuteWiseCount() {

    //     List<NestedAndMinute> successList = null ;
    //     List<NestedAndMinute> failedList = null ;
    //     List<NestedAndMinute> pendingList = null ;   

    //     List<StatusAndMinute> queryResult = transactionDetailsRepository.getStatusAndMinuteWiseCount();

    //     StatusAndMinuteDto statusDto=new StatusAndMinuteDto();
    //     if(queryResult!=null ){
    //     for (StatusAndMinute eachItem: queryResult) {
           
    //         if(eachItem.getStatus().equalsIgnoreCase("SUCCESS")){
    //             NestedAndMinute successDto=new NestedAndMinute();
    //             successDto.setCount(eachItem.getCnt());
    //             successDto.setMinute(eachItem.getMinutes());
    //             successDto.setTotal(eachItem.getTotal());

    //             successList.add(successDto);
    //         }else if(eachItem.getStatus().equalsIgnoreCase("FAILED")){
    //             NestedAndMinute failedDto=new NestedAndMinute();
    //             failedDto.setCount(eachItem.getCnt());
    //             failedDto.setMinute(eachItem.getMinutes());
    //             failedDto.setTotal(eachItem.getTotal());
    //             failedList.add(failedDto);
    //         }else if(eachItem.getStatus().equalsIgnoreCase("PENDING")){
    //             NestedAndMinute pendingDto=new NestedAndMinute(); 
    //             pendingDto.setCount(eachItem.getCnt());
    //             pendingDto.setMinute(eachItem.getMinutes());
    //             pendingDto.setTotal(eachItem.getTotal());
    //             pendingList.add(pendingDto);
    //         }
            
    //     }
    // }
    // if(pendingList!=null){
    //     statusDto.setPending(pendingList);
    // }
    // if(failedList!=null){
    // statusDto.setFailed(failedList);
    // }
    // if(successList!=null){
    // statusDto.setSuccess(successList);
    // }
    //     return statusDto; 
    // }

    public List<StatusAndMinute> getStatusAndMinuteWiseCount() throws JsonProcessingException {
        //ACTUAL
                List<StatusAndMinute> queryResult = transactionDetailsRepository.getStatusAndMinuteWiseCount();
                // Utility.convertDTO2JsonString(queryResult);
                // logger.info(Utility.convertDTO2JsonString(queryResult));
        
                return queryResult;
            }
            public Object getHourandStatusWiseCountAndDate(String start_date){
                return transactionDetailsRepository.getHourandStatusWiseCountAndDate(start_date);
            }
            public List<IPGWiseMerchantDetails> getAllMerchantListFromPgId(String pgId, String uuid){
        
        
                /* CHECK UUID */
                
        
                List<IPGWiseMerchantDetails> merchants = (List<IPGWiseMerchantDetails>) merchantPGServicesRepository.getAllMerchantByPgId(pgId);
              
        
                return merchants;
                    
                
            }


            public Object getHourandStatusWiseCountAndDateAndSum(){
                return transactionDetailsRepository.getHourandStatusWiseCountAndDateAndSum();
            }
}
