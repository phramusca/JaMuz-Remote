package phramusca.com.jamuzremote;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.service.media.MediaBrowserService;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ServiceAudioPlayer extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    private MediaMetadataCompat.Builder metadataBuilder;
    private NotificationCompat.Builder builder;
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    private static final String TAG = ServiceBase.class.getName();
    private boolean enableControl = false;
    private int duration;
    private boolean mediaPlayerWasPlayingOnFocusLost = false;
    private final HelperToast helperToast = new HelperToast(this);
    private static final int NOTIFICATION_ID = NotificationId.get();
    private static SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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

        String channelId = "Audio service"+"A notification with media controls.";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(channelId, "Audio service", NotificationManager.IMPORTANCE_LOW);
            chan.setDescription("A notification with media controls.");
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(chan);
        }
        builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
        startForeground(NOTIFICATION_ID, getNotification(PlaybackStateCompat.ACTION_PLAY));
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
                playNext();
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.i(TAG, "onStop()"); //NON-NLS
            stop(false);
        }

        @Override
        public void onPause() {
            super.onPause();
            pause();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            if (mediaPlayer != null && enableControl) {
                mediaPlayer.seekTo((int) pos);
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            }
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
            if (mediaPlayer != null && enableControl) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + duration / 10);
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            }
        }

        @Override
        public void onRewind() {
            super.onRewind();
            if (mediaPlayer != null && enableControl) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - duration / 10);
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            playNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            playPrevious();
        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
            if(command.equals("setBaseVolume")) {
                int volume = extras.getInt("baseVolume", preferences.getInt("baseVolume", 70));
                Track track = PlayQueue.queue.get(PlayQueue.queue.positionPlaying);
                setVolume(volume, track);
            }
        }
    };

    private void playNext() {
        PlayQueue.queue.fill();
        Track track = PlayQueue.queue.getNext();
        if (track != null) {
            Track displayedTrack = PlayQueue.queue.get(PlayQueue.queue.positionPlaying);
            if(displayedTrack!=null) {
                new Thread() {
                    public void run() {
                        displayedTrack.updatePlayCounterAndLastPlayed();
                    }
                }.start();
            }
            // and Play next one
            if (play(track)) {
                PlayQueue.queue.setNext();
            } else {
                PlayQueue.queue.removeNext();
                playNext();
            }
        } else {
            helperToast.toastLong(getString(R.string.mainToastEmptyPlaylist));
        }
    }

    private void playPrevious() {
        Track track = PlayQueue.queue.getPrevious();
        if (track != null) {
            if (play(track)) {
                PlayQueue.queue.setPrevious();
            } else {
                PlayQueue.queue.removePrevious();
                playPrevious();
            }
        } else {
            helperToast.toastLong(getString(R.string.mainToastNoTracksBeyond));
        }
    }

    private boolean play(Track track) {
        boolean fileExists;
        if (track.getPath().startsWith("content://")) {
            fileExists = HelperFile.checkUriExist(this, Uri.parse(track.getPath()));
        } else {
            File file = new File(track.getPath());
            fileExists = file.exists();
        }
        if (!fileExists) {
            Log.d(TAG, "play(): " + track); //NON-NLS
            track.delete();
            return false;
        }
        stop(false);
        track.setSource(
                track.isHistory()
                        ? getString(R.string.playlistLabelHistory)
                        : track.isLocked()
                            ? getString(R.string.playlistLabelUser)
                            : PlayQueue.queue.getPlaylist().toString());
        try {
            Log.i(TAG, "Playing " + track.getPath()); //NON-NLS
            enableControl = false;
            mediaPlayer = new MediaPlayer();
            if(track.getPath().startsWith("content://")) {
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(track.getPath()));
            } else {
                mediaPlayer.setDataSource(track.getPath());
            }
            mediaPlayer.prepare();
            setVolume(preferences.getInt("baseVolume", 70), track);
            mediaPlayer.setOnPreparedListener(mp -> {
                duration = mediaPlayer.getDuration();
                setMediaSessionMetadata(track);
                askFocusAndPlay();
                mediaPlayer.setOnCompletionListener(mediaPlayer -> playNext());
                enableControl = true;
            });
        } catch (IOException e) {
            Log.e(TAG, "Error playing (\"" + track.getPath() + "\") => DELETING IT !!!!!!", e); //NON-NLS
            stop(false);
            File file = new File(track.getPath());
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            playNext();
        }
        return true;
    }

    private void setVolume(int volume, Track track) {
        if (volume >= 0) {
            this.baseVolume = ((float) volume / 100.0f);
            Log.i(TAG, "setVolume("+baseVolume+")"); //NON-NLS
            if (mediaPlayer != null) { //why  && mediaPlayer.isPlaying() ?
                try {
                    applyReplayGain(track);
                } catch (Exception e) {
                    Log.w(TAG, "Failed to set volume"); //NON-NLS
                }
            }
        }
    }

    private void pause() {
        pause(false);
    }

    private void pause(boolean mediaPlayerWasPlaying) {
        Log.i(TAG, "pause()"); //NON-NLS
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            this.mediaPlayerWasPlayingOnFocusLost = mediaPlayerWasPlaying;
//            stopForeground(false);
        }
    }

    private final boolean mReplayGainTrackEnabled = true;
    private final boolean mReplayGainAlbumEnabled = false;
    private float baseVolume = 0.70f;

    /**
     * Enables or disables Replay Gain.
     * Taken partially from <a href="https://github.com/vanilla-music/vanilla">...</a>
     */
    private void applyReplayGain(Track track) {
        ReplayGain.GainValues replayGain = track.getReplayGain();
        float albumGain = replayGain.getAlbumGain();
        float trackGain = replayGain.getTrackGain();
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
        if (!msg.equals("")) {
            helperToast.toastLong(msg);
        }
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
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            mediaPlayerWasPlayingOnFocusLost = true;
        }
    }

    private void stop(boolean release) {
        Log.i(TAG, "stop()"); //NON-NLS
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                Log.i(TAG, "mediaPlayer.stop()" + Arrays.toString(mediaPlayer.getTrackInfo())); //NON-NLS
                mediaPlayer.stop();
                setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
//                stopForeground(false);
                if (release) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
            this.mediaPlayerWasPlayingOnFocusLost = false;
        } catch (Exception e) { //NON-NLS //NON-NLS
            Log.w(TAG, "Failed to stop"); //NON-NLS
        }
    }

    private final BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pause();
        }
    };

    private void setMediaSessionMetadata(Track track) {
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration);

