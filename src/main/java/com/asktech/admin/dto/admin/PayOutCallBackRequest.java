package com.asktech.admin.dto.admin;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PayOutCallBackRequest {
    List<String> validOrderIds = new ArrayList<>();
}
