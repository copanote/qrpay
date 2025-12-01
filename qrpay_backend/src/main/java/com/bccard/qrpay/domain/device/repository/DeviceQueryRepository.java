package com.bccard.qrpay.domain.device.repository;

import com.bccard.qrpay.domain.device.Device;
import com.bccard.qrpay.domain.device.QDevice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class DeviceQueryRepository {
    @PersistenceContext
    private EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public DeviceQueryRepository(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    private static final QDevice device = QDevice.device;

    public Optional<Device> findByMemberId(String memberId) {
        Device d = queryFactory
                .selectFrom(device)
                .where(device.memberId.eq(memberId))
                .fetchFirst();
        return Optional.ofNullable(d);
    }
}
