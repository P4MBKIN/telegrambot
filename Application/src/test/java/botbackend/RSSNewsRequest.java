package botbackend;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RSSNewsRequest {

    private HashMap<RSSNames, HashSet<Integer>> lastTime;

    public RSSNewsRequest(){
        lastTime = new HashMap<>();
        for(RSSNames rssNames : RSSNames.values()){
            lastTime.put(rssNames, new HashSet<>());
        }
    }

    public ArrayList<News> getRSSNews(RSSNames rssNames, int percentzip, int maxcount) throws Exception {
        ArrayList<News> result = new ArrayList<>();

        if(maxcount <= 0){
            return result;
        }


        SyndFeed syndFeed = new SyndFeedInput().build(new XmlReader(new URL(rssNames.ID())));
        List<SyndEntry> list = syndFeed.getEntries();
        int iter = 0;
        for(SyndEntry feed : list){
            Integer time = Math.toIntExact(feed.getPublishedDate().getTime() / 1000L);
            if(!lastTime.get(rssNames).contains(time)){
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
                lastTime.get(rssNames).add(time);
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

        return result;
    }

}
