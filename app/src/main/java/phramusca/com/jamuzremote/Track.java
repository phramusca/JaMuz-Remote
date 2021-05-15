package phramusca.com.jamuzremote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by raph on 01/05/17.
 */
public class Track implements Serializable {
    private static final String TAG = Track.class.getName();
    private boolean isHistory=false;
    private boolean isUser=false;
    private int idFileRemote = -1;
    private int idFileServer = -1;
    private String artist = "";
    private String title = "";
    private String album = "";
    private String genre = "";
    private double rating = 0.0;
    private Date addedDate = new Date(0);
    private int playCounter = 0;
    private Date lastPlayed = new Date(0);
    private Status status = Status.LOCAL;
    private String path = "";
    private String relativeFullPath = "";
    private ArrayList<String> tags = null;
    private String source="";
    private long size;
    private int length;
    //FIXME: Get replaygain from server and Store in db : no to read too often AND as a workaround for flac
    private ReplayGain.GainValues replayGain=new ReplayGain.GainValues();
		//jsonAsMap.put("replaygain", replaygain.toMap());
    //FIXME !!!!! Use a Repo for thumbnails !!!
    //=> Add a limit (FIFO) to those repos not to overload android memory
    //As it is read too many times now that there is album list
    //+ we need (by using transient) to ignore it in Intent serialization and re-read each time !
    private transient Bitmap thumb;
    //TODO: coverHash should be changed together with thumb above
    private String coverHash="";

    public Track(File getAppDataPath, int idFileRemote, int idFileServer, double rating, String title,
                 String album, String artist, String coverHash, String path, String genre,
                 Date addedDate, Date lastPlayed, int playCounter, String status, long size, int length) {
        this.idFileRemote = idFileRemote;
        this.idFileServer = idFileServer;
        this.rating = rating;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.coverHash = coverHash;
        this.genre=genre;
        this.path = path;
        this.length = length;
        if(getAppDataPath.getAbsolutePath().length()+1>path.length()) {
            this.relativeFullPath = path;
        } else {
            this.relativeFullPath = path.substring(getAppDataPath.getAbsolutePath().length()+1);
        }

        this.addedDate = addedDate;
        this.lastPlayed = lastPlayed;
        this.playCounter = playCounter;
        this.status = Status.valueOf(status);
        this.size = size;
    }

    public Track(double rating, String title, String album,
                 String artist, String coverHash, String genre) {
        this.rating = rating;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.coverHash = coverHash;
        this.genre=genre;
        source="Remote";
    }

    public Track(File getAppDataPath, String absolutePath) {
        this.path = absolutePath;
        try {
            this.relativeFullPath = path.substring(getAppDataPath.getAbsolutePath().length() + 1);
        } catch (StringIndexOutOfBoundsException ex) {
            Log.e(TAG, "Error getting relativeFullPath; path="+path+", getAppDataPath="+getAppDataPath, ex);
        }
    }

