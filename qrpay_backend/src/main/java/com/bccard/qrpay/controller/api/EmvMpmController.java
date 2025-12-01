package com.bccard.qrpay.controller.api;

import com.bccard.qrpay.controller.api.dtos.EmvMpmQrPublishInDto;
import com.bccard.qrpay.controller.api.dtos.EmvMpmQrPublishOutDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emvmpm")
public class EmvMpmController {

    //	@Autowired private EmvMpmService emvMpmService;
    //	@Autowired private FIMerchantId.MerchantService ms;

    @PostMapping(value = "/publish")
    @ResponseBody
    public EmvMpmQrPublishOutDto publish(@RequestBody EmvMpmQrPublishInDto inDto) {

        /*
         *  1. Request Parameter Validation
         */

        String merchantId = inDto.getAffiCoReqVal();
        /*
         *  2. Retrieve merchant by request
         */
        //		Optional<Merchant> m = ms.findById(merchantId);
        //		Merchant merchant = m.orElseThrow();

        /*
         *  3. Make EmvMpm QR data
         */

        /*
         *  4. Regist Mpm QR
         */

        /*
         * 5. Response out dto
         */
        EmvMpmQrPublishOutDto outDto = new EmvMpmQrPublishOutDto();
        return outDto;
    }
}
