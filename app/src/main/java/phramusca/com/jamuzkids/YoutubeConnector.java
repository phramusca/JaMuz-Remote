package phramusca.com.jamuzkids;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

// Adapted from https://github.com/abhi5658/search-youtube

public class YoutubeConnector {

    private YouTube youtube;

    //custom list of youtube which gets returned when searched for keyword
    //Returns a collection of search results that match the query parameters specified in the API request
    //By default, a search result set identifies matching video, channel, and playlist resources,
    //but you can also configure queries to only retrieve a specific type of resource
    private YouTube.Search.List query;

    //Developer API key a developer can obtain after creating a new project in google developer console
    //Developer has to enable YouTube Data API v3 in the project
    //Add credentials and then provide the Application's package name and SHA fingerprint
    public static String KEY;

    //SHA1 fingerprint of APP can be found by double clicking on the app signing report on right tab called gradle
    public static final String SHA1 = "A9:F0:59:BF:34:49:FB:67:4A:C8:08:A6:EC:E2:AD:BE:3E:1C:06:61";

    //Package name of the app that will call the YouTube Data API
    public static final String PACKAGENAME = "phramusca.com.jamuzremote";

    //maximum results that should be downloaded via the YouTube data API at a time
    private static final long MAXRESULTS = 25;

    public YoutubeConnector(Context context) {
        try {

            //TODO: Read only once at app start
            AssetManager am = context.getAssets();
            InputStream inputStream = am.open("keys.properties");
            Properties properties= new Properties();;
            properties.load(inputStream);
            KEY=properties.getProperty("youtube.key");

            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
                request.getHeaders().set("X-Android-Package", PACKAGENAME);
                request.getHeaders().set("X-Android-Cert",SHA1);
            }).build();

            // Define the API request for retrieving search results.
            query = youtube.search().list("id,snippet");
            query.setKey(KEY);
            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            query.setType("video");

            //setting fields which should be returned
            //setting only those fields which are required
            //for maximum efficiency
            //here we are retreiving fiels:
            //-kind of video
            //-video ID
            //-title of video
            //-description of video
            //high quality thumbnail url of the video
            query.setFields("items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url)");

        } catch (IOException e) {
            Log.e("YC", "Could not initialize: " + e);
        }
    }

    public List<YouTubeVideoItem> search(String keywords) {
        query.setQ(keywords);
        query.setMaxResults(MAXRESULTS);

        try {
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();
            List<YouTubeVideoItem> items = new ArrayList<>();
            if (results != null) {
                items = setItemsList(results.iterator());
            }
            return items;

        } catch (IOException e) {
            Log.e("YC", "Could not search: " + e);
            return null;
        }
    }

    private static List<YouTubeVideoItem> setItemsList(Iterator<SearchResult> iteratorSearchResults) {
        List<YouTubeVideoItem> tempSetItems = new ArrayList<>();
        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }
        while (iteratorSearchResults.hasNext()) {
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();
            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            //getKind() returns which type of resource it is which can be video, playlist or channel
            if (rId.getKind().equals("youtube#video")) {
                YouTubeVideoItem item = new YouTubeVideoItem();
                //URL of thumbnail is in the hierarchy snippet/thumbnails/high/url
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getHigh();
                //retrieving title,description,thumbnail url, id from the heirarchy of each resource
                //Video ID - id/videoId
                //Title - snippet/title
                //Description - snippet/description
                //Thumbnail - snippet/thumbnails/high/url
                item.setId(singleVideo.getId().getVideoId());
                item.setTitle(singleVideo.getSnippet().getTitle());
                item.setDescription(singleVideo.getSnippet().getDescription());
                item.setThumbnailURL(thumbnail.getUrl());
                tempSetItems.add(item);
            }
        }
        return tempSetItems;
    }
}