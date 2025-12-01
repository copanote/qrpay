package com.bccard.qrpay.domain.device;

import com.bccard.qrpay.domain.common.code.DeviceType;
import com.bccard.qrpay.domain.common.converter.BooleanYnConverter;
import com.bccard.qrpay.domain.common.converter.DeviceTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.*;

@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class DeviceMeta {

    @Column(name = "PROOF_KEY_VAL", length = 128)
    private String deviceToken;

    @Column(name = "DEVI_TYPE")
    @Convert(converter = DeviceTypeConverter.class)
    private DeviceType deviceType = DeviceType.UNKNOWN;

    @Column(name = "MOBIL_OS_NM", length = 40)
    private String osName;

    @Column(name = "MODL_NM", length = 40)
    private String modelName;

    @Column(name = "DEVI_ID", length = 128)
    private String deviceId;

    @Column(name = "APP_VER", length = 20)
    private String appVersion;

    @Column(name = "DEVI_VAL", length = 256)
    private String devicePushToken;

    @Column(name = "PUSH_RECP_YN", length = 1)
    @Convert(converter = BooleanYnConverter.class)
    private Boolean pushReceive;

    @Builder
    public DeviceMeta(
            String deviceToken,
            DeviceType deviceType,
            String osName,
            String modelName,
            String deviceId,
            String appVersion,
            String devicePushToken,
            Boolean pushReceive) {
        this.deviceToken = deviceToken;
        this.deviceType = deviceType;
        this.osName = osName;
        this.modelName = modelName;
        this.deviceId = deviceId;
        this.appVersion = appVersion;
        this.devicePushToken = devicePushToken;
        this.pushReceive = pushReceive;
    }
}
