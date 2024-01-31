package com.asktech.admin.dto.payout.pgPayout;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PgConfigResponse {
    private String status;
    private MerchantPgConfig pgres;
}
