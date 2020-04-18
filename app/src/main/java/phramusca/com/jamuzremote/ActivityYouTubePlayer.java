package phramusca.com.jamuzremote;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube_player);
        YouTubePlayerView playerView = findViewById(R.id.player_view);
        playerView.initialize(YoutubeConnector.KEY, this);
        TextView video_title = findViewById(R.id.player_title);
        TextView video_desc = findViewById(R.id.player_description);
        TextView video_id = findViewById(R.id.player_id);
        video_title.setText(getIntent().getStringExtra("VIDEO_TITLE"));
        video_id.setText("Video ID : "+(getIntent().getStringExtra("VIDEO_ID")));
        video_desc.setText(getIntent().getStringExtra("VIDEO_DESC"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityMain.audioPlayer.resume();
    }

    @Override
    public void onInitializationFailure(Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(this, "Failed to initialize Youtube Player", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player,
                                        boolean restored) {

        //initialise the video player only if it is not restored or is not yet set
        if(!restored){
            //cueVideo takes video ID as argument and initialise the player with that video
            //this method just prepares the player to play the video
            //but does not download any of the video stream until play() is called
            player.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onLoaded(String s) {
                    player.play();
                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {
                    ActivityMain.audioPlayer.pause();
                }

                @Override
                public void onVideoEnded() {
                    ActivityMain.audioPlayer.resume();
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }
            });

            
        }
    }


}