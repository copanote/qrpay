package com.bccard.qrpay.controller.api.dtos;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BcMerchantResDto {
    private boolean hasNext;
    private String nextKey;
    private int size;
    private List<BcMerchantInfo> list;
}
