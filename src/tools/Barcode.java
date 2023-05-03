/*
package tools;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;


public class Barcode {
    public static BufferedImage getBarCodeImage(String data){
    try {
            Code128Writer barcodeWriter = new Code128Writer();
            BitMatrix bitMatrix = barcodeWriter.encode(data, BarcodeFormat.CODE_128, 100, 50);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
            WritableRaster raster = image.getRaster();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    raster.setSample(x, y, 0, bitMatrix.get(x, y) ? 0 : 255);
                }
            }
            return image;
            
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
*/