package com.bccard.qrpay.domain.device;

import com.bccard.qrpay.domain.common.code.DeviceType;
import com.bccard.qrpay.domain.device.repository.DeviceQueryRepository;
import com.bccard.qrpay.domain.device.repository.DeviceRepository;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.merchant.Merchant;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class DeviceRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceQueryRepository deviceQueryRepository;

    @Test
    void test_save() {

        Merchant merchant = Merchant.createNewMerchant().merchantId("m999").build();

        Member member = Member.createNormalMember()
                .memberId("999")
                .merchant(merchant)
                .loginId("test123")
                .build();

        DeviceMeta deviceMeta = DeviceMeta.builder()
                .deviceToken("dtoken")
                .deviceType(DeviceType.IOS)
                .devicePushToken("dpushtoken")
                .appVersion("1.11")
                .build();

        DeviceAccessToken accessToken = DeviceAccessToken.builder()
                .apiRefreshToken("refresh")
                .apiAccessTokenExpires(3600L)
                .apiAccessExpireDate("date")
                .build();

        Device newDevice = Device.createNewDevice()
                .member(member)
                .deviceMeta(deviceMeta)
                .deviceAccessToken(accessToken)
                .build();

        deviceRepository.save(newDevice);
        em.flush();

        DeviceMeta updatedDeviceMeta = DeviceMeta.builder()
                .deviceToken("updated")
                .deviceType(DeviceType.IOS)
                .devicePushToken("updateddpushtoken")
                .appVersion("1.12")
                .build();

        newDevice.changeDeviceMeta(updatedDeviceMeta);
        em.flush();

        Optional<Device> byMemberId = deviceQueryRepository.findByMemberId(member.getMemberId());
        byMemberId.ifPresent(device -> System.out.println(device.getMemberRole()));
    }

    @Test
    void test_findByMemeberId() {}
}
