package com.asktech.admin.customInterface;

import java.util.List;

import com.asktech.admin.reports.dto.NestedAndHour;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListStatusHourWise {
    
    private List<NestedAndHour> success;
    private List<NestedAndHour> failed;
    private List<NestedAndHour> pending;
}
