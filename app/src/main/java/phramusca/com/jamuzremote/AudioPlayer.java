package phramusca.com.jamuzremote;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by raph on 17/06/17.
 */
public class AudioPlayer {

    private final ICallBackPlayer callback;
    private static final String TAG = AudioPlayer.class.getName();
    private static MediaPlayer mediaPlayer;
    private CountDownTimer timer;

    public AudioPlayer(final ICallBackPlayer callback) {
        this.callback = callback;
    }

    public void play(String path) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            startTimer();

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
            Log.i(TAG, e.toString());
        }
    }

    public void play() {
        if(mediaPlayer==null) {
            playRandom();
        }
        else if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startTimer();
        }
    }

    public void playRandom() {
        callback.onPlayRandom();
    }

    public void togglePlay() {
        if(mediaPlayer ==null) {
            playRandom();
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
