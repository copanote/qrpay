package com.bccard.qrpay.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * Wrapper class for google zxing library
 *
 */
public class ZxingQrcode {

    private static final MatrixToImageConfig MATRIX_TO_IMAGE_CONFIG =
            new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);
    private static final String DEFAULT_IMAGE_FORMAT = "png";

    public static byte[] qrcodeImageBinary(String content, int width, int height, String imageFormat) throws Exception {

        String ifm = DEFAULT_IMAGE_FORMAT;

        if (ZxingImageFormat.isAvailable(imageFormat)) {
            ifm = imageFormat;
        }

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.toString());
        hints.put(EncodeHintType.MARGIN, 0);

        BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, ifm, baos, MATRIX_TO_IMAGE_CONFIG);

        return baos.toByteArray();
    }

    public enum ZxingImageFormat {
        JPG, BMP, GIF, WBMP, PNG, JPEG,
        ;

        public static boolean isAvailable(String format) {
            Optional<ZxingImageFormat> ozif = Arrays.stream(ZxingImageFormat.values())
                    .filter(item -> item.toString().equalsIgnoreCase(format))
                    .findAny();
            return ozif.isPresent();
        }
    }
}
