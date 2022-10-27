package phramusca.com.jamuzremote;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AudioPlayerRaspberry implements IAudioPlayer {

    private final Context mContext;
    private final IListenerPlayer callback;
    private static final String TAG = AudioPlayerRaspberry.class.getName();
    private static CountDownTimer timer;
    private int duration;
    private final ClientInfo clientInfo;
    protected static OkHttpClient client = new OkHttpClient();
    private Track track;
    private boolean isPlaying = false;

    public AudioPlayerRaspberry(Context context, final IListenerPlayer callback) {
        mContext = context;
        this.callback = callback;
        //FIXME: IP as option
        //FIXME Why port is -1 ? Should be 8080, confusing
        // 192.168.1.27 (dev local)
        // 192.168.1.145" (raspberry)
        clientInfo = new ClientInfo("192.168.1.145", 8079, "", "", -1, "", "");
    }

    private boolean isPlaying() {
        return isPlaying;
    }

    public void play(Track track, HelperToast helperToast) {
        this.track = track;
        Log.i(TAG, "Playing " + track.getRelativeFullPath()); //NON-NLS
        callback.reset();
        //FIXME: Apply replayGain !
//            String msg = applyReplayGain(mediaPlayer, track);
//            if (!msg.equals("")) {
//                helperToast.toastLong(msg);
//            }
        //FIXME: Get track duration
//            duration = mediaPlayer.getDuration();
        askFocusAndPlay();
        //FIXME: Get callbacks from raspberry
//            mediaPlayer.setOnCompletionListener(mediaPlayer -> callback.onPlayBackEnd());
//            mediaPlayer.setOnSeekCompleteListener(mediaPlayer -> startTimer());
    }

    private void askFocusAndPlay() {
        new Thread() {
            public void run() {
            HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("api/play/" + track.getIdFileServer()); //NON-NLS
            Request request = clientInfo.getRequestBuilder(urlBuilder).post(RequestBody.create("", MediaType.parse("application/json; charset=utf-8"))).build();
            Response response;
            try {
                response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    if (response.code() == 400) {
                        //TODO: Already playing
                    }
                    else if (response.code() == 404) {
                        //TODO: Not found
                    }
                } else {
                    isPlaying = true;
                    startTimer();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            }

        }.start();
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
                    rg,
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
        //FIXME: setVolume !
//        if (volume >= 0) {
//            this.baseVolume = ((float) volume / 100.0f);
//            Log.i(TAG, "setVolume()"); //NON-NLS
//            if (track != null && isPlaying()) {
//                try {
//                    return applyReplayGain(mediaPlayer, track);
//                } catch (Exception e) {
//                    Log.w(TAG, "Failed to set volume"); //NON-NLS
//                }
//            }
//        }
        return "";
    }

    public void play() {
        Log.i(TAG, "play()"); //NON-NLS
        if (track == null) {
            playNext();
        } else if (!isPlaying()) {
            askFocusAndPlay();
        }
    }

    public void playNext() {
        callback.doPlayNext();
    }

    public void playPrevious() {
        callback.doPlayPrevious();
    }

    public void togglePlay() { //NON-NLS
        Log.i(TAG, "togglePlay()"); //NON-NLS
        if (track == null) {
            playNext();
        } else if (isPlaying()) {
            pause();
            stopTimer();
        } else {
            askFocusAndPlay();
        }
    }

    public void pause() {
        Log.i(TAG, "pause()"); //NON-NLS
        if (track != null && isPlaying()) {
            stop(false); //TODO: Do pause instead !!
            stopTimer();
        }
    }

    public void resume() {
        Log.i(TAG, "resume()"); //NON-NLS
        if (track != null && !isPlaying()) {
            askFocusAndPlay();
        }
    }

    public void stop(boolean release) {
        Log.i(TAG, "stop()"); //NON-NLS
        if (track != null && isPlaying()) {
            Log.i(TAG, "mediaPlayer.stop() " + track); //NON-NLS
            new Thread() {
                public void run() {
                    HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("api/stop"); //NON-NLS
                    Request request = clientInfo.getRequestBuilder(urlBuilder).post(RequestBody.create("", MediaType.parse("application/json; charset=utf-8"))).build();
                    Response response;
                    try {
                        response = client.newCall(request).execute();
                        if (!response.isSuccessful()) {
                            if (response.code() == 400) {
                                //TODO: Already playing
                            }
                            else if (response.code() == 404) {
                                //TODO: Not found
                            }
                        } else {
                            isPlaying = false;
                            if(release) {
                                track = null;
                            }
                            callback.onPositionChanged(0, 1);
                            stopTimer();
                        }
                    } catch (IOException e) {
                        Log.w(TAG, "Failed to stop", e); //NON-NLS
                    }
                }

            }.start();
        }
    }

    public void forward() {
        //FIXME forward
//        if (mediaPlayer != null && enableControl) {
//            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + duration / 10);
//        }
    }

    public void rewind() {
        //FIXME rewind
//        if (mediaPlayer != null && enableControl) {
//            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - duration / 10);
//        }
    }

    public void pullUp() {
        //FIXME pullUp
//        if (mediaPlayer != null && enableControl) {
//            mediaPlayer.seekTo(0);
//        }
    }

    private void startTimer() {
        callback.onPlayBackStart();
        //FIXME CountDownTimer
//        timer = new CountDownTimer(duration - mediaPlayer.getCurrentPosition() - 1, 500) {
//            @Override
//            public void onTick(long millisUntilFinished_) {
//                if (mediaPlayer != null) {
//                    callback.onPositionChanged(mediaPlayer.getCurrentPosition(), duration);
//                }
//                if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
//                    this.cancel();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//            }
//        }.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
