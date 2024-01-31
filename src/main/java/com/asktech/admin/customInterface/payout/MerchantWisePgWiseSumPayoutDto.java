package com.asktech.admin.customInterface.payout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantWisePgWiseSumPayoutDto {
  private  String merchantId;
  private String cnt;
  private  String totalAmt;
  private   String transactionType;
    private   String merchantName;
    private	String companyName;

}
