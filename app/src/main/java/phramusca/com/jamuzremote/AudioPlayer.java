package phramusca.com.jamuzremote;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import phramusca.com.jamuzremote.tags.BastpUtil;

/**
 * Created by raph on 17/06/17.
 */
public class AudioPlayer {

    private final ICallBackPlayer callback;
    private static final String TAG = AudioPlayer.class.getSimpleName();
    private static MediaPlayer mediaPlayer;
    private CountDownTimer timer;

    public AudioPlayer(final ICallBackPlayer callback) {
        this.callback = callback;
    }

    public void play(Track track) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(track.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            startTimer();

            applyReplayGain(mediaPlayer, track);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    callback.onPlayBackEnd();
                }
            });

            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    startTimer();
                }
            });

        } catch (IOException e) {
            Log.e(TAG, "Error playing (\""+track+"\") => DELETING IT !!!!!!", e);
            //TODO: Put back in FilesToGet if in FilesToKeep (take info from there)
            stop(false);
            File file = new File(track.getPath());
            file.delete();
            callback.onPlayBackEnd();
        }
    }

    //TODO: Make this an option.
    private static final boolean PREFERENCES_KEY_REPLAY_GAIN = true;

    //FIXME: These were default values. Try adjusting (find limits)
    private static final int PREFERENCES_KEY_REPLAY_GAIN_BUMP = 150; //Default: 150
    private static final int PREFERENCES_KEY_REPLAY_GAIN_UNTAGGED = 50; //Default: 0

    //https://www.programcreek.com/java-api-examples/index.php?class=android.media.MediaPlayer&method=setVolume
    //https://www.programcreek.com/java-api-examples/index.php?source_dir=Subsonic-master/app/src/main/java/github/daneren2005/dsub/service/DownloadService.java
    private void applyReplayGain(MediaPlayer mediaPlayer, Track track) {
        float[] rg = BastpUtil.getReplayGainValues(track.getPath()); /* track, album */

        Log.i(TAG, "rg[0] (track gain)="+rg[0]);
        Log.i(TAG, "rg[1] (album gain)="+rg[1]);
        float adjust = 0f;
        if (PREFERENCES_KEY_REPLAY_GAIN) {
            boolean singleAlbum = false;

/*              String replayGainType = prefs.getString(PREFERENCES_KEY_REPLAY_GAIN_TYPE, "1");
            // 1 => Smart replay gain
            // => Not useful here, not kept

            // 2 => Use album tags
            else if("2".equals(replayGainType)) {
                singleAlbum = true;
            }
            // 3 => Use track tags
            // Already false, no need to do anything here

*/
            // If playing a single album or no track gain, use album gain
            if((singleAlbum || rg[0] == 0) && rg[1] != 0) {
                adjust = rg[1];
            } else {
                // Otherwise, give priority to track gain
                adjust = rg[0];
            }
            Log.i(TAG, "adjust="+adjust);
            if (adjust == 0) {
            /* No RG value found: decrease volume for untagged song if requested by user */
                adjust = (PREFERENCES_KEY_REPLAY_GAIN_UNTAGGED - 150) / 10f;
            } else {
                adjust += (PREFERENCES_KEY_REPLAY_GAIN_BUMP - 150) / 10f;
            }
            Log.i(TAG, "adjust="+adjust);
        }
        Log.i(TAG, "volume="+volume);
        float rg_result = ((float) Math.pow(10, (adjust / 20))) * volume;
        Log.i(TAG, "rg_result="+rg_result);
        if (rg_result > 1.0f) {
            rg_result = 1.0f; /* android would IGNORE the change if this is > 1 and we would end up with the wrong volume */
        } else if (rg_result < 0.0f) {
            rg_result = 0.0f;
        }
        Log.i(TAG, "mediaPlayer.setVolume("+rg_result+", "+rg_result+")");
        mediaPlayer.setVolume(rg_result, rg_result);
    }

    private float volume = 1.0f;

    //FIXME: Shall I use this instead of system volume mixer ?
    public void setVolume(float volume, Track track) {
        if(mediaPlayer!=null && mediaPlayer.isPlaying()) { //mediaPlayer != null && (playerState == STARTED || playerState == PAUSED || playerState == STOPPED)) {
            try {
                this.volume = volume;
                reapplyVolume(track);
            } catch(Exception e) {
                Log.w(TAG, "Failed to set volume");
            }
        }
    }

    public void reapplyVolume(Track track) {
        applyReplayGain(mediaPlayer, track);
    }

    public void play() {
        if(mediaPlayer==null) {
            playNext();
        }
        else if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startTimer();
        }
    }

    public void playNext() {
        callback.doPlayNext();
    }

    public void playPrevious() {
        callback.doPlayPrevious();
    }

    public void togglePlay() {
        if(mediaPlayer ==null) {
            playNext();
        }
        else if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopTimer();
        } else {
            mediaPlayer.start();
            startTimer();
        }
    }

    public void pause() {
        if(mediaPlayer!=null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopTimer();
        }
    }

    public void resume() {
        if(mediaPlayer!=null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startTimer();
        }
    }

    public void stop(boolean release) {
        if (mediaPlayer !=null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            if(release) {
                mediaPlayer.release();
                mediaPlayer=null;
            }
            callback.onPositionChanged(0, 1);
        }
        stopTimer();
    }

    public void forward() {
        if(mediaPlayer != null) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+ mediaPlayer.getDuration()/10);
        }
    }

    public void rewind() {
        if(mediaPlayer != null) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - mediaPlayer.getDuration() / 10);
        }
    }

    public void pullUp() {
        if(mediaPlayer != null) {
            mediaPlayer.seekTo(0);
        }
    }

    public boolean isPlaying() {
        if(mediaPlayer == null || !mediaPlayer.isPlaying()) {
            return false;
        }
        return true;
    }

    private void startTimer() {
        callback.onPlayBackStart();

        timer = new CountDownTimer(mediaPlayer.getDuration()- mediaPlayer.getCurrentPosition()-1,500) {
            @Override
            public void onTick(long millisUntilFinished_) {
                if(mediaPlayer !=null) {
                    callback.onPositionChanged(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                }
                if(!isPlaying()) {
                    this.cancel();
                }
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void stopTimer() {
        if(timer!=null) {
            timer.cancel();
            timer=null;
        }
    }
}
