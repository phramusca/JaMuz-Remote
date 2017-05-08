package phramusca.com.jamuzremote;

/**
 * Created by raph on 01/05/17.
 */
public class Track {
    private int rating;
    private String title;
    private String album;
    private String artist;
    private String coverHash;

    public Track(int rating, String title, String album, String artist, String coverHash) {
        this.rating = rating;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.coverHash = coverHash;
    }

    @Override
    public String toString() {
        return "<b>" + title + "</b><BR/>" + //NOI18N
                "<i>" + album + "</i><BR/>" + //NOI18N
                "" + artist + ""; //NOI18N
    }

    public String getCoverHash() {
        return coverHash;
    }

    public int getRating() {
        return rating;
    }
}
