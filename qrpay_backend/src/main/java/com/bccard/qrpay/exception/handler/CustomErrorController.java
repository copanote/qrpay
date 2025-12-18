package com.bccard.qrpay.exception.handler;

import com.bccard.qrpay.exception.ApiError;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;


@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {
        String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Throwable ex = errorAttributes.getError(new ServletWebRequest(request));
        HttpStatus httpStatus = getStatus(request);

        log.info("path={}, ex={}, exMsg={}, httpStatus={} ", path, ex.getClass().getSimpleName(), ex.getMessage(), httpStatus);
        if (path != null && path.startsWith("/pages")) {
            int status = httpStatus.value();

            // templates/error/customErrorPage.html
            return "error/" + status;
        } else {
            int status = httpStatus.value();
            ApiError apiError = ApiError.builder()
                    .status(status)
                    .code(String.valueOf(status))
                    .message(ex.getMessage())
                    .build();
            return ResponseEntity.status(status).body(apiError);
        }
    }

    public String getErrorPath() {
        return "/error";
    }

    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception var4) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }

}
