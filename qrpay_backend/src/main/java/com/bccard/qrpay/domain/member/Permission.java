package com.bccard.qrpay.domain.member;


import lombok.Getter;

@Getter
public enum Permission {
    CANCEL("cancel"),
    ;

    Permission(String name) {
        this.name = name;
    }

    private final String name;

}
