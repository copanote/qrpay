package com.bccard.qrpay.domain.member;

import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.MemberStatus;
import com.bccard.qrpay.domain.common.converter.BooleanYnConverter;
import com.bccard.qrpay.domain.common.converter.MemberRoleConverter;
import com.bccard.qrpay.domain.common.converter.MemberStatusConverter;
import com.bccard.qrpay.domain.common.entity.BaseEntity;
import com.bccard.qrpay.domain.device.Device;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.exception.MemberException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBMPMMERCDHDINFO")
public class Member extends BaseEntity implements Persistable<String> {
    @Id
    @Column(name = "MER_CDHD_NO", length = 9, nullable = false)
    private String memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MER_MGMT_NO")
    private Merchant merchant;

    @Column(name = "MER_CDHD_ID", length = 40)
    private String loginId;

    @Column(name = "EPASWD", length = 64, nullable = false)
    private String hashedPassword;

    @Column(name = "BAS_YN")
    @Convert(converter = MemberRoleConverter.class)
    private MemberRole role = MemberRole.UNDEFINED;

    @Column(name = "CDHD_STAT", nullable = false)
    @Convert(converter = MemberStatusConverter.class)
    private MemberStatus status = MemberStatus.UNKNOWN;

    @Column(name = "RC_LOGIN_ATON", length = 14)
    private String lastLoginAt;

    @Column(name = "PASWD_ERR_CNT", length = 12)
    private Long passwordErrorCount;

    @Column(name = "PASWD_ERR_DATE", length = 8)
    private String passwordErrorAt;

    @Column(name = "PASWD_FNL_CHNG_ATON", length = 14)
    private String passwordChangedAt;

    @Column(name = "STIP_CNST_INFO", length = 100)
    private String termsAgreeInfo;

    @Column(name = "AUTH_CNCL_ABLE_YN", length = 1)
    @Convert(converter = BooleanYnConverter.class)
    private Boolean permissionToCancel;

    @Column(name = "AFFI_CO_ID", length = 40)
    private String affiCoId; //BCQRCPAY

    @Column(name = "RCRU_PE_NO", length = 10)
    private String referrerId;

    @Column(name = "STIP_CNST_ATON", length = 14)
    private String termsAgreedAt;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "SCSS_ATON", length = 14)
    private String withdrawalAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MER_CDHD_NO")
    private Device device;

    @Override
    public String toString() {
        return "Member{" +
                "memberId='" + memberId + '\'' +
                ", loginId='" + loginId + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", lastLoginAt='" + lastLoginAt + '\'' +
                ", passwordErrorCount=" + passwordErrorCount +
                ", passwordErrorAt='" + passwordErrorAt + '\'' +
                ", passwordChangedAt='" + passwordChangedAt + '\'' +
                ", termsAgreeInfo='" + termsAgreeInfo + '\'' +
                ", authCnclAbleYn=" + permissionToCancel +
                ", affiCoId='" + affiCoId + '\'' +
                ", referrerId='" + referrerId + '\'' +
                ", termsAgreedAt='" + termsAgreedAt + '\'' +
                ", email='" + email + '\'' +
                ", withdrawalAt='" + withdrawalAt + '\'' +
                ", device=" + device +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedAt='" + lastModifiedAt + '\'' +
                '}';
    }

    @Override
    public String getId() {
        return memberId;
    }

    @Builder(builderMethodName = "createEmployee")
    public Member(String memberId, Merchant merchant, String loginId, String hashedPassword, Boolean permissionToCancel) {
        this.memberId = memberId;
        this.merchant = merchant;
        this.loginId = loginId;
        this.hashedPassword = hashedPassword;
        this.permissionToCancel = permissionToCancel;

        this.role = MemberRole.EMPLOYEE;
        this.status = MemberStatus.ACTIVE;
        this.affiCoId = "BCQRCPAY";
    }

    @Builder(builderMethodName = "createMasterMemeber")
    public Member(String memberId, Merchant merchant, String loginId, String hashedPassword, String termsAgreeInfo, String referrerId, String email) {
        this.memberId = memberId;
        this.merchant = merchant;
        this.loginId = loginId;
        this.hashedPassword = hashedPassword;
        this.termsAgreeInfo = termsAgreeInfo;
        this.referrerId = referrerId;
        this.email = email;


        this.role = MemberRole.MASTER;
        this.status = MemberStatus.ACTIVE;
        this.termsAgreedAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.FORMATTER_YEAR_TO_SEC);
        this.permissionToCancel = Boolean.TRUE;
        this.affiCoId = "BCQRCPAY";
    }


    public void onPasswordFail() {
        passwordErrorAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_DATE);
        passwordErrorCount += 1;
    }

    public void updatePermissionToCancel(boolean permissionToCancel) {
        this.permissionToCancel = permissionToCancel;
    }

    public void updateStatus(MemberStatus status) {
        if (this.status == MemberStatus.CANCELLED) {
            throw new MemberException(QrpayErrorCode.MEMBER_STATUS_CHANGE_NOT_ALLOWED);
        }
        this.status = status;
    }

    public void updatePassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
        this.passwordChangedAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC);
    }

    public void cancel() {
        if (this.status != MemberStatus.CANCELLED) {
            this.withdrawalAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_DATE);
            this.status = MemberStatus.CANCELLED;
        }
    }

}

/**
 * 이름                  널        유형
 * ------------------- -------- -------------
 * MER_CDHD_NO         NOT NULL VARCHAR2(9)
 * MER_MGMT_NO         NOT NULL VARCHAR2(9)
 * MER_CDHD_ID                  VARCHAR2(40)
 * EPASWD              NOT NULL VARCHAR2(64)
 * BAS_YN                       VARCHAR2(1)
 * CDHD_STAT           NOT NULL VARCHAR2(2)
 * REG_PE_ID                    VARCHAR2(40)
 * REG_ATON                     CHAR(14)
 * CORR_PE_ID                   VARCHAR2(40)
 * CORR_ATON                    CHAR(14)
 * RC_LOGIN_ATON                CHAR(14)
 * PASWD_ERR_CNT                NUMBER(12)
 * PASWD_ERR_DATE               CHAR(8)
 * PASWD_FNL_CHNG_ATON          CHAR(14)
 * STIP_CNST_INFO               VARCHAR2(100)
 * AUTH_CNCL_ABLE_YN            VARCHAR2(1)
 * AFFI_CO_ID                   VARCHAR2(40)
 * RCRU_PE_NO                   VARCHAR2(10)
 * STIP_CNST_ATON               CHAR(14)
 * EMAIL                        VARCHAR2(100)
 * SCSS_ATON                    CHAR(14)
 */
