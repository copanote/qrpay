package com.bccard.qrpay.domain.mpmqr;

import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.repository.MerchantQueryRepository;
import com.copanote.emvmpm.data.EmvMpmDataObject;
import com.copanote.emvmpm.data.EmvMpmNode;
import com.copanote.emvmpm.data.EmvMpmNodeFactory;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;
import com.copanote.emvmpm.parser.EmvMpmParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

@SpringBootTest
@Transactional
@Sql("classpath:sql/data.sql")
public class EmvMpmTest {

    @Autowired
    private MerchantQueryRepository merchantQueryRepository;

    @Autowired
    private EmvMpmService emvMpmService;

    @Test
    void test() throws ParserConfigurationException, IOException, SAXException {

        //        //GIVEN
        String qrData =
                "0102110002011531260004102600041071479286900000026310014D410000001401005091000058325204581253034105802KR5925OSULROWOOKOPI TEUUINTAUEO6013SEOUL JUNG-GU610504548625603091000058320515MQ202000004761806080000000007080000000164310002ko0112오슬로우커피 트윈타워점0205서울 중구6304C38C";
        EmvMpmPackager emp = new EmvMpmPackager();
        emp.setEmvMpmPackager("emvmpm_bc.xml");
        EmvMpmDefinition emd = emp.create();

        // WHEN
        EmvMpmNode actualNode = EmvMpmParser.parse(qrData, emd);

        System.out.println(actualNode);
    }

    @Test
    public void constructTreeTest() {

        EmvMpmNode root = EmvMpmNodeFactory.root();

        EmvMpmNode payloadFormatIndicator = EmvMpmNodeFactory.of(EmvMpmDataObject.PAYLOAD_FORMAT_INDICATOR);
        EmvMpmNode t2 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("02", "1234"));
        EmvMpmNode t3 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("03", "12345"));
        EmvMpmNode t4 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("04", "123456"));
        EmvMpmNode t5 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("05", "1234567"));
        EmvMpmNode t6 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("06", "12345678"));
        EmvMpmNode t7 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("07", "123456789"));
        EmvMpmNode t8 = EmvMpmNodeFactory.of(EmvMpmDataObject.of("08", "1234567890"));

        List<EmvMpmNode> micList = new ArrayList<>();
        micList.add(EmvMpmNodeFactory.of(EmvMpmDataObject.of("00", "D4100000014010")));
        micList.add(EmvMpmNodeFactory.of(EmvMpmDataObject.of("05", "100005832")));
        EmvMpmNode mic = EmvMpmNodeFactory.createTemplate("26", micList);
        mic.add(EmvMpmNodeFactory.of(EmvMpmDataObject.of("09", "9999")));

        List<EmvMpmNode> micList2 = new ArrayList<>();
        micList.add(EmvMpmNodeFactory.of(EmvMpmDataObject.of("00", "D4100000014010")));
        micList.add(EmvMpmNodeFactory.of(EmvMpmDataObject.of("05", "100005832")));
        EmvMpmNode mic2 = EmvMpmNodeFactory.createTemplate("55", micList);
        mic2.add(EmvMpmNodeFactory.of(EmvMpmDataObject.of("09", "9999")));

        root.add(payloadFormatIndicator);
        root.add(t2);
        root.add(t3);
        root.add(t4);
        root.add(t5);
        root.add(t6);
        root.add(t7);
        root.add(t8);
        root.add(mic);
        root.add(mic2);
        root.markCrc();
        System.out.println(root.toQrCodeData());
    }

    @Test
    void test_createMpmQr() throws ParserConfigurationException, IOException, SAXException {
        EmvMpmPackager emp = new EmvMpmPackager();
        emp.setEmvMpmPackager("emvmpm_bc.xml");
        EmvMpmDefinition emd = emp.create();

        Merchant merchant = merchantQueryRepository.findById("900004503").orElseThrow();

        EmvMpmNode testqrrefid = emvMpmService.createEmvMpmVer15(emd, merchant, "TESTQRREFID");
        System.out.println(testqrrefid.toString());
        System.out.println(testqrrefid.toQrCodeData());
    }
}
