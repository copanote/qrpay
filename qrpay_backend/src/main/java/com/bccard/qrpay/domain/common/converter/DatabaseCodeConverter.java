package com.bccard.qrpay.domain.common.converter;

import com.bccard.qrpay.domain.common.code.DatabaseCodeConvertable;
import jakarta.persistence.AttributeConverter;
import java.util.EnumSet;

public abstract class DatabaseCodeConverter<E extends Enum<E> & DatabaseCodeConvertable>
        implements AttributeConverter<E, String> {

    private final Class<E> target;

    protected DatabaseCodeConverter(Class<E> target) {
        this.target = target;
    }

    @Override
    public String convertToDatabaseColumn(E e) {
        if (e == null) return null;

        return e.getDbCode();
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return EnumSet.allOf(target).stream()
                .filter(e -> e.getDbCode().equals(dbData))
                .findAny()
                .orElse(null);
    }
}
