package com.bccard.qrpay.controller.page;

import com.bccard.qrpay.domain.common.code.MemberStatus;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.MerchantService;
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
public class MerchantPageController {

    private final MerchantService merchantService;
    private final MemberService memberService;

    @GetMapping("/merchant/vat")
    public String vatInfo(Model model,
                          @CookieValue(value = "memId", required = false) String memId) {

        if (memId == null) {
            log.info("Needs Authenticate");
            return "auth/login";
        }

        Member member = memberService.findBy(memId).orElseGet(() -> null);
        if (member == null || member.getStatus() != MemberStatus.ACTIVE) {
            log.info("Needs Authenticate");
            return "auth/login";
        }

        Merchant merchant = member.getMerchant();
        model.addAttribute("tipRate", merchant.getTipRate());
        model.addAttribute("vatRate", merchant.getVatRate());
        return "member/master/vat";
    }

    @GetMapping("/merchant/tip")
    public String tipInfo(Model model,
                          @CookieValue(value = "memId", required = false) String memId) {
        Member member = memberService.findBy(memId).orElseGet(() -> null);
        if (member == null || member.getStatus() != MemberStatus.ACTIVE) {
            log.info("Needs Authenticate");
            model.addAttribute("returnKey", "");
            return "auth/login";
        }

        Merchant merchant = member.getMerchant();
        model.addAttribute("tipRate", merchant.getTipRate());
        model.addAttribute("vatRate", merchant.getVatRate());
        return "member/master/tip";
    }


}
