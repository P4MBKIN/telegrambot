package botbackend;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

public class News {
    private String linkPost;
    private String text;
    private BufferedImage image;
    private BufferedImage allNewsPicture;
    private int time;
    private String links;

    public News(String LinkPost, String Text, BufferedImage Image, String Links, int Time){
        linkPost = LinkPost;
        text = Text;
        image = Image;
        links = Links;
        time = Time;
        if(Text.isEmpty()){
            allNewsPicture = Image;
        }
        else {
            allNewsPicture = MethodsNews.addTextToPicture(Image, Text, Color.BLACK);
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
