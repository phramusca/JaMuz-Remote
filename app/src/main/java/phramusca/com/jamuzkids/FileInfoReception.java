package phramusca.com.jamuzkids;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by raph on 01/05/17.
 */
public class FileInfoReception {
    public String relativeFullPath;
    public long size;
    public int idFile;
    public int rating;
    public Date addedDate = new Date(0);
    public Date lastPlayed = new Date(0);
    public int playCounter;
    public ArrayList<String> tags = null;
    public String genre;
    public Status status=Status.NEW;

    FileInfoReception() {
    }

    public enum Status {
        NEW, LOCAL, IN_DB, ACK;

        Status() {
        }
    }

    /**
     * @param json FileInfoReception as JSON string
     * @throws JSONException on JSON failure
     */
    FileInfoReception(String json) throws JSONException {
        this(new JSONObject(json));
    }

    /**
     * @param file FileInfoReception as JSONObject
     */
    FileInfoReception(JSONObject file) {
        try {
            relativeFullPath = file.getString("path");
            size = file.getLong("size");
            idFile = file.getInt("idFile");
            rating = file.getInt("rating");
            addedDate = getDate(file, "addedDate");
            lastPlayed = getDate(file, "lastPlayed");
            playCounter = file.getInt("playCounter");
            genre = file.getString("genre");

            JSONArray files = (JSONArray) file.get("tags");

            JSONArray jsonTags = (JSONArray) file.get("tags");
            tags = new ArrayList<>();
            for(int i=0; i<jsonTags.length(); i++) {
                String tag = (String) jsonTags.get(i);
                tags.add(tag);
            }
        } catch (JSONException ignored) {
        }
    }

    /**
     * @param jsonObject the one including date to get
     * @param id id where to get date from file
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

    @Override
    public String toString() {
        return relativeFullPath+"\nSize: "+size+" bytes. idFile="+idFile+". status="+status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfoReception that = (FileInfoReception) o;
        return relativeFullPath != null ? relativeFullPath.equals(that.relativeFullPath) : that.relativeFullPath == null;
    }

    @Override
    public int hashCode() {
        return relativeFullPath != null ? relativeFullPath.hashCode() : 0;
    }
}