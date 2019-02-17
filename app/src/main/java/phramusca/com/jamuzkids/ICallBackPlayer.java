package phramusca.com.jamuzkids;

/**
 * Created by raph on 17/06/17.
 */
public interface ICallBackPlayer {
    void onPlayBackStart();
    void onPositionChanged(int position, int duration);
    void onPlayBackEnd();
    void doPlayPrevious();
    void doPlayNext();
    void displaySpeechRecognizer();
    void reset();
}
