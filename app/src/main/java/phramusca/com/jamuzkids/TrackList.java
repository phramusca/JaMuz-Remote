package phramusca.com.jamuzkids;

import java.util.List;

public class TrackList {
    List<Track> tracks;
    int positionPlaying;

    TrackList(List<Track> tracks, int positionPlaying) {
        this.tracks = tracks;
        this.positionPlaying = positionPlaying;
    }

    synchronized public boolean insertNext(int oldPosition) {
        if(oldPosition!=positionPlaying) {
            Track track = get(oldPosition);
            if(track!=null) {
                tracks.remove(oldPosition);
                if(oldPosition<positionPlaying) {
                    positionPlaying--; //Not observed as done on both directly
                }
                track.setUser(true);
                tracks.add(positionPlaying+1, track);
                return true;
            }
        }
        return false;
    }

    synchronized public Track get(int position) {
        Track track=null;
        if(position<tracks.size() && position>=0) {
            track=tracks.get(position);
        }
        return track;
    }

    synchronized public void moveDown(int oldPosition) {
        if(oldPosition!=positionPlaying
                && oldPosition<tracks.size()-1) {
            Track track = get(oldPosition);
            if(track!=null) {
                tracks.remove(oldPosition);
                oldPosition++;
                Track movedUpTrack = get(oldPosition);
                movedUpTrack.setUser(true);
                if(oldPosition==positionPlaying) {
                    positionPlaying--; //Not observed as done on both directly
                }
                track.setUser(true);
                tracks.add(oldPosition, track);
            }
        }
    }

    synchronized public void remove(int position) {
        if(position!= positionPlaying) {
            Track track = tracks.get(position);
            if(track!=null) {
                tracks.remove(position);
                if(position<positionPlaying) {
                    positionPlaying--; //Not observed as done on both directly
                }
            }
        }
    }

    synchronized public void setPositionPlaying(int positionPlaying) {
        this.positionPlaying=positionPlaying;
    }

    synchronized public int size() {
        return tracks == null ? 0 : tracks.size();
    }

    synchronized public int getPositionPlaying() {
        return positionPlaying;
    }

    synchronized public void addBottom(List<Track> newTracks) {
        if(newTracks!=null) {
            tracks.addAll(newTracks);
        }
    }

    synchronized public void addTop(List<Track> newTracks) {
        if(newTracks!=null) {
            tracks.addAll(1, newTracks);
        }
    }

    synchronized public List<Track> get() {
        return tracks;
    }

    synchronized int addLoader() {
        tracks.add(null);
        return tracks.size() - 1;
    }

    synchronized void addLoaderTop() {
        tracks.add(0, null);
    }

    synchronized public void removeLoader(int position) {
        tracks.remove(position);
    }
}
