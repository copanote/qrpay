package com.bccard.qrpay.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ViewExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {

        log.info("ControllerAdvice");

        //        log.error(e);
        model.addAttribute("error", "데이터가 존재하지 않습니다.");
        return "error/404"; // error/400.html (또는 .jsp) 뷰 반환
    }
}
