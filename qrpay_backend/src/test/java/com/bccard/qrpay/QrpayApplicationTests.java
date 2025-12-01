package com.bccard.qrpay;

import com.bccard.qrpay.domain.mpmqr.MpmQrPublicationService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class QrpayApplicationTests {

    @Autowired
    MpmQrPublicationService mqs;

    @Test
    void contextLoads() {
    }

    @Order(1)
    @Test
    void insertData() {

        //		Merchant m = new Merchant();
        //		m.setRegistraionRequester(FinanceInstitute.BCCARD);;
        //		ms.save(m);
        //		System.out.println(m);
        //
        //		FinanceInstituteMerchant fim = new FinanceInstituteMerchant();
        //		fim.setMerMgmtNo(m.getMerMgmtNo());
        //		fim.setFi(FinanceInstitute.BCCARD);
        //		fim.setFiMerMgmtNo("test01");
        //		ms.save(fim);
        //
        //		System.out.println(fim.toString());
        //		FinanceInstituteMerchant fim2 = new FinanceInstituteMerchant();
        //		fim2.setMerMgmtNo(m.getMerMgmtNo());
        //		fim2.setFi(FinanceInstitute.HANACARD);
        //		fim2.setFiMerMgmtNo("test02");
        //
        //		ms.save(fim2);
        //
        //		System.out.println(ms.getAll());;  //java.lang.StackOverflowError
        //		System.out.println(ms.get("00000001"));  //LazyInitialization could not initialize...

    }

    @Order(2)
    @Test
    void selectData() {
        //		FIMerchantId id = new FIMerchantId();
        //		id.setMerMgmtNo("00000001");
        //		id.setFi(FinanceInstitute.BCCARD);
        //
        //		System.out.println("selectData:" + ms.get(id));
    }

    @Test
    void testZxing() {

        byte[] b = null;

        try {
            b = generateQrImage("qrqrqrqr", 100, 100, "png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(base64EncodedString(b));
    }

    public static void generate(String content, int width, int height, OutputStream outStream) throws Exception {

        int qrcodeColor = 0xFF000000;
        int backgroudColor = 0xFFFFFFFF;

        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(qrcodeColor, backgroudColor);

        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.toString());

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

        MatrixToImageWriter.writeToStream(bitMatrix, content, outStream, matrixToImageConfig);
    }

    private static final String INAMGE_FORMAT = "png";

    //	public static void generate(String content, int width, int height, OutputStream outStream) throws Exception {
    //
    //		int qrcodeColor = 0xFF000000;
    //		int backgroudColor = 0xFFFFFFFF;
    //
    //		MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(qrcodeColor, backgroudColor);
    //
    //		Map<EncodeHintType, String> hints = new HashMap<>();
    //		hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.toString());
    //
    //		QRCodeWriter qrCodeWriter = new QRCodeWriter();
    //		BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
    //
    //		MatrixToImageWriter.writeToStream(bitMatrix, content, outStream, matrixToImageConfig);
    //	}

    private static final MatrixToImageConfig MATRIX_TO_IMAGE_CONFIG =
            new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);

    public static byte[] generateQrImage(String content, int width, int height, String imageFormat) throws Exception {

        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.toString());

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(bitMatrix, INAMGE_FORMAT, baos, MATRIX_TO_IMAGE_CONFIG);

        return baos.toByteArray();
    }

    public static String base64EncodedString(byte[] b) {
        byte[] based64Encoded = Base64.getEncoder().encode(b);
        return new String(based64Encoded);
    }
}
