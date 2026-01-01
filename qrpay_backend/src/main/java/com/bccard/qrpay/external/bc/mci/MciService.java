package com.bccard.qrpay.external.bc.mci;

import com.bccard.qrpay.controller.api.dtos.BcMerchantInfo;
import com.bccard.qrpay.external.bc.mci.dto.MCDQCOOAMO01561ResDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class MciService {

    public MCDQCOOAMO01561ResDto mockSearchBcMerchantDetails(String bcMerchantNo) {
        MCDQCOOAMO01561ResDto resDto = MCDQCOOAMO01561ResDto.builder()
                .rsvrCiV88("snNuXx097WLeqIGmDH4St7hB3p9JZSaIw+s87affJeg+PqQvZ/oMELzMjX/6/RuT8uzFLvup0mSIh+lFzke7rg==")
                .build();
        return resDto;
    }


    public List<BcMerchantInfo> mockBcMerchantList(String bizNo, String nextKey) {
        int pageSize = 10;
        List<BcMerchantInfo> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(BcMerchantInfoGenerator.createRandomMerchant());
        }
        return list;
    }


}
