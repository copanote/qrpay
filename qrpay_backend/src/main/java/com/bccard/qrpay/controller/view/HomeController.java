package com.bccard.qrpay.controller.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/view")
public class HomeController {

    @GetMapping("/login")
    public String loginView(Model model) {
        model.addAttribute("data", "hello!!");
        return "/home/login";
    }

    @GetMapping("/home/mpmqr")
    public String home(Model model) {
        return "home/mpmqr/main-mpmqr";
    }
}
