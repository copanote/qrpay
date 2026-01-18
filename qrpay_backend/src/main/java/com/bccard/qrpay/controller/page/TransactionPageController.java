package com.bccard.qrpay.controller.page;


import com.bccard.qrpay.controller.page.dto.HomeMpmqrNavi;
import com.bccard.qrpay.controller.page.dto.HomeMpmqrResDto;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.merchant.Merchant;
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
public class TransactionPageController {

    private final MemberService memberService;

    @GetMapping("/transaction/list")
    public String list(Model model,
                       @CookieValue(value = "memId", required = false) String memId) {
        Member member = memberService.findBy(memId).orElseGet(() -> null);
        if (member == null) {
            log.info("Needs Authenticate");
            return "auth/login";
        }
        Merchant merchant = member.getMerchant();

        HomeMpmqrNavi mpmqrNavi = HomeMpmqrNavi.builder()
                .merchantName(merchant.getMerchantName())
                .loginId(member.getLoginId())
                .isAdmin(member.getRole() == MemberRole.MASTER)
                .build();

        HomeMpmqrResDto homeMpmqrResDto = HomeMpmqrResDto
                .builder()
                .homeMpmqrNavi(mpmqrNavi)
                .build();
        model.addAttribute("homeMpmqrResDto", homeMpmqrResDto);
        return "transaction/list";
    }

    @GetMapping("/transaction/monthly-sales")
    public String monthly_sales(Model model,
                                @CookieValue(value = "memId", required = false) String memId) {
        Member member = memberService.findBy(memId).orElseGet(() -> null);
        if (member == null) {
            log.info("Needs Authenticate");
            return "auth/login";
        }
        Merchant merchant = member.getMerchant();

        HomeMpmqrNavi mpmqrNavi = HomeMpmqrNavi.builder()
                .merchantName(merchant.getMerchantName())
                .loginId(member.getLoginId())
                .isAdmin(member.getRole() == MemberRole.MASTER)
                .build();

        HomeMpmqrResDto homeMpmqrResDto = HomeMpmqrResDto
                .builder()
                .homeMpmqrNavi(mpmqrNavi)
                .build();
        model.addAttribute("homeMpmqrResDto", homeMpmqrResDto);
        return "transaction/monthly-sales";
    }


}