    public boolean readMetadata() {
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            return true;
        } catch (RuntimeException ex) {
            Log.e(TAG, "Error reading file tags "+path, ex);
        }
        return false;
    }

    Track(JSONObject file, File getAppDataPath, boolean statsOnly) {
        try {

            addedDate = getDate(file, "addedDate");
            rating = file.getInt("rating");
            int previousPlayCounter = file.getInt("previousPlayCounter");
            lastPlayed = getDate(file, "lastPlayed");
            JSONArray jsonTags = (JSONArray) file.get("tags");
            tags = new ArrayList<>();
            for(int i=0; i<jsonTags.length(); i++) {
                String tag = (String) jsonTags.get(i);
                tags.add(tag);
            }
            Date genreModifDate = getDate(file, "genreModifDate");
            relativeFullPath = file.getString("path");
            path = new File(getAppDataPath, relativeFullPath)
                    .getAbsolutePath();
            boolean deleted = file.getBoolean("deleted");
            Date tagsModifDate = getDate(file, "tagsModifDate");
            idFileServer = file.getInt("idFile");
            genre = file.getString("genre");
            playCounter = file.getInt("playCounter");

            if(!statsOnly) {
                artist = file.getString("artist");
                title = file.getString("title");
                album = file.getString("album");
                length = file.getInt("length");
                size = file.getLong("size");
                status = Status.valueOf(file.getString("status"));

                //FIXME: Store and use unsused variables below !!!
                // WARNING: Some do not have proper value on server !!
                String year = file.getString("year");
                int trackNo = file.getInt("trackNo");
                Date ratingModifDate = getDate(file, "ratingModifDate");
                int discNo = file.getInt("discNo");
                String bitRate = file.getString("bitRate");
                String lyrics = file.getString("lyrics");
                //FIXME: Change the way replayGain is used before setting it from server
//            JSONObject replayGainJsonObject = file.getJSONObject("replaygain");
//            ReplayGain.GainValues replayGainServer = new ReplayGain.GainValues();
//            replayGainServer.setTrackGain((float) replayGainJsonObject.getDouble("trackGain"));
//            replayGainServer.setAlbumGain((float) replayGainJsonObject.getDouble("albumGain"));
                //replayGain = replayGainServer;
                //FIXME: Change the way coverHash is used before setting it from server
                //coverHash = file.getString("coverHash");
                String checkedFlag = file.getString("checkedFlag");
                Date pathModifDate = getDate(file, "pathModifDate");
                String copyRight = file.getString("copyRight");
                String pathMbid = file.getString("pathMbid");
                int discTotal = file.getInt("discTotal");
                String format = file.getString("format");
                Date modifDate = getDate(file, "modifDate");
                int idPath = file.getInt("idPath");
                String albumArtist = file.getString("albumArtist");
                String comment = file.getString("comment");
                double BPM = file.getDouble("BPM");
                int trackTotal = file.getInt("trackTotal");
            }
        } catch (JSONException ex) {
            Log.e(TAG, "Error creating new Track "+file, ex);
        }
    }

    /**
     * @param jsonObject the one including date to get
     * @param id idFileRemote where to get date from file
     * @return Date from jsonObject
     */
    private Date getDate(JSONObject jsonObject, String id) {
        String dateStr = "";
        try {
            dateStr = jsonObject.getString(id);
        } catch (JSONException ignored) {
        }
        return HelperDateTime.parseSqlUtc(dateStr);
    }

    public int getIdFileServer() {
        return idFileServer;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRelativeFullPath() {
        return relativeFullPath;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setUser(boolean isUser) {
        this.isUser=isUser;
    }

    public boolean isUser() {
        return isUser;
    }

    public int getLength() {
        return length;
    }

    public enum Status {
        NEW, REC, LOCAL, INFO;

        Status() {
        }
    }

    @Override
    public String toString() {
        return  "<BR/>" + getTags() + " " + (int)rating+"/5" + " " + genre + "<BR/>" +
                getLastPlayedAgo() + getAddedDateAgo() +"<BR/>";
    }

    public String getLastPlayedAgo() {
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        return playCounter<=0?"Jamais joué. "
                : "Joué " + prettyTime.format(lastPlayed) + " ("+ playCounter + "x). ";
    }

    public String getAddedDateAgo() {
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        return "Ajouté " + prettyTime.format(addedDate)+". ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track that = (Track) o;
        return Objects.equals(relativeFullPath, that.relativeFullPath);
    }

    @Override
    public int hashCode() {
        return relativeFullPath != null ? relativeFullPath.hashCode() : 0;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }

    public int getIdFileRemote() {
        return idFileRemote;
    }

    public String getCoverHash() {
        return coverHash;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = lastPlayed;
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

    //FIXME: Use the same cache system as for remote (that is not used by the way !!)
    public byte[] getArt() {
        byte[]art=null;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            art = mmr.getEmbeddedPicture();
        } catch (final RuntimeException ex) {
            Log.e("Track", "Error reading art of "+relativeFullPath);
        }
        return art;
    }

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

    //TODO: Do not update all, only requested fields
    public boolean update() {
        return HelperLibrary.musicLibrary != null && HelperLibrary.musicLibrary.updateTrack(this, false);
    }

    /**
     *
     * @return boolean
     */
    public ArrayList<String> getTags(boolean force) {
        if(HelperLibrary.musicLibrary!=null && idFileRemote>-1 && (force || tags==null)) {
            tags = HelperLibrary.musicLibrary.getTags(idFileRemote);
        }
        return tags;
    }

    public String getTags() {
        String tagsString="";
        ArrayList<String> tags = getTags(false);
        if(tags!=null) {
            StringBuilder msg = new StringBuilder();
            for (String tag : tags) {
                msg.append(tag).append(",");
            }
            tagsString = msg.toString();
            if (tagsString.endsWith(",")) {
                tagsString = tagsString.substring(0, tagsString.length() - 1);
            }
        }
        return tagsString;
    }

    /**
     * @param value Tag value
     */
    public void toggleTag(String value) {
        if(HelperLibrary.musicLibrary!=null) {
            if (getTags(false).contains(value)) {
                tags.remove(value);
                HelperLibrary.musicLibrary.removeTag(idFileRemote, value);
            } else {
                tags.add(value);
                HelperLibrary.musicLibrary.addTag(idFileRemote, value);
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

    public void setIdFileRemote(int idFileRemote) {
        this.idFileRemote = idFileRemote;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("path", relativeFullPath);
            jsonObject.put("rating", (int)rating);
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

    public Status getStatus() {
        return status;
    }
}
