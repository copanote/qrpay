package com.bccard.qrpay.apns.CaSd;

import lombok.Getter;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@ToString
public class CasdCertificate {

    // 모든 필드(정의된 것 + 정의되지 않은 것)를 들어온 순서대로 통합 관리
    // Key: Tag (String), Value: Tlv 객체
    private final Map<String, Tlv> allFields = new LinkedHashMap<>();

    // 7F49 내부 데이터
    private final Map<String, Tlv> eccFields = new LinkedHashMap<>();

    public Tlv get(CasdField casdField) {
        return this.allFields.get(casdField.tag);
    }

    public Tlv get(EccPublicKeyField eccPublicKeyField) {
        return this.eccFields.get(eccPublicKeyField.tag);
    }

    public byte[] rawSignatureData() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Map.Entry<String, Tlv> stringTlvEntry : allFields.entrySet()) {
            if (stringTlvEntry.getKey().equals("5F37")) continue;

            Tlv tlv = stringTlvEntry.getValue();
            baos.write(tlv.getAll());
        }

        return baos.toByteArray();
    }

    @Getter
    public enum CasdField {
        TAG_93("93", "Certificate SerialNumber"),
        TAG_42("42", "CA Identifier"),
        TAG_5F20("5F20", "Subject Identifier"),
        TAG_95("95", "Key Usage"),
        TAG_5F25("5F25", "Effective Date(YYYYMMDD, BCD Format)"),
        TAG_5F24("5F25", "Expiration Date(YYYYMMDD, BCD Format)"),
        TAG_45("45", "CA Security Domain Image Number"),
        TAG_53("53", "Discretionary Data(Unspecified Format)"),
        TAG_73("73", "Discretionary Data(BER-TLV Encoded)"),
        TAG_7F49("7F49", "Public Key Data Object", true),
        TAG_5F37("5F37", "Signature");

        CasdField(String tag, String desc) {
            this.tag = tag;
            this.desc = desc;
            this.isConstructed = false;
        }

        CasdField(String tag, String desc, boolean isConstructed) {
            this.tag = tag;
            this.desc = desc;
            this.isConstructed = isConstructed;
        }

        final String tag;
        final String desc;
        final boolean isConstructed;

        public static CasdField fromTag(String tagHex) {
            for (CasdField f : values()) {
                if (f.tag.equalsIgnoreCase(tagHex)) return f;
            }
            return null;
        }
    }

    @Getter
    public enum EccPublicKeyField {
        TAG_B0("B0", "Ecc Public Key - Q"),
        TAG_F0("F0", "Key Parameter Reference(Curve)"),
        ;

        EccPublicKeyField(String tag, String desc) {
            this.tag = tag;
            this.desc = desc;
        }

        final String tag;
        final String desc;

        public static EccPublicKeyField fromTag(String tagHex) {
            for (EccPublicKeyField f : values()) {
                if (f.tag.equalsIgnoreCase(tagHex)) return f;
            }
            return null;
        }
    }

    @Getter
    public enum EccKeyParameterReference {
        P_256("00", "P-256"),
        P_384("01", "P-384"),
        P_521("02", "P-512"),
        ;

        EccKeyParameterReference(String value, String curve) {
            this.value = value;
            this.curve = curve;
        }

        final String value;
        final String curve;

        public static EccKeyParameterReference fromValue(String value) {
            for (EccKeyParameterReference f : values()) {
                if (f.value.equalsIgnoreCase(value)) return f;
            }
            return null;
        }

    }


}
