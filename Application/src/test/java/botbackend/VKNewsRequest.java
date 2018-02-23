package botbackend;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.base.Link;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.queries.users.UserField;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class VKNewsRequest {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private Properties properties;
    private HttpTransportClient transportClient;
    private VkApiClient vk;
    private ServiceActor serviceActor;
    private Integer clientid;
    private String servisetoken;
    private HashMap<VKNames, Integer> lastTime;


    public VKNewsRequest(){
        loadConfiguration();
        clientid = Integer.valueOf(properties.getProperty("client.id"));
        servisetoken = properties.getProperty("servise.token");
        transportClient = new HttpTransportClient();
        vk = new VkApiClient(transportClient);
        serviceActor = new ServiceActor(clientid, servisetoken);
        Long currentTime = System.currentTimeMillis() / 1000L;
        lastTime = new HashMap<>();
        for(VKNames vkNames : VKNames.values()){
            lastTime.put(vkNames, Math.toIntExact(currentTime - 86400)); //делаем последнее время = день
        }
    }

    public ArrayList<News> getVKNews(VKNames vkNames, int percentzip, int maxcount) throws Exception{
        ArrayList<News> result = new ArrayList<>();

        if(maxcount <= 0){
            return result;
        }

        Integer lastPostTime = lastTime.get(vkNames);
        List<WallPostFull> list;
        list = vk.wall().get(serviceActor).
                ownerId(-vkNames.ID()).
                count(maxcount+1).
                filter(WallGetFilter.OWNER).
                execute().getItems();
        for(WallPostFull post : list){
            if(post.getDate() > lastPostTime){
                String linkPost = "https://vk.com/wall-" +
                        (-post.getOwnerId()) + "_" + post.getId();
                String text;
                Integer time;
                String links = "";
                ArrayList<BufferedImage> vkImages = null;
                text = post.getText();
                time = post.getDate(); // Они держат время поста в int и лет через 15 он переполниться у них
                List<WallpostAttachment> wallpostAttachments = post.getAttachments();

                if( wallpostAttachments != null) {

                    for (WallpostAttachment wallpostAttachment : wallpostAttachments) {
                        Link link = wallpostAttachment.getLink();
                        if (link != null) {
                            links += link.getUrl() + "\n";
                        }

                        Photo photo = wallpostAttachment.getPhoto();
                        if (photo != null) {
                            if (vkImages == null) {
                                vkImages = new ArrayList<>();
                                vkImages.add(takeBestPicture(photo));
                            } else {
                                vkImages.add(takeBestPicture(photo));
                            }
                        }
                    }
                }
                result.add(new News(linkPost, text, vkImages, links, time));
            }
        }
        for(int i = 0; i < result.size(); i++){
            Thread thread = result.get(i);
            thread.start();
        }

        for(int i = 0; i < result.size(); i++){
            Thread thread = result.get(i);
            thread.join();
        }

        if(result.size() > 0){
            lastPostTime = result.get(0).getTime();
            lastTime.put(vkNames, lastPostTime);
        }

        return result;
    }

    private BufferedImage takeBestPicture(Photo photo) throws Exception{
        BufferedImage image;
        URL url;
        if(photo.getPhoto2560() != null){
            url = new URL(photo.getPhoto2560());
        }
        else if(photo.getPhoto1280() != null){
            url = new URL(photo.getPhoto1280());
        }
        else if(photo.getPhoto807() != null){
            url = new URL(photo.getPhoto807());
        }
        else if(photo.getPhoto604() != null){
            url = new URL(photo.getPhoto604());
        }
        else if(photo.getPhoto130() != null){
            url = new URL(photo.getPhoto130());
        }
        else if(photo.getPhoto75() != null){
            url = new URL(photo.getPhoto75());
        }
        else{
            throw new IOException("Can't find picture");
        }
        image = ImageIO.read(url);
        return image;
    }

    private void loadConfiguration() {
        properties = new Properties();
        try (InputStream is = Application.class.getResourceAsStream("/config.properties")) {
            properties.load(is);
        } catch (IOException e) {
            LOG.error("Can't load properties file", e);
            throw new IllegalStateException(e);
        }
    }
}
