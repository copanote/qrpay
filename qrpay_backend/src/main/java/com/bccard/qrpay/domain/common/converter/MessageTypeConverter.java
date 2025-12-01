package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.MessageType;
import jakarta.persistence.Converter;

@Converter
public class MessageTypeConverter extends DatabaseCodeConverter<MessageType> {
    protected MessageTypeConverter() {
        super(MessageType.class);
    }
}
