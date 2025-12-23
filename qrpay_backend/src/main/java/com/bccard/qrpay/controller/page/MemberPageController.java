package com.bccard.qrpay.controller.page;

import com.bccard.qrpay.domain.common.code.MemberStatus;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/pages")
public class MemberPageController {

    private final MemberService memberService;


    @GetMapping("/member/employee/add")
    public String employee_add(Model model) {
        return "member/employee/employee-add";
    }

    @GetMapping("/member/employee/list")
    public String employee_list(Model model,
                                @CookieValue(value = "memId", required = false) String memId) {
        Member member = memberService.findBy(memId).orElseGet(() -> null);
        if (member == null || member.getStatus() != MemberStatus.ACTIVE) {
            log.info("Needs Authenticate");
            return "/home/login";
        }

        model.addAttribute("authLoginId", member.getLoginId());
        model.addAttribute("authMemberId", member.getMemberId());

        return "member/employee/employee-list";
    }

    @GetMapping("/member/{memberId}/employee/change-pw")
    public String employee_pw_change(Model model,
                                     @PathVariable(value = "memberId") String memberId) {

        Member byMemberId = memberService.findByMemberId(memberId);
        model.addAttribute("toChangePwLoginId", byMemberId.getLoginId());
        model.addAttribute("toChangePwMemberId", byMemberId.getMemberId());
        return "member/employee/employee-change-password";
    }


}
