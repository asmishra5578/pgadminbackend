package com.asktech.admin.dto.paytmwebhook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class VanWebhookResData {
private String status;
private String bankTxnIdentifier;
private String remitterIfsc;
}
