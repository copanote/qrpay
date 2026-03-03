package com.bccard.qrpay.apns.CaSd;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;

public class ECDSAKeyLoader {

    public static PublicKey getPublicKey(CasdCertificate.EccKeyParameterReference parameterReference, byte[] rawKeyBytes) throws Exception {
        String curveName;

        // 1. 인덱스에 따른 표준 곡선 이름 매핑
        switch (parameterReference) {
            case P_256:
                curveName = "secp256r1";
                break; // P-256
            case P_384:
                curveName = "secp384r1";
                break; // P-384
            case P_521:
                curveName = "secp521r1";
                break; // P-521
            default:
                throw new IllegalArgumentException("Unknown algo index: " + parameterReference);
        }

        // 2. JDK 내장 곡선 파라미터 가져오기
        AlgorithmParameters params = AlgorithmParameters.getInstance("EC");
        params.init(new ECGenParameterSpec(curveName));
        ECParameterSpec ecParameters = params.getParameterSpec(ECParameterSpec.class);

        // 3. Raw Bytes(Uncompressed: 04 + X + Y)에서 좌표 추출
        // 첫 바이트 04를 제외한 나머지의 절반이 X, 나머지가 Y
        int offset = 1;
        int coordinateLen = (rawKeyBytes.length - offset) / 2;

        byte[] xBytes = new byte[coordinateLen];
        byte[] yBytes = new byte[coordinateLen];

        System.arraycopy(rawKeyBytes, offset, xBytes, 0, coordinateLen);
        System.arraycopy(rawKeyBytes, offset + coordinateLen, yBytes, 0, coordinateLen);

        // 4. ECPoint 및 KeySpec 생성
        ECPoint point = new ECPoint(new BigInteger(1, xBytes), new BigInteger(1, yBytes));
        ECPublicKeySpec keySpec = new ECPublicKeySpec(point, ecParameters);

        // 5. PublicKey 객체 생성
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePublic(keySpec);
    }
}