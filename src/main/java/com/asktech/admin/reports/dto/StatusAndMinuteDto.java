package com.asktech.admin.reports.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusAndMinuteDto {
    private List<NestedAndMinute> success;
    private List<NestedAndMinute> failed;
    private List<NestedAndMinute> pending;
}
