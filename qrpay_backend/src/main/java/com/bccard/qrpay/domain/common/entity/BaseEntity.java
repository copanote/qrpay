package com.bccard.qrpay.domain.common.entity;

import com.bccard.qrpay.utils.MpmDateTimeUtils;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedBy
    @Column(name = "REG_PE_ID", length = 40, updatable = false)
    protected String createdBy;

    @Column(name = "REG_ATON", length = 14, updatable = false)
    protected String createdAt;

    @Column(name = "CORR_PE_ID", length = 40)
    @LastModifiedBy
    protected String lastModifiedBy;

    @Column(name = "CORR_ATON", length = 14)
    protected String lastModifiedAt;

    @PrePersist
    protected void onPrePersist() {
        createdAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC);
        lastModifiedAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC);
    }

    @PreUpdate
    protected void onPreUpdate() {
        lastModifiedAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_SEC);
    }

    public boolean isNew() {
        return this.createdAt == null;
    }
}
