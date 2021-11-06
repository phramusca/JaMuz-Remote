package phramusca.com.jamuzremote;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by raph on 17/06/17.
 */
public class AudioPlayer {

    private Context mContext;
    private final IListenerPlayer callback;
    private static final String TAG = AudioPlayer.class.getName();
    private static MediaPlayer mediaPlayer;
    private static CountDownTimer timer;
    private int duration;
    private boolean enableControl = false;

    AudioPlayer(Context context, final IListenerPlayer callback) {
        mContext = context;
        this.callback = callback;
    }

    public void play(Track track, HelperToast helperToast) {
        try {
            Log.i(TAG, "Playing " + track.getRelativeFullPath()); //NON-NLS
            enableControl = false;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(track.getPath());
            mediaPlayer.prepare();
            callback.reset();
            String msg = applyReplayGain(mediaPlayer, track);
            if (!msg.equals("")) {
                helperToast.toastLong(msg);
            }
            mediaPlayer.setOnPreparedListener(mp -> {
                duration = mediaPlayer.getDuration();
                mediaPlayer.start();
                startTimer();
                mediaPlayer.setOnCompletionListener(mediaPlayer -> callback.onPlayBackEnd());
                mediaPlayer.setOnSeekCompleteListener(mediaPlayer -> startTimer());
                enableControl = true;
            });
        } catch (IOException e) {
            Log.e(TAG, "Error playing (\"" + track + "\") => DELETING IT !!!!!!", e); //NON-NLS
            //TODO: Put back in RepoSync (take info from there)
            stop(false);
            File file = new File(track.getPath());
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            callback.onPlayBackEnd();
        }
    }

    //TODO: Make Replaygain options.
    private final boolean mReplayGainTrackEnabled = true;
    private final boolean mReplayGainAlbumEnabled = false;
    private float baseVolume = 0.70f;

    /**
     * Enables or disables Replay Gain.
     * Taken partially from https://github.com/vanilla-music/vanilla
     */
    private String applyReplayGain(MediaPlayer mediaPlayer, Track track) {
        ReplayGain.GainValues rg = track.getReplayGain(false);
        Log.i(TAG, rg.toString());

        float adjust = 0f;
        if (rg.isValid()) {
            if (mReplayGainAlbumEnabled) {
                adjust = (rg.getTrackGain() != 0 ? rg.getTrackGain() : adjust); /* do we have track adjustment ? */
                adjust = (rg.getAlbumGain() != 0 ? rg.getAlbumGain() : adjust); /* ..or, even better, album adj? */
            }

            if (mReplayGainTrackEnabled || (mReplayGainAlbumEnabled && adjust == 0)) {
                adjust = (rg.getAlbumGain() != 0 ? rg.getAlbumGain() : adjust); /* do we have album adjustment ? */
                adjust = (rg.getTrackGain() != 0 ? rg.getTrackGain() : adjust); /* ..or, even better, track adj? */
            }
        }

        if (!mReplayGainAlbumEnabled && !mReplayGainTrackEnabled) {
            /* Feature is disabled: Make sure that we are going to 100% volume */
            adjust = 0f;
        }

        String msg = "";
        Log.i(TAG, "baseVolume=" + baseVolume); //NON-NLS
        float rg_result = ((float) Math.pow(10, (adjust / 20))) * baseVolume;
        Log.i(TAG, "rg_result=" + rg_result); //NON-NLS
        if (rg_result > 1.0f) {
            msg = String.format(
                    "%s \n%s\n---------------\n %s\n Base Volume=%s\n Adjust=%s\n Set Volume=%s (limit 1.0)", //NON-NLS
                    mContext.getString(R.string.audioPlayerToastRgBaseVolTooHigh),
                    mContext.getString(R.string.audioPlayerToastRgConsiderLower),
                    rg.toString(),
                    baseVolume,
                    adjust,
                    rg_result);
            rg_result = 1.0f; /* android would IGNORE the change if this is > 1
                                    and we would end up with the wrong volume */
        } else if (rg_result < 0.0f) {
            rg_result = 0.0f;
        }
        Log.i(TAG, "mediaPlayer.setVolume(" + rg_result + ", " + rg_result + ")"); //NON-NLS
        mediaPlayer.setVolume(rg_result, rg_result);

        return msg;
    }

    public String setVolume(int volume, Track track) {
        if (volume >= 0) {
            this.baseVolume = ((float) volume / 100.0f);
            Log.i(TAG, "setVolume()"); //NON-NLS
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                try {
                    return applyReplayGain(mediaPlayer, track);
                } catch (Exception e) {
                    Log.w(TAG, "Failed to set volume"); //NON-NLS
                }
            }
        }
        return "";
    }

    public void play() {
        Log.i(TAG, "play()"); //NON-NLS
        if (mediaPlayer == null) {
            playNext();
        } else if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startTimer();
        }
    }

    public void playNext() {
        callback.doPlayNext();
    }

    public void displaySpeechRecognizer() {
        callback.displaySpeechRecognizer();
    }

    public void playPrevious() {
        callback.doPlayPrevious();
    }

    public void togglePlay() { //NON-NLS
        Log.i(TAG, "togglePlay()"); //NON-NLS
        if (mediaPlayer == null) {
            playNext();
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopTimer();
        } else {
            mediaPlayer.start();
            startTimer();
        }
    }

    public void pause() {
        Log.i(TAG, "pause()"); //NON-NLS
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopTimer();
        }
    } //NON-NLS

    public void resume() {
        Log.i(TAG, "resume()"); //NON-NLS
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startTimer();
        } //NON-NLS
    }

    public void stop(boolean release) {
        Log.i(TAG, "stop()"); //NON-NLS
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                Log.i(TAG, "mediaPlayer.stop()" + mediaPlayer.getTrackInfo().toString()); //NON-NLS
                mediaPlayer.stop();
                if (release) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                callback.onPositionChanged(0, 1);
            }
            stopTimer();
        } catch (Exception e) { //NON-NLS //NON-NLS
            Log.w(TAG, "Failed to stop"); //NON-NLS
        }
    }

    public void forward() {
        if (mediaPlayer != null && enableControl) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + duration / 10);
        }
    }

    public void rewind() {
        if (mediaPlayer != null && enableControl) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - duration / 10);
        }
    }

    public void pullUp() {
        if (mediaPlayer != null && enableControl) {
            mediaPlayer.seekTo(0);
        }
    }

    public boolean isPlaying() {
        return !(mediaPlayer == null || !mediaPlayer.isPlaying());
    }

    private void startTimer() {
        callback.onPlayBackStart();
        timer = new CountDownTimer(duration - mediaPlayer.getCurrentPosition() - 1, 500) {
            @Override
            public void onTick(long millisUntilFinished_) {
                if (mediaPlayer != null) {
                    callback.onPositionChanged(mediaPlayer.getCurrentPosition(), duration);
                }
                if (!isPlaying()) {
                    this.cancel();
                }
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
