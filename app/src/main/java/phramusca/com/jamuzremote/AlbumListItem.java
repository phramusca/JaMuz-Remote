package phramusca.com.jamuzremote;

import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ALBUM;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ARTIST;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_COVER_HASH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_GENRE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_PLAY_COUNTER;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_RATING;

import android.database.Cursor;

public class AlbumListItem {
    private final String album;
    private final String artist;
    private final int nbTracks;
    private final double rating;
    private final String genre;
    private final String coverHash;

    public AlbumListItem(String album, String artist, int nbTracks, double rating, String genre, String coverHash) {
        this.album = album;
        this.artist = artist;
        this.nbTracks = nbTracks;
        this.rating = rating;
        this.genre = genre;
        this.coverHash = coverHash;
    }

    public static AlbumListItem fromCursor(Cursor c) {
        return new AlbumListItem(
                c.getString(c.getColumnIndex(COL_ALBUM)),
                c.getString(c.getColumnIndex(COL_ARTIST)),
                c.getInt(c.getColumnIndex(COL_PLAY_COUNTER)), //Ugly trick
                c.getDouble(c.getColumnIndex(COL_RATING)),
                c.getString(c.getColumnIndex(COL_GENRE)),
                c.getString(c.getColumnIndex(COL_COVER_HASH))
        );
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
}