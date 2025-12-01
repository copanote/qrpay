package com.bccard.qrpay.domain.mpmqr;

import com.bccard.qrpay.domain.common.code.FinancialInstitution;
import com.bccard.qrpay.domain.merchant.FinancialInstitutionMerchant;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.copanote.emvmpm.data.EmvMpmDataObject;
import com.copanote.emvmpm.data.EmvMpmNode;
import com.copanote.emvmpm.data.EmvMpmNodeFactory;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * MpmQr Service
 * - Mpm Emv Qr 생성
 */

@Service
public class EmvMpmService {

    private static final String BCCARD_IIN = "26000410"; // Issuer Identification Number
    private static final String BCCARD_AID = "D4100000014010"; // Application Identifier
    private static final String KOREA_COUNTY_CODE = "KR";

    private static final String TERMINAL_ID = "00000001"; // for UPLAN

    @Autowired
    private MpmQrPublicationService mpmqrPublicationService;

    public String publishEmvMpmQr(Merchant m) {
        // amount, transaction_currency, mer_cdhd_no, membership, installment
        return "";
    }

    public String publishStaticEmvMpmQr() {
        return "";
    }

    public String publishDynamicEmvMpmQr() {
        return "";
    }


    public EmvMpmNode createEmvMpmVer15(EmvMpmDefinition def, Merchant m, String qrRefId) {
        /*
         * ID 00 Payload Format Indicator
         */
        EmvMpmNode id00_PayloadFormatIndicator = EmvMpmNodeFactory.of(EmvMpmDataObject.PAYLOAD_FORMAT_INDICATOR);

        /*
         * ID 01 Point of Initiation Method
         */
        EmvMpmNode id01_PointOfInitiationMethod = EmvMpmNodeFactory.createPrimitive("01", "11"); //11 고정형, 12 변동형


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

        String mai = BCCARD_IIN + BCCARD_IIN + StringUtils.leftPad(bcMerchantNo, 9, "0");
        EmvMpmNode id15_MerchantAccountInfo = EmvMpmNodeFactory.createPrimitive("15", mai);


        /*
         * ID 26 Merchant Account Information Template
         */
        EmvMpmNode id26_maiTemplate = EmvMpmNodeFactory.createTemplate("26",
                Arrays.asList(EmvMpmNodeFactory.createPrimitive("00", BCCARD_AID),
                        EmvMpmNodeFactory.createPrimitive("05", StringUtils.leftPad(m.getMerchantId(), 9, "0"))));

        /*
         * ID 52 Merchant Category Code Primitive
         */
        EmvMpmNode id52_mcc = EmvMpmNodeFactory.createPrimitive("52", m.getMcc());

        /*
         * ID 53 Transaction Currency Primitive
         */
        EmvMpmNode id53_TransCurrency = EmvMpmNodeFactory.createPrimitive("53", StringUtils.defaultIfBlank("", "410"));

        /*
         * ID 54 Transaction Amount  Primitive
         */
        EmvMpmNode id54_Amount = EmvMpmNodeFactory.createPrimitive("54", "");  //up to fim

        /*
         * ID 58 Country Code  Primitive
         */
        EmvMpmNode id58_CountryCode = EmvMpmNodeFactory.createPrimitive("58", KOREA_COUNTY_CODE);

        /*
         * ID 59 Merchant Name Primitive
         */
        EmvMpmNode id59_MerchantEngName = EmvMpmNodeFactory.createPrimitive("59", StringUtils.truncate(m.getMerchantEnglishName(), def.find("/59").get().getMaxlength()));

        /*
         *  ID 60  Merchant City Primitive
         */
        EmvMpmNode id60_MerchantEngCity = EmvMpmNodeFactory.createPrimitive("60", StringUtils.truncate(StringUtils.defaultIfBlank(m.getCityEnglishName(), "SEOUL"), def.find("/60").get().getMaxlength()));

        /*
         * ID 61  Postal Code Primitive
         */
        EmvMpmNode id61_PostalCode = EmvMpmNodeFactory.createPrimitive("61", StringUtils.truncate(StringUtils.defaultIfBlank(m.getMerchantZipCode(), ""), def.find("/61").get().getMaxlength()));

        /*
         * ID 62  Additional Data Field Template
         */

        EmvMpmNode id62_50_BcLocalTemplate =
                EmvMpmNodeFactory.createTemplate("50",
                        Arrays.asList(EmvMpmNodeFactory.createPrimitive("00", BCCARD_AID),
                                EmvMpmNodeFactory.createPrimitive("01", ""),  // 00 일시불, 01-99 할부
                                EmvMpmNodeFactory.createPrimitive("02", ""),  // MEMBERSHIP A1: TOP Point
                                EmvMpmNodeFactory.createPrimitive("03", m.getMerchantStatus().getDbCode()))); //mer State

        EmvMpmNode id62_AdditionalDataFieldTemplate =
                EmvMpmNodeFactory.createTemplate("62",
                        Arrays.asList(EmvMpmNodeFactory.createPrimitive("03", StringUtils.leftPad(m.getMerchantId(), 9, '0')),  //Store ID
                                EmvMpmNodeFactory.createPrimitive("05", StringUtils.defaultIfBlank(qrRefId, "")),  //Reference Id
                                //EmvMpmNodeFactory.createPrimitive("06", StringUtils.leftPad("", 8, '0')),  //Customer ID
                                EmvMpmNodeFactory.createPrimitive("07", TERMINAL_ID), // terminal id
                                id62_50_BcLocalTemplate));

        /*
         * ID 64 Merchant Information Language Template
         */
        String krMerchantName = StringUtils.defaultIfBlank(m.getMerchantName(), "");
        String krCityName = StringUtils.defaultIfBlank(m.getCityName(), "서울");
        EmvMpmNode id64_MerchantInfomationLanguageTemplate =
                EmvMpmNodeFactory.createTemplate("64",
                        Arrays.asList(EmvMpmNodeFactory.createPrimitive("00", KOREA_COUNTY_CODE),
                                EmvMpmNodeFactory.createPrimitive("01", StringUtils.truncate(krMerchantName, def.find("/64/01").get().getMaxlength())),
                                EmvMpmNodeFactory.createPrimitive("02", StringUtils.truncate(krMerchantName, def.find("/64/02").get().getMaxlength()))
                        ));


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
        root.add(id62_50_BcLocalTemplate);
        root.add(id62_AdditionalDataFieldTemplate);
        root.add(id64_MerchantInfomationLanguageTemplate);
        root.markCrc(); //ID 63 Cyclic Redundancy Check Primitive

        //Construct Tree.
        return root;
    }
}
