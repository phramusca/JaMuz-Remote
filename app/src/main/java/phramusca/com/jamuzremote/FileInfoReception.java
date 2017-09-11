package phramusca.com.jamuzremote;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public FileInfoReception(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public FileInfoReception(JSONObject file) {
        try {
            relativeFullPath = file.getString("path");
            size = file.getLong("size");
            idFile = file.getInt("idFile");
            rating = file.getInt("rating");
            addedDate = getDate(file, "addedDate");
            lastPlayed = getDate(file, "lastPlayed");
            playCounter = file.getInt("playCounter");

            JSONArray jsonTags = (JSONArray) file.get("tags");
            tags = new ArrayList<>();
            for(int i=0; i<jsonTags.length(); i++) {
                String tag = (String) jsonTags.get(i);
                tags.add(tag);
            }
        } catch (JSONException e) {
        }
    }

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
