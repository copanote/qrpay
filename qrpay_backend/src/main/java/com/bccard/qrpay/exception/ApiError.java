package com.bccard.qrpay.exception;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiError {
    private int status;
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    @Builder
    public ApiError(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
