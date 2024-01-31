package com.asktech.admin.service.payout.merchantManagement;

import com.asktech.admin.dto.payout.merchant.WalletCreateReqDto;

import kong.unirest.Unirest;

public class MerchantUtils {
    protected static String createWallet(WalletCreateReqDto dto, String payoutBaseUrl, String merchantid) {
        String res = Unirest.post(payoutBaseUrl + "admin/wallet/walletCreation/" + merchantid)
                .header("Content-Type", "application/json").body(dto).asString().getBody();

        return res;
    }
}
