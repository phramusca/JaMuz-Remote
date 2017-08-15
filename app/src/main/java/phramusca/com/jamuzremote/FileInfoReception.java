package phramusca.com.jamuzremote;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by raph on 01/05/17.
 */
public class FileInfoReception {
    public String relativeFullPath;
    public long size;
    public int idFile;
    public int rating;
    public Date addedDate = new Date(0); //FIXME: watchout defaults
    public Date lastPlayed = new Date(0); //FIXME: watchout defaults
    public int playCounter;

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
        } catch (JSONException e) {
        }
    }

    private Date getDate(JSONObject file, String id) {
        Date date = new Date(); //FIXME: watchout default
        try {
            String dateStr = file.getString(id);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(dateStr);
        } catch (ParseException e) {

        } catch (JSONException e) {

        }
        return date;
    }

    @Override
    public String toString() {
        return relativeFullPath+"\nSize: "+size+" bytes. idFile="+idFile;
    }
}
