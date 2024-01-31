package com.asktech.admin.dto.callback;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PayInPayOutCallBackRequest {
    
    public String adminUuid;
    public List<String> orderIds = new ArrayList<>();
}
