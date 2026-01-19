package com.bccard.qrpay.controller.page;

import com.bccard.qrpay.controller.page.dto.QrKitApplyPageDto;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.qrkit.MpmQrKitApplication;
import com.bccard.qrpay.domain.qrkit.QrKitService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/pages")
public class QrKitPageController {

    private final MemberService memberService;
    private final QrKitService qrKitService;

    @GetMapping("/qrkit/apply")
    public String apply(Model model, @CookieValue(value = "memId", required = false) String memId) {

        Member member = memberService.findBy(memId).orElseGet(() -> null);
        if (member == null) {
            log.info("Needs Authenticate");
            return "auth/login";
        }

        Merchant merchant = member.getMerchant();

        QrKitApplyPageDto resDto = QrKitApplyPageDto.builder()
                .merchantId(merchant.getMerchantId())
                .merchantName(merchant.getMerchantName())
                .build();

        List<MpmQrKitApplication> mpmQrKitApplications = qrKitService.find(member);
        if (!mpmQrKitApplications.isEmpty()) {
            MpmQrKitApplication first = mpmQrKitApplications.getFirst();
            resDto = QrKitApplyPageDto.builder()
                    .merchantId(merchant.getMerchantId())
                    .merchantName(merchant.getMerchantName())
                    .phoneNo(first.getPhoneNo())
                    .address1(first.getAddress1())
                    .address2(first.getAddress2())
                    .postNo(first.getZipCode())
                    .build();
        }

        log.info("qrkitApplyPageDto={}", resDto);

        model.addAttribute("qrkitApplyPageDto", resDto);
        return "qr-kit/apply";
    }

    @GetMapping("/qrkit/status")
    public String history(Model model) {
        return "qr-kit/status";
    }
}
