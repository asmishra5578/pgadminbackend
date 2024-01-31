package com.asktech.admin.dto.merchant;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantPgdetails {
	private String pgname;
	private String pgstatus;
	private String pguuid;
	private List<MerchantServiceDetails> merchantservicedetails;
}
