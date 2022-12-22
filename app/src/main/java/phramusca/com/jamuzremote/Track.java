package phramusca.com.jamuzremote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Created by raph on 01/05/17.
 */
public class Track implements Serializable {
    private static final String TAG = Track.class.getName();
    private final String lyrics = "";
    private Date pathModifDate = new Date(0);
    private String pathMbId = "";
    private String comment = "";
    private String idPath = "";
    private String albumArtist = "";
    private String year = "";
    private int trackNo = -1;
    private int trackTotal = -1;
    private int discNo = -1;
    private int discTotal = -1;
    private String bitRate = "";
    private String format = "";
    private double BPM = -1;
    private Date modifDate = new Date(0);
    private String checkedFlag = "";
    private String copyRight = "";
    private boolean isHistory = false;
    private boolean isLocked = false;
    private boolean isSync = false;
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
    private String source = "";
    private long size;
    private int length;
    private ReplayGain.GainValues replayGain = new ReplayGain.GainValues();
    private String coverHash = "";

    /**
     * From database
     *
     * @param pathModifDate  -
     * @param pathMbId       -
     * @param comment        -
     * @param idPath         -
     * @param albumArtist    -
     * @param year           -
     * @param trackNo        -
     * @param trackTotal     -
     * @param discNo         -
     * @param discTotal      -
     * @param bitRate        -
     * @param format         -
     * @param bpm            -
     * @param modifDate      -
     * @param checkedFlag    -
     * @param copyRight      -
     * @param getAppDataPath -
     * @param idFileRemote   -
     * @param idFileServer   -
     * @param rating         -
     * @param title          -
     * @param album          -
     * @param artist         -
     * @param coverHash      -
     * @param path           -
     * @param genre          -
     * @param addedDate      -
     * @param lastPlayed     -
     * @param playCounter    -
     * @param status         -
     * @param size           -
     * @param length         -
     */
    public Track(Date pathModifDate, String pathMbId, String comment, String idPath,
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
        this.genre = genre;
        this.path = path;
        this.length = length;
        if (getAppDataPath.getAbsolutePath().length() + 1 > path.length()) {
            this.relativeFullPath = path;
        } else {
            this.relativeFullPath = path.substring(getAppDataPath.getAbsolutePath().length() + 1);
        }
        this.addedDate = addedDate;
        this.lastPlayed = lastPlayed;
        this.playCounter = playCounter;
        this.status = Status.valueOf(status);
        this.size = size;
        this.coverHash = coverHash;
        this.replayGain = new ReplayGain.GainValues(trackGain, albumGain);
    }

    /**
     * Track to display
     *
     * @param albumArtist -
     * @param year        -
     * @param trackNo     -
     * @param trackTotal  -
     * @param discNo      -
     * @param discTotal   -
     * @param bitRate     -
     * @param format      -
     * @param bpm         -
     * @param rating      -
     * @param title       -
     * @param album       -
     * @param artist      -
     * @param coverHash   -
     * @param genre       -
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
        this.genre = genre;
        this.source = "Remote"; //NON-NLS
    }

    /**
     * Creates a LOCAL track
     *
     * @param absolutePath Track absolute path.
     */
    public Track(String absolutePath, String idPath) {
        this.path = absolutePath;
        this.idPath = idPath;
    }

