package botbackend;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RSSNewsRequest {

    private HashMap<RSSNames, Integer> lastTime;

    public RSSNewsRequest(){
        Long currentTime = System.currentTimeMillis() / 1000L;
        lastTime = new HashMap<>();
        for(RSSNames rssNames : RSSNames.values()){
            lastTime.put(rssNames, Math.toIntExact(currentTime - 86400)); //делаем последнее время = день
        }
    }

    public ArrayList<News> getRSSNews(RSSNames rssNames, int percentzip, int maxcount) throws Exception {
        ArrayList<News> result = new ArrayList<>();

        if(maxcount <= 0){
            return result;
        }

        Integer lastPostTime = lastTime.get(rssNames);
        Integer maxPostTime = lastTime.get(rssNames);
        SyndFeed syndFeed = new SyndFeedInput().build(new XmlReader(new URL(rssNames.ID())));
        List<SyndEntry> list = syndFeed.getEntries();
        int iter = 0;
        for(SyndEntry feed : list){
            Integer time = Math.toIntExact(feed.getPublishedDate().getTime() / 1000L);
            if(time > lastPostTime){
                String linkFeed = feed.getLink();
                String text = "";
                if(!feed.getAuthor().isEmpty()){
                    text += "\"" + feed.getAuthor() + ".\" ";
                }
                if(!feed.getTitle().isEmpty()){
                    text += feed.getTitle() + ". ";
                }
                String links = "";

                SyndContent content = feed.getDescription();
                if (content != null){
                    text += content.getValue();
                }
                text = text.replaceAll("&quot;", "\"");
                result.add(new News(linkFeed, text, null, links, time));
                if(maxPostTime < time){
                    maxPostTime = time;
                }
            }
            if(++iter >= maxcount)
                break;
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
            lastTime.put(rssNames, maxPostTime);
        }
        return result;
    }

}