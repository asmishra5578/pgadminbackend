package com.asktech.admin.dto.payout.beneficiary;

import java.util.ArrayList;
import java.util.List;

import com.asktech.admin.dto.admin.UpdateTransactionDetailsRequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionChangeRequestDto {
    private String uuid;
    private List<UpdateTransactionDetailsRequestDto> updateDataDto = new ArrayList<>();
}