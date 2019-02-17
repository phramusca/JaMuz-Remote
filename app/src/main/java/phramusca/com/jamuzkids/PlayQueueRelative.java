package phramusca.com.jamuzkids;

import java.util.ArrayList;

public class PlayQueueRelative {
    private int position;
    private int offset;
    private ArrayList<Track> tracks;

    public PlayQueueRelative(int position, int offset, ArrayList<Track> tracks) {
        this.position = position;
        this.offset = offset;
        this.tracks = tracks;
    }

    public PlayQueueRelative() {
        this.position = -1;
        this.offset = -1;
        this.tracks = new ArrayList<>();
    }

    public int getPosition() {
        return position;
    }

    public int getOffset() {
        return offset;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }
}
