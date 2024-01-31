package com.asktech.admin.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashBoardDetails {
	private String todaysTransactions;
	private String lastSettlements;
	private String unsettledAmount;
}
