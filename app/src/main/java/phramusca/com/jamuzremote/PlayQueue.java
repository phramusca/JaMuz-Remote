package phramusca.com.jamuzremote;

import java.util.ArrayList;
import java.util.List;

public class PlayQueue {
    private static List<Track> queue= new ArrayList<>();
    private static int position=-1;

    //TODO: Make MAX_QUEUE_PREVIOUS and MAX_QUEUE_NEXT user options
    private static final int MAX_QUEUE_PREVIOUS = 10;
    private static final int MAX_QUEUE_NEXT = 20;

    synchronized static PlayQueueRelative getActivityList() {
        //TODO: Implement pagination
        // https://stackoverflow.com/questions/16661662/how-to-implement-pagination-in-android-listview
        if(position>-1) {
            int indexStart = (position-MAX_QUEUE_PREVIOUS)>0?position-MAX_QUEUE_PREVIOUS:0;
            int indexEnd   = (position+MAX_QUEUE_NEXT)<queue.size()?position+MAX_QUEUE_NEXT:queue.size()-1;
            ArrayList<Track> list = new ArrayList<>();
            list.addAll(queue.subList(indexStart, indexEnd+1));
            return new PlayQueueRelative(position, indexStart, list);
        }
        return new PlayQueueRelative();
    }

    synchronized static void insertNext(int oldPosition) {
        if(oldPosition!=position) {
            Track track = getTrack(oldPosition);
            if(track!=null) {
                if(oldPosition>position) {
                    queue.remove(oldPosition);
                }
                queue.add(position+1, track);
            }
        }
    }

    synchronized static void refresh(Playlist playlist) {
        for(int i=position+1; i<queue.size(); i++) {
            queue.remove(i);
        }
        add(new ArrayList<>(), playlist);
    }

    synchronized static void fill(Playlist playlist) {
        List<Integer> queueIds = new ArrayList<>();
        for(Track track : queue) {
            queueIds.add(track.getIdFileRemote());
        }
        add(queueIds, playlist);
    }

    private synchronized static void add(List<Integer> excluded, Playlist playlist) {
        int currentlyNext = queue.size()-position-1;
        if(playlist!=null && currentlyNext<MAX_QUEUE_NEXT+1 ) {
            List<Track> addToQueue = playlist.getTracks(MAX_QUEUE_NEXT-currentlyNext+2, excluded);
            queue.addAll(addToQueue);
        }
    }

    synchronized static Track getNext() {
        return getTrack(position+1);
    }

    synchronized static Track getPrevious() {
        return getTrack(position-1);
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
        position=0;
    }

    static void setNext() {
        position++;
    }

    static void setPrevious() {
        position--;
    }
}
