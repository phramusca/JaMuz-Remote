package phramusca.com.jamuzremote;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ResultReceiver;
import android.service.media.MediaBrowserService;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceAudioPlayer extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    private MediaMetadataCompat.Builder metadataBuilder;
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    private static final String TAG = ServiceBase.class.getName();

    private boolean enableControl = false;
    private int duration;
    private boolean mediaPlayerWasPlayingOnFocusLost = false;
    private static CountDownTimer timer;

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .setWillPauseWhenDucked(true)
                    .build();
        } else {
            audioFocusRequest = null;
        }
        playbackStateBuilder = new PlaybackStateCompat.Builder();
        metadataBuilder = new MediaMetadataCompat.Builder();
        initMediaSession();
        initNoisyReceiver();
        setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
    }

    private void initMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        Intent mediaSessionIntent = new Intent(getApplicationContext(), ServiceAudioPlayer.class);
        int flag = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flag  = PendingIntent.FLAG_MUTABLE;
        }
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, mediaSessionIntent, flag);
        mediaSession = new MediaSessionCompat(getApplicationContext(), TAG, mediaButtonReceiver, intent);
        mediaSession.setCallback(mediaSessionCallback);
        mediaSession.setFlags( MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS );
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, flag);
        mediaSession.setMediaButtonReceiver(pendingIntent);
        setSessionToken(mediaSession.getSessionToken());
    }

    private void initNoisyReceiver() {
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(noisyReceiver, filter);
    }

    private final MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                askFocusAndPlay();
            } else {
                //FIXME: Do play next (need to manage queue here)
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.i(TAG, "onStop()"); //NON-NLS
            stop(false);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            try {
                //FIXME: get position from PlayQueue.queue.get(Integer.parseInt(mediaId));
                // and eventually use this listener :
//                PlayQueue.queue.addListener(positionPlaying -> {
//                    trackAdapter.trackList.setPositionPlaying(positionPlaying - offset);
//                    trackAdapter.notifyDataSetChanged();
//                });
                float albumGain = extras.getFloat("AlbumGain");
                float trackGain = extras.getFloat("TrackGain");
                int volume = extras.getInt("baseVolume");

                String title = extras.getString("title");
                String subtitle = extras.getString("subtitle");
                Long trackNumber = extras.getLong("trackNumber");
                Long numTracks = extras.getLong("numTracks");
                initMediaSessionMetadata(title, subtitle, trackNumber, numTracks);

                Log.i(TAG, "Playing " + mediaId); //NON-NLS
                enableControl = false;
                mediaPlayer = new MediaPlayer();
                if(mediaId.startsWith("content://")) {
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(mediaId));
                } else {
                    mediaPlayer.setDataSource(mediaId);
                }
                mediaPlayer.prepare();
                if (volume >= 0) {
                    baseVolume = ((float) volume / 100.0f);
                }
                String msg = applyReplayGain(mediaPlayer, albumGain, trackGain);
                //FIXME: Use replaygain message
//                if (!msg.equals("")) {
//                    helperToast.toastLong(msg);
//                }
                mediaPlayer.setOnPreparedListener(mp -> {
                    duration = mediaPlayer.getDuration();
                    askFocusAndPlay();
                    mediaPlayer.setOnCompletionListener(mediaPlayer -> setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT));
                    mediaPlayer.setOnSeekCompleteListener(mediaPlayer -> startTimer());
                    enableControl = true;
                });
            } catch (IOException e) {
                Log.e(TAG, "Error playing (\"" + mediaId + "\") => DELETING IT !!!!!!", e); //NON-NLS
                stop(false);
                File file = new File(mediaId);
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            pause();

        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
//            if( COMMAND_EXAMPLE.equalsIgnoreCase(command) ) {
//                //Custom command here
//            }
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            if (mediaPlayer != null && enableControl) {
                mediaPlayer.seekTo((int) pos);
            }
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
            if (mediaPlayer != null && enableControl) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + duration / 10);
            }
        }

        @Override
        public void onRewind() {
            super.onRewind();
            if (mediaPlayer != null && enableControl) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - duration / 10);
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            //FIXME: Do skip to next (need to manage queue from here)
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            //FIXME: Do skip to previous (need to manage queue from here)
        }
    };

    //FIXME: Use setVolume
    private String setVolume(int volume, float albumGain, float trackGain) {
        if (volume >= 0) {
            this.baseVolume = ((float) volume / 100.0f);
            Log.i(TAG, "setVolume()"); //NON-NLS
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                try {
                    return applyReplayGain(mediaPlayer, albumGain, trackGain);
                } catch (Exception e) {
                    Log.w(TAG, "Failed to set volume"); //NON-NLS
                }
            }
        }
        return "";
    }

    private void pause() {
        pause(false);
    }

    private void pause(boolean mediaPlayerWasPlaying) {
        Log.i(TAG, "pause()"); //NON-NLS
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            stopTimer(mediaPlayerWasPlaying);
//            stopForeground(false);
        }
    }

    private final boolean mReplayGainTrackEnabled = true;
    private final boolean mReplayGainAlbumEnabled = false;
    private float baseVolume = 0.70f;

    /**
     * Enables or disables Replay Gain.
     * Taken partially from https://github.com/vanilla-music/vanilla
     */
    private String applyReplayGain(MediaPlayer mediaPlayer, float albumGain, float trackGain) {
        String rgStr = "albumGain=" + albumGain + ", trackGain=" + trackGain;
        Log.i(TAG, rgStr);
        float adjust = 0f;
        if (!Float.isNaN(albumGain) && !Float.isNaN(trackGain)) {
            if (mReplayGainAlbumEnabled) {
                adjust = (trackGain != 0 ? trackGain : adjust); /* do we have track adjustment ? */
                adjust = (albumGain != 0 ? albumGain : adjust); /* ..or, even better, album adj? */
            }

            if (mReplayGainTrackEnabled || (mReplayGainAlbumEnabled && adjust == 0)) {
                adjust = (albumGain != 0 ? albumGain : adjust); /* do we have album adjustment ? */
                adjust = (trackGain != 0 ? trackGain : adjust); /* ..or, even better, track adj? */
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
                    getApplicationContext().getString(R.string.audioPlayerToastRgBaseVolTooHigh),
                    getApplicationContext().getString(R.string.audioPlayerToastRgConsiderLower),
                    rgStr,
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

    private void askFocusAndPlay() {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            startService(new Intent(getApplicationContext(), MediaBrowserService.class));
            mediaSession.setActive(true);
            mediaPlayer.start();
            metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration);
            mediaSession.setMetadata(metadataBuilder.build());
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            showPlayingNotification();
            mediaPlayerWasPlayingOnFocusLost = true;
            startTimer();
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(duration - mediaPlayer.getCurrentPosition() - 1, 30000) {
            @Override
            public void onTick(long millisUntilFinished_) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    playbackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1);
                    mediaSession.setPlaybackState(playbackStateBuilder.build());
                } else {
                    this.cancel();
                }
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void stop(boolean release) {
        Log.i(TAG, "stop()"); //NON-NLS
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                Log.i(TAG, "mediaPlayer.stop()" + Arrays.toString(mediaPlayer.getTrackInfo())); //NON-NLS
                mediaPlayer.stop();
                setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
                if (release) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
            stopTimer();
        } catch (Exception e) { //NON-NLS //NON-NLS
            Log.w(TAG, "Failed to stop"); //NON-NLS
        }
    }

    private void stopTimer() {
        stopTimer(false);
    }

    private void stopTimer(boolean mediaPlayerWasPlaying) {
        this.mediaPlayerWasPlayingOnFocusLost = mediaPlayerWasPlaying;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private final BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pause();
        }
    };

    private void initMediaSessionMetadata(String title, String subtitle, Long trackNumber, Long numTracks) {

        //Notification icon in card
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        //lock screen icon for pre lollipop
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, subtitle);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, numTracks);

        mediaSession.setMetadata(metadataBuilder.build());
    }

    private void showPlayingNotification() {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationChannel chan;
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel("myNotificationChannelId", "channelName", NotificationManager.IMPORTANCE_NONE);
            chan.setDescription("<Add channel Description>");
            nm.createNotificationChannel(chan);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "myNotificationChannelId");
        builder
                // Add the metadata for the currently playing track
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                // Enable launching the player by clicking the notification
                .setContentIntent(controller.getSessionActivity())
                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                        PlaybackStateCompat.ACTION_STOP))
                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // Add an app icon and set its accent color
                // Be careful about the color
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                // Add a pause button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_action_play, "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)))
                // Take advantage of MediaStyle features
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0)
                        // Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                                PlaybackStateCompat.ACTION_STOP)));
        // Display the notification and place the service in the foreground
        startForeground(NotificationId.get(), builder.build());
    }

    private void setMediaPlaybackState(int state) {
        if( state == PlaybackStateCompat.STATE_PLAYING ) {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_PLAY_PAUSE
                    | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            if (mediaPlayer != null) {
                playbackStateBuilder.setState(state, mediaPlayer.getCurrentPosition(), 1);
            }
        }
        else {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_PLAY_PAUSE
                    | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            playbackStateBuilder.setState(state,
                    mediaPlayer != null
                            ? mediaPlayer.getCurrentPosition()
                            : PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    0);
        }
        mediaSession.setPlaybackState(playbackStateBuilder.build());
    }

    private void resume() {
        Log.i(TAG, "resume()"); //NON-NLS
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            askFocusAndPlay();
        }
    }

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = focusChange -> {
        switch( focusChange ) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            {
                pause(mediaPlayerWasPlayingOnFocusLost);
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if(mediaPlayerWasPlayingOnFocusLost) {
                    resume();
                }
                break;
            }
        }
    };

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // Returning null == no one can connect

        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierarchy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
        }
    }

    private boolean allowBrowsing(String clientPackageName, int clientUid) {
        return false;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        //  Browsing not allowed
        if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentId)) {
            result.sendResult(null);
            return;
        }
        // Assume for example that the music catalog is already loaded/cached.
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        // Check if this is the root menu:
        if (MY_MEDIA_ROOT_ID.equals(parentId)) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if( this.mediaPlayer != null ) {
            this.mediaPlayer.release();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(audioFocusChangeListener);
        unregisterReceiver(noisyReceiver);
        NotificationManagerCompat.from(this).cancel(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
        }
        mediaSession.release();
        mediaSession.setActive(false);
    }
}