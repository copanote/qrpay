package com.bccard.qrpay.domain.merchant;

import com.bccard.qrpay.domain.common.code.*;
import com.bccard.qrpay.domain.common.converter.*;
import com.bccard.qrpay.domain.common.entity.BaseEntity;
import com.bccard.qrpay.exception.MerchantException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBMPMMERBASINFO")
public class Merchant extends BaseEntity implements Persistable<String>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MER_MGMT_NO", length = 9, nullable = false)
    private String merchantId;

    @Column(name = "URNK_MER_MGMT_NO", length = 9)
    private String urnkMerMgmtNo;

    @Column(name = "MER_TP_CODE", length = 2)
    private String merTpCode; // ML MS

    @Column(name = "MER_STAT")
    @Convert(converter = MerchantStatusConverter.class)
    private MerchantStatus merchantStatus;

    @Column(name = "MO_LIM_AMT", precision = 15)
    private Long monthlyLimitAmount;

    @Column(name = "ONCE_LIM_AMT", precision = 15)
    private Long singleLimitAmount;

    @Column(name = "REG_PE_CLSS")
    @Convert(converter = MerchantRegisterConverter.class)
    private MerchantRegister merchantRegister;

    @Column(name = "CORR_PE_CLSS")
    @Convert(converter = MerchantRegisterConverter.class)
    private MerchantRegister merchantCorrector;

    @Column(name = "MCC_CODE")
    private String mcc; // mcc

    @Column(name = "MER_NM")
    private String merchantName;

    @Column(name = "MER_ENG_NM")
    private String merchantEnglishName;

    @Column(name = "CTY_NM")
    private String cityName;

    @Column(name = "CTY_ENG_NM")
    private String cityEnglishName;

    @Column(name = "BUZ_NO")
    private String businessNo;

    @Column(name = "MER_DDD_NO")
    private String merchantTelAreaNo;

    @Column(name = "MER_TEL_HNO")
    private String merchantTelMiddleNo;

    @Column(name = "MER_TEL_SNO")
    private String merchantTelLastNo;

    @Column(name = "MER_ZP")
    private String merchantZipCode;

    @Column(name = "RSV_NM")
    private String representativeName;

    @Column(name = "RSV_BTHD")
    private String representativeBirthDay;

    @Column(name = "RSV_EMAIL")
    private String representativeEmail;

    @Column(name = "RCRU_FNST_COPR_CODE")
    @Convert(converter = FinanceInstitutionConverter.class)
    private FinancialInstitution registrationRequestor;

    @Column(name = "SCSS_ATON")
    private String secessionDate;

    @Column(name = "QR_MER_CLSS")
    @Convert(converter = MerchantTypeConverter.class)
    private MerchantType merchantType;

    @Column(name = "SPCR_TYP_NO")
    @Convert(converter = AcquisitionMethodConverter.class)
    private AcquisitionMethod acquisitionMethod;

    @Column(name = "VAT_RT")
    private Long vatRate;

    @Column(name = "SVC_FEE_RT")
    private Long tipRate;

    @Singular
    @OneToMany(mappedBy = "merchant", fetch = FetchType.LAZY)
    private List<FinancialInstitutionMerchant> fiMerchants = new ArrayList<>();

    @Override
    public String getId() {
        return merchantId;
    }

    public void addFinancialInstitute(FinancialInstitutionMerchant financialInstitutionMerchant) {
        this.fiMerchants.add(financialInstitutionMerchant);
    }

    @Builder(builderMethodName = "createNewMerchant")
    public Merchant(
            String merchantId,
            MerchantStatus merchantStatus,
            MerchantType merchantType,
            MerchantRegister merchantRegister,
            String mcc,
            String businessNo,
            String merchantName,
            String merchantEnglishName,
            String cityName,
            String cityEnglishName,
            String merchantZipCode,
            String merchantTelAreaNo,
            String merchantTelMiddleNo,
            String merchantTelLastNo,
            String representativeName,
            String representativeBirthDay,
            String representativeEmail,
            FinancialInstitution registrationRequestor,
            AcquisitionMethod acquisitionMethod) {
        this.merchantId = merchantId;
        this.merchantStatus = merchantStatus;
        this.merchantType = merchantType;
        this.merchantRegister = merchantRegister;
        this.mcc = mcc;
        this.businessNo = businessNo;
        this.merchantName = merchantName;
        this.merchantEnglishName = merchantEnglishName;
        this.cityName = cityName;
        this.cityEnglishName = cityEnglishName;
        this.merchantZipCode = merchantZipCode;
        this.merchantTelAreaNo = merchantTelAreaNo;
        this.merchantTelMiddleNo = merchantTelMiddleNo;
        this.merchantTelLastNo = merchantTelLastNo;
        this.representativeName = representativeName;
        this.representativeBirthDay = representativeBirthDay;
        this.representativeEmail = representativeEmail;
        this.registrationRequestor = registrationRequestor;
        this.acquisitionMethod = acquisitionMethod;

        // 고정값
        this.urnkMerMgmtNo = merchantId;
        this.merTpCode = "ML";
        this.monthlyLimitAmount = 10000000L;
        this.singleLimitAmount = 1000000L;
    }

    public static final int MAX_NAME_LENGTH = 14;

    public void updateMerchantName(String merchantName) {

        if (merchantName.length() > MAX_NAME_LENGTH) {
            throw new MerchantException(QrpayErrorCode.MERCHANT_NAME_LENGTH_POLICY_VIOLATION);
        }

        if (!this.getMerchantName().equals(merchantName)) {
            this.merchantName = merchantName;
        }
    }

    public void updateVat(Long vatRate) {

        if (vatRate == null) {
            this.vatRate = null;
            return;
        }

        if (vatRate > 99 || vatRate < 0) {
            throw new MerchantException(QrpayErrorCode.MERCHANT_VAT_POLICY_VIOLATION);
        }

        Long tip = this.tipRate == null ? 0L : this.tipRate;
        if (tip + vatRate >= 100) {
            throw new MerchantException(QrpayErrorCode.MERCHANT_VAT_TIP_SUM_POLICY_VIOLATION);
        }
        this.vatRate = vatRate;
    }

    public void updateTip(Long tipRate) {
        if (tipRate == null) {
            this.tipRate = null;
            return;
        }

        if (tipRate > 99 || tipRate < 0) {
            throw new MerchantException(QrpayErrorCode.MERCHANT_TIP_POLICY_VIOLATION);
        }

        Long vat = this.vatRate == null ? 0L : this.vatRate;
        if (vat + tipRate >= 100) {
            throw new MerchantException(QrpayErrorCode.MERCHANT_VAT_TIP_SUM_POLICY_VIOLATION);
        }
        this.tipRate = tipRate;
    }

    public void cancel() {
        if (MerchantStatus.CANCELLED != this.merchantStatus) {
            this.merchantStatus = MerchantStatus.CANCELLED;
            this.secessionDate = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_DATE);
        }
    }
}
