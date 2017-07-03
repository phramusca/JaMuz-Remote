package phramusca.com.jamuzremote;

/**
 * Created by raph on 17/06/17.
 */
public interface ICallBackPlayer {
    public void onPlayBackStart();
    public void onPositionChanged(int position, int duration);
    public void onPlayBackEnd();

    public void doPlayRandom();
    public void doPlayPrevious();
    public void doPlayNext();
}
