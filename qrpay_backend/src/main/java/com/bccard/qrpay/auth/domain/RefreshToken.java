package com.bccard.qrpay.auth.domain;


import com.bccard.qrpay.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String memberId;


    @Column(nullable = false, length = 512)
    private String tokenHash;

    @Column(nullable = false, length = 20)
    private Long issuedAt;

    @Column(nullable = false, length = 20)
    private Long expiresAt;

    private boolean revoked = false;
    private String revokeReason;
    @Column(length = 20)
    private Long revokedAt;

    private String deviceId;

    @Column(length = 20)
    private Long lastUsedAt;

    @Builder(builderMethodName = "createNew")
    public RefreshToken(String memberId, String tokenHash, Long issuedAt,
                        Long expiresAt, String deviceId) {
        this.memberId = memberId;
        this.tokenHash = tokenHash;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revoked = false;
        this.deviceId = deviceId;
    }

    public void revoke(String revokeReason) {
        if (revoked) {
            return;
        }

        revoked = true;
        this.revokeReason = revokeReason;
        revokedAt = Instant.now().toEpochMilli();
    }

    public void refresh() {
        lastUsedAt = Instant.now().toEpochMilli();
    }

    public boolean isValid() {
        return !revoked && Instant.now().toEpochMilli() < expiresAt;
    }
    
}
