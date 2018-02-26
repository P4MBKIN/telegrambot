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
import java.util.*;

public class VKNewsRequest {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private Properties properties;
    private HttpTransportClient transportClient;
    private VkApiClient vk;
    private ServiceActor serviceActor;
    private Integer clientid;
    private String servisetoken;
    private HashMap<VKNames, HashSet<Integer>> lastTime;


    public VKNewsRequest(){
        loadConfiguration();
        clientid = Integer.valueOf(properties.getProperty("client.id"));
        servisetoken = properties.getProperty("servise.token");
        transportClient = new HttpTransportClient();
        vk = new VkApiClient(transportClient);
        serviceActor = new ServiceActor(clientid, servisetoken);
        lastTime = new HashMap<>();
        for(VKNames vkNames : VKNames.values()){
            lastTime.put(vkNames, new HashSet<>()); //делаем последнее время = день
        }
    }

    /**
     * Получение набора новостей по определенной теме
     * @param vkNames
     * @param percentzip
     * @param maxcount
     * @return
     * @throws Exception
     */
    public ArrayList<News> getVKNews(VKNames vkNames, int percentzip, int maxcount) throws Exception{
        ArrayList<News> result = new ArrayList<>();

        if(maxcount <= 0){
            return result;
        }

        List<WallPostFull> list;
        list = vk.wall().get(serviceActor).
                ownerId(-vkNames.ID()).
                count(maxcount).
                filter(WallGetFilter.OWNER).
                execute().getItems();
        for(WallPostFull post : list){
            Integer time = post.getDate();
            if(!lastTime.get(vkNames).contains(time)){
                String linkPost = "https://vk.com/wall-" +
                        (-post.getOwnerId()) + "_" + post.getId();
                String text;
                String links = "";
                ArrayList<BufferedImage> vkImages = null;
                text = post.getText();
                time = post.getDate();
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
                lastTime.get(vkNames).add(time);
                if((vkImages == null) && (text.isEmpty())){
                    continue;
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

        return result;
    }

    /**
     * Получение лучшего по качеству изображения из сети
     * @param photo
     * @return
     * @throws Exception
     */
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
