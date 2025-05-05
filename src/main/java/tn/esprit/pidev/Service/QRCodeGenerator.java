package tn.esprit.pidev.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating QR codes for 2FA.
 * This implementation uses the ZXing library to create real, scannable QR codes.
 */
public class QRCodeGenerator {

    /**
     * Generates a QR code image from a URL.
     * Uses the ZXing library to create a real, scannable QR code.
     *
     * @param url The URL to encode in the QR code
     * @param size The size of the QR code image
     * @return A JavaFX Image containing the QR code
     */
    public static Image generateQRCode(String url, int size) {
        try {
            // Set QR code parameters
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // High error correction
            hints.put(EncodeHintType.MARGIN, 1); // Smaller margin for better scanning

            // Create QR code writer
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, size, size, hints);

            // Convert to BufferedImage
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convert to JavaFX Image
            WritableImage writableImage = new WritableImage(size, size);
            PixelWriter pixelWriter = writableImage.getPixelWriter();

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    pixelWriter.setArgb(x, y, bufferedImage.getRGB(x, y));
                }
            }

            return writableImage;
        } catch (WriterException e) {
            System.err.println("Error generating QR code: " + e.getMessage());
            e.printStackTrace();

            // Return a simple error image if QR code generation fails
            return createErrorImage(size);
        }
    }

    /**
     * Creates a simple error image when QR code generation fails.
     *
     * @param size The size of the image
     * @return A JavaFX Image indicating an error
     */
    private static Image createErrorImage(int size) {
        WritableImage errorImage = new WritableImage(size, size);
        PixelWriter pixelWriter = errorImage.getPixelWriter();

        // Fill with light red background
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                pixelWriter.setColor(x, y, Color.rgb(255, 200, 200));
            }
        }

        // Draw X pattern
        for (int i = 0; i < size; i++) {
            int thickness = 3;
            for (int t = -thickness/2; t <= thickness/2; t++) {
                int x1 = i;
                int y1 = i + t;
                int x2 = i;
                int y2 = size - i - 1 + t;

                if (y1 >= 0 && y1 < size) {
                    pixelWriter.setColor(x1, y1, Color.RED);
                }
                if (y2 >= 0 && y2 < size) {
                    pixelWriter.setColor(x2, y2, Color.RED);
                }
            }
        }

        return errorImage;
    }
}
