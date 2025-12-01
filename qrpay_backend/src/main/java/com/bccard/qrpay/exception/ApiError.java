package com.bccard.qrpay.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@ToString
public class ApiError {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    @Builder
    public ApiError(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
