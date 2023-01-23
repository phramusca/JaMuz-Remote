package phramusca.com.jamuzremote;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.service.media.MediaBrowserService;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackgroundAudioService extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    private MediaMetadataCompat.Builder metadataBuilder;
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    private static final String TAG = ServiceBase.class.getName();

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

        initMediaPlayer();
        initMediaSession();
        initNoisyReceiver();

        setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setVolume(1.0f, 1.0f);
    }

    private void initMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        Intent mediaSessionIntent = new Intent(getApplicationContext(), BackgroundAudioService.class);
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
            if (!successfullyRetrievedAudioFocus()) {
                return;
            }
            // Start the service
            startService(new Intent(getApplicationContext(), MediaBrowserService.class));
            // Set the session active  (and update metadata and state)
            mediaSession.setActive(true);
            // start the player (custom call)
            mediaPlayer.start();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            // Register BECOME_NOISY BroadcastReceiver
            initNoisyReceiver();
            // Put the service in the foreground, post notification
            showPlayingNotification();
        }

        @Override
        public void onStop() {
            super.onStop();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            }
            unregisterReceiver(noisyReceiver);
            // Stop the service
            stopSelf();
            // Set the session inactive  (and update metadata and state)
            mediaSession.setActive(false);
            // stop the player (custom call)
            mediaPlayer.stop();
            // Take the service out of the foreground
            stopForeground(false);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            try {
                try {
                    if(mediaId.startsWith("content://")) {
                        mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(mediaId));
                    } else {
                        mediaPlayer.setDataSource(mediaId);
                    }
                } catch( IllegalStateException e ) {
                    mediaPlayer.release();
                    initMediaPlayer();
                }
                initMediaSessionMetadata();
            } catch (IOException e) {
                return;
            }
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {}
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
                showPausedNotification();
            }
            unregisterReceiver(noisyReceiver);
            // Take the service out of the foreground, retain the notification
            stopForeground(false);
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
        }
    };

    private final BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    };

    private void initMediaSessionMetadata() {

        //Notification icon in card
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        //lock screen icon for pre lollipop
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Display Title");
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "Display Subtitle");
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1);

        mediaSession.setMetadata(metadataBuilder.build());
    }

    private void showPlayingNotification() {
//        NotificationCompat.Builder builder = MediaStyleHelper.from(BackgroundAudioService.this, mMediaSessionCompat);
//        if (builder == null) {
//            return;
//        }
//        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
//        //builder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        NotificationManagerCompat.from(BackgroundAudioService.this).notify(1, builder.build());

        // Given a media session and its context (usually the component containing the session)
// Create a NotificationCompat.Builder

// Get the session's metadata
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationChannel chan = null;
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

    private void showPausedNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mediaSession);
        if (builder == null) {
            return;
        }
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        //builder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(this).notify(1, builder.build());
    }

    private void setMediaPlaybackState(int state) {
        if( state == PlaybackStateCompat.STATE_PLAYING ) {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
        } else {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        }
        playbackStateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mediaSession.setPlaybackState(playbackStateBuilder.build());
    }

    private boolean successfullyRetrievedAudioFocus() {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
//        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//            mediaPlayer.start();
//            mediaPlayerWasPlaying = true;
//            startTimer();
//        }

        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = focusChange -> {
//        if (focusChange == AudioManager.AUDIOFOCUS_GAIN && mediaPlayerWasPlaying) {
//            resume();
//        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS
//                || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
//                || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
//            pause(mediaPlayerWasPlaying);
//        }
        switch( focusChange ) {
            case AudioManager.AUDIOFOCUS_LOSS: {
                if( mediaPlayer.isPlaying() ) {
                    mediaPlayer.stop();
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                mediaPlayer.pause();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                if( mediaPlayer != null ) {
                    mediaPlayer.setVolume(0.3f, 0.3f);
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if( mediaPlayer != null ) {
                    if( !mediaPlayer.isPlaying() ) {
                        mediaPlayer.start();
                    }
                    mediaPlayer.setVolume(1.0f, 1.0f);
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
//        unregisterReceiver(noisyReceiver); //FIXME: Can be not registered, which causes an exception ( java.lang.IllegalArgumentException: Receiver not registered)
        mediaSession.release();
        NotificationManagerCompat.from(this).cancel(1);
    }
}