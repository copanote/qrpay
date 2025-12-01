package com.bccard.qrpay.domain.log;

import com.bccard.qrpay.utils.MpmStringUtils;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

@Embeddable
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class LogMessage {

    private static final int BODY_LENGTH = 4000;
    
    private String standardHeader;
    private String header;
    private String body;

    @Builder(builderMethodName = "create")
    public LogMessage(String standardHeader, String header, String body) {
        this.standardHeader = standardHeader;
        this.header = header;
        this.body = body;

        if (!StringUtils.isEmpty(this.body) && this.body.getBytes(StandardCharsets.UTF_8).length > BODY_LENGTH) {
            this.body = MpmStringUtils.safeSubstringByBytes(this.body, BODY_LENGTH, StandardCharsets.UTF_8);
        }
    }
}
