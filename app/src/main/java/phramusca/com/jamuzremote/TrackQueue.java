package phramusca.com.jamuzremote;

import java.util.ArrayList;
import java.util.List;

public class TrackQueue extends TrackList {

    //TODO: Make MAX_QUEUE_PREVIOUS and MAX_QUEUE_NEXT user options
    private static final int MAX_QUEUE_PREVIOUS = 5;
    private static final int MAX_QUEUE_NEXT = 10;

    TrackQueue(List<Track> tracks, int positionPlaying) {
        super(tracks, positionPlaying);
    }

    synchronized PlayQueueRelative getActivityList() {
        //FIXME !!!!! Implement pagination
        if(positionPlaying >-1) {
            int indexStart = (positionPlaying - MAX_QUEUE_PREVIOUS) > 0 ? positionPlaying - MAX_QUEUE_PREVIOUS : 0;
            int indexEnd   = (positionPlaying + MAX_QUEUE_NEXT) < tracks.size()? positionPlaying + MAX_QUEUE_NEXT : tracks.size() - 1;
            ArrayList<Track> list = new ArrayList<>(tracks.subList(indexStart, indexEnd + 1));
            return new PlayQueueRelative(positionPlaying, indexStart, list);
        }
        return new PlayQueueRelative();
    }

    synchronized void refresh(Playlist playlist) {
        for (int i=tracks.size()-1;i>positionPlaying;i--) {
            Track track = tracks.get(i);
            if(!(track.isHistory() || track.isUser())) {
                tracks.remove(i);
            }
        }
        add(new ArrayList<>(), playlist);
    }

    synchronized void insert(Playlist playlist) {
        List<Track> insertIntotracks = playlist.getTracks();
        tracks.addAll(positionPlaying+1, insertIntotracks);
    }

    synchronized void insert(Track track) {
        tracks.add(positionPlaying+1, track);
    }

    synchronized List<Track> fill(Playlist playlist) {
        List<Integer> excluded = new ArrayList<>();
        for(Track track : tracks) {
            excluded.add(track.getIdFileRemote());
        }
        return add(excluded, playlist);
    }

    private synchronized List<Track> add(List<Integer> excluded, Playlist playlist) {
        List<Track> addToTracks=new ArrayList<>();
        if(playlist!=null ) {// && nbTracksAfterPlaying<MAX_QUEUE_NEXT+1 ) {
            addToTracks = playlist.getTracks(MAX_QUEUE_NEXT, excluded);
            tracks.addAll(addToTracks);
        }
        return addToTracks;
    }

    synchronized Track getNext() {
        return get(positionPlaying + 1);
    }

    synchronized Track getPrevious() {
        return get(positionPlaying - 1);
    }

    synchronized void setQueue(List<Track> tracks) {
        this.tracks = tracks;
        positionPlaying = 0;
        sendListener();
    }

    synchronized void setNext() {
        positionPlaying++;
        sendListener();
    }

    synchronized void setPrevious() {
        positionPlaying--;
        sendListener();
    }

    private ArrayList<IListenerQueue> mListListener = new ArrayList<>();

    private synchronized void sendListener() {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onPositionChanged(positionPlaying);
        }
    }

    synchronized void addListener(IListenerQueue aListener) {
        mListListener.add(aListener);
    }
}
