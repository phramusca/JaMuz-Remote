package phramusca.com.jamuzremote;

interface IAudioPlayer {
    void play(Track track, HelperToast helperToast);
    String setVolume(int volume, Track track);
    void play();
    void playNext();
    void playPrevious();
    void togglePlay();
    void pause();
    void resume();
    void stop(boolean release);
    void forward();
    void rewind();
    void pullUp();
}
