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
    private String lyrics = "";
    private Date pathModifDate = new Date(0);
    private String pathMbId = "";
    private String comment = "";
    private int idPath = -1;
    private String albumArtist = "";
    private String year = "";
    private int trackNo = -1;
    private int trackTotal = -1;
    private int discNo = -1;
    private int discTotal = -1;
    private String bitRate = "";
    private String format;
    private double BPM = -1;
    private Date modifDate = new Date(0);
    private String checkedFlag = "";
    private String copyRight = "";
    private boolean isHistory=false;
    private boolean isUser=false; //FIXME Change to isLocked and lock when inserting playlists, not only track
    private boolean isSync=false;
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
    private ReplayGain.GainValues replayGain=new ReplayGain.GainValues();
    private String coverHash="";

    /** From database
     * @param pathModifDate
     * @param pathMbId
     * @param comment
     * @param idPath -
     * @param albumArtist -
     * @param year -
     * @param trackNo -
     * @param trackTotal -
     * @param discNo -
     * @param discTotal -
     * @param bitRate -
     * @param format -
     * @param bpm -
     * @param modifDate
     * @param checkedFlag
     * @param copyRight
     * @param getAppDataPath
     * @param idFileRemote -
     * @param idFileServer -
     * @param rating -
     * @param title -
     * @param album -
     * @param artist -
     * @param coverHash
     * @param path -
     * @param genre -
     * @param addedDate -
     * @param lastPlayed -
     * @param playCounter -
     * @param status -
     * @param size -
     * @param length -
     */
    public Track(Date pathModifDate, String pathMbId, String comment, int idPath,
                 String albumArtist, String year, int trackNo, int trackTotal,
                 int discNo, int discTotal, String bitRate, String format, double bpm,
                 Date modifDate, String checkedFlag, String copyRight, File getAppDataPath,
                 int idFileRemote, int idFileServer, double rating, String title, String album,
                 String artist, String coverHash, String path, String genre, Date addedDate,
                 Date lastPlayed, int playCounter, String status, long size, int length,
                 float trackGain, float albumGain) {
        this.pathModifDate = pathModifDate;
        this.pathMbId = pathMbId;
        this.comment = comment;
        this.idPath = idPath;
        this.albumArtist = albumArtist;
        this.year = year;
        this.trackNo = trackNo;
        this.trackTotal = trackTotal;
        this.discNo = discNo;
        this.discTotal = discTotal;
        this.bitRate = bitRate;
        this.format = format;
        this.BPM = bpm;
        this.modifDate = modifDate;
        this.checkedFlag = checkedFlag;
        this.copyRight = copyRight;
        this.idFileRemote = idFileRemote;
        this.idFileServer = idFileServer;
        this.rating = rating;
        this.title = title;
        this.album = album;
        this.artist = artist;
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
        this.coverHash = coverHash;
        this.replayGain =new ReplayGain.GainValues(trackGain, albumGain);
    }

    /** Track to display
     * @param albumArtist -
     * @param year -
     * @param trackNo -
     * @param trackTotal -
     * @param discNo -
     * @param discTotal -
     * @param bitRate -
     * @param format
     * @param bpm -
     * @param rating -
     * @param title -
     * @param album -
     * @param artist -
     * @param coverHash -
     * @param genre -
     */
    public Track(String albumArtist, String year, int trackNo, int trackTotal, int discNo,
                 int discTotal, String bitRate, String format, double bpm, double rating, String title,
                 String album, String artist, String coverHash, String genre) {
        this.albumArtist = albumArtist;
        this.year = year;
        this.trackNo = trackNo;
        this.trackTotal = trackTotal;
        this.discNo = discNo;
        this.discTotal = discTotal;
        this.bitRate = bitRate;
        this.format = format;
        this.BPM = bpm;
        this.rating = rating;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.coverHash = coverHash;
        this.genre=genre;
        this.source="Remote";
    }

    /** Creates a LOCAL track
     * @param getAppDataPath
     * @param absolutePath
     */
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
            lastPlayed = getDate(file, "lastPlayed");
            JSONArray jsonTags = (JSONArray) file.get("tags");
            tags = new ArrayList<>();
            for(int i = 0; i < jsonTags.length(); i++) {
                String tag = (String) jsonTags.get(i);
                tags.add(tag);
            }
            relativeFullPath = file.getString("path");
            path = new File(getAppDataPath, relativeFullPath)
                    .getAbsolutePath();
            idFileServer = file.getInt("idFile");
            genre = file.getString("genre");
            playCounter = file.getInt("playCounter");
            //FIXME: Use those below, to improve sync and/or merge
