package com.asktech.admin.repository.payout;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asktech.admin.model.payout.PayoutApiUserDetails;
@Repository
public interface PayoutApiUserDetailsRepo extends JpaRepository<PayoutApiUserDetails, String>{
  List<PayoutApiUserDetails> findAllByMerchantIdAndTokenAndWhitelistedip(String merchantid, String token, String whitelistedip);

  PayoutApiUserDetails findAllByMerchantIdAndToken(String merchantid, String secret);
  
  PayoutApiUserDetails findByMerchantId(String merchantid);
}
