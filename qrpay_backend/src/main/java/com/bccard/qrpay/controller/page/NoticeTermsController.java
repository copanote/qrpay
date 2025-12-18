package com.bccard.qrpay.controller.page;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pages")
public class NoticeTermsController {

    @GetMapping("/settings/notice")
    public String notice(Model model) {
        return "settings/notices/notice";
    }

    @GetMapping("/settings/guide")
    public String guide(Model model) {
        return "settings/guides/guide";
    }

    @GetMapping("/settings/terms-service")
    public String terms_service(Model model) {
        return "settings/terms/terms-service";
    }

    @GetMapping("/settings/terms-service/terms")
    public String terms(Model model) {
        return "settings/terms/details/terms";
    }

    @GetMapping("/settings/terms-service/permissions")
    public String permission(Model model) {
        //TODO AOS/IOS 판별
        return "settings/terms/details/app-permission-aos";
    }

    @GetMapping("/settings/terms-service/cancel")
    public String cancel(Model model) {
        //TODO AOS/IOS 판별
        return "settings/terms/details/service-cancel";
    }

}
