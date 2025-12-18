package com.bccard.qrpay.controller.page;


import com.bccard.qrpay.auth.service.AuthService;
import com.bccard.qrpay.controller.page.dto.HomeMpmqrMain;
import com.bccard.qrpay.controller.page.dto.HomeMpmqrNavi;
import com.bccard.qrpay.controller.page.dto.HomeMpmqrResDto;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.MemberStatus;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.MerchantService;
import com.bccard.qrpay.domain.mpmqr.EmvMpmQrService;
import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;
import com.bccard.qrpay.utils.ZxingQrcode;
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
public class HomeController {

    private final AuthService authService;
    private final MemberService memberService;
    private final MerchantService merchantService;
    private final EmvMpmQrService emvMpmQrService;

    @GetMapping("/login")
    public String loginView(Model model) {
        model.addAttribute("data", "hello!!");
        return "/home/login";
    }

    @GetMapping("/home/mpmqr")
    public String home(
//            @CookieValue(value = "accessToken", required = false) String accessToken,
//            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            Model model,
            @CookieValue(value = "memId", required = false) String memId
    ) throws Exception {
        if (memId == null || memId.isBlank()) {
            //Needs Authenticate
            return "/home/login";
        }
        Member member = memberService.findBy(memId).orElseGet(() -> null);
        if (member == null || member.getStatus() != MemberStatus.ACTIVE) {
            log.info("Needs Authenticate");
            return "/home/login";
        }

        Merchant merchant = member.getMerchant();
        MpmQrPublication staticMpmQrOrCreate = emvMpmQrService.findStaticMpmQrOrCreate(memId, merchant);
        String qrImage = ZxingQrcode.base64EncodedQrImageForQrpay(staticMpmQrOrCreate.getQrData());

        HomeMpmqrMain mpmqrMain = HomeMpmqrMain.builder()
                .merchantName(merchant.getMerchantName())
                .qrBase64Image(qrImage)
                .isAdmin(member.getRole() == MemberRole.MASTER)
                .build();

        HomeMpmqrNavi mpmqrNavi = HomeMpmqrNavi.builder()
                .merchantName(merchant.getMerchantName())
                .loginId(member.getLoginId())
                .isAdmin(member.getRole() == MemberRole.MASTER)
                .build();

        HomeMpmqrResDto homeMpmqrResDto = HomeMpmqrResDto
                .builder()
                .homeMpmqrMain(mpmqrMain)
                .homeMpmqrNavi(mpmqrNavi)
                .build();

        log.info("mpmqr/main-mpmqr");

        model.addAttribute("homeMpmqrResDto", homeMpmqrResDto);
        return "/home/mpmqr/main-mpmqr";
    }


}
