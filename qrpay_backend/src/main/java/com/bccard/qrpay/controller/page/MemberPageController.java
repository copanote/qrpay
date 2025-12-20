package com.bccard.qrpay.controller.page;

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
public class MemberPageController {


    @GetMapping("/member/employee/add")
    public String employee_add(Model model) {
        return "member/employee/employee-add";
    }

    @GetMapping("/member/employee/list")
    public String employee_list(Model model) {
        return "member/employee/employee-list";
    }

    @GetMapping("/member/employee/change-pw")
    public String employee_pw_change(Model model) {
        return "member/employee/employee-change-password";
    }


}
