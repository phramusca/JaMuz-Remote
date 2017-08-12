package phramusca.com.jamuzremote;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;

/**
 * Created by raph on 01/05/17.
 */
public class Track {
    private int id;
    private int rating=0;
    private String title="";
    private String album="";
    private String artist="";
    private String coverHash="";
    private String path;
    private String genre="";

    public Track(int id, int rating, String title, String album,
                 String artist, String coverHash, String path, String genre) {
        this.id = id;
        this.rating = rating;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.coverHash = coverHash;
        this.genre=genre;
        this.path = path;
    }

    @Override
    public String toString() {
        return   title + "<BR/>" +
                artist + "<BR/>"+
                album + "<BR/>"+
                genre + "<BR/>";
    }

    public int getId() {
        return id;
    }

    public String getCoverHash() {
        return coverHash;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title==null?"":title;
    }

    public String getAlbum() {
        return album==null?"":album;
    }

    public String getArtist() {
        return artist==null?"":artist;
    }

    public String getGenre() {
        return genre==null?"":genre;
    }

    public String getPath() {
        return path;
    }

    //TODO: Use the same cache system as for remote
    public byte[] getArt() {
        byte[]art=null;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            art = mmr.getEmbeddedPicture();
        } catch (final RuntimeException ex) {
            Log.e("Track", "Error reading art of "+toString());
        }
        return art;
    }
}
