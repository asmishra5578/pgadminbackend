package com.asktech.admin.dto.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResetPasswordResponse {

    private List<String> msg = new ArrayList<String>();
    private boolean status;
    private String exception;
    private int statusCode;
    private Map<String, Object> extraData = new HashMap<>();

}