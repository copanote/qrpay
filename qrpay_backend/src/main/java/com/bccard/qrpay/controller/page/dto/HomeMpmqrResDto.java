package com.bccard.qrpay.controller.page.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeMpmqrResDto {
    private HomeMpmqrMain homeMpmqrMain;
    private HomeMpmqrNavi homeMpmqrNavi;
}
