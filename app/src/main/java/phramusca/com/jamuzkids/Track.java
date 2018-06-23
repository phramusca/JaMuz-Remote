package phramusca.com.jamuzkids;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by raph on 01/05/17.
 */
public class Track implements Serializable {
    private int id=-1;
    private int rating=0;
    private String title="";
    private String album="";
    private String artist="";
    private String coverHash="";
    private String path="";
    private String genre="";
    public Date addedDate = new Date(0);
    public Date lastPlayed = new Date(0);
    public int playCounter=0;
    private ArrayList<String> tags = null;
    private ReplayGain.GainValues replayGain=new ReplayGain.GainValues();
    public String source="";
    private boolean isHistory=false;
    private static final String TAG = Track.class.getName();

    //TODO: Store replaygain, no to read too often AND as a workaround for flac
    // replaygain that cannot be read

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

    public Track(String absolutePath) {
        try {
            this.path = absolutePath;
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(absolutePath);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        } catch (final RuntimeException ex) {
            Log.e(TAG, "Error reading file tags "+absolutePath, ex);
        }
    }

    @Override
    public String toString() {
        return  title + "<BR/>" +
                artist + "<BR/>"+
                album + "<BR/>";
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
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
     * Get Last Played Date (utc)
     * @return date in "yyyy-MM-dd HH:mm:ss" format
     */
    public String getFormattedLastPlayed() {
        return HelperDateTime.formatUTCtoSqlUTC(this.lastPlayed);
    }

    /**
     *  Get Last Played Date (local)
     * @return date in "yyyy-MM-dd HH:mm:ss" format,
     */
    public String getLastPlayedLocalTime() {
        return HelperDateTime.formatUTCtoSqlLocal(this.lastPlayed);
    }

    /**
     * Get Added Date (utc)
     * @return date in "yyyy-MM-dd HH:mm:ss" format
     */
    public String getFormattedAddedDate() {
        return HelperDateTime.formatUTCtoSqlUTC(this.addedDate);
    }

    /**
     * Get Added Date (local)
     * @return date in "yyyy-MM-dd HH:mm:ss" format,
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

    private Bitmap thumb;

    public Bitmap getTumb(boolean read) {
        if(thumb==null && read) {
            byte[]art=getArt();
            if(art!=null) {
                thumb = BitmapFactory.decodeByteArray(art, 0, art.length);
                thumb = Bitmap.createScaledBitmap(thumb, 120, 120, false);
            }
        }
        return thumb;
    }

    public boolean update() {
        return HelperLibrary.musicLibrary != null && HelperLibrary.musicLibrary.updateTrack(this);
    }

    /**
     *
     * @return boolean
     */
    public ArrayList<String> getTags(boolean force) {
        if(HelperLibrary.musicLibrary!=null && (force || tags==null)) {
            tags = HelperLibrary.musicLibrary.getTags(id);
        }
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    /**
     * @param value Tag value
     */
    public void toggleTag(String value) {
        if(HelperLibrary.musicLibrary!=null) {
            if (getTags(false).contains(value)) {
                tags.remove(value);
                HelperLibrary.musicLibrary.removeTag(id, value);
            } else {
                tags.add(value);
                HelperLibrary.musicLibrary.addTag(id, value);
            }
        }
    }

    public boolean updateGenre(String genre) {
        if(HelperLibrary.musicLibrary!=null) {
            setGenre(genre);
            HelperLibrary.musicLibrary.updateGenre(this);
        }
        return false;
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

    public void delete() {
        if(HelperLibrary.musicLibrary!=null) {
            HelperLibrary.musicLibrary.deleteTrack(path);
        }
    }

    public void setId(int id) {
        this.id=id;
    }

    public JSONObject toJSONObject(File getAppDataPath) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("path", path.substring(getAppDataPath.getAbsolutePath().length()+1));
            jsonObject.put("rating", rating);
            jsonObject.put("addedDate", getFormattedAddedDate());
            jsonObject.put("lastPlayed", getFormattedLastPlayed());
            jsonObject.put("playCounter", playCounter);
            jsonObject.put("genre", genre);
            JSONArray tagsAsMap = new JSONArray();
            getTags(false);
            for(String tag : tags) {
                tagsAsMap.put(tag);
            }
            jsonObject.put("tags", tagsAsMap);
        } catch (JSONException e) {
        }
        return jsonObject;
    }
}