    public boolean read(Context context) {
        try {
            MediaExtractor mex = new MediaExtractor();
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            Bitmap bitmap;
            addedDate=new Date();
            if (path.startsWith("content://")) {
                mex.setDataSource(context, Uri.parse(path), null);
                mmr.setDataSource(context, Uri.parse(path));
                bitmap = readCover(mmr);
                //TODO: Set those:
//                File file = new File(path);
//                size=file.length();
//                modifDate=new Date(file.lastModified());
//                pathModifDate=new Date(Objects.requireNonNull(folder).lastModified());
            } else {
                File file = new File(path);
                size=file.length();
                modifDate=new Date(file.lastModified());
                File folder = file.getParentFile();
                pathModifDate=new Date(Objects.requireNonNull(folder).lastModified());
                mex.setDataSource(path);
                mmr.setDataSource(path);
                bitmap = readCover(path);
            }
            if(bitmap != null) {
                coverHash = RepoCovers.readCoverHash(bitmap);
                if(!RepoCovers.contains(coverHash, RepoCovers.IconSize.COVER)) {
                    RepoCovers.writeIconsToCache(coverHash, bitmap, RepoCovers.IconSize.COVER);
                }
            }
            MediaFormat mf = mex.getTrackFormat(0);
            try {
                bitRate = String.valueOf(mf.getInteger(MediaFormat.KEY_BIT_RATE));
            } catch (NullPointerException ignored) {
                bitRate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            }
            try {
                length = (int) Math.round(mf.getLong(MediaFormat.KEY_DURATION)/1000.0/1000.0);
            } catch (NullPointerException ignored) {
                length = (int) Math.round(tryParseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION), 0)/1000.0);
            }
            format = mf.getString(MediaFormat.KEY_MIME);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            album = album==null||album.isEmpty()?"Unknown album":album;
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            artist = artist==null||artist.isEmpty()?"Unknown artist":artist;
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            title = title==null||title.isEmpty()?"Unknown title":title;
            genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            albumArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
            albumArtist = albumArtist==null||albumArtist.isEmpty()?"Unknown album artist":albumArtist;
            String yearMeta = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
            if(yearMeta!=null) {
                year = yearMeta;
            }
            String trackNumber = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
            if(trackNumber!=null) {
                if(trackNumber.contains("/")) {
                    String[] split = trackNumber.split("/");
                    trackNo = tryParseInt(split[0], -1);
                    trackTotal = tryParseInt(split[1], -1);
                } else {
                    trackNo = tryParseInt(trackNumber, -1);
                    trackTotal = tryParseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS), -1);
                }
            }
            String discNumber = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER);
            if(discNumber!=null) {
                if(discNumber.contains("/")) {
                    String[] split = discNumber.split("/");
                    discNo = tryParseInt(split[0], -1);
                    discTotal = tryParseInt(split[1], -1);
                } else {
                    discNo = tryParseInt(discNumber, -1);
                    discTotal = tryParseInt(discNumber, -1);
                }
            }
            //TODO: Get comment
            return true;
        } catch (RuntimeException | IOException | NoSuchAlgorithmException ex) {
            Log.e(TAG, "Error reading file tags " + path, ex); //NON-NLS
        }
        return false;
    }

    private int tryParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    Track(JSONObject file, File getAppDataPath, boolean statsOnly) {
        try {
            addedDate = getDate(file, "addedDate");
            rating = file.getInt("rating"); //NON-NLS
            lastPlayed = getDate(file, "lastPlayed");
            JSONArray jsonTags = (JSONArray) file.get("tags"); //NON-NLS
            tags = new ArrayList<>();
            for (int i = 0; i < jsonTags.length(); i++) {
                String tag = (String) jsonTags.get(i);
                tags.add(tag);
            }
            relativeFullPath = file.getString("path"); //NON-NLS
            path = new File(getAppDataPath, relativeFullPath)
                    .getAbsolutePath();
            idFileServer = file.getInt("idFile");
            genre = file.getString("genre"); //NON-NLS
            playCounter = file.getInt("playCounter");
            //TODO Use those below, to improve sync and/or merge
//            boolean deleted = file.getBoolean("deleted");
//            //Those are only valid during merge process
//            int previousPlayCounter = file.getInt("previousPlayCounter");
//            Date genreModifDate = getDate(file, "genreModifDate");
//            Date tagsModifDate = getDate(file, "tagsModifDate");
//            Date ratingModifDate = getDate(file, "ratingModifDate");
//            //END Those are only valid during merge process
            if (!statsOnly) {
                artist = file.getString("artist"); //NON-NLS //NON-NLS
                title = file.getString("title"); //NON-NLS
                album = file.getString("album"); //NON-NLS //NON-NLS
                length = file.getInt("length"); //NON-NLS
                size = file.getLong("size"); //NON-NLS
                status = Status.valueOf(file.getString("status")); //NON-NLS
                idPath = file.getString("idPath"); //NON-NLS
                albumArtist = file.getString("albumArtist");
                year = file.getString("year"); //NON-NLS
                trackNo = file.getInt("trackNo");
                trackTotal = file.getInt("trackTotal");
                discNo = file.getInt("discNo");
                discTotal = file.getInt("discTotal"); //NON-NLS
                bitRate = file.getString("bitRate");
                format = file.getString("format"); //NON-NLS
                BPM = file.getDouble("BPM");
                checkedFlag = file.getString("checkedFlag");
                copyRight = file.getString("copyRight");
                coverHash = file.getString("coverHash");
                modifDate = getDate(file, "modifDate");
                pathModifDate = getDate(file, "pathModifDate");
                pathMbId = file.getString("pathMbid"); //NON-NLS
                comment = file.getString("comment"); //NON-NLS
                JSONObject replayGainJsonObject = file.getJSONObject("replaygain"); //NON-NLS
                ReplayGain.GainValues replayGainServer = new ReplayGain.GainValues();
                replayGainServer.setTrackGain((float) replayGainJsonObject.getDouble("trackGain"));
                replayGainServer.setAlbumGain((float) replayGainJsonObject.getDouble("albumGain"));
                replayGain = replayGainServer;
            }
        } catch (JSONException ex) {
            Log.e(TAG, "Error creating new Track " + file, ex); //NON-NLS
        }
    }

    /**
     * @param jsonObject the one including date to get
     * @param id         idFileRemote where to get date from file
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

    public void setLocked(boolean isUser) {
        this.isLocked = isUser;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public int getLength() {
        return length;
    }

    public String getIdPath() {
        return idPath;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getYear() {
        return year;
    }

    public Integer getTrackNo() {
        return trackNo;
    }

    public Integer getTrackTotal() {
        return trackTotal;
    }

    public Integer getDiscNo() {
        return discNo;
    }

    public Integer getDiscTotal() {
        return discTotal;
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

    public Date getModifDate() {
        return modifDate;
    }

    public String getCheckedFlag() {
        return checkedFlag;
    }

    public String getCopyRight() {
        return copyRight;
    }

    public String getLyrics() {
        return lyrics;
    }

    public Date getPathModifDate() {
        return pathModifDate;
    }

    public String getPathMbId() {
        return pathMbId;
    }

    public String getComment() {
        return comment;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    public enum Status {
        NEW, REC, LOCAL, INFO, ERROR;

        Status() {
        }
    }

    @NonNull
    @Override
    public String toString() {
        return path;
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
     *
     * @return date in "yyyy-MM-dd HH:mm:ss" format
     */
    public String getFormattedLastPlayed() {
        return HelperDateTime.formatUTCtoSqlUTC(this.lastPlayed);
    }

    /**
     * Get Added Date (utc)
     *
     * @return date in "yyyy-MM-dd HH:mm:ss" format
     */
    public String getFormattedAddedDate() {
        return HelperDateTime.formatUTCtoSqlUTC(this.addedDate);
    }

    public int getPlayCounter() {
        return playCounter;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public String getAlbum() {
        return album == null ? "" : album;
    }

    public String getArtist() {
        return artist == null ? "" : artist;
    }

    public String getGenre() {
        return genre == null ? "" : genre;
    }

    public String getPath() {
        return path;
    }

    public String getCoverHash() {
        return coverHash;
    }

    public static Bitmap readCover(MediaMetadataRetriever mmr) {
        Bitmap cover = null;
        try {
            byte[] art = mmr.getEmbeddedPicture();
            if (art != null) {
                cover = BitmapFactory.decodeByteArray(art, 0, art.length); //NON-NLS
            } //NON-NLS
        } catch (final RuntimeException ex) { //NON-NLS
            Log.e("Track", "Error reading art of " + mmr.toString() + " " + ex); //NON-NLS
        }
        return cover;
    }

    public static Bitmap readCover(String path) {
        if(new File(path).exists()) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            return readCover(mmr);
        }
        return null;
    }

    //TODO: Do not update all, only requested fields
    public boolean update() {
        return HelperLibrary.musicLibrary != null
            && HelperLibrary.musicLibrary.updateTrack(this, false);
    }

    /**
     * @return boolean
     */
    public ArrayList<String> getTags(boolean force) {
        if (HelperLibrary.musicLibrary != null && idFileRemote > -1 && (force || tags == null)) {
            tags = HelperLibrary.musicLibrary.getTags(idFileRemote);
        }
        return tags;
    }

    public String getTags() {
        String tagsString = "";
        ArrayList<String> tags = getTags(false);
        if (tags != null) {
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
        if (HelperLibrary.musicLibrary != null) {
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
        if (HelperLibrary.musicLibrary != null) {
            setGenre(genre);
            HelperLibrary.musicLibrary.updateGenre(this);
            RepoAlbums.reset();
        }
        return false;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public ReplayGain.GainValues getReplayGain(boolean read) {
        if (read || !replayGain.isValid()) {
            replayGain = ReplayGain.read(new File(path), path.substring(path.lastIndexOf(".") + 1));
        }
        return replayGain;
    }

    public void delete() {
        if (HelperLibrary.musicLibrary != null) {
            HelperLibrary.musicLibrary.deleteTrack(path);
        }
    }

    public void setIdFileRemote(int idFileRemote) {
        this.idFileRemote = idFileRemote;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idFile", idFileServer); //NON-NLS
            jsonObject.put("path", relativeFullPath); //NON-NLS
            jsonObject.put("rating", (int) rating); //NON-NLS
            jsonObject.put("addedDate", getFormattedAddedDate());
            jsonObject.put("lastPlayed", getFormattedLastPlayed()); //NON-NLS
            jsonObject.put("playCounter", playCounter); //NON-NLS
            jsonObject.put("genre", genre); //NON-NLS
            JSONArray tagsAsMap = new JSONArray();
            getTags(false);
            for (String tag : tags) { //NON-NLS
                tagsAsMap.put(tag);
            }
            jsonObject.put("tags", tagsAsMap); //NON-NLS
        } catch (JSONException ignored) {
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
