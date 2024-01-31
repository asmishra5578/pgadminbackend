package com.asktech.admin.dto.merchant;

import java.util.List;

import com.asktech.admin.model.TransactionDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetailsResponse {

	private List<TransactionDetails> transactionDetails; 
}
