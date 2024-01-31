package com.asktech.admin.dto.payout.pgPayout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PgResponse {
    private String status;
    private PgDetails pgres;
}
