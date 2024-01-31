package com.asktech.admin.dto.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessSettlementRequest {

	private List<ProcessSettlement> processSettlement;
}
