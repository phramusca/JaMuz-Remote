package phramusca.com.jamuzremote;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

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
    public Date addedDate = new Date(0);
    public Date lastPlayed = new Date(0);
    public int playCounter=0;
    private ArrayList<String> tags = null;
    private ReplayGain.GainValues replayGain=new ReplayGain.GainValues();
    public String source="";


    //TODO: Store replaygain, no to read too often

    public Track(int id, int rating, String title, String album,
                 String artist, String coverHash, String path, String genre,
                 Date addedDate, Date lastPlayed, int playCounter) {
        this.id = id;
        this.rating = rating;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.coverHash = coverHash;
        this.genre=genre;
        this.path = path;
        this.addedDate = addedDate;
        this.lastPlayed = lastPlayed;
        this.playCounter = playCounter;
    }

    @Override
    public String toString() {
        return   title + "<BR/>" +
                artist + "<BR/>"+
                album + "<BR/>";
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

    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public void setPlayCounter(int playCounter) {
        this.playCounter = playCounter;
    }

    /**
     * Returns last played date in "yyyy-MM-dd HH:mm:ss" format
     * @return
     */
    public String getFormattedLastPlayed() {
        return HelperDateTime.formatUTCtoSqlUTC(this.lastPlayed);
    }

    /**
     * Returns last played date in "yyyy-MM-dd HH:mm:ss" format,
     * translated to local time.
     * @return
     */
    public String getLastPlayedLocalTime() {
        return HelperDateTime.formatUTCtoSqlLocal(this.lastPlayed);
    }

    /**
     * Returns added date in "yyyy-MM-dd HH:mm:ss" format
     * @return
     */
    public String getFormattedAddedDate() {
        return HelperDateTime.formatUTCtoSqlUTC(this.addedDate);
    }

    /**
     * Returns added date date in "yyyy-MM-dd HH:mm:ss" format,
     * translated to local time.
     * @return
     */
    public String getAddedDateLocalTime() {
        return HelperDateTime.formatUTCtoSqlLocal(this.addedDate);
    }

    public int getPlayCounter() {
        return playCounter;
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

    //TODO: Use the same cache system as for remote (that is not used by the way !!)
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

    public boolean update() {
        if(MainActivity.musicLibrary!=null) {
            return MainActivity.musicLibrary.updateTrack(this);
        }
        return false;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getTags(boolean force) {
        if(MainActivity.musicLibrary!=null && (force || tags==null)) {
            tags = MainActivity.musicLibrary.getTags(id);
        }
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    /**
     *
     * @param value
     */
    public void toggleTag(String value) {
        if(getTags(false).contains(value)) {
            tags.remove(value);
            MainActivity.musicLibrary.removeTag(id, value);
        }
        else {
            tags.add(value);
            MainActivity.musicLibrary.addTag(id, value);
        }
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public ReplayGain.GainValues getReplayGain(boolean read) {
        if(read || !replayGain.isValid()) {
            replayGain = ReplayGain.read(new File(path), path.substring(path.lastIndexOf(".")+1));
        }
        return replayGain;
    }
}
