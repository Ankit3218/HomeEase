package com.homeease.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

public class QrCodeGenerator {

	public static byte[] generateQRCodeImage(String data, int width, int height) throws Exception {
	    QRCodeWriter qrCodeWriter = new QRCodeWriter();
	    Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
	    hints.put(EncodeHintType.MARGIN, 1); // Small margin

	    BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

	    // Make the image bigger and sharper
	    int scale = 2; // scale factor
	    int scaledWidth = width * scale;
	    int scaledHeight = height * scale;

	    BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
	    for (int x = 0; x < scaledWidth; x++) {
	        for (int y = 0; y < scaledHeight; y++) {
	            int realX = x / scale;
	            int realY = y / scale;
	            bufferedImage.setRGB(x, y, bitMatrix.get(realX, realY) ? 0xFF000000 : 0xFFFFFFFF);
	        }
	    }

	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    ImageIO.write(bufferedImage, "png", outputStream);

	    return outputStream.toByteArray();
	}

}

