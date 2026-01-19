package com.bccard.qrpay.domain.auth.repository;

import com.bccard.qrpay.domain.auth.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Modifying
    @Transactional
    @Query(
            "UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :revokedAt, rt.revokeReason = :revokeReason WHERE rt.memberId = :memberId and rt.revoked <> true")
    int revokeAllRefreshTokenByMemberId(
            @Param("memberId") String memberId,
            @Param("revokedAt") Long revokedAt,
            @Param("revokeReason") String revokeReason);
}
