package phramusca.com.jamuzremote;

import java.util.ArrayList;
import java.util.List;

public class PlayQueue {
    private static List<Track> queue= new ArrayList<>();
    private static int positionPlaying =-1; //FIXME !!! Sync with real positionPlaying when it changes (and refresh display of course)

    //TODO: Make MAX_QUEUE_PREVIOUS and MAX_QUEUE_NEXT user options
    private static final int MAX_QUEUE_PREVIOUS = 5;
    private static final int MAX_QUEUE_NEXT = 10;

    synchronized static PlayQueueRelative getActivityList() {
        //FIXME !!!!! Implement pagination
        if(positionPlaying >-1) {
            int indexStart = (positionPlaying - MAX_QUEUE_PREVIOUS) > 0 ? positionPlaying - MAX_QUEUE_PREVIOUS : 0;
            int indexEnd   = (positionPlaying + MAX_QUEUE_NEXT) < queue.size()? positionPlaying + MAX_QUEUE_NEXT : queue.size() - 1;
            ArrayList<Track> list = new ArrayList<>(queue.subList(indexStart, indexEnd + 1));
            return new PlayQueueRelative(positionPlaying, indexStart, list);
        }
        return new PlayQueueRelative();
    }

    //TODO: Merge with the same ones on AdapterTrack
    synchronized static boolean insert(int oldPosition) {
        if(oldPosition!= positionPlaying) {
            Track track = getTrack(oldPosition);
            if(track!=null) {
                queue.remove(oldPosition);
                if(oldPosition<positionPlaying) {
                    positionPlaying--;
                    sendListener();
                }
                track.setUser(true);
                queue.add(positionPlaying +1, track);
                return true;
            }
        }
        return false;
    }

    synchronized static void moveDown(int oldPosition) {
        if(oldPosition!= positionPlaying
                && oldPosition<queue.size()-1) {
            Track track = getTrack(oldPosition);
            if(track!=null) {
                queue.remove(oldPosition);
                oldPosition++;
                Track movedUpTrack = getTrack(oldPosition);
                movedUpTrack.setUser(true);
                if(oldPosition == positionPlaying) {
                    positionPlaying--;
                    sendListener();
                }
                track.setUser(true);
                queue.add(oldPosition, track);
            }
        }
    }

    synchronized static void remove(int position) {
        if(position!= positionPlaying) {
            Track track = getTrack(position);
            if(track!=null) {
                queue.remove(position);
                if(position<positionPlaying) {
                    positionPlaying--;
                    sendListener();
                }
            }
        }
    }

    synchronized static void refresh(Playlist playlist) {
        for (int i=queue.size()-1;i>positionPlaying;i--) {
            Track track = queue.get(i);
            if(!(track.isHistory() || track.isUser())) {
                queue.remove(i);
            }
        }
        add(new ArrayList<>(), playlist);
    }

    synchronized static void insert(Playlist playlist) {
        List<Track> insertIntoQueue = playlist.getTracks();
        queue.addAll(positionPlaying+1, insertIntoQueue);
    }

    synchronized static void insert(Track track) {
        queue.add(positionPlaying+1, track);
    }

    synchronized static void fill(Playlist playlist) {
        List<Integer> excluded = new ArrayList<>();
        for(Track track : queue) {
            excluded.add(track.getIdFileRemote());
        }
        add(excluded, playlist);
    }

    private synchronized static void add(List<Integer> excluded, Playlist playlist) {
        int nbTracksAfterPlaying = queue.size()-1 - positionPlaying;
        if(playlist!=null && nbTracksAfterPlaying<MAX_QUEUE_NEXT+1 ) {
            List<Track> addToQueue = playlist.getTracks(MAX_QUEUE_NEXT-nbTracksAfterPlaying+2, excluded);
            queue.addAll(addToQueue);
        }
    }

    synchronized static Track getNext() {
        return getTrack(positionPlaying +1);
    }

    synchronized static Track getPrevious() {
        return getTrack(positionPlaying -1);
    }

    private synchronized static Track getTrack(int index) {
        Track track=null;
        if(index<queue.size() && index>=0) {
            track=queue.get(index);
        }
        return track;
    }

    synchronized static void setQueue(List<Track> tracks) {
        queue = tracks;
        positionPlaying =0;
        sendListener();
    }

    synchronized static void setNext() {
        positionPlaying++;
        sendListener();
    }

    synchronized static void setPrevious() {
        positionPlaying--;
        sendListener();
    }

    private static ArrayList<IListenerQueue> mListListener = new ArrayList<>();

    synchronized public static void addListener(IListenerQueue aListener) {
        mListListener.add(aListener);
    }

    synchronized static void sendListener() {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onPositionChanged(positionPlaying);
        }
    }
}
