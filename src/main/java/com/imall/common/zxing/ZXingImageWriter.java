package com.imall.common.zxing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by frankie on 15/7/5.
 * <p/>
 * 该类的作用为根据文本文件生成二维码
 */

public class ZXingImageWriter {

    private static final int BLACK = 0x000000;

    private static final int WHITE = 0xFFFFFFFF;


    private ZXingImageWriter() {

    }

    /**
     * @param matrix
     * @return
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * @param matrix
     * @param format
     * @param file
     * @throws IOException
     */
    public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {

        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }


    /**
     * @param matrix
     * @param format
     * @param stream
     * @throws IOException
     */
    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    /**
     * @param matrix
     * @param format
     * @param logoImage
     * @param stream
     * @throws IOException
     */
    public static void writeToStreamWithLogo(BitMatrix matrix, String format, String logoPath, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        Graphics2D graphics2D = image.createGraphics();

        Image logoImage = ImageIO.read(new File(logoPath));

        int logoWidth = image.getWidth() / 8;
        int logoHeight = image.getHeight() / 8;
        int logoX = (image.getWidth() - logoWidth) / 2;
        int logoY = (image.getHeight() - logoHeight) / 2;

        graphics2D.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, null);
        graphics2D.dispose();
        logoImage.flush();

        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    /**
     * @param text
     * @param width
     * @param height
     * @param format
     * @param outputStream
     * @throws IOException
     * @throws WriterException
     */
    public static void generateTwoDimensionCode(String text,
                                                int width,
                                                int height,
                                                String format,
                                                OutputStream outputStream) throws IOException, WriterException {
        HashMap<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        ZXingImageWriter.writeToStream(bitMatrix, format, outputStream);
    }


    /**
     * @param text
     * @param width
     * @param height
     * @param format
     * @param logoPath
     * @param outputStream
     * @throws IOException
     * @throws WriterException
     */
    public static void generateTwoDimensionCodeWithIMallLog(String text,
                                                            int width,
                                                            int height,
                                                            String format,
                                                            String logoPath,
                                                            OutputStream outputStream) throws IOException, WriterException {
        HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        ZXingImageWriter.writeToStreamWithLogo(bitMatrix, format, logoPath, outputStream);
    }

}