//        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, RepoCovers.getCoverIcon(track, RepoCovers.IconSize.THUMB, true));
//        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, RepoCovers.getCoverIcon(track, RepoCovers.IconSize.THUMB, true));
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, RepoCovers.getCoverIcon(track, RepoCovers.IconSize.THUMB, true));

        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER, track.getDiscNo());
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, track.getTrackNo());
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, track.getTrackTotal());

//        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, track.getTitle());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.getTitle());
//        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, track.getArtist());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.getArtist());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, track.getAlbumArtist());
//        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, track.getAlbum());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.getAlbum());

        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DATE, track.getFormattedLastPlayed());
        long year = 0;
        try {
            year = Long.parseLong(track.getYear());
        } catch(NumberFormatException ex){
        }
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_YEAR, year);
        metadataBuilder.putRating(MediaMetadataCompat.METADATA_KEY_USER_RATING, RatingCompat.newStarRating(RatingCompat.RATING_5_STARS, (float) track.getRating()));
        metadataBuilder.putRating(MediaMetadataCompat.METADATA_KEY_RATING, RatingCompat.newStarRating(RatingCompat.RATING_5_STARS, (float) track.getRating()));
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_GENRE, track.getGenre());
        mediaSession.setMetadata(metadataBuilder.build());
    }

    private Notification getNotification(@PlaybackStateCompat.MediaKeyAction long action) {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        String contentTitle;
        String contentText;
        String subText;
        Bitmap iconBitmap;
        if(mediaMetadata!=null) {
            MediaDescriptionCompat description = mediaMetadata.getDescription();
            contentTitle = (String) description.getTitle();
            contentText = (String) description.getSubtitle();
            subText = (String) description.getDescription();
            iconBitmap = description.getIconBitmap();
        } else {
            contentTitle = getString(R.string.mainWelcomeTitle);
            contentText = getString(R.string.applicationName);
            subText = getString(R.string.mainWelcomeYear);
            iconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        }
        int actionIcon = R.drawable.ic_action_speech; //Should not happen
        int smallIcon = R.drawable.buttons_red; //Should not happen
        if(action==PlaybackStateCompat.ACTION_PLAY) {
            actionIcon = R.drawable.ic_action_play_dark;
            smallIcon = R.drawable.ic_action_pause;
        } else if(action==PlaybackStateCompat.ACTION_PAUSE) {
            actionIcon = R.drawable.ic_action_pause;
            smallIcon = R.drawable.ic_action_play_dark;
        }
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags  = flags | PendingIntent.FLAG_MUTABLE;
        }
        Intent activityIntent = new Intent(getApplicationContext(), ActivityMain.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, flags);
        builder
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSubText(subText)
                .setLargeIcon(iconBitmap)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(), PlaybackStateCompat.ACTION_PAUSE))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(smallIcon)
                .clearActions()
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_action_previous_dark, "Previous",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
                .addAction(new NotificationCompat.Action(
                        actionIcon, "Play/Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                                action)))
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_action_next_dark, "Next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                                PlaybackStateCompat.ACTION_PAUSE)));
        return builder.build();
    }

    private void updateNotification(@PlaybackStateCompat.MediaKeyAction long action) {
        Notification notification = getNotification(action);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void setMediaPlaybackState(int state) {
        if( state == PlaybackStateCompat.STATE_PLAYING ) {
            updateNotification(PlaybackStateCompat.ACTION_PAUSE);
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            if (mediaPlayer != null) {
                playbackStateBuilder.setState(state, mediaPlayer.getCurrentPosition(), 1);
            }
        }
        else {
            updateNotification(PlaybackStateCompat.ACTION_PLAY);
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY
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