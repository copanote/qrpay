package com.bccard.qrpay.apns;

import com.bccard.qrpay.apns.CaSd.*;
import com.bccard.qrpay.utils.security.HashCipher;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayOutputStream;
import java.security.PublicKey;
import java.util.Base64;


@Slf4j
@ActiveProfiles("test")
public class CasdCertTest {


    @Test
    void testTlv() throws Exception {
        log.info("testTlv");
        CasdCertificate c2 = CasdParser.parse("931004383C25ED2E900252341412656014164207637093010000215F2001A9950282005F240421160707450100530803C38CB85B33300E7F4946B04104653E3E40BEEF905DBC9AB61E5FEA982E97200041BBD534757D32DBE3C83F10B12DC47A26ECA33BDC245B7ACCA8DE3FFB9EACEFF613A2E4C11A4F1912CCBF013BF001005F37406BC1F3DF43F6DDE7BFE63D87B2105319E368623168D4FDB30DF6554D2ECC6626712F250C1C320BFE4299442988BD652DBAA212ACD2F6C85CAD7AF68948F8FB5E");
        System.out.println(c2.toString());


        CasdCertificate parse = CasdParser.parse("9310042D14EB6D50800190111358259494144207637093010000215F2001A9950282005F24042116070745010053085439CA518B0861B77F4946B04104D51C23293766803CC1354761AAEDA3470357D69F471060EB22E26C530AC0BF725A3F9E7E2AF05FCEF0943E704B19262DB6ABDCFCAD222C0633E848B376E70CFFF001005F3740D1739D91DFBFA3B27B3F9BFE5AF839E30839668AC4586F8C09EEC9013ECF855F3872832E70437E9F0A9A68B48FBF931FC9814A3B852CACB7ABE8C7DEFFFCB2A7");
        System.out.println(c2.toString());

        byte[] bbb = c2.rawSignatureData();

        System.out.println(bbb.length);
        System.out.println(c2.getAllFields().get("5F37").getValue().length);


        String pem = "-----BEGIN CERTIFICATE-----" +
                "MIIBSjCB8KADAgECAgEBMAwGCCqGSM49BAMCBQAwGTEXMBUGA1UEAxMONjM3MDkz" +
                "MDEwMDAwMjEwIhgPMjAxNjEyMjEwMDAwMDBaGA8yMTE2MTIyMTAwMDEwMFowGTEX" +
                "MBUGA1UEAxMONjM3MDkzMDEwMDAwMjEwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNC" +
                "AAQtriqyVAUMUxwYAg1/stzT+ggsB1hFL/AK528RGsDyPNoMPNrcO4R/TqXIBtod" +
                "8l3fss/x68/cFLxIRsJD6O97oyMwITASBgNVHRMBAf8ECDAGAQH/AgEBMAsGA1Ud" +
                "DwQEAwICBDAMBggqhkjOPQQDAgUAA0cAMEQCIBXWJyFxnY2UELXyuhXT/G1u31be" +
                "+HUiK0smn4NCaIJBAiAYILB3qyeo+2DQ7MbsT+3bOhJCruFjXgqt2s5aR6wqHw==" +
                "-----END CERTIFICATE-----";
        PublicKey publicKeyFromString = SignatureVerifier.getPublicKeyFromString();
        log.info("5F37={}", c2.getAllFields().get("5F37").getHexValue());
        boolean b = SignatureVerifier.verifySignature(publicKeyFromString, bbb, c2.getAllFields().get("5F37").getValue());

        System.out.println(b);

    }

    /**
     *{
     *   "version": 2,
     *   "registrationData": "eyJwdXNoVG9rZW4iOiJlMzcwNmEyZjZhMTEwNGNiMzUyOGE2ODBlY2FiNmEzNGE2ZTYyNDI5NTc2YjM4OGU1M2M1YjNiNjYwNmJjOTY4IiwiYWNjb3VudEhhc2giOiIyZjVkYjE4NTUxYjAzMDdkYWU4NjlkNzNkMjE3N2Y4MTBkZWI0M2JjNWRkNzEzMWQwZmUyMmJkNGMxMTExNDJjIn0=",
     *   "casdCertificate": "9310042D14EB6D50800190111358259494144207637093010000215F2001A9950282005F24042116070745010053085439CA518B0861B77F4946B04104D51C23293766803CC1354761AAEDA3470357D69F471060EB22E26C530AC0BF725A3F9E7E2AF05FCEF0943E704B19262DB6ABDCFCAD222C0633E848B376E70CFFF001005F3740D1739D91DFBFA3B27B3F9BFE5AF839E30839668AC4586F8C09EEC9013ECF855F3872832E70437E9F0A9A68B48FBF931FC9814A3B852CACB7ABE8C7DEFFFCB2A7",
     *   "signature": "QBPrGO3JpKpOx2aInKzlfJrjdbh1OVII4kxddIjRN2IJZSP+QdGTbaHU/MwIRU867c7evp+QlBjUem+yUji23BsvhjPh9wRTXX5k5s9XOPGm"
     * }
     *
     */

    @Test
    void testVerifyt() throws Exception {
        //1. casd certi Tlv형태로 파싱함

        //2. 전달받은 ca certificate를 로드함

        //3. casd 값을  모든 값을 븉여서 sha256 수행  5F37태그는 서명값  즉 2번의 공개키를 활용하여 검증 수행\

        //4 인풋 signatre Json 값을 가져와서  base64decode -> byte[
        //
        /**
         * The Secure Element outputs ECDSA signatures as a Length-Value data structure (except the nonce
         * which has only value and no length tag) with the following definition:
         * 1 Length of the ECDSA Signature Mandatory
         * 64 Signature Data Mandatory
         * 16 Random Nonce Mandatory
         */
        //     *   "signature": "QBPrGO3JpKpOx2aInKzlfJrjdbh1OVII4kxddIjRN2IJZSP+QdGTbaHU/MwIRU867c7evp+QlBjUem+yUji23BsvhjPh9wRTXX5k5s9XOPGm"
        String s = "QBPrGO3JpKpOx2aInKzlfJrjdbh1OVII4kxddIjRN2IJZSP+QdGTbaHU/MwIRU867c7evp+QlBjUem+yUji23BsvhjPh9wRTXX5k5s9XOPGm";
        byte[] sig = Base64.getDecoder().decode(s);
        System.out.println(sig.length);
        byte[] signature = new byte[64];
        byte[] randomNounce = new byte[16];
        System.arraycopy(sig, sig.length - 16, randomNounce, 0, 16);
        System.arraycopy(sig, 1, signature, 0, 64);


        System.out.println(HexUtils.toHexString(randomNounce));
        //5. registrationData를 base64decode후  sha256 수행
//     *   "registrationData": "eyJwdXNoVG9rZW4iOiJlMzcwNmEyZjZhMTEwNGNiMzUyOGE2ODBlY2FiNmEzNGE2ZTYyNDI5NTc2YjM4OGU1M2M1YjNiNjYwNmJjOTY4IiwiYWNjb3VudEhhc2giOiIyZjVkYjE4NTUxYjAzMDdkYWU4NjlkNzNkMjE3N2Y4MTBkZWI0M2JjNWRkNzEzMWQwZmUyMmJkNGMxMTExNDJjIn0=",
        //6.  5번의 결과 + 4번 RandomNounce + tag93의 밸류  + tag53의 별류 if presetnt
        //tag=93, length=10, value=04383C25ED2E90025234141265601416
        //53=Tlv{tag=53, length=08, value=03C38CB85B33300E}

        String ca = "9310042D14EB6D50800190111358259494144207637093010000215F2001A9950282005F24042116070745010053085439CA518B0861B77F4946B04104D51C23293766803CC1354761AAEDA3470357D69F471060EB22E26C530AC0BF725A3F9E7E2AF05FCEF0943E704B19262DB6ABDCFCAD222C0633E848B376E70CFFF001005F3740D1739D91DFBFA3B27B3F9BFE5AF839E30839668AC4586F8C09EEC9013ECF855F3872832E70437E9F0A9A68B48FBF931FC9814A3B852CACB7ABE8C7DEFFFCB2A7";
        CasdCertificate casdCertificate = CasdParser.parse(ca);
        System.out.println(casdCertificate);


        String regData = "eyJwdXNoVG9rZW4iOiJlMzcwNmEyZjZhMTEwNGNiMzUyOGE2ODBlY2FiNmEzNGE2ZTYyNDI5NTc2YjM4OGU1M2M1YjNiNjYwNmJjOTY4IiwiYWNjb3VudEhhc2giOiIyZjVkYjE4NTUxYjAzMDdkYWU4NjlkNzNkMjE3N2Y4MTBkZWI0M2JjNWRkNzEzMWQwZmUyMmJkNGMxMTExNDJjIn0=";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(HashCipher.sha256(Base64.getDecoder().decode(regData)));
        baos.write(randomNounce);
        baos.write(casdCertificate.get(CasdCertificate.CasdField.TAG_93).getValue());
        baos.write(casdCertificate.get(CasdCertificate.CasdField.TAG_53).getValue());


        Tlv publicKey = casdCertificate.get(CasdCertificate.EccPublicKeyField.TAG_B0);
        Tlv eccParams = casdCertificate.get(CasdCertificate.EccPublicKeyField.TAG_F0);
        System.out.println(publicKey);
        System.out.println(eccParams);
        CasdCertificate.EccKeyParameterReference eccKeyParameterReference = CasdCertificate.EccKeyParameterReference.fromValue(eccParams.getHexValue());
        System.out.println(eccKeyParameterReference);

        PublicKey eccPk = ECDSAKeyLoader.getPublicKey(eccKeyParameterReference, publicKey.getValue());

        boolean b = SignatureVerifier.verifySignature(eccPk, baos.toByteArray(), signature);
        System.out.println(b);


//7. Verify the signature from step 4 over the bytes from step 6 against the CASD Certificate from step 1(4)

//8. Validate that the DPAN Identifier is valid for the Secure Element identified as part of thesignature.
    }

}
