package phramusca.com.jamuzremote;

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
    public Status status=Status.NEW; //FIXME: DO NOT change from ACK to LOCAL (only to NEW if missing file)

    public FileInfoReception() {
    }

    //FIXME: Either use or remove LOCAL status from enum
    public enum Status {
        NEW, LOCAL, ACK;

        Status() {
        }
    }

    /**
     * @param json
     * @throws JSONException
     */
    public FileInfoReception(String json) throws JSONException {
        this(new JSONObject(json));
    }

    /**
     * @param file
     */
    public FileInfoReception(JSONObject file) {
        try {
            relativeFullPath = file.getString("path");
            size = file.getLong("size");
            idFile = file.getInt("idFile");
            rating = file.getInt("rating");
            addedDate = getDate(file, "addedDate");
            lastPlayed = getDate(file, "lastPlayed");
            playCounter = file.getInt("playCounter");
            genre = file.getString("genre");

            JSONArray jsonTags = (JSONArray) file.get("tags");
            tags = new ArrayList<>();
            for(int i=0; i<jsonTags.length(); i++) {
                String tag = (String) jsonTags.get(i);
                tags.add(tag);
            }
        } catch (JSONException e) {
        }
    }

    /**
     * @param file
     * @param id
     * @return
     */
    private Date getDate(JSONObject file, String id) {
        String dateStr = "";
        try {
            dateStr = file.getString(id);
        } catch (JSONException e) {
        }
        return HelperDateTime.parseSqlUtc(dateStr);
    }

    @Override
    public String toString() {
        return relativeFullPath+"\nSize: "+size+" bytes. idFile="+idFile;
    }
}
