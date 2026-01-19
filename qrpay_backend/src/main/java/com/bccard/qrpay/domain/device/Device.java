package com.bccard.qrpay.domain.device;

import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.converter.MemberRoleConverter;
import com.bccard.qrpay.domain.common.entity.BaseEntity;
import com.bccard.qrpay.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBMPMMERCDHDDEVIINFO")
@DynamicUpdate
public class Device extends BaseEntity implements Persistable<String> {

    @Id
    @Column(name = "MER_CDHD_NO", length = 9, nullable = false)
    private String memberId;

    @Column(name = "MER_MGMT_NO", length = 9, nullable = false)
    private String merchantId;

    @Column(name = "MER_CDHD_ID", length = 40)
    private String loginId;

    @Column(name = "BAS_YN")
    @Convert(converter = MemberRoleConverter.class)
    private MemberRole memberRole = MemberRole.UNDEFINED;

    @Embedded
    private DeviceMeta deviceMeta;

    @Embedded
    private DeviceAccessToken apiAccessToken;

    @Override
    public String getId() {
        return memberId;
    }

    @Builder(builderMethodName = "createNewDevice")
    public Device(Member member, DeviceMeta deviceMeta, DeviceAccessToken deviceAccessToken) {
        this.memberId = member.getMemberId();
        this.merchantId = member.getMerchant().getMerchantId();
        this.loginId = member.getLoginId();
        this.memberRole = member.getRole();
        this.deviceMeta = deviceMeta;
        this.apiAccessToken = deviceAccessToken;
    }

    public void changeDeviceMeta(DeviceMeta updatedMeta) {
        deviceMeta = updatedMeta;
    }

    public void changeAccessToken(DeviceAccessToken updatedAccessToken) {
        apiAccessToken = updatedAccessToken;
    }
}

/**
 *
 * 이름                    널        유형
 * --------------------- -------- -------------
 * MER_CDHD_NO           NOT NULL VARCHAR2(9)
 * MER_MGMT_NO           NOT NULL VARCHAR2(9)
 * MER_CDHD_ID                    VARCHAR2(40)
 * BAS_YN                         VARCHAR2(1)
 * REG_PE_ID                      VARCHAR2(40)
 * REG_ATON                       CHAR(14)
 * CORR_PE_ID                     VARCHAR2(40)
 * CORR_ATON                      CHAR(14)
 * PROOF_KEY_VAL                  VARCHAR2(128)
 * DEVI_TYPE                      VARCHAR2(2)
 * MOBIL_OS_NM                    VARCHAR2(40)
 * MODL_NM                        VARCHAR2(40)
 * DEVI_VAL                       VARCHAR2(256)
 * DEVI_ID                        VARCHAR2(128)
 * APP_VER                        VARCHAR2(20)
 * PUSH_RECP_YN                   VARCHAR2(1)
 * API_CONN_TKN_DATA              VARCHAR2(256)
 * API_TKN_VALD_TIME_NUM          NUMBER(12)
 * API_RNEW_TKN_DATA              VARCHAR2(256)
 * API_CONN_EXPI_ATON             CHAR(14)
 *
 */
