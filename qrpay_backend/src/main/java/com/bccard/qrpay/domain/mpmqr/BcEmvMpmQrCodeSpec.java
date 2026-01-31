package com.bccard.qrpay.domain.mpmqr;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;
import com.bccard.qrpay.domain.common.code.PointOfInitMethod;
import com.bccard.qrpay.domain.merchant.FinancialInstitutionMerchant;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.copanote.emvmpm.data.EmvMpmDataObject;
import com.copanote.emvmpm.data.EmvMpmNode;
import com.copanote.emvmpm.data.EmvMpmNodeFactory;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * MpmQr Service
 * - Mpm Emv Qr Code Spec
 */
@Slf4j
public class BcEmvMpmQrCodeSpec {

    private static final String BCCARD_IIN = "26000410"; // Issuer Identification Number
    private static final String BCCARD_AID = "D4100000014010"; // Application Identifier
    private static final String KOREA_COUNTY_CODE = "KR";
    private static final String TERMINAL_ID = "00000001"; // for UPLAN

    private static final int DEFAULT_SIZE_WIDTH_HEIGHT = 200;
    private static final String IMAGE_FORMAT = "png";

    private static final EmvMpmDefinition bcEmvmpmDefinition;

    static {
        EmvMpmPackager emp = new EmvMpmPackager();
        ClassPathResource classPathResource = new ClassPathResource("emvmpm_bc.xml");
        try {
            emp.setEmvMpmPackager(classPathResource.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        bcEmvmpmDefinition = emp.create();
    }

    public static String staticCodeData(String memberId, Merchant m, String qrRefId, String currency) {
        EmvMpmNode node = staticEmvMpmQr(bcEmvmpmDefinition, memberId, m, qrRefId, currency);
        return node.toQrCodeData();
    }

    public static String dynamicCodeData(
            String memberId, Merchant m, String qrRefId, String currency, long amount, long installment) {
        EmvMpmNode node = dynamicEmvMpmQr(bcEmvmpmDefinition, memberId, m, qrRefId, currency, amount, installment);
        return node.toQrCodeData();
    }

    private static EmvMpmNode staticEmvMpmQr(
            EmvMpmDefinition def, String memberId, Merchant m, String qrRefId, String currency) {
        /*
         * ID 00 Payload Format Indicator
         */
        EmvMpmNode id00_PayloadFormatIndicator = EmvMpmNodeFactory.of(EmvMpmDataObject.PAYLOAD_FORMAT_INDICATOR);

        /*
         * ID 01 Point of Initiation Method
         */
        EmvMpmNode id01_PointOfInitiationMethod =
                EmvMpmNodeFactory.createPrimitive("01", PointOfInitMethod.STATIC.getDbCode()); // 11 고정형, 12 변동형

        /*
         * ID 15 Merchant Account Information Primitive
         */
        List<FinancialInstitutionMerchant> fim = m.getFiMerchants();
        String bcMerchantNo = "";
        for (FinancialInstitutionMerchant financeInstituteMerchant : fim) {
            if (financeInstituteMerchant.getFinancialInstitution() == FinancialInstitution.BCCARD) {
                bcMerchantNo = financeInstituteMerchant.getFiMerchantNo();
            }
        }

        String mai = BCCARD_IIN + BCCARD_IIN + StringUtils.rightPad(bcMerchantNo, 15, "0");
        EmvMpmNode id15_MerchantAccountInfo = EmvMpmNodeFactory.createPrimitive("15", mai);

        /*
         * ID 26 Merchant Account Information Template
         */
        EmvMpmNode id26_maiTemplate = EmvMpmNodeFactory.createTemplate(
                "26",
                Arrays.asList(
                        EmvMpmNodeFactory.createPrimitive("00", BCCARD_AID),
                        EmvMpmNodeFactory.createPrimitive("05", StringUtils.leftPad(m.getMerchantId(), 9, "0"))));

        /*
         * ID 52 Merchant Category Code Primitive
         */
        EmvMpmNode id52_mcc = EmvMpmNodeFactory.createPrimitive("52", m.getMcc());

        /*
         * ID 53 Transaction Currency Primitive
         */
        EmvMpmNode id53_TransCurrency =
                EmvMpmNodeFactory.createPrimitive("53", StringUtils.defaultIfBlank(currency, "410"));

        /*
         * ID 54 Transaction Amount  Primitive
         */
        //        EmvMpmNode id54_Amount = EmvMpmNodeFactory.createPrimitive("54", "");  //up to fim

        /*
         * ID 58 Country Code  Primitive
         */
        EmvMpmNode id58_CountryCode = EmvMpmNodeFactory.createPrimitive("58", KOREA_COUNTY_CODE);

        /*
         * ID 59 Merchant Name Primitive
         */
        EmvMpmNode id59_MerchantEngName = EmvMpmNodeFactory.createPrimitive(
                "59",
                StringUtils.truncate(
                        m.getMerchantEnglishName(), def.find("/59").get().getMaxlength()));

        /*
         *  ID 60  Merchant City Primitive
         */
        EmvMpmNode id60_MerchantEngCity = EmvMpmNodeFactory.createPrimitive(
                "60",
                StringUtils.truncate(
                        StringUtils.defaultIfBlank(m.getCityEnglishName(), "SEOUL"),
                        def.find("/60").get().getMaxlength()));

        /*
         * ID 61  Postal Code Primitive
         */
        EmvMpmNode id61_PostalCode = EmvMpmNodeFactory.createPrimitive(
                "61",
                StringUtils.truncate(
                        StringUtils.defaultIfBlank(m.getMerchantZipCode(), ""),
                        def.find("/61").get().getMaxlength()));

        /*
         * ID 62  Additional Data Field Template
         */

        EmvMpmNode id62_50_BcLocalTemplate = EmvMpmNodeFactory.createTemplate(
                "50",
                Arrays.asList(
                        EmvMpmNodeFactory.createPrimitive("00", BCCARD_AID),
                        //                                EmvMpmNodeFactory.createPrimitive("01", ""),  // 00 일시불, 01-99
                        // 할부
                        //                                EmvMpmNodeFactory.createPrimitive("02", ""),  // MEMBERSHIP
                        // A1: TOP Point
                        EmvMpmNodeFactory.createPrimitive(
                                "03", m.getMerchantStatus().getDbCode()))); // mer State

        EmvMpmNode id62_AdditionalDataFieldTemplate = EmvMpmNodeFactory.createTemplate(
                "62",
                Arrays.asList(
                        EmvMpmNodeFactory.createPrimitive(
                                "03", StringUtils.leftPad(m.getMerchantId(), 9, '0')), // Store ID
                        EmvMpmNodeFactory.createPrimitive(
                                "05", StringUtils.defaultIfBlank(qrRefId, "")), // Reference Id
                        EmvMpmNodeFactory.createPrimitive("06", StringUtils.leftPad(memberId, 8, '0')), // Customer ID
                        EmvMpmNodeFactory.createPrimitive("07", TERMINAL_ID) // terminal id
                        // id62_50_BcLocalTemplate
                        ));

        /*
         * ID 64 Merchant Information Language Template
         */
        String krMerchantName = StringUtils.defaultIfBlank(m.getMerchantName(), "");
        String krCityName = StringUtils.defaultIfBlank(m.getCityName(), "서울");
        EmvMpmNode id64_MerchantInfomationLanguageTemplate = EmvMpmNodeFactory.createTemplate(
                "64",
                Arrays.asList(
                        EmvMpmNodeFactory.createPrimitive("00", KOREA_COUNTY_CODE),
                        EmvMpmNodeFactory.createPrimitive(
                                "01",
                                StringUtils.truncate(
                                        krMerchantName, def.find("/64/01").get().getMaxlength())),
                        EmvMpmNodeFactory.createPrimitive(
                                "02",
                                StringUtils.truncate(
                                        krCityName, def.find("/64/02").get().getMaxlength()))));

        EmvMpmNode root = EmvMpmNodeFactory.root();
        root.add(id00_PayloadFormatIndicator);
        root.add(id01_PointOfInitiationMethod);
        root.add(id15_MerchantAccountInfo);
        root.add(id26_maiTemplate);
        root.add(id52_mcc);
        root.add(id53_TransCurrency);
        //        root.add(id54_Amount);
        root.add(id58_CountryCode);
        root.add(id59_MerchantEngName);
        root.add(id60_MerchantEngCity);
        root.add(id61_PostalCode);
        root.add(id62_AdditionalDataFieldTemplate);
        root.add(id64_MerchantInfomationLanguageTemplate);
        root.markCrc(); // ID 63 Cyclic Redundancy Check Primitive

        // Construct Tree.
        return root;
    }

    private static EmvMpmNode dynamicEmvMpmQr(
            EmvMpmDefinition def,
            String memberId,
            Merchant m,
            String qrRefId,
            String currency,
            long amount,
            long installment) {

        /*
         * ID 00 Payload Format Indicator
         */
        EmvMpmNode id00_PayloadFormatIndicator = EmvMpmNodeFactory.of(EmvMpmDataObject.PAYLOAD_FORMAT_INDICATOR);

        /*
         * ID 01 Point of Initiation Method
         */
        EmvMpmNode id01_PointOfInitiationMethod =
                EmvMpmNodeFactory.createPrimitive("01", PointOfInitMethod.DYNAMIC.getDbCode()); // 11 고정형, 12 변동형

        /*
         * ID 15 Merchant Account Information Primitive
         */
        List<FinancialInstitutionMerchant> fim = m.getFiMerchants();
        String bcMerchantNo = "";
        for (FinancialInstitutionMerchant financeInstituteMerchant : fim) {
            if (financeInstituteMerchant.getFinancialInstitution() == FinancialInstitution.BCCARD) {
                bcMerchantNo = financeInstituteMerchant.getFiMerchantNo();
            }
        }

        String mai = BCCARD_IIN + BCCARD_IIN + StringUtils.rightPad(bcMerchantNo, 15, "0");
        EmvMpmNode id15_MerchantAccountInfo = EmvMpmNodeFactory.createPrimitive("15", mai);

        /*
         * ID 26 Merchant Account Information Template
         */
        EmvMpmNode id26_maiTemplate = EmvMpmNodeFactory.createTemplate(
                "26",
                Arrays.asList(
                        EmvMpmNodeFactory.createPrimitive("00", BCCARD_AID),
                        EmvMpmNodeFactory.createPrimitive("05", StringUtils.leftPad(m.getMerchantId(), 9, "0"))));

        /*
         * ID 52 Merchant Category Code Primitive
         */
        EmvMpmNode id52_mcc = EmvMpmNodeFactory.createPrimitive("52", m.getMcc());

        /*
         * ID 53 Transaction Currency Primitive
         */
        EmvMpmNode id53_TransCurrency =
                EmvMpmNodeFactory.createPrimitive("53", StringUtils.defaultIfBlank(currency, "410"));

        /*
         * ID 54 Transaction Amount  Primitive
         */
        EmvMpmNode id54_Amount = EmvMpmNodeFactory.createPrimitive("54", String.valueOf(amount));

        /*
         * ID 58 Country Code  Primitive
         */
        EmvMpmNode id58_CountryCode = EmvMpmNodeFactory.createPrimitive("58", KOREA_COUNTY_CODE);

        /*
         * ID 59 Merchant Name Primitive
         */
        EmvMpmNode id59_MerchantEngName = EmvMpmNodeFactory.createPrimitive(
                "59",
                StringUtils.truncate(
                        m.getMerchantEnglishName(), def.find("/59").get().getMaxlength()));

        /*
         *  ID 60  Merchant City Primitive
         */
        EmvMpmNode id60_MerchantEngCity = EmvMpmNodeFactory.createPrimitive(
                "60",
                StringUtils.truncate(
                        StringUtils.defaultIfBlank(m.getCityEnglishName(), "SEOUL"),
                        def.find("/60").get().getMaxlength()));

        /*
         * ID 61  Postal Code Primitive
         */
        EmvMpmNode id61_PostalCode = EmvMpmNodeFactory.createPrimitive(
                "61",
                StringUtils.truncate(
                        StringUtils.defaultIfBlank(m.getMerchantZipCode(), ""),
                        def.find("/61").get().getMaxlength()));

        /*
         * ID 62  Additional Data Field Template
         */

        EmvMpmNode id62_50_BcLocalTemplate = EmvMpmNodeFactory.createTemplate(
                "50",
                Arrays.asList(
                        EmvMpmNodeFactory.createPrimitive("00", BCCARD_AID),
                        EmvMpmNodeFactory.createPrimitive(
                                "01", StringUtils.leftPad(String.valueOf(installment), 2, '0')), // 00 일시불, 01-99 할부
                        //                                EmvMpmNodeFactory.createPrimitive("02", ""),  // MEMBERSHIP
                        // A1: TOP Point
                        EmvMpmNodeFactory.createPrimitive(
                                "03", m.getMerchantStatus().getDbCode()))); // mer State

        List<EmvMpmNode> list = new ArrayList<>();
        list.add(EmvMpmNodeFactory.createPrimitive("03", StringUtils.leftPad(m.getMerchantId(), 9, '0')));
        list.add(EmvMpmNodeFactory.createPrimitive("05", StringUtils.defaultIfBlank(qrRefId, "")));
        list.add(EmvMpmNodeFactory.createPrimitive("06", StringUtils.leftPad(memberId, 8, '0')));
        list.add(EmvMpmNodeFactory.createPrimitive("07", TERMINAL_ID));
        if (installment != 0) {
            list.add(id62_50_BcLocalTemplate);
        }

        EmvMpmNode id62_AdditionalDataFieldTemplate = EmvMpmNodeFactory.createTemplate("62", list);

        /*
         * ID 64 Merchant Information Language Template
         */
        String krMerchantName = StringUtils.defaultIfBlank(m.getMerchantName(), "");
        String krCityName = StringUtils.defaultIfBlank(m.getCityName(), "서울");
        EmvMpmNode id64_MerchantInfomationLanguageTemplate = EmvMpmNodeFactory.createTemplate(
                "64",
                Arrays.asList(
                        EmvMpmNodeFactory.createPrimitive("00", KOREA_COUNTY_CODE),
                        EmvMpmNodeFactory.createPrimitive(
                                "01",
                                StringUtils.truncate(
                                        krMerchantName, def.find("/64/01").get().getMaxlength())),
                        EmvMpmNodeFactory.createPrimitive(
                                "02",
                                StringUtils.truncate(
                                        krCityName, def.find("/64/02").get().getMaxlength()))));

        EmvMpmNode root = EmvMpmNodeFactory.root();
        root.add(id00_PayloadFormatIndicator);
        root.add(id01_PointOfInitiationMethod);
        root.add(id15_MerchantAccountInfo);
        root.add(id26_maiTemplate);
        root.add(id52_mcc);
        root.add(id53_TransCurrency);
        root.add(id54_Amount);
        root.add(id58_CountryCode);
        root.add(id59_MerchantEngName);
        root.add(id60_MerchantEngCity);
        root.add(id61_PostalCode);
        root.add(id62_AdditionalDataFieldTemplate);
        root.add(id64_MerchantInfomationLanguageTemplate);
        root.markCrc(); // ID 63 Cyclic Redundancy Check Primitive

        // Construct Tree.
        return root;
    }
}
