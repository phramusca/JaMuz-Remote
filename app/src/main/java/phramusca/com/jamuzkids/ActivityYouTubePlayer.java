package phramusca.com.jamuzkids;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

// Adapted from https://github.com/abhi5658/search-youtube

public class ActivityYouTubePlayer extends YouTubeBaseActivity implements OnInitializedListener {

    private static final String TAG = ActivityYouTubePlayer.class.getName();
    private YouTubePlayer youTubePlayer;
    private static final String EXTRA_VIDEO_TIME = "videoTime";
    private int videoTime;
    private String videoId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        videoId = getIntent().getStringExtra("VIDEO_ID");

        if (savedInstanceState != null) {
            videoTime = savedInstanceState.getInt(EXTRA_VIDEO_TIME);
        }

        YouTubePlayerView playerView = findViewById(R.id.player_view);
        playerView.initialize(YoutubeConnector.KEY, this);
        TextView video_title = findViewById(R.id.player_title);
        TextView video_desc = findViewById(R.id.player_description);
        TextView video_id = findViewById(R.id.player_id);
        video_title.setText(getIntent().getStringExtra("VIDEO_TITLE"));
        video_id.setText(String.format("Video ID : %s", videoId));
        video_desc.setText(getIntent().getStringExtra("VIDEO_DESC"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityMain.audioPlayer.resume();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (youTubePlayer != null) {
            bundle.putInt(EXTRA_VIDEO_TIME, youTubePlayer.getCurrentTimeMillis());
        }
    }

    @Override
    public void onInitializationFailure(Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(this, "Failed to initialize Youtube Player", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {

        Log.i(TAG, "onInitializationSuccess("+wasRestored+")");
        youTubePlayer = player;
        youTubePlayer.setOnFullscreenListener(fullscreen -> {
            Log.i(TAG, "onFullscreenListener(" + fullscreen + ") => youTubePlayer.play()");
            youTubePlayer.play();
        });

        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {
                Log.i(TAG, "onLoading()");
            }

            @Override
            public void onLoaded(String s) {
                Log.i(TAG, "onLoaded(" + s + ") => youTubePlayer.play()");
                youTubePlayer.play();
            }

            @Override
            public void onAdStarted() {
                Log.i(TAG, "onAdStarted()");
            }

            @Override
            public void onVideoStarted() {
                Log.i(TAG, "onVideoStarted()");
            }

            @Override
            public void onVideoEnded() {
                Log.i(TAG, "onVideoEnded()");
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {
                Log.i(TAG, "onError()");
            }
        });

        youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {
                Log.i(TAG, "onPlaying() => audioPlayer.pause()");
                ActivityMain.audioPlayer.pause();
            }

            @Override
            public void onPaused() {
                Log.i(TAG, "onPaused() => audioPlayer.resume()");
                ActivityMain.audioPlayer.resume();
            }

            @Override
            public void onStopped() {
                Log.i(TAG, "onStopped() => audioPlayer.resume()");
                ActivityMain.audioPlayer.resume();
            }

            @Override
            public void onBuffering(boolean b) {
                Log.i(TAG, "onBuffering(" + b + ")");
            }

            @Override
            public void onSeekTo(int i) {
                Log.i(TAG, "onSeekTo(" + i + ")");
            }
        });

        if (wasRestored) {
            Log.i(TAG, "RESTORED: cueVideo(" + videoId + "," + videoTime + ")");
            youTubePlayer.cueVideo(videoId, videoTime);
        }
        else {
            Log.i(TAG, "NOT restored: cueVideo(" + videoId + ")");
            youTubePlayer.cueVideo(videoId);
        }
    }
}