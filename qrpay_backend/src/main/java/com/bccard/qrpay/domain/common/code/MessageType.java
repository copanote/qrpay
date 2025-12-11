package com.bccard.qrpay.domain.common.code;


import lombok.Getter;

@Getter
public enum MessageType implements DatabaseCodeConvertable {
    INBOUND_REQUEST("1"),
    INBOUND_RESPONSE("1"),
    OUTBOUND_RESPONSE("2"),
    OUTBOUND_REQUEST("2"),
    ;

    private final String dbCode;

    MessageType(String dbCode) {
        this.dbCode = dbCode;
    }

    public static MessageType of(String dbCode) {
        for (MessageType mt : MessageType.values()) {
            if (mt.getDbCode().equalsIgnoreCase(dbCode)) {
                return mt;
            }
        }
        return null;
    }

}
