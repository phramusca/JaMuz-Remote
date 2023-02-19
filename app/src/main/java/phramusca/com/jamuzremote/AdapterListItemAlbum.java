package phramusca.com.jamuzremote;

import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ALBUM;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ARTIST;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_COVER_HASH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_GENRE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ID_PATH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_PATH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_PLAY_COUNTER;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_RATING;

import android.database.Cursor;

public class AdapterListItemAlbum {
    private final String album;
    private final String artist;
    private final int nbTracks;
    private final double rating;
    private final String genre;
    private final String coverHash;
    private final String path;
    private final String idPath;

    public AdapterListItemAlbum(String album, String artist, int nbTracks, double rating, String genre, String coverHash, String path, String idPath) {
        this.album = album;
        this.artist = artist;
        this.nbTracks = nbTracks;
        this.rating = rating;
        this.genre = genre;
        this.coverHash = coverHash;
        this.path = path;
        this.idPath = idPath;
    }

    public static AdapterListItemAlbum fromCursor(Cursor c) {
        return new AdapterListItemAlbum(
                c.getString(c.getColumnIndexOrThrow(COL_TRACKS_ALBUM)),
                c.getString(c.getColumnIndexOrThrow(COL_TRACKS_ARTIST)),
                c.getInt(c.getColumnIndexOrThrow(COL_TRACKS_PLAY_COUNTER)), //Ugly trick => nbTracks
                c.getDouble(c.getColumnIndexOrThrow(COL_TRACKS_RATING)),
                c.getString(c.getColumnIndexOrThrow(COL_TRACKS_GENRE)),
                c.getString(c.getColumnIndexOrThrow(COL_TRACKS_COVER_HASH)),
                c.getString(c.getColumnIndexOrThrow(COL_TRACKS_PATH)),
                c.getString(c.getColumnIndexOrThrow(COL_TRACKS_ID_PATH)));
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public int getNbTracks() {
        return nbTracks;
    }

    public double getRating() {
        return rating;
    }

    public String getGenre() {
        return genre;
    }

    public String getCoverHash() {
        return coverHash;
    }

    public String getPath() {
        return path;
    }

    public String getIdPath() {
        return idPath;
    }
}