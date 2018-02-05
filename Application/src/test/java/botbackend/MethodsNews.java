package botbackend;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

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
        graphics2D.dispose();
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

    public static BufferedImage addTextToPicture(BufferedImage bufferedPicture,
                                                 String text, Color color){

        BufferedImage tmp = new BufferedImage(1,1,BufferedImage.TYPE_3BYTE_BGR);
        Font font = new Font("Pragmatica", Font.PLAIN, 16);
        Graphics2D g = tmp.createGraphics();
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(font);
        final FontMetrics fontMetrics = g.getFontMetrics();
        g.dispose();

        String[] words = text.split(" ");
        String line = "";
        List<String> lines = new ArrayList<>();
        int lineHeight = fontMetrics.getHeight();

        String maxSymvolsInLine = createLongString(33);
        String maxInLine = maxSymvolsInLine + "wwwwww";

        for (int i = 0; i < words.length; i++) {
            if (fontMetrics.stringWidth(line + words[i]) > fontMetrics.stringWidth(maxSymvolsInLine)) {
                lines.add(line);
                line = "";
            }
            line += words[i] + " ";
        }

        lines.add(line);


        BufferedImage bufferedImage = new BufferedImage(fontMetrics.stringWidth(maxInLine),
                lineHeight*(lines.size() + 2) , BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D graphics2D = bufferedImage.createGraphics();
        Color oldColor = graphics2D.getColor();
        graphics2D.setPaint(Color.WHITE);
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics2D.setColor(oldColor);
        graphics2D.dispose();

        int topX = fontMetrics.charWidth('w')*3;
        int topY = (int)(lineHeight*1.5);

        for(int i = 0; i < lines.size(); i++){
            addLineToImage(bufferedImage, lines.get(i), topX, topY, font, color);
            topY += lineHeight;
        }

        if(bufferedPicture != null){
            ArrayList<BufferedImage> allNewsPicture = new ArrayList<>();
            allNewsPicture.add(bufferedImage);
            allNewsPicture.add(bufferedPicture);
            return createBigPicture(allNewsPicture);
        }
        else {
            return bufferedImage;
        }
    }

    private static void addLineToImage(BufferedImage bufferedImage,
                                       String text,
                                       int topX, int topY,
                                       Font font,
                                       Color color){
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(color);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(font);
        g.drawString(text, topX, topY);
        g.dispose();

    }

    private static String createLongString(int len){
        String result = "";
        for(int i = 0; i < len; i++)
            result += 'w';
        return result;
    }

    private static BufferedImage deepCopy(BufferedImage bi){
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
