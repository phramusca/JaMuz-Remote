package phramusca.com.jamuzremote;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;

import com.beaglebuddy.ape.APEItem;
import com.beaglebuddy.ape.APETag;
import com.beaglebuddy.mp3.MP3;

import java.io.File;
import java.io.IOException;

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

    public String play(Track track) {
        String msg ="";
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(track.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            startTimer();

            msg = applyReplayGain(mediaPlayer, track);

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
        return msg;
    }

    public static final boolean ENABLE_TRACK_REPLAYGAIN = true;
    public static final boolean ENABLE_ALBUM_REPLAYGAIN = false;
    public static final int     REPLAYGAIN_BUMP = 75; // seek bar is 150 -> 75 == middle == 0
    public static final int     REPLAYGAIN_UNTAGGED_DEBUMP = 150; // seek bar is 150 -> == 0

    //FIXME: Make Replaygain options.
    /**
     * Enables or disables Replay Gain
     */
    private boolean mReplayGainTrackEnabled=ENABLE_TRACK_REPLAYGAIN;
    private boolean mReplayGainAlbumEnabled=ENABLE_ALBUM_REPLAYGAIN;
    private int mReplayGainBump=REPLAYGAIN_BUMP;
    private int mReplayGainUntaggedDeBump=REPLAYGAIN_UNTAGGED_DEBUMP;

    //https://www.programcreek.com/java-api-examples/index.php?class=android.media.MediaPlayer&method=setVolume
    //https://www.programcreek.com/java-api-examples/index.php?source_dir=Subsonic-master/app/src/main/java/github/daneren2005/dsub/service/DownloadService.java
    private String applyReplayGain(MediaPlayer mediaPlayer, Track track) {
        /*float[] rg = BastpUtil.getReplayGainValues(track.getPath());  //track, album*/

        GainValues rg = readReplayGainFromAPE(track);
        Log.i(TAG, rg.toString());

        float adjust = 0f;
        if(mReplayGainAlbumEnabled) {
            adjust = (rg.trackGain != 0 ? rg.trackGain : adjust); /* do we have track adjustment ? */
            adjust = (rg.albumGain != 0 ? rg.albumGain : adjust); /* ..or, even better, album adj? */
        }

        if(mReplayGainTrackEnabled || (mReplayGainAlbumEnabled && adjust == 0)) {
            adjust = (rg.albumGain != 0 ? rg.albumGain : adjust); /* do we have album adjustment ? */
            adjust = (rg.trackGain != 0 ? rg.trackGain : adjust); /* ..or, even better, track adj? */
        }

        if(adjust == 0) {
			/* No RG value found: decrease volume for untagged song if requested by user */
            adjust = (mReplayGainUntaggedDeBump-150)/10f;
        } else {
			/* This song has some replay gain info, we are now going to apply the 'bump' value
			** The preferences stores the raw value of the seekbar, that's 0-150
			** But we want -15 <-> +15, so 75 shall be zero */
            adjust += 2*(mReplayGainBump-75)/10f; /* 2* -> we want +-15, not +-7.5 */
        }

        if(mReplayGainAlbumEnabled == false && mReplayGainTrackEnabled == false) {
			/* Feature is disabled: Make sure that we are going to 100% volume */
            adjust = 0f;
        }

        String msg="";
        Log.i(TAG, "baseVolume="+ baseVolume);
        float rg_result = ((float) Math.pow(10, (adjust / 20))) * baseVolume;
        Log.i(TAG, "rg_result="+rg_result);
        if (rg_result > 1.0f) {
            msg =   "Base volume too high. " +
                    "\nConsider lower it for replayGain to work properly !" +
                    "\n---------------"+
                    "\n "+rg.toString()+
                    "\n baseVolume="+ baseVolume +
                    "\n adjust="+adjust+
                    "\n setVolume="+rg_result+" => 1.0";
            rg_result = 1.0f; /* android would IGNORE the change if this is > 1
                                    and we would end up with the wrong volume */
        } else if (rg_result < 0.0f) {
            rg_result = 0.0f;
        }
        Log.i(TAG, "mediaPlayer.setVolume("+rg_result+", "+rg_result+")");
        mediaPlayer.setVolume(rg_result, rg_result);

        return msg;
    }

    public class GainValues {
        public float albumGain;
        public float trackGain;
        public float trackPeak;
        public float albumPeak;

        @Override
        public String toString() {
            return "albumGain="+ albumGain +
                    "\n trackGain="+trackGain;
        }
    }

    public GainValues readReplayGainFromAPE(Track track) {
        GainValues gv = new GainValues();
        try {
            MP3 mp3 = new MP3(track.getPath());
            if (mp3.hasAPETag()) {
//				mp3 contains an APEv2 tag
//				MP3GAIN_MINMAX : 052,209
//				MP3GAIN_ALBUM_MINMAX : 052,209
//				MP3GAIN_UNDO : +001,+001,N
//				REPLAYGAIN_TRACK_GAIN : +0.920000 dB
//				REPLAYGAIN_TRACK_PEAK : 0.860053
//				REPLAYGAIN_ALBUM_GAIN : +0.400000 dB
//				REPLAYGAIN_ALBUM_PEAK : 0.899413
                APETag apeTag = mp3.getAPETag();
//				System.out.println("mp3 contains an " + apeTag.getVersionString() + " tag");
//				System.out.println(apeTag);
                for(APEItem item : apeTag.getItems()) {
                    if(item.isValueText()) {
                        if(item.getKey().toUpperCase().equals("REPLAYGAIN_TRACK_GAIN")) {
                            gv.trackGain = getFloatFromString(item.getTextValue());
                        }
                        else if(item.getKey().toUpperCase().equals("REPLAYGAIN_ALBUM_GAIN")) {
                            gv.albumGain = getFloatFromString(item.getTextValue());
                        }
                        else if(item.getKey().toUpperCase().equals("REPLAYGAIN_ALBUM_PEAK")) {
                            gv.albumPeak = getFloatFromString(item.getTextValue());
                        }
                        else if(item.getKey().toUpperCase().equals("REPLAYGAIN_TRACK_PEAK")) {
                            gv.trackPeak = getFloatFromString(item.getTextValue());
                        }
                    }
                }
//					else {
//						System.out.println(item.getKey() + " (binary data): "
//								+ item.getBinaryValue().length + " bytes.");
//					}
            }
        }
        catch (IOException ex) {
            System.out.println("An error occurred while reading the mp3 file.");
        }
        return gv;
    }

    /**
     * Parses common replayGain string values
     */
    private float getFloatFromString(String dbFloat) {
        float rg_float = 0f;
        try {
            String nums = dbFloat.replaceAll("[^0-9.-]","");
            rg_float = Float.parseFloat(nums);
        } catch(Exception e) {}
        return rg_float;
    }

    private float baseVolume = 0.70f;

    public String setVolumeUp(Track track) {
        return (baseVolume +0.1f>1.0f)?"Max":setVolume(baseVolume +0.1f, track);
    }

    public String setVolumeDown(Track track) {
        return (baseVolume -0.1f<0f)?"Min":setVolume(baseVolume -0.1f, track);
    }

    //FIXME: Shall I use this instead of system volume mixer ?
    public String setVolume(float volume, Track track) {
        if(mediaPlayer!=null && mediaPlayer.isPlaying()) { //mediaPlayer != null && (playerState == STARTED || playerState == PAUSED || playerState == STOPPED)) {
            try {
                this.baseVolume = volume;
                return applyReplayGain(mediaPlayer, track);
            } catch(Exception e) {
                Log.w(TAG, "Failed to set volume");
            }
        }
        return "";
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
