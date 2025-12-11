package com.bccard.qrpay.domain.device;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class DeviceAccessToken {
    @Column(name = "API_CONN_TKN_DATA", length = 256)
    private String apiAccessToken;

    @Column(name = "API_TKN_VALD_TIME_NUM", length = 12)
    private Long apiAccessTokenExpires;

    @Column(name = "API_RNEW_TKN_DATA", length = 256)
    private String apiRefreshToken;

    @Column(name = "API_CONN_EXPL_ATON", length = 14)
    private String apiAccessExpireDate;

    @Builder
    public DeviceAccessToken(String apiAccessToken, Long apiAccessTokenExpires, String apiRefreshToken, String apiAccessExpireDate) {
        this.apiAccessToken = apiAccessToken;
        this.apiAccessTokenExpires = apiAccessTokenExpires;
        this.apiRefreshToken = apiRefreshToken;
        this.apiAccessExpireDate = apiAccessExpireDate;
    }
}
