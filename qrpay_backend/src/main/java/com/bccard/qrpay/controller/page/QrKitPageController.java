package com.bccard.qrpay.controller.page;

import com.bccard.qrpay.domain.member.MemberService;
import com.bccard.qrpay.domain.qrkit.QrKitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String apply(Model model) {
        return "qr-kit/apply";
    }

    @GetMapping("/qrkit/status")
    public String history(Model model) {
        return "qr-kit/status";
    }


}
