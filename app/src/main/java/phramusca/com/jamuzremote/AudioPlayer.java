package phramusca.com.jamuzremote;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class AudioPlayer implements IAudioPlayer {

    private final Context mContext;
    private final IListenerPlayer callback;
    private final SharedPreferences preferences;
    private final Map<ActivityMain.AudioOutput, IAudioPlayer> audioPlayers;

    public AudioPlayer(Context context, final IListenerPlayer callback, SharedPreferences preferences) {
        mContext = context;
        this.callback = callback;
        this.preferences = preferences;
        audioPlayers = new HashMap<>();
    }

    private IAudioPlayer getAudioPlayer() {
        ActivityMain.AudioOutput audioOutput = ActivityMain.AudioOutput.valueOf(preferences.getString("audioOutput", ActivityMain.AudioOutput.LOCAL.name()));
        if(!audioPlayers.containsKey(audioOutput)) {
            IAudioPlayer audioPlayer = null;
            switch (audioOutput) {
                case LOCAL:
                    audioPlayer = new AudioPlayerLocal(mContext, callback);
                    break;
                case RASPBERRY:
                    audioPlayer = new AudioPlayerRaspberry(mContext, callback);
                    break;
            }
            audioPlayers.put(audioOutput, audioPlayer);
        }
        return audioPlayers.get(audioOutput);
    }

    @Override
    public void play(Track track, HelperToast helperToast) {
        getAudioPlayer().play(track, helperToast);
    }

    @Override
    public String setVolume(int volume, Track track) {
        return getAudioPlayer().setVolume(volume, track);
    }

    @Override
    public void play() {
        getAudioPlayer().play();
    }

    @Override
    public void playNext() {
        getAudioPlayer().playNext();
    }

    @Override
    public void playPrevious() {
        getAudioPlayer().playPrevious();
    }

    @Override
    public void togglePlay() {
        getAudioPlayer().togglePlay();
    }

    @Override
    public void pause() {
        getAudioPlayer().pause();
    }

    @Override
    public void resume() {
        getAudioPlayer().resume();
    }

    @Override
    public void stop(boolean release) {
        getAudioPlayer().stop(release);
    }

    @Override
    public void forward() {
        getAudioPlayer().forward();
    }

    @Override
    public void rewind() {
        getAudioPlayer().rewind();
    }

    @Override
    public void pullUp() {
        getAudioPlayer().pullUp();
    }
}
