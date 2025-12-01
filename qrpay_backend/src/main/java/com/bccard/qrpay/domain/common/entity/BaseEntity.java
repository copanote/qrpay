package com.bccard.qrpay.domain.common.entity;

import com.bccard.qrpay.utils.MpmDateTimeUtils;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntity {
    @Column(name = "REG_PE_ID", length = 40, updatable = false)
    protected String createdBy;

    @Column(name = "REG_ATON", length = 14, updatable = false)
    protected String createdAt;

    @Column(name = "CORR_PE_ID", length = 40)
    protected String lastModifiedBy;

    @Column(name = "CORR_ATON", length = 14)
    protected String lastModifiedAt;

    @PrePersist
    protected void onPrePersist() {
        createdAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC);
        lastModifiedAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC);
        // TODO
        createdBy = "QRPAY";
        lastModifiedBy = "QRPAY";
    }

    @PreUpdate
    protected void onPreUpdate() {
        lastModifiedAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC);
        lastModifiedBy = "QRPAY";
    }

    public boolean isNew() {
        return this.createdAt == null;
    }
}
