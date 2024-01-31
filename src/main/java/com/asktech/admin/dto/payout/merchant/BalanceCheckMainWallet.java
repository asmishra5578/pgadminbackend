package com.asktech.admin.dto.payout.merchant;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceCheckMainWallet implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7387139381375906648L;
	private String walletid;
    private String status;
    private String name;
    private String amount;
}