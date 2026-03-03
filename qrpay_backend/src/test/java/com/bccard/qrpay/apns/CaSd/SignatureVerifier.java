package com.bccard.qrpay.apns.CaSd;

import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignatureVerifier {
    public static PublicKey getPublicKeyFromPem(String filePath) throws Exception {
        // 1. 파일 읽기
        String pem = new String(Files.readAllBytes(Paths.get(filePath)));

        // 2. 헤더/푸터 및 줄바꿈 제거
        String publicKeyPem = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "")
                .trim();

        // 3. Base64 디코딩
        byte[] encoded = Base64.getDecoder().decode(publicKeyPem);

        // 4. KeyFactory를 이용해 PublicKey 객체 생성 (ECDSA용)
        KeyFactory keyFactory = KeyFactory.getInstance("EC"); // ECDSA이므로 "EC" 사용
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }

    public static PublicKey getPublicKeyFromString() throws Exception {
        // 1. 파일 읽기
        ClassPathResource classPathResource = new ClassPathResource("63709301000021.pem");

        // 2. CertificateFactory 생성 (X.509)
        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        // 3. 바이트 배열을 인증서 객체로 변환
        X509Certificate cert = (X509Certificate) factory.generateCertificate(classPathResource.getInputStream());
        System.out.println(cert.getPublicKey().getAlgorithm());

        // 4. 인증서에서 공개키 추출
        return cert.getPublicKey();
    }


    public static boolean verifySignature(PublicKey publicKey, byte[] tbsData, byte[] signatureValue) {
        try {
            // SHA256withECDSA 알고리즘 사용
            Signature signature = Signature.getInstance("SHA256withECDSAinP1363Format");

            // 검증 모드 초기화 (공개키 입력)
            signature.initVerify(publicKey);

            // 원본 데이터 입력 (내부적으로 SHA256 해시를 수행함)
            signature.update(tbsData);

            // 서명 값 대조 검증
            return signature.verify(signatureValue);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
