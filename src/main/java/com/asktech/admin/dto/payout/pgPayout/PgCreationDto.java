package com.asktech.admin.dto.payout.pgPayout;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PgCreationDto {
    private String pgName;
    private String pgStatus;
    private String pgConfigKey;
    private String pgConfigSecret;
    private String pgConfig1;
    private String pgConfig2;
    private String pgConfig3;
}
