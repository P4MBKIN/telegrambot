package botbackend;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MethodsNews {

    public static BufferedImage createBigPicture(ArrayList<BufferedImage> arrImages){
        int width = 0;
        int height = 0;
        ArrayList<BufferedImage> resizablePictures = new ArrayList<>();
        for(BufferedImage image : arrImages) {
            width = Math.max(width, image.getWidth());
        }
        for(BufferedImage image : arrImages){
            double proportion = (double) width / image.getWidth();
            BufferedImage resizePicture = resizePicture(image,
                    (int)(proportion * image.getWidth()),
                    (int)(proportion * image.getHeight()));
            resizablePictures.add(resizePicture);
            height += (int)(proportion * image.getHeight());
        }
        BufferedImage bigPicture = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = bigPicture.createGraphics();
        Color oldColor = graphics2D.getColor();
        graphics2D.setPaint(Color.WHITE);
        graphics2D.fillRect(0, 0, width, height);
        graphics2D.setColor(oldColor);
        int y = 0;
        for(BufferedImage image : resizablePictures){
            graphics2D.drawImage(image, null, (width - image.getWidth())/2, y);
            y += image.getHeight();
        }
        return bigPicture;
    }

    public static BufferedImage resizePicture(BufferedImage img, int newW, int newH){
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
