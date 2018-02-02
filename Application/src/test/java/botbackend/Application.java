package botbackend;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.base.Link;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Anton Tsivarev on 15.10.16.
 */
//Pasha pidor
public class Application {

    public static void main(String[] args) throws Exception {
        VKNewsRequest tmp = new VKNewsRequest();

        ArrayList<News> allNews = tmp.getVKNews(VKNames.TEST_GROUP, 100, 10);
        for(int i = 0; i < allNews.size(); i++){
            allNews.get(i).writeNews("4CH"+ (i+1), "");
        }
    }
}
