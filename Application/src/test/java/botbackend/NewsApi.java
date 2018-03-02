package botbackend;

public class NewsApi {
    public VKNewsRequest vk;
    public RSSNewsRequest rss;
    public int koef = 1;

    public NewsApi(VKNewsRequest a, RSSNewsRequest b){
        vk = a;
        rss = b;
    }
}