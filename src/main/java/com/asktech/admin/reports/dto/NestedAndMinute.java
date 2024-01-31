package com.asktech.admin.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NestedAndMinute {
    private String minute;
    private String count;
    private String total;
}
