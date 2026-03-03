package com.bccard.qrpay.apns;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;


@Slf4j
@ActiveProfiles("test")
public class ApnsTest {


    @Test
    void testApns() throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        log.info("testLogback");
// 1. APNs 클라이언트 생성 (P8 키 파일 사용)
        Http2FrameLogger frameLogger = new Http2FrameLogger(LogLevel.DEBUG, Http2FrameLogger.class);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("AuthKey_5Y44XGXP7A.p8");
        if (inputStream == null) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다: resources/AuthKey_ABC123DEFG.p8");
        }


        ApnsSigningKey signingKey = ApnsSigningKey.loadFromInputStream(
                inputStream,
                "NC777HLHAM",
                "5Y44XGXP7A"
        );

        ApnsClient apnsClient = new ApnsClientBuilder()
                .setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST) // 테스트 시 Sandbox
                .setSigningKey(signingKey)
                .setFrameLogger(frameLogger) // 프레임 로거 주입
                .build();

//// 2. 푸시 페이로드 생성
        SimpleApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
        payloadBuilder.setContentAvailable(true);
        String payload = payloadBuilder.build();
        String token = "e3706a2f6a1104cb3528a680ecab6a34a6e62429576b388e53c5b3b6606bc968"; // 수신자 디바이스 토큰
        log.info("payload={}", payload);

        SimpleApnsPayloadBuilder payloadBuilder2 = new SimpleApnsPayloadBuilder();
        String safePayload = payloadBuilder2.build();
        log.info("safePayload={}", safePayload);

//
//// 3. 알림 전송 (비동기 처리)
        SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, "com.bccard.brts.apple.cns", payload);

        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                sendNotificationFuture = apnsClient.sendNotification(pushNotification);
//
//// 4. 결과 처리
        try {
            PushNotificationResponse<SimpleApnsPushNotification> response = sendNotificationFuture.get();
            if (response.isAccepted()) {
                System.out.println("푸시 전송 성공!");
            } else {
                System.err.println("전송 실패 사유: " + response.getRejectionReason());
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }



    }

}
