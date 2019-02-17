package phramusca.com.jamuzremote;

import java.util.ArrayList;
import java.util.List;

public class PlayQueue {
    private static List<Track> queue= new ArrayList<>();
    private static int positionPlaying =-1;

    //TODO: Make MAX_QUEUE_PREVIOUS and MAX_QUEUE_NEXT user options
    private static final int MAX_QUEUE_PREVIOUS = 5;
    private static final int MAX_QUEUE_NEXT = 10;

    synchronized static PlayQueueRelative getActivityList() {
        //TODO: Implement pagination
        // https://stackoverflow.com/questions/16661662/how-to-implement-pagination-in-android-listview
        if(positionPlaying >-1) {
            int indexStart = (positionPlaying -MAX_QUEUE_PREVIOUS)>0? positionPlaying -MAX_QUEUE_PREVIOUS:0;
            int indexEnd   = (positionPlaying +MAX_QUEUE_NEXT)<queue.size()? positionPlaying +MAX_QUEUE_NEXT:queue.size()-1;
            ArrayList<Track> list = new ArrayList<>();
            list.addAll(queue.subList(indexStart, indexEnd+1));
            return new PlayQueueRelative(positionPlaying, indexStart, list);
        }
        return new PlayQueueRelative();
    }

    //TODO: Merge with the same ones on TrackAdapter
    synchronized static void insertNext(int oldPosition) {
        if(oldPosition!= positionPlaying) {
            Track track = getTrack(oldPosition);
            if(track!=null) {
                if(oldPosition> positionPlaying) {
                    queue.remove(oldPosition);
                }
                queue.add(positionPlaying +1, track);
            }
        }
    }

    synchronized static void moveDown(int oldPosition) {
        if(oldPosition!= positionPlaying) {
            Track track = getTrack(oldPosition);
            if(track!=null) {
                queue.remove(oldPosition);
                oldPosition++;
                if(oldPosition == positionPlaying) {
                    positionPlaying--;
                }
                queue.add(oldPosition, track);
            }
        }
    }

    synchronized static void refresh(Playlist playlist) {
        for(int i = positionPlaying +1; i<queue.size(); i++) {
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
        int currentlyNext = queue.size()- positionPlaying -1;
        if(playlist!=null && currentlyNext<MAX_QUEUE_NEXT+1 ) {
            List<Track> addToQueue = playlist.getTracks(MAX_QUEUE_NEXT-currentlyNext+2, excluded);
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
    }

    static void setNext() {
        positionPlaying++;
    }

    static void setPrevious() {
        positionPlaying--;
    }


}