//            boolean deleted = file.getBoolean("deleted");
//            //Those are only valid during merge process
//            int previousPlayCounter = file.getInt("previousPlayCounter");
//            Date genreModifDate = getDate(file, "genreModifDate");
//            Date tagsModifDate = getDate(file, "tagsModifDate");
//            Date ratingModifDate = getDate(file, "ratingModifDate");
//            //END Those are only valid during merge process
            if(!statsOnly) {
                artist = file.getString("artist");
                title = file.getString("title");
                album = file.getString("album");
                length = file.getInt("length");
                size = file.getLong("size");
                status = Status.valueOf(file.getString("status"));
                idPath = file.getInt("idPath");
                albumArtist = file.getString("albumArtist");
                year = file.getString("year");
                trackNo = file.getInt("trackNo");
                trackTotal = file.getInt("trackTotal");
                discNo = file.getInt("discNo");
                discTotal = file.getInt("discTotal");
                bitRate = file.getString("bitRate");
                format = file.getString("format");
                BPM = file.getDouble("BPM");
                checkedFlag = file.getString("checkedFlag");
                copyRight = file.getString("copyRight");
                coverHash = file.getString("coverHash");
                modifDate = getDate(file, "modifDate");
                pathModifDate = getDate(file, "pathModifDate");
                pathMbId = file.getString("pathMbid");
                comment = file.getString("comment");
                JSONObject replayGainJsonObject = file.getJSONObject("replaygain");
                ReplayGain.GainValues replayGainServer = new ReplayGain.GainValues();
                replayGainServer.setTrackGain((float) replayGainJsonObject.getDouble("trackGain"));
                replayGainServer.setAlbumGain((float) replayGainJsonObject.getDouble("albumGain"));
                replayGain = replayGainServer;
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

    public Integer getIdPath() {
        return idPath;
    }

    public void setIdPath(Integer idPath) {
        this.idPath = idPath;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(Integer trackNo) {
        this.trackNo = trackNo;
    }

    public Integer getTrackTotal() {
        return trackTotal;
    }

    public void setTrackTotal(Integer trackTotal) {
        this.trackTotal = trackTotal;
    }

    public Integer getDiscNo() {
        return discNo;
    }

    public void setDiscNo(Integer discNo) {
        this.discNo = discNo;
    }

    public Integer getDiscTotal() {
        return discTotal;
    }

    public void setDiscTotal(Integer discTotal) {
        this.discTotal = discTotal;
    }

    public String getBitrate() {
        return bitRate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Double getBPM() {
        return BPM;
    }

    public void setBPM(Double bpm) {
        this.BPM = bpm;
    }

    public Date getModifDate() {
        return modifDate;
    }

    public void setModifDate(Date modifDate) {
        this.modifDate = modifDate;
    }

    public String getCheckedFlag() {
        return checkedFlag;
    }

    public void setCheckedFlag(String checkedFlag) {
        this.checkedFlag = checkedFlag;
    }

    public String getCopyRight() {
        return copyRight;
    }

    public void setCopyRight(String copyRight) {
        this.copyRight = copyRight;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public Date getPathModifDate() {
        return pathModifDate;
    }

    public void setPathModifDate(Date pathModifDate) {
        this.pathModifDate = pathModifDate;
    }

    public String getPathMbId() {
        return pathMbId;
    }

    public void setPathMbId(String pathMbId) {
        this.pathMbId = pathMbId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public enum Status {
        NEW, REC, LOCAL, INFO, ERROR;

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

    public String getCoverHash() {
        return coverHash;
    }

    public Bitmap readCover() {
        Bitmap cover = null;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            byte[] art = mmr.getEmbeddedPicture();
            if (art != null) {
                cover = BitmapFactory.decodeByteArray(art, 0, art.length);
            }
        } catch (final RuntimeException ex) {
            Log.e("Track", "Error reading art of "+relativeFullPath+" "+ex);
        }
        return cover;
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
            jsonObject.put("idFile", (int)idFileServer);
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

    public boolean isSync() {
        return isSync;
    }

    public void setSync() {
        isSync = true;
    }
}
