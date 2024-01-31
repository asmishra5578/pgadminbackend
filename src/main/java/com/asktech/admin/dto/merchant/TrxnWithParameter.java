package com.asktech.admin.dto.merchant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrxnWithParameter {
    private  String orderId;
    private  String merchant_id;
    private   String pg_id;
    private  String start_date;
    private    String  end_date;
    private    String oder_id;
     private   String trx_msg;
     private   String utrid;
     private   String internalOrderId;
     private   String internalOrderIds;
     private  String utrids;
}
