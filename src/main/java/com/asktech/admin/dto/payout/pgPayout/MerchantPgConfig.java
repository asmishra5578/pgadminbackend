package com.asktech.admin.dto.payout.pgPayout;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = { 
    @Index(columnList = "merchantId"),
    @Index(columnList = "service")
})
public class MerchantPgConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String merchantId;
    private String pgId;
    private String service;
    private String status;
    private String enableRandomName;
}
