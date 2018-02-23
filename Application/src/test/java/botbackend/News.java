package botbackend;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class News extends Thread{
    private String linkPost;
    private String text;
    private ArrayList<BufferedImage> arrayImages;
    private BufferedImage image;
    private BufferedImage allNewsPicture;
    private int time;
    private String links;

    public News(String LinkPost, String Text, ArrayList<BufferedImage> ArrayImages, String Links, int Time){
        linkPost = LinkPost;
        text = Text;
        arrayImages = ArrayImages;
        links = Links;
        time = Time;
    }

    @Override
    public void run(){
        createAllNewsPicture();
    }

    public void createAllNewsPicture() {

        if(arrayImages != null){
            image = MethodsNews.createBigPicture(arrayImages);
        }
        if(text.isEmpty()){
            allNewsPicture = image;
        }
        else {
            allNewsPicture = MethodsNews.addTextToPicture(image, text, Color.BLACK);
        }
    }

    public String getLinkPost() {
        return linkPost;
    }

    public String getText(){
        return text;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getTime() {
        return time;
    }

    public String getLinks() {
        return links;
    }

    public BufferedImage getAllNewsPicture() {
        return allNewsPicture;
    }

    public void writeNews(String name, String path) throws IOException{
        if(allNewsPicture != null){
            ImageIO.write(allNewsPicture, "jpg",new File(path + name + "ALL" + ".jpg"));
        }
        if(image != null)
            ImageIO.write(image, "jpg",new File(path + name + ".jpg"));
        PrintWriter writer = new PrintWriter(path + name +".txt", "UTF-8");
        writer.println(linkPost + "\n");
        writer.println(new Date(time*1000) + "\n");
        writer.println(text);
        writer.println(links);
        writer.close();
    }
}
