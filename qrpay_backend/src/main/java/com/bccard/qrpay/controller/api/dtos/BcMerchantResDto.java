package com.bccard.qrpay.controller.api.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BcMerchantResDto {
    private boolean hasNext;
    private String nextKey;
    private int size;
    private List<BcMerchantInfo> list;
}
