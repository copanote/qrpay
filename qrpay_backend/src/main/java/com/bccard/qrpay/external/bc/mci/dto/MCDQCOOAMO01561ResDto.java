package com.bccard.qrpay.external.bc.mci.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MCDQCOOAMO01561ResDto {
    private String rspnCdV1;
    private String merNmV60;
    private String rsvrNmV20;
    private String biznoV10;
    private String rsvrCiV88;
}
