package phramusca.com.jamuzremote;

/**
 * Created by raph on 17/06/17.
 */
public interface IListenerPlayer {
    void onPlayBackStart();

    void onPositionChanged(int position, int duration);

    void onPlayBackEnd();

    void doPlayPrevious();

    void doPlayNext();

    void reset();
}
