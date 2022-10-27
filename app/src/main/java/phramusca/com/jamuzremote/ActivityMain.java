package phramusca.com.jamuzremote;

import static phramusca.com.jamuzremote.Playlist.Order.PLAYCOUNTER_LASTPLAYED;
import static phramusca.com.jamuzremote.Playlist.Order.RANDOM;
import static phramusca.com.jamuzremote.StringManager.trimTrailingWhitespace;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.flexbox.FlexboxLayout;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

//FIXME: Use auto backup for database and playlists
//https://developer.android.com/guide/topics/data/backup

// FIXME: Move audio to a service
// Why not using the standard android player by the way ? (less control for replaygain ?)

//TODO: Find another library for kiosk mode since kidsplace has to be removed for fdroid inclusion

public class ActivityMain extends AppCompatActivity {

    private static final String TAG = ActivityMain.class.getName();
    private static SharedPreferences preferences;
    private final HelperToast helperToast = new HelperToast(this);
    private ClientRemote clientRemote;
    private Track displayedTrack;
    private Track localTrack;
    private AudioManager audioManager;
    public static AudioPlayer audioPlayer; //TODO: Remove static
    public static String login;
    public File musicLibraryDbFile;

    private Map<String, Playlist> localPlaylists = new LinkedHashMap<>();
    private ArrayAdapter<Playlist> playListArrayAdapter;
    private Playlist localSelectedPlaylist;

    private static final int SPEECH_REQUEST_CODE = 15489;
    private static final int LISTS_REQUEST_CODE = 60568;
    private static final int SETTINGS_REQUEST_CODE = 23548;

    private static Context mContext; //FIXME: Remove static
    private static PrettyTime prettyTime;

    // GUI elements
    private TextView textViewFileInfo1;
    private TextView textViewFileInfo2;
    private TextView textViewFileInfo3;
    private TextView textViewFileInfo4;
    private TextView textViewPlaylist;
    private Button buttonRemote;
    private Button buttonSync;
    private Button button_settings;
    private ToggleButton toggleButtonDimMode;
    private ToggleButton toggleButtonControls;
    private ToggleButton toggleButtonTagsPanel;
    private ToggleButton toggleButtonRatingPanel;
    private ToggleButton toggleButtonOrderPanel;
    private ToggleButton toggleButtonGenresPanel;
    private ToggleButton toggleButtonEditTags;
    private ToggleButton toggleButtonPlaylist;
    private Button buttonRatingOperator;
    private Button button_save;
    private Button button_new;
    private Button button_delete;
    private SeekBar seekBarPosition;
    private Spinner spinnerPlaylist;
    private Spinner spinnerGenre;
    private Spinner spinnerPlaylistLimitUnit;
    private Spinner spinnerPlaylistLimitValue;
    private static boolean spinnerPlaylistSend = false;
    private static boolean spinnerGenreSend = false;
    private static boolean spinnerLimitUnitSend = false;
    private static boolean spinnerLimitValueSend = false;
    private ArrayAdapter<CharSequence> playListLimitUnitArrayAdapter;
    private ArrayAdapter<Integer> playListLimitValueArrayAdapter;
    private RatingBar ratingBar;
    private RatingBar ratingBarPlaylist;
    private LinearLayout layoutOrderPlaylistLayout;
    private RadioGroup playListOrderRadio;
    private ImageView imageViewCover;
    private LinearLayout layoutMain;
    private LinearLayout layoutControls;
    private FlexboxLayout layoutTags;
    private FlexboxLayout layoutTagsPlaylist;
    private FlexboxLayout layoutGenrePlaylist;
    private LinearLayout layoutTagsPlaylistLayout;
    private LinearLayout layoutRatingPlaylistLayout;
    private LinearLayout layoutGenrePlaylistLayout;
    private LinearLayout layoutEditTags;
    private LinearLayout layoutPlaylist;
    private LinearLayout layoutPlaylistEditBar;
    private GridLayout layoutPlaylistToolBar;
    private TextView textFileInfo_seekBefore;
    private TextView textFileInfo_seekAfter;

    @SuppressLint({"HardwareIds", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ActivityMain onCreate"); //NON-NLS
        mContext = this;
        setContentView(R.layout.activity_main);
        layoutMain = findViewById(R.id.panel_main);

        //TODO: Disable touch events while loading until panels are toggled off.
        //setEnabled does not seem enough (need to disable inner views too?) + some widgets are disabled/enabled during onCreate
        //layoutMain.setEnabled(false);

        if(!HelperFile.init(mContext)) {
            helperToast.toastLong("Unable to find a writable application folder. Exiting :(");
            return;
        }
        musicLibraryDbFile = HelperFile.getFile("JaMuzRemote.db");

        VoiceKeyWords.set(mContext);
        prettyTime = new PrettyTime(Locale.getDefault());
        prettyTime.removeUnit(org.ocpsoft.prettytime.units.Decade.class);
        login = Settings.Secure.getString(ActivityMain.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(textToSpeech.getDefaultVoice().getLocale());
            }
        });
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                SpeechPostAction speechPostAction = SpeechPostAction.valueOf(utteranceId);
                switch (speechPostAction) {
                    case NONE:
                        break;
                    case ASK_WITH_DELAY:
                        new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(3 * 1000);
                                    runOnUiThread(() -> speak(getString(R.string.speakYourTurn)));
                                    speechRecognizer();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        break;
                }
            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        layoutTags = findViewById(R.id.panel_tags);
        layoutTagsPlaylist = findViewById(R.id.panel_tags_playlist);
        layoutGenrePlaylist = findViewById(R.id.panel_genre_playlist);
        layoutTagsPlaylistLayout = findViewById(R.id.panel_tags_playlist_layout);
        layoutRatingPlaylistLayout = findViewById(R.id.panel_rating_playlist_layout);
        layoutOrderPlaylistLayout = findViewById(R.id.panel_order_playlist_layout);

        playListOrderRadio = findViewById(R.id.playlist_order_radio);

        layoutGenrePlaylistLayout = findViewById(R.id.panel_genre_playlist_layout);
        layoutEditTags = findViewById(R.id.panel_edit);
        layoutPlaylist = findViewById(R.id.panel_playlist);

        layoutPlaylistToolBar = findViewById(R.id.panel_playlist_toolbar);
        layoutPlaylistEditBar = findViewById(R.id.panel_playlist_editbar);

        layoutControls = findViewById(R.id.panel_controls);

        textViewFileInfo1 = findViewById(R.id.textFileInfo_line1);
        textViewFileInfo2 = findViewById(R.id.textFileInfo_line2);
        textViewFileInfo3 = findViewById(R.id.textFileInfo_line3);
        textViewFileInfo4 = findViewById(R.id.textFileInfo_line4);

        textFileInfo_seekBefore = findViewById(R.id.textFileInfo_seekBefore);
        textFileInfo_seekAfter = findViewById(R.id.textFileInfo_seekAfter);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        buttonRemote = findViewById(R.id.button_connect);
        buttonRemote.setOnClickListener(v -> {
            dimOn();
            buttonRemote.setEnabled(false);
            buttonRemote.setBackgroundResource(R.drawable.remote_ongoing);
            if (buttonRemote.getText().equals("1")) {
                ClientInfo clientInfo = null;
                if (checkWifiConnection()) {
                    clientInfo = getClientInfo(ClientCanal.REMOTE, helperToast);
                }
                if (clientInfo != null) {
                    clientRemote = new ClientRemote(clientInfo, new ListenerRemote(), mContext);
                    new Thread() {
                        public void run() {
                            enableRemote(!clientRemote.connect());
                        }
                    }.start();
                } else {
                    enableRemote(true);
                }
            } else {
                enableRemote(true);
                stopRemote();
            }
        });

        buttonSync = findViewById(R.id.button_sync);
        buttonSync.setOnClickListener(v -> {
            dimOn();
            buttonSync.setBackgroundResource(R.drawable.connect_ongoing);
            if (buttonSync.getText().equals("1")) {
                enableSync(false);
                ClientInfo clientInfo = null;
                if (checkWifiConnection()) {
                    clientInfo = getClientInfo(ClientCanal.SYNC, helperToast);
                }
                if (clientInfo != null) {
                    if (!isMyServiceRunning(ServiceSync.class)) {
                        Intent service = new Intent(getApplicationContext(), ServiceSync.class);
                        service.putExtra("clientInfo", clientInfo);
                        service.putExtra("getAppDataPath", HelperFile.getAudioRootFolder());
                        startService(service);
                    }
                } else {
                    enableSync(true);
                }
            } else {
                Log.i(TAG, "Broadcast(" + ServiceSync.USER_STOP_SERVICE_REQUEST + ")"); //NON-NLS
                sendBroadcast(new Intent(ServiceSync.USER_STOP_SERVICE_REQUEST));
                enableSync(true);
            }
        });

        getFromQRcode(getIntent().getDataString());

        textViewPlaylist = findViewById(R.id.textViewPlaylist);

        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) { //as it is also set when server sends file info (and it can be 0)
                dimOn();
                setRating((int) rating);
            }
        });

        ratingBarPlaylist = findViewById(R.id.ratingBarPlaylist);
        ratingBarPlaylist.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                dimOn();
                ratingBarPlaylist.setEnabled(false);
                if (localSelectedPlaylist != null) {
                    localSelectedPlaylist.setRating(Math.round(rating));
                    refreshQueueAndPlaylistSpinner();
                }
                ratingBarPlaylist.setEnabled(true);
            }
        });

        Button buttonClearRating = findViewById(R.id.button_clear_rating);
        buttonClearRating.setOnClickListener(v -> {
            ratingBarPlaylist.setRating(0F);
            if (localSelectedPlaylist != null) {
                localSelectedPlaylist.setRating(0);
                refreshQueueAndPlaylistSpinner();
            }
        });

        buttonRatingOperator = findViewById(R.id.button_rating_operator);
        buttonRatingOperator.setOnClickListener(v -> {
            if (localSelectedPlaylist != null) {
                buttonRatingOperator.setText(localSelectedPlaylist.setRatingOperator());
                refreshQueueAndPlaylistSpinner();
            }
        });

        playListLimitUnitArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.limitUnits, android.R.layout.simple_spinner_item);
        playListLimitUnitArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaylistLimitUnit = findViewById(R.id.spinner_playlist_limit_unit);
        spinnerPlaylistLimitUnit.setAdapter(playListLimitUnitArrayAdapter);
        spinnerPlaylistLimitUnit.setOnItemSelectedListener(spinnerLimitUnitListener);
        spinnerPlaylistLimitUnit.setOnTouchListener(dimOnTouchListener);

        Integer[] limitValues = new Integer[100];
        for (int i = 0; i < 100; i++) {
            limitValues[i] = i;
        }
        playListLimitValueArrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, limitValues);
        playListLimitValueArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaylistLimitValue = findViewById(R.id.numberPicker_playlist_limit_value);
        spinnerPlaylistLimitValue.setAdapter(playListLimitValueArrayAdapter);
        spinnerPlaylistLimitValue.setOnItemSelectedListener(spinnerLimitValueListener);
        spinnerPlaylistLimitValue.setOnTouchListener(dimOnTouchListener);

        seekBarPosition = findViewById(R.id.seekBar);
        seekBarPosition.setEnabled(false);

        spinnerPlaylist = findViewById(R.id.spinner_playlist);
        spinnerPlaylist.setOnItemSelectedListener(spinnerPlaylistListener);
        spinnerPlaylist.setOnTouchListener(dimOnTouchListener);

        spinnerGenre = findViewById(R.id.spinner_genre);
        spinnerGenre.setOnItemSelectedListener(spinnerGenreListener);
        spinnerGenre.setOnTouchListener(dimOnTouchListener);

        setupButton(R.id.button_previous, "previousTrack"); //NON-NLS
        setupButton(R.id.button_play, "playTrack"); //NON-NLS
        setupButton(R.id.button_next, "nextTrack"); //NON-NLS
        setupButton(R.id.button_rewind, "rewind"); //NON-NLS
        setupButton(R.id.button_pullup, "pullup"); //NON-NLS
        setupButton(R.id.button_forward, "forward"); //NON-NLS
        setupButton(R.id.button_volUp, "volUp"); //NON-NLS
        setupButton(R.id.button_volDown, "volDown"); //NON-NLS

        toggleButtonDimMode = findViewById(R.id.button_dim_mode);
        toggleButtonDimMode.setOnClickListener(v -> setDimMode(toggleButtonDimMode.isChecked()));

        toggleButtonControls = findViewById(R.id.button_controls_toggle);
        toggleButtonControls.setOnClickListener(v -> {
            dimOn();
            toggle(layoutControls, !toggleButtonControls.isChecked());
        });

        toggleButtonTagsPanel = findViewById(R.id.button_tags_panel_toggle);
        toggleButtonTagsPanel.setOnClickListener(v -> {
            dimOn();
            toggle(layoutTagsPlaylistLayout, !toggleButtonTagsPanel.isChecked());
            if (toggleButtonTagsPanel.isChecked()) {
                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            }
        });

        toggleButtonRatingPanel = findViewById(R.id.button_rating_layout);
        toggleButtonRatingPanel.setOnClickListener(v -> {
            dimOn();
            toggle(layoutRatingPlaylistLayout, !toggleButtonRatingPanel.isChecked());
            if (toggleButtonRatingPanel.isChecked()) {
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            }
        });

        toggleButtonOrderPanel = findViewById(R.id.button_order_panel_toggle);
        toggleButtonOrderPanel.setOnClickListener(v -> {
            dimOn();
            toggle(layoutOrderPlaylistLayout, !toggleButtonOrderPanel.isChecked());
            if (toggleButtonOrderPanel.isChecked()) {
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
            }
        });

        toggleButtonGenresPanel = findViewById(R.id.button_genres_panel_toggle);
        toggleButtonGenresPanel.setOnClickListener(v -> {
            dimOn();
            toggle(layoutGenrePlaylistLayout, !toggleButtonGenresPanel.isChecked());
            if (toggleButtonGenresPanel.isChecked()) {
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            }
        });

        toggleButtonEditTags = findViewById(R.id.button_edit_toggle);
        toggleButtonEditTags.setOnClickListener(v -> {
            dimOn();
            toggle(layoutEditTags, !toggleButtonEditTags.isChecked());
            if (toggleButtonEditTags.isChecked()) {
                toggleOff(toggleButtonPlaylist, layoutPlaylist);

                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            }
        });

        toggleButtonPlaylist = findViewById(R.id.button_playlist_toggle);
        toggleButtonPlaylist.setOnClickListener(v -> {
            dimOn();
            toggle(layoutPlaylist, !toggleButtonPlaylist.isChecked());
            if (toggleButtonPlaylist.isChecked()) {
                toggleOff(toggleButtonEditTags, layoutEditTags);
                refreshLocalPlaylistSpinner(true);
            } else {
                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            }
        });

        button_settings = findViewById(R.id.button_settings);
        button_settings.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ActivitySettings.class);
            intent.putExtra("localPlaylists", new ArrayList<>(localPlaylists.values()));
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
        });

        button_new = findViewById(R.id.button_new);
        button_new.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
            builder.setTitle(R.string.playlistLabelNewName);
            final EditText input = new EditText(ActivityMain.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            builder.setView(input);
            builder.setPositiveButton(R.string.globalLabelOK, (dialog, which) -> {
                String text = input.getText().toString().trim();
                if (!localPlaylists.containsKey(text)) {
                    Playlist newPlaylist = null;
                    if (localSelectedPlaylist != null) {
                        newPlaylist = clonePlaylist(localSelectedPlaylist);
                        newPlaylist.setName(text);
                    }
                    if (newPlaylist == null) {
                        newPlaylist = new Playlist(text, true);
                    }
                    localPlaylists.put(newPlaylist.getName(), newPlaylist);
                    setupLocalPlaylistSpinner(newPlaylist.getName());
                } else {
                    helperToast.toastLong(getString(R.string.playlistLabel) + " \"" + text + "\" " + getString(R.string.playlistLabelAlreadyExists));
                }
            });
            builder.setNegativeButton(R.string.globalLabelCancel, (dialog, which) -> dialog.cancel());

            builder.show();
        });

        button_save = findViewById(R.id.button_save);
        button_save.setOnClickListener(v -> {
            if (localSelectedPlaylist != null) {
                StringBuilder msg = new StringBuilder().append(getString(R.string.playlistLabel))
                        .append(" \"")
                        .append(localSelectedPlaylist.getName())
                        .append("\" ")
                        .append(getString(R.string.playlistLabelSaved));
                if (localSelectedPlaylist.save()) {
                    button_save.setBackgroundResource(localSelectedPlaylist.isModified() ?
                            R.drawable.ic_button_save_red : R.drawable.ic_button_save);
                    msg.append(" ").append(getString(R.string.playlistLabelSuccessfully));
                } else {
                    msg.append(" ").append(getString(R.string.playlistLabelWithErrors));
                }
                helperToast.toastShort(msg.toString());
            }
        });

        Button button_restore = findViewById(R.id.button_restore);
        button_restore.setOnClickListener(v -> {
            if (localSelectedPlaylist != null) {
                StringBuilder msg = new StringBuilder().append(getString(R.string.playlistLabel))
                        .append(" \"")
                        .append(localSelectedPlaylist.getName())
                        .append("\" ")
                        .append(getString(R.string.playlistLabelRestored));
                Playlist playlist = readPlaylist(localSelectedPlaylist.getName() + ".plli");
                if (playlist != null) {
                    msg.append(" ").append(getString(R.string.playlistLabelSuccessfully));
                    playlist.setModified(false);
                    playlist.resetNbFilesAndLengthOrSize(); // Otherwise it displays values from .plli file (when last saved), then the new ones. And this is confusing
                    applyPlaylist(playlist, false);
                    setupLocalPlaylistSpinner(playlist);
                } else {
                    msg.append(" ").append(getString(R.string.playlistLabelWithErrors));
                }
                helperToast.toastShort(msg.toString());
            }
        });

        button_delete = findViewById(R.id.button_delete);
        button_delete.setOnClickListener(v -> {
            if (localSelectedPlaylist != null) {
                new AlertDialog.Builder(ActivityMain.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.playlistLabelQuestionDeleteTitle)
                        .setMessage(getString(R.string.playlistLabelQuestionDelete) +
                                " \"" + localSelectedPlaylist.getName() + "\" " + getString(R.string.playlistLabelQuestionDeleteSuffix))
                        .setPositiveButton(R.string.globalLabelYes, (dialog, which) -> {
                            if (localPlaylists.size() > 1) {
                                HelperFile.delete(localSelectedPlaylist.getName() + ".plli", "playlists");
                                localPlaylists.remove(localSelectedPlaylist.getName());
                                setupLocalPlaylistSpinner((String) null);
                            } else {
                                helperToast.toastShort(getString(R.string.playlistLabelCannotDeleteLastPlaylist));
                            }
                        })
                        .setNegativeButton(R.string.globalLabelNo, null)
                        .show();
            }
        });

        Button button_queue = findViewById(R.id.button_queue);
        button_queue.setOnClickListener(v -> displayQueue());

        Button button_albums = findViewById(R.id.button_albums);
        button_albums.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ActivityAlbums.class);
            startActivityForResult(intent, LISTS_REQUEST_CODE);
        });

        Button button_speech = findViewById(R.id.button_speech);
        button_speech.setOnClickListener(v -> speechRecognizer());

        imageViewCover = findViewById(R.id.imageView);

        LinearLayout layoutTrackInfo = findViewById(R.id.trackInfo);
        layoutTrackInfo.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                Log.v(TAG, "onSwipeTop");
                if (isRemoteConnected()) {
                    clientRemote.send("forward"); //NON-NLS
                } else {
                    audioPlayer.forward();
                }

            }

            @Override
            public void onSwipeRight() {
                Log.v(TAG, "onSwipeRight");
                if (isRemoteConnected()) {
                    clientRemote.send("previousTrack");
                } else {
                    playPrevious();
                }
            }

            @Override
            public void onSwipeLeft() {
                Log.v(TAG, "onSwipeLeft");
                if (isRemoteConnected()) {
                    clientRemote.send("nextTrack");
                } else {
                    playNext();
                }
            }

            @Override
            public void onSwipeBottom() {
                Log.v(TAG, "onSwipeBottom");
                if (isRemoteConnected()) {
                    clientRemote.send("rewind"); //NON-NLS
                } else {
                    audioPlayer.rewind();
                }
            }

            @Override
            public void onTouch() {
                dimOn();
            }

            @Override
            public void onTap() {
                if (isDimOn) {
                    if (isRemoteConnected()) {
                        clientRemote.send("playTrack");
                    } else {
                        audioPlayer.togglePlay();
                    }
                }
            }

            @Override
            public void onDoubleTapUp() {
                if (!isRemoteConnected() && isDimOn) {
                    audioPlayer.pullUp();
                    audioPlayer.resume(); //As toggled by simple Tap
                }
                //TODO Send "pullup" to server if isRemoteConnected() && pullup is added back on JaMuz
            }

            @Override
            public void onLongPressed() {
                speechRecognizer();
            }
        });

        String version = "version";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        localTrack = new Track("albumArtist", "v" + version, -1, -1,
                -1, -1, -1, "format", -1, 5, //NON-NLS
                getString(R.string.mainWelcomeTitle),
                getString(R.string.mainWelcomeYear), getString(R.string.applicationName),
                "welcomeHash", //Warning: "welcomeHash" value has a meaning
                "---");
        displayedTrack = localTrack;
        displayTrack();

        ListenerPlayer callBackPlayer = new ListenerPlayer();
        audioPlayer = new AudioPlayer(this, callBackPlayer, preferences);
        audioPlayer.setVolume(preferences.getInt("baseVolume", 70), displayedTrack);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        registerButtonReceiver();

        //Start BT HeadSet connexion detection
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (audioManager.isBluetoothScoAvailableOffCall()) {
                mBluetoothAdapter.getProfileProxy(this, mHeadsetProfileListener,
                        BluetoothProfile.HEADSET);
            }
        }

        //TODO: Why this one needs registerReceiver whereas ReceiverPhoneCall does not
        registerReceiver(receiverHeadSetPlugged,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        if (isMyServiceRunning(ServiceSync.class)) {
            enableSync(false);
        }

        setDimMode(toggleButtonDimMode.isChecked());
        checkPermissionsThenScanLibrary();
    }

    private void displayQueue() {
        Intent intent = new Intent(getApplicationContext(), ActivityPlayQueue.class);
        intent.putExtra("SelectedPlaylist", localSelectedPlaylist);
        startActivityForResult(intent, LISTS_REQUEST_CODE);
    }

    private void setRating(int rating) {
        ratingBar.setEnabled(false);
        displayedTrack.setRating(Math.round(rating));
        if (isRemoteConnected()) {
            clientRemote.send("setRating".concat(String.valueOf(Math.round(rating))));
        } else {
            displayedTrack.update();
            displayTrackDetails();
            RepoAlbums.reset();
        }
        ratingBar.setEnabled(true);
    }

    private void setGenre(String genre) {
        spinnerGenre.setEnabled(false);
        displayedTrack.setGenre(genre);
        if (isRemoteConnected()) {
            clientRemote.send("setGenre".concat(genre));
        } else {
            displayedTrack.updateGenre(genre);
            displayTrackDetails();
        }
        spinnerGenre.setEnabled(true);
    }

    private boolean checkWifiConnection() {
        if (!checkConnectedViaWifi()) {
            helperToast.toastLong(getString(R.string.mainToastCheckWifiConnection));
            return false;
        }
        return true;
    }

    static ClientInfo getClientInfo(int canal, HelperToast helperToast) {
        String infoConnect = preferences.getString(
                "connectionString",
                mContext.getString(R.string.settingsServerDefaultConnectionString));
        String[] split = infoConnect.split(":");  //NOI18N
        if (split.length < 2) {
            helperToast.toastLong(
                    String.format("%s %s\n%s <%s>:<%s>\n%s %s", //NON-NLS
                            mContext.getString(R.string.mainToastClientInfoBadFormat),
                            infoConnect,
                            mContext.getString(R.string.mainToastClientInfoExpected),
                            mContext.getString(R.string.mainToastClientInfoIP),
                            mContext.getString(R.string.mainToastClientInfoPort),
                            mContext.getString(R.string.mainToastClientInfoEx),
                            mContext.getString(R.string.settingsServerDefaultConnectionString)));
            return null;
        }
        String address = split[0];
        int port;
        try {
            port = Integer.parseInt(split[1]);
        } catch (NumberFormatException ex) {
            port = 2013;
        }
        //TODO Use a real password, from QR code
        return new ClientInfo(address, port, login, "tata", canal, //NON-NLS
                "jamuz", HelperFile.getAudioRootFolder().getAbsolutePath()); //NON-NLS
    }

    private void toggleOff(ToggleButton button, View layout) {
        button.setChecked(false);
        toggle(layout, true);
    }

    private ToggleButton getButtonTag(int key, String value) {
        ToggleButton button = new ToggleButton(this);
        button.setId(key);
        button.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        button.setBackgroundResource(R.drawable.ic_tags);
        button.setAlpha(0.7F);
        button.setAllCaps(false);
        button.setText(value);
        button.setTextOff(value);
        button.setTextOn(value);
        return button;
    }

    private void makeButtonTag(int key, String value) {
        ToggleButton button = getButtonTag(key, value);
        button.setOnClickListener(view -> {
            dimOn();
            ToggleButton button1 = (ToggleButton) view;
            setTagButtonTextColor(button1);
            String buttonText = button1.getText().toString();
            toggleTag(buttonText);
            displayTrackDetails();
        });
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        layoutTags.addView(button, lp);
    }

    private void toggleTag(String tag) {
        if (isRemoteConnected()) {
            clientRemote.send("toggleTag".concat(tag));
        } else {
            displayedTrack.toggleTag(tag);
        }
    }

    private void makeButtonTagPlaylist(int key, String value) {
        TriStateButton button = new TriStateButton(this);
        button.setId(key);
        button.setTag(value);
        button.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        button.setBackgroundResource(R.drawable.ic_tags);
        button.setAlpha(0.7F);
        button.setAllCaps(false);
        button.setText(value);
        button.setState(TriStateButton.STATE.ANY);
        setTagButtonTextColor(button, TriStateButton.STATE.ANY);
        button.setOnClickListener(view -> {
            dimOn();
            TriStateButton button1 = (TriStateButton) view;
            TriStateButton.STATE state = button1.getState();
            setTagButtonTextColor(button1, state);
            if (localSelectedPlaylist != null) {
                String buttonText = button1.getText().toString();
                localSelectedPlaylist.toggleTag(buttonText, state);
                refreshQueueAndPlaylistSpinner();
            }
        });
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        layoutTagsPlaylist.addView(button, lp);
    }

    private void makeButtonGenrePlaylist(int key, String value) {
        TriStateButton button = new TriStateButton(this);
        button.setId(key);
        button.setTag(value);
        button.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        button.setBackgroundResource(R.drawable.ic_tags);
        button.setAlpha(0.7F);
        button.setAllCaps(false);
        button.setText(value);
        button.setState(TriStateButton.STATE.ANY);
        setTagButtonTextColor(button, TriStateButton.STATE.ANY);
        button.setOnClickListener(view -> {
            dimOn();
            TriStateButton button1 = (TriStateButton) view;
            TriStateButton.STATE state = button1.getState();
            setTagButtonTextColor(button1, state);
            String buttonText = button1.getText().toString();
            if (localSelectedPlaylist != null) {
                localSelectedPlaylist.toggleGenre(buttonText, state);
                refreshQueueAndPlaylistSpinner();

            }
        });
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        layoutGenrePlaylist.addView(button, lp);
    }

    private void refreshQueueAndPlaylistSpinner() {
        PlayQueue.queue.refresh(localSelectedPlaylist);
        refreshLocalPlaylistSpinner(false);
        button_save.setBackgroundResource(localSelectedPlaylist.isModified() ?
                R.drawable.ic_button_save_red : R.drawable.ic_button_save);
        textViewPlaylist.setText(localSelectedPlaylist.getSummary(this));
    }

    //This is a trick since the following (not in listener) is not working:
    //button.setTextColor(ContextCompat.getColor(this, R.color.toggle_text));
    private void setTagButtonTextColor(ToggleButton b) {
        if (b != null) {
            boolean checked = b.isChecked();
            b.setTextColor(ContextCompat.getColor(this, checked ? R.color.textColor : R.color.colorPrimaryDark));
        }
    }

    private void setTagButtonTextColor(TriStateButton button, TriStateButton.STATE state) {
        switch (state) {
            case ANY:
                button.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                button.setBackgroundResource(R.drawable.ic_gradient_button);
                break;
            case TRUE:
                button.setTextColor(ContextCompat.getColor(this, R.color.textColor));
                button.setBackgroundResource(R.drawable.ic_gradient_button_pressed);
                break;
            case FALSE:
                button.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                button.setBackgroundResource(R.drawable.ic_gradient_button_pressed);
                break;
            default:
                break;
        }
    }

    static ArrayList<Track.Status> getScope() {
        return getScope(false);
    }

    static ArrayList<Track.Status> getScope(boolean getAll) {
        ArrayList<Track.Status> statuses = new ArrayList<>();
        boolean displayServer = preferences.getBoolean("displayServer", true);
        boolean displayMediaStore = preferences.getBoolean("displayMediaStore", true);
        if(displayServer) {
            statuses.add(Track.Status.REC);
            if(getAll) {
                statuses.add(Track.Status.INFO);
                statuses.add(Track.Status.NEW);
                statuses.add(Track.Status.ERROR);
            }
        }
        if(displayMediaStore) {
            statuses.add(Track.Status.LOCAL);
        }
        return statuses;
    }

    private void applyPlaylist(Playlist playlist, boolean playNext) {
        dimOn();
        if (isRemoteConnected()) {
            clientRemote.send("setPlaylist".concat(playlist.toString()));
        } else {
            displayPlaylist(playlist);
            localSelectedPlaylist = playlist;
            if (playNext) {
                PlayQueue.queue.setQueue(playlist.getTracks(10, getScope()));
                playNext();
            } else {
                refreshQueueAndPlaylistSpinner();
            }
        }
    }

    Spinner.OnItemSelectedListener spinnerPlaylistListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            if (spinnerPlaylistSend) {
                spinnerPlaylistSend = false;
                //TODO: When from another activity (ex: queue or albums)
                //  This can be triggered if Filter button is pressed too quickly
                Playlist playlist = (Playlist) parent.getItemAtPosition(pos);
                applyPlaylist(playlist, false);
            }
            spinnerPlaylistSend = true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            dimOn();
        }
    };

    Spinner.OnItemSelectedListener spinnerGenreListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            if (spinnerGenreSend) {
                dimOn();
                String genre = (String) parent.getItemAtPosition(pos);
                setGenre(genre);
            }
            spinnerGenreSend = true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            dimOn();
        }
    };

    Spinner.OnItemSelectedListener spinnerLimitUnitListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (spinnerLimitUnitSend) {
                dimOn();
                String value = (String) parent.getItemAtPosition(pos);
                if (!isRemoteConnected() && localSelectedPlaylist != null) {
                    Playlist.LimitUnit limitUnit1 = null;
                    for (Playlist.LimitUnit limitUnit : Playlist.LimitUnit.values()) {
                        if (limitUnit.getDisplay(mContext).equals(value)) {
                            limitUnit1 = limitUnit;
                            break;
                        }
                    }
                    localSelectedPlaylist.setLimitUnit(limitUnit1);
                    refreshQueueAndPlaylistSpinner();
                }
            }
            spinnerLimitUnitSend = true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            dimOn();
        }
    };

    Spinner.OnItemSelectedListener spinnerLimitValueListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (spinnerLimitValueSend) {
                dimOn();
                Integer value = (Integer) parent.getItemAtPosition(pos);
                if (!isRemoteConnected() && localSelectedPlaylist != null) {
                    localSelectedPlaylist.setLimitValue(value);
                    refreshQueueAndPlaylistSpinner();
                }
            }
            spinnerLimitValueSend = true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            dimOn();
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener dimOnTouchListener = (view, motionEvent) -> {
        dimOn();
        return false;
    };

    private boolean isRemoteConnected() {
        return (clientRemote != null && clientRemote.isConnected());
    }

    private void setDimMode(boolean enable) {
        if (enable) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            isDimOn = false;
            dimOn();
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            timer.cancel();
            timer.purge();
            setBrightness(-1);
            isDimOn = true;
        }
    }

    private void toggle(View view, boolean collapse) {
        //https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
        if (collapse) {
            collapse(view);
        } else {
            expand(view);
        }
    }

    //https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
    public static void expand(final View v) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    //https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "ActivityMain onPause"); //NON-NLS
        wasRemoteConnected = isRemoteConnected();
        stopRemote();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        try {
            if (hasFocus) {
                setDimMode(toggleButtonDimMode.isChecked());
            } else {
                setDimMode(true);
            }
        } catch (Exception ex) {
            Log.e(TAG, "onWindowFocusChanged", ex);
        }
    }

    private boolean wasRemoteConnected = false;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "ActivityMain onResume"); //NON-NLS

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("ServiceBase"));

        getFromQRcode(getIntent().getDataString());

        if (toggleButtonDimMode.isChecked()) {
            dimOn();
        }

        audioManager.unregisterMediaButtonEventReceiver(receiverMediaButtonName);
        registerButtonReceiver();
    }

    private void registerButtonReceiver() {
        receiverMediaButtonName = new ComponentName(getPackageName(),
                ReceiverMediaButton.class.getName());
        audioManager.registerMediaButtonEventReceiver(receiverMediaButtonName);
    }

    public enum SpeechPostAction {
        NONE, ASK_WITH_DELAY
    }

    //https://developer.android.com/training/wearables/apps/voice.html
    public void speakAnd(String msg, SpeechPostAction utteranceId) {
        helperToast.toastLong(msg);
        textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null, utteranceId.name());
    }

    public void speak(String msg) {
        speakAnd(msg, SpeechPostAction.NONE);
    }

    private void speechRecognizer() {
        speechFavor(true);
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voicePromptCommand));
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            helperToast.toastLong(getString(R.string.mainVoiceToastNeedToInstall));
            String appPackageName = "com.google.android.googlequicksearchbox";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName))); //NON-NLS
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName))); //NON-NLS
            }
        }

        //TODO: Use a custom speech recognition:
        // - to avoid google ui
        // - to handle errors and so being able to ask user again
        // - to avoid issue selecting wrongly Playlist subActivity with "Suivant" vocal command
        //https://www.truiton.com/2014/06/android-speech-recognition-without-dialog-custom-activity/
    }

    enum SpeechFlavor {
        PAUSE,
        LOWER_VOLUME,
        NONE
    }

    private void speechFavor(boolean favor) {
        SpeechFlavor speechFavor = SpeechFlavor.valueOf(preferences.getString("speechFavor", SpeechFlavor.PAUSE.name()));
        switch (speechFavor) {
            case PAUSE:
                if (favor) {
                    audioPlayer.pause();
                } else {
                    audioPlayer.resume();
                }
                break;
            case LOWER_VOLUME:
                if (favor) {
                    audioPlayer.setVolume(20, displayedTrack);
                } else {
                    audioPlayer.setVolume(preferences.getInt("baseVolume", 70), displayedTrack);
                }
                break;
            case NONE:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);

                //TODO: speech recognition:
                //
                // Mot a trouver | Mot à dire
                // ------------- | -----------
                // Dub           | deben
                // Electro       | électro
                // Ska Punk      | ska punk
                // Trip Hop      | trip hop

                VoiceKeyWords.KeyWord keyWord = VoiceKeyWords.get(spokenText);
                String arguments = keyWord.getKeyword();
                String msg = getString(R.string.speakUnknownCommand) + " \"" + spokenText + "\".";
                switch (keyWord.getCommand()) {
                    case UNKNOWN:
                        speak(msg);
                        try {
                            Thread.sleep(2000); //TODO: Can't we wait for speak to complete instead of sleeping ?
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        msg = "";
                        askEdition(true);
                        break;
                    case PLAY_PLAYLIST:
                        msg = getString(R.string.playlistLabel) + " \"" + arguments + "\" " + getString(R.string.speakNotFound);
                        for (Playlist playlist : localPlaylists.values()) {
                            if (playlist.getName().equalsIgnoreCase(arguments)) {
                                applyPlaylist(playlist, true);
                                setupPlaylistSpinner(playListArrayAdapter, localSelectedPlaylist);
                                msg = "";
                                break;
                            }
                        }
                        break;
                    case PLAY_ARTIST_ONGOING:
                        arguments = displayedTrack.getArtist();
                    case PLAY_ARTIST:
                        msg = getString(R.string.speakArtist) + " \"" + arguments + "\" " + getString(R.string.speakNotFound);
                        if (arguments.equals("")) {
                            msg = getString(R.string.speakSpecifyArtist);
                        } else {
                            arguments = keyWord.getCommand().equals(VoiceKeyWords.Command.PLAY_ARTIST)
                                    ? "%" + arguments + "%" : arguments;
                            Playlist playlist = new Playlist(arguments, true);
                            playlist.setArtist(arguments);
                            if (PlayQueue.queue.insert(playlist) > 0) {
                                playNext();
                                msg = "";
                            }
                        }
                        break;
                    case PLAY_ALBUM_ONGOING:
                        arguments = displayedTrack.getAlbum();
                    case PLAY_ALBUM:
                        msg = getString(R.string.speakAlbum) + " \"" + arguments + "\" " + getString(R.string.speakNotFound);
                        if (arguments.equals("")) {
                            msg = getString(R.string.speakSpecifyAlbum);
                        } else {
                            arguments = keyWord.getCommand().equals(VoiceKeyWords.Command.PLAY_ALBUM)
                                    ? "%" + arguments + "%" : arguments;
                            Playlist playlist = new Playlist(arguments, true);
                            playlist.setAlbum(arguments);
                            if (PlayQueue.queue.insert(playlist) > 0) {
                                playNext();
                                msg = "";
                            }
                        }
                        break;
                    case SET_GENRE:
                        String genre = arguments;
                        if (arguments.length() > 1) {
                            genre = arguments.substring(0, 1).toUpperCase() + arguments.substring(1);
                        }
                        if (RepoGenres.get().contains(genre)) {
                            setupSpinnerGenre(RepoGenres.get(), genre);
                            setGenre(genre);
                        } else {
                            speak(getString(R.string.speakGenre).concat(" ").concat(genre).concat(" ").concat(getString(R.string.speakUnknown)));
                            try {
                                Thread.sleep(2000); //TODO: Can't we wait for speak to complete instead of sleeping ?
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        askEdition(true);
                        msg = "";
                        break;
                    case SET_RATING:
                        int rating;
                        try {
                            rating = Integer.parseInt(arguments);
                        } catch (NumberFormatException ex) {
                            rating = -1;
                        }
                        if (rating > 0 && rating < 6) {
                            ratingBar.setRating(rating);
                            setRating(rating);
                        } else {
                            speak(getString(R.string.speakRating).concat(" ").concat(arguments).concat(" ").concat(getString(R.string.speakRatingIncorrect)));
                            try {
                                Thread.sleep(2000); //TODO: Can't we wait for speak to complete instead of sleeping ?
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        askEdition(true);
                        msg = "";
                        break;
                    case SET_TAGS:
                        String[] tags = arguments.split(" ");
                        for (String tag : tags) {
                            if (tag.length() > 1) {
                                String s1 = tag.substring(0, 1).toUpperCase();
                                String tagCamel = s1 + tag.substring(1).toLowerCase();
                                if (RepoTags.get().containsValue(tagCamel)) {
                                    toggleTag(tagCamel);
                                }
                            }
                        }
                        displayTrackTags();
                        displayTrackDetails();
                        askEdition(true);
                        msg = "";
                        break;
                    case PLAYER_NEXT:
                        playNext();
                        msg = "";
                        break;
                    case PLAYER_PREVIOUS:
                        playPrevious();
                        msg = "";
                        break;
                    case PLAYER_PAUSE:
                        audioPlayer.pause();
                        msg = "";
                        break;
                    case PLAYER_RESUME:
                        audioPlayer.resume();
                        msg = "";
                        break;
                    case PLAYER_PULLUP:
                        audioPlayer.pullUp();
                        msg = "";
                        break;
                }
                if (!msg.equals("")) {
                    helperToast.toastLong(msg);
                    speak(msg);
                }
            }
            speechFavor(false);
        }
        else if (requestCode == LISTS_REQUEST_CODE && resultCode == RESULT_OK) {
            String action = data.getStringExtra("action"); //NON-NLS
            switch (action) {
                case "playNextAndDisplayQueue": //NON-NLS
                    playNext();
                    displayQueue();
                    break;
                case "playNext": //NON-NLS
                    playNext();
                    break;
                case "displayQueue": //NON-NLS
                    displayQueue();
                    break;
            }

        }
        else if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            String action = data.getStringExtra("action"); //NON-NLS
            if (action != null && action.equals("checkPermissionsThenScanLibrary")) { //NON-NLS
                checkPermissionsThenScanLibrary();
            }

            //FIXME: Update volume directly from Settings activity
            // Need to move audio to a service, which is a good thing anyway !
            int value = data.getIntExtra("volume", -1); //NON-NLS
            if (value >= 0) {
                String msg = audioPlayer.setVolume(value, displayedTrack);
                if (!msg.equals("")) {
                    (new HelperToast(getApplicationContext())).toastLong(msg);
                }
            }

            //TODO New Feature: read CD barcode, get album info from musicbrainz and display album
            String QRcode = data.getStringExtra("QRcode");
            if (QRcode != null) {
                getFromQRcode(QRcode);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getFromQRcode(String content) {
        if (content != null) {
            if (!content.equals("")) {
                content = content.substring("jamuzremote://".length());
                content = Encryption.decrypt(content, "NOTeBrrhzrtestSecretK");

                buttonRemote.setEnabled(false);
                buttonSync.setEnabled(false);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("connectionString", content);
                editor.apply();
                Toast.makeText(this, R.string.mainToastQR, Toast.LENGTH_LONG).show();
                buttonRemote.setEnabled(true);
                buttonSync.setEnabled(true);
            }
        }
    }

    TextToSpeech textToSpeech;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ActivityMain onDestroy"); //NON-NLS
        stopRemote();

        //Better unregister as it does not trigger anyway + raises exceptions if not
        unregisterReceiver(receiverHeadSetPlugged);
        try {
            unregisterReceiver(mHeadsetBroadcastReceiver);
        } catch (IllegalArgumentException ex) {
            //TODO: Why does this occurs in Galaxy tablet
            //TODO: Test mHeadsetBroadcastReceiver in Galaxy tablet
        }

        //Note: receiverMediaButtonName remains active if not unregistered
        //but causes issues
        audioManager.unregisterMediaButtonEventReceiver(receiverMediaButtonName);

        audioPlayer.stop(true);

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        // Not closing as services may still need it
        //TODO: Close when everything's complete (scan, sync and jamuz)
        /*HelperLibrary.close();*/
    }

    private void connectDatabase() {
        HelperLibrary.open(this, HelperFile.getAudioRootFolder(), musicLibraryDbFile);

        new Thread() {
            public void run() {
                setupTags();
                setupGenres();
                setupLocalPlaylistsThenStartServiceScan();
            }
        }.start();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean play(Track track) {
        displayedTrack = track;
        boolean fileExists;
        if (displayedTrack.getPath().startsWith("content://")) {
            fileExists = HelperFile.checkUriExist(this, Uri.parse(displayedTrack.getPath()));
        } else {
            File file = new File(displayedTrack.getPath());
            fileExists = file.exists();
        }
        if (!fileExists) {
            Log.d(TAG, "play(): " + displayedTrack); //NON-NLS
            displayedTrack.delete();
            return false;
        }
        dimOn();
        localTrack = displayedTrack;
        audioPlayer.stop(false);
        displayedTrack.setSource(
                displayedTrack.isHistory()
                        ? getString(R.string.playlistLabelHistory)
                        : displayedTrack.isLocked()
                        ? getString(R.string.playlistLabelUser)
                        : localSelectedPlaylist.toString());
        audioPlayer.play(displayedTrack, helperToast);
        displayedTrack.setHistory(true);
        return true;
    }

    private void playNext() {
        PlayQueue.queue.fill(localSelectedPlaylist);
        Track track = PlayQueue.queue.getNext();
        if (track != null) {
            if(!track.isHistory() && !track.isLocked()) {
                refreshLocalPlaylistSpinner(false);
            }

            //Update lastPlayed and playCounter of previous track
            displayedTrack.setPlayCounter(displayedTrack.getPlayCounter() + 1);
            displayedTrack.setLastPlayed(new Date());
            displayedTrack.update();

            //Play next one
            if (play(track)) {
                PlayQueue.queue.setNext();
            } else {
                PlayQueue.queue.removeNext();
                playNext();
            }
        } else {
            refreshLocalPlaylistSpinner(false);
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

    class ListenerPlayer implements IListenerPlayer {

        private int quarterPosition = 0;

        @Override
        public void reset() {
            quarterPosition = 0;
        }

        @Override
        public void onPositionChanged(int position, int duration) {
            if (!isRemoteConnected()) {
                setSeekBar(position, duration);
                int remaining = (duration - position);
                if (remaining < 5001 && remaining > 4501) { //TODO: Why those numbers ? (can't remember ...)
                    dimOn();
                }

                if (remaining > 1 && quarterPosition < 4) {
                    int quarter = duration / 4;
                    if (quarterPosition < 1 && (remaining < 3 * quarter)) {
                        quarterPosition = 1;
                        askEdition(false);
                    } else if (quarterPosition < 2 && (remaining < 2 * quarter)) {
                        quarterPosition = 2;
                        askEdition(false);
                    } else if (quarterPosition < 3 && (remaining < quarter)) {
                        quarterPosition = 3;
                        askEdition(false);
                    }
                }
            }
        }

        @Override
        public void displaySpeechRecognizer() {
            speechRecognizer();
        }

        @Override
        public void onPlayBackStart() {
            displayTrack();
        }

        @Override
        public void onPlayBackEnd() {
            if (!isRemoteConnected()) {
                playNext();
            }
        }

        @Override
        public void doPlayPrevious() {
            playPrevious();
        }

        @Override
        public void doPlayNext() {
            playNext();
        }
    }

    private void askEdition(boolean force) {
        SpeechPostAction speechPostAction = SpeechPostAction.NONE;
        if (force
                || displayedTrack.getRating() < 1 //no rating
                || displayedTrack.getTags(false).size() < 1)  //no user tags
        {
            if (ScreenReceiver.isScreenOn
                    && toggleButtonDimMode.isChecked()
                    && (!isDimOn || force)) {
                if (!toggleButtonEditTags.isChecked()) {
                    speechPostAction = SpeechPostAction.ASK_WITH_DELAY;
                }
                speakAnd(getDisplayedTrackStatus(), speechPostAction);
            }
        }
    }

    private String getDisplayedTrackStatus() {
        StringBuilder msg = new StringBuilder();
        ArrayList<String> trackTags = displayedTrack.getTags(false);
        if (trackTags.size() > 0) {
            msg.append(getString(R.string.speakTags)).append(": ");
            for (String tag : trackTags) {
                msg.append(tag).append(", ");
            }
            msg.delete(msg.lastIndexOf(", "), msg.length()).append(". ");
        } else {
            msg.append(getString(R.string.speakTagsNone)).append(". ");
        }

        if (displayedTrack.getRating() > 0) {
            String string = getString(R.string.speakRating);
            msg.append(string).append(": ").append((int) displayedTrack.getRating()).append(". ");
        } else {
            msg.append(getString(R.string.speakRatingNone)).append(". ");
        }
        String string = getString(R.string.speakGenre);
        msg.append(string).append(": ").append(displayedTrack.getGenre()).append(".");
        return msg.toString();
    }

    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String msg = (String) message.obj;
            Log.i(TAG, "handleMessage(" + msg + ")"); //NON-NLS
            switch (msg) {
                case "play": //NON-NLS
                    audioPlayer.play();
                    break;
                case "pause": //NON-NLS
                    audioPlayer.pause();
                    break;
                case "togglePlay": //NON-NLS
                    audioPlayer.togglePlay();
                    break;
                case "playNext": //NON-NLS
                    audioPlayer.playNext();
                    break;
                case "playPrevious": //NON-NLS
                    audioPlayer.playPrevious();
                    break;
            }
        }
    };

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("message"); //NON-NLS
            Log.i(TAG, "Broadcast.onReceive(" + msg + ")"); //NON-NLS
            switch (msg) {
                case "enableSync":
                    enableSync(true);
                    break;
                case "setupGenres":
                    setupGenres();
                    break;
                case "setupTags":
                    setupTags();
                    break;
            }
        }
    };

    private void dim(final boolean on) {
        new CountDownTimer(500, 50) {
            private float brightness = on ? 0 : 1;

            @Override
            public void onTick(long millisUntilFinished_) {
                if (on) {
                    setBrightness(brightness += 0.1);
                } else {
                    setBrightness(brightness -= 0.1);
                }
            }

            @Override
            public void onFinish() {
                setBrightness(on ? 1 : 0);
                isDimOn = true;
            }
        }.start();
    }

    private static Timer timer = new Timer();
    private boolean isDimOn = true;

    private void dimOn() {
        if (toggleButtonDimMode.isChecked()) {
            if (!isDimOn) {
                dim(true);
            }
            timer.cancel();
            timer.purge();
            Log.v(TAG, "timerTask cancelled"); //NON-NLS
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.v(TAG, "timerTask performed"); //NON-NLS
                    setBrightness(0);
                    //dim(false);
                    isDimOn = false;
                }
            }, 5 * 1000);
            Log.v(TAG, "timerTask scheduled"); //NON-NLS
        }
    }

    private void setBrightness(final float brightness) {
        Log.v(TAG, "setBrightness(" + brightness + ");"); //NON-NLS
        runOnUiThread(() -> {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.screenBrightness = brightness;
            getWindow().setAttributes(params);
        });
    }

    @SuppressLint("SetTextI18n")
    private void enableRemote(final boolean enable) {
        runOnUiThread(() -> {
            if (enable) {
                buttonRemote.setBackgroundResource(R.drawable.remote_off);
                enablePlaylistEdit(true);
                setupLocalPlaylistSpinner();
            } else {
                buttonRemote.setText("0");
                buttonRemote.setBackgroundResource(R.drawable.remote_on);
            }
            buttonRemote.setEnabled(true);
        });
    }

    @SuppressLint("SetTextI18n")
    private void enableSync(final boolean enable) {
        runOnUiThread(() -> {
            buttonSync.setEnabled(false);
            if (enable) {
                buttonSync.setText("1");
                buttonSync.setBackgroundResource(R.drawable.connect_off_new);
            } else {
                buttonSync.setText("0");
                buttonSync.setBackgroundResource(R.drawable.connect_on);
            }
            buttonSync.setEnabled(true);
        });
    }

    @SuppressLint("SetTextI18n")
    private void enableClientRemote(final Button button) {
        runOnUiThread(() -> {
            button.setEnabled(false);
            button.setText("1");
            button.setBackgroundResource(R.drawable.remote_off);
            button.setEnabled(true);
        });
    }

    private void enablePlaylistEdit(final boolean enable) {
        runOnUiThread(() -> {
            toggle(layoutPlaylistEditBar, !enable);
            layoutPlaylistToolBar.setVisibility(enable ? View.VISIBLE : View.GONE);
        });
    }

    private static final int REQUEST = 112;

    private String[] PERMISSIONS;

    public void checkPermissionsThenScanLibrary() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else {
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        }
        if (!hasPermissions(this, PERMISSIONS)) {
            String msgStr = "<html><b>" + getString(R.string.permissionMsg_01) + "</b>" + getString(R.string.permissionMsg_02)
                    + "<BR/><BR/>"
                    + "<i>- <u>" + getString(R.string.permissionMsg_11) + "</u></i>: " + getString(R.string.permissionMsg_12)
                    + "</html>";

            AlertDialog alertDialog = new AlertDialog.Builder(ActivityMain.this).create();
            alertDialog.setTitle(getString(R.string.permissionTitle));
            alertDialog.setMessage(Html.fromHtml(msgStr));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    (dialog, which) -> {
                        dialog.dismiss();
                        askPermissions();
                    });
            alertDialog.show();
        } else {
            connectDatabase();
        }
    }

    private void setupTags() {
        runOnUiThread(() -> {
            layoutTags.removeAllViews();
            layoutTagsPlaylist.removeAllViews(); //NON-NLS //NON-NLS
            makeButtonTagPlaylist(Integer.MAX_VALUE, "null"); //NON-NLS
            if (RepoTags.get() != null) {
                for (Map.Entry<Integer, String> tag : RepoTags.get().entrySet()) {
                    makeButtonTag(tag.getKey(), tag.getValue());
                    makeButtonTagPlaylist(tag.getKey(), tag.getValue());
                }
                //Re-display track and playlist
                displayTrackDetails();
                displayPlaylist(localSelectedPlaylist);
            }
        });
    }

    private void setupGenres() {
        runOnUiThread(() -> {
            layoutGenrePlaylist.removeAllViews();
            for (String genre : RepoGenres.get()) {
                makeButtonGenrePlaylist(-1, genre);
            }
            //Re-display track and playlist
            displayTrack(); //spinner genre is re-set in there
            displayPlaylist(localSelectedPlaylist);
        });
    }

    private void setupSpinnerGenre(final List<String> genres, final String genre) {
        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genres);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        runOnUiThread(() -> {
            spinnerGenreSend = false;
            spinnerGenre.setAdapter(arrayAdapter);
            if (!genre.equals("")) {
                spinnerGenre.setSelection(arrayAdapter.getPosition(genre));
            }
        });
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST);
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            connectDatabase();
        }
    }

    private void setupButton(final int buttonName, final String msg) {
        Button button = findViewById(buttonName);
        button.setOnClickListener(v -> doAction(msg));
    }

    protected void doAction(String msg) {
        dimOn();
        if (isRemoteConnected()) {
            clientRemote.send(msg);
        } else {
            switch (msg) {
                case "previousTrack":
                    playPrevious();
                    break;
                case "nextTrack":
                    playNext();
                    break;
                case "playTrack":
                    audioPlayer.togglePlay(); //NON-NLS
                    break;
                case "pullup": //NON-NLS
                    audioPlayer.pullUp();
                    break; //NON-NLS
                case "rewind": //NON-NLS
                    audioPlayer.rewind(); //NON-NLS
                    break;
                case "forward": //NON-NLS
                    audioPlayer.forward();
                    break;
                case "volUp":
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    break;
                case "volDown":
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    break;
                default:
                    //Popup("Error", "Not implemented");
                    helperToast.toastLong(getString(R.string.mainToastNotImplemented));
                    break;
            }
        }
    }

    private void setupLocalPlaylistsThenStartServiceScan() {
        localPlaylists = new HashMap<>();
        File playlistFolder = HelperFile.getFolder("playlists");
        if (playlistFolder != null) {
            for (String file : Objects.requireNonNull(playlistFolder.list())) {
                if (file.endsWith(".plli")) {
                    Playlist playlist = readPlaylist(file);
                    if (playlist != null) {
                        localPlaylists.put(playlist.getName(), playlist);
                    }
                }
            }
        }
        setupLocalPlaylistSpinner((String) null);

        mHandler.postDelayed(() -> {
            toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
            toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
            toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
            toggleOff(toggleButtonPlaylist, layoutPlaylist);
            toggleOff(toggleButtonEditTags, layoutEditTags);
            toggleOff(toggleButtonControls, layoutControls);
        }, 500);

        mHandler.postDelayed(() -> {
            refreshLocalPlaylistSpinner(true);
        }, 5000);

        //Start Scan Service
        if (!isMyServiceRunning(ServiceScan.class)) {
            Intent service = new Intent(getApplicationContext(), ServiceScan.class);
            service.putExtra("getAppDataPath", HelperFile.getAudioRootFolder());
            service.putExtra("forceRefresh", false);
            startService(service);
        }
    }

    private void setupLocalPlaylistSpinner(String playlistName) {
        if (localPlaylists.size() > 0) {
            localPlaylists = sortHashMapByValues(localPlaylists);
        } else {
            Playlist playlistAll = new Playlist(getString(R.string.playlistDefaultAllPlaylistName), true);
            localPlaylists.put(playlistAll.getName(), playlistAll);
            playlistName = playlistAll.getName();
        }

        if (playlistName != null && localPlaylists.containsKey(playlistName)) {
            localSelectedPlaylist = localPlaylists.get(playlistName);
        } else {
            localSelectedPlaylist = localPlaylists.values().iterator().next();
        }

        playListArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                new ArrayList<>(localPlaylists.values()));
        setupLocalPlaylistSpinner();
        runOnUiThread(() -> displayPlaylist(localSelectedPlaylist));
    }

    private void setupLocalPlaylistSpinner(Playlist playlist) {
        localPlaylists.put(playlist.getName(), playlist);
        playListArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                new ArrayList<>(localPlaylists.values()));
        setupPlaylistSpinner(playListArrayAdapter, localSelectedPlaylist);
    }

    private void setupLocalPlaylistSpinner() {
        setupPlaylistSpinner(playListArrayAdapter, localSelectedPlaylist);
        refreshLocalPlaylistSpinner(false);
    }

    private void setupPlaylistSpinner(final ArrayAdapter<Playlist> arrayAdapter,
                                      final Playlist selectedPlaylist) {
        runOnUiThread(() -> {
            spinnerPlaylistSend = false;
            spinnerPlaylist.setAdapter(arrayAdapter);
            if (selectedPlaylist != null) {
                spinnerPlaylist.setSelection(arrayAdapter.getPosition(selectedPlaylist));
            }
        });
    }

    private void refreshLocalPlaylistSpinner(final boolean refreshAll) {
        if (localSelectedPlaylist != null) {
            new Thread() {
                public void run() {
                    if (refreshAll) {
                        for (Playlist playlist : localPlaylists.values()) {
                            playlist.getNbFiles();
                        }
                    } else {
                        localSelectedPlaylist.getNbFiles();
                    }
                    runOnUiThread(() -> playListArrayAdapter.notifyDataSetChanged());
                }
            }.start();
        }
    }

    private void displayPlaylist(Playlist playlist) {
        if (playlist != null) {
            for (int i = 0; i < layoutTagsPlaylist.getFlexItemCount(); i++) {
                TriStateButton button = (TriStateButton) layoutTagsPlaylist.getFlexItemAt(i);
                if (button != null) {
                    button.setState(TriStateButton.STATE.ANY);
                    setTagButtonTextColor(button, TriStateButton.STATE.ANY);
                }
            }
            for (int i = 0; i < layoutGenrePlaylist.getFlexItemCount(); i++) {
                TriStateButton button = (TriStateButton) layoutGenrePlaylist.getFlexItemAt(i);
                if (button != null) {
                    button.setState(TriStateButton.STATE.ANY);
                    setTagButtonTextColor(button, TriStateButton.STATE.ANY);
                }
            }
            TriStateButton nullButton = layoutTagsPlaylist.findViewWithTag("null"); //NON-NLS
            if (nullButton != null) {
                nullButton.setState(playlist.getUnTaggedState());
                setTagButtonTextColor(nullButton, playlist.getUnTaggedState());
            }
            for (Map.Entry<String, TriStateButton.STATE> entry : playlist.getTags()) {
                TriStateButton button = layoutTagsPlaylist.findViewWithTag(entry.getKey());
                if (button != null) {
                    button.setState(entry.getValue());
                    setTagButtonTextColor(button, entry.getValue());
                }
            }
            for (Map.Entry<String, TriStateButton.STATE> entry : playlist.getGenres()) {
                TriStateButton button = layoutGenrePlaylist.findViewWithTag(entry.getKey());
                if (button != null) {
                    button.setState(entry.getValue());
                    setTagButtonTextColor(button, entry.getValue());
                }
            }
            buttonRatingOperator.setText(playlist.getRatingOperator());
            ratingBarPlaylist.setRating(playlist.getRating());
            switch (playlist.getOrder()) {
                case RANDOM:
                    playListOrderRadio.check(R.id.order_random);
                    break;
                case PLAYCOUNTER_LASTPLAYED:
                    playListOrderRadio.check(R.id.order_playCounter_lastPlayed);
                    break;
            }

            spinnerLimitUnitSend = false;
            spinnerPlaylistLimitUnit.setAdapter(playListLimitUnitArrayAdapter);
            spinnerPlaylistLimitUnit.setSelection(playListLimitUnitArrayAdapter.getPosition(playlist.getLimitUnit().getDisplay(mContext)));

            spinnerLimitValueSend = false;
            spinnerPlaylistLimitValue.setAdapter(playListLimitValueArrayAdapter);
            spinnerPlaylistLimitValue.setSelection(playlist.getLimitValue());
            textViewPlaylist.setText(playlist.getSummary(this));
            button_save.setBackgroundResource(playlist.isModified() ?
                    R.drawable.ic_button_save_red : R.drawable.ic_button_save);
        }
    }

    public Playlist readPlaylist(String filename) {
        String readJson = HelperFile.readTextFile(filename, "playlists");
        if (!readJson.equals("")) {
            Playlist playlist = new Playlist(
                    filename.replaceFirst("[.][^.]+$", ""), true);
            Gson gson = new Gson();
            Type mapType = new TypeToken<Playlist>(){}.getType();
            try {
                playlist = gson.fromJson(readJson, mapType);
            } catch (JsonSyntaxException ex) {
                Log.e(TAG, "", ex);
            }
            return playlist;
        }
        return null;
    }

    public LinkedHashMap<String, Playlist> sortHashMapByValues(
            Map<String, Playlist> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Playlist> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Playlist> sortedMap = new LinkedHashMap<>();

        for (Playlist val : mapValues) {
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Playlist comp1 = passedMap.get(key);

                if (comp1.equals(val)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public void onPlaylistOrderRadioButtonClicked(View view) {
        dimOn();
        layoutOrderPlaylistLayout.setEnabled(false);
        boolean checked = ((RadioButton) view).isChecked();
        if (checked && localSelectedPlaylist != null) {
            switch (view.getId()) {
                case R.id.order_random:
                    localSelectedPlaylist.setOrder(RANDOM);
                    break;
                case R.id.order_playCounter_lastPlayed:
                    localSelectedPlaylist.setOrder(PLAYCOUNTER_LASTPLAYED);
                    break;
            }
            refreshQueueAndPlaylistSpinner();
        }
        layoutOrderPlaylistLayout.setEnabled(true);
    }

    ///TODO: Detect WIFI connection to allow/disallow "Connect" buttons
    //https://stackoverflow.com/questions/5888502/how-to-detect-when-wifi-connection-has-been-established-in-android
    private boolean checkConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifi.isConnected();
        }
        return false;
    }

    private void setTextView(final TextView textview, final Spanned msg) {
        runOnUiThread(() -> textview.setText(msg));
    }

    private void setSeekBar(final int currentPosition, final int total) {
        runOnUiThread(() -> {
            seekBarPosition.setMax(total);
            seekBarPosition.setProgress(currentPosition);
            textFileInfo_seekBefore.setText(StringManager.secondsToMMSS(currentPosition / 1000));
            textFileInfo_seekAfter.setText(String.format(
                    "- %s / %s",
                    StringManager.secondsToMMSS((total - currentPosition) / 1000),
                    StringManager.secondsToMMSS(total / 1000)));
        });
    }

    private void displayTrackDetails() {
        runOnUiThread(() -> setTextView(textViewFileInfo4, trimTrailingWhitespace(Html.fromHtml(
                String.format(Locale.getDefault(), "<html><BR/>%s<BR/>%s %d/5 %s %s<BR/>%s%s<BR/></html>", //NON-NLS
                        displayedTrack.getSource().equals("") //NON-NLS
                                ? "" //NON-NLS
                                : "<u>".concat(displayedTrack.getSource()).concat("</u>"), //NON-NLS
                        displayedTrack.getTags(),
                        (int) displayedTrack.getRating(),
                        displayedTrack.getGenre(),
                        displayedTrack.getYear(),
                        getLastPlayedAgo(displayedTrack),
                        getAddedDateAgo(displayedTrack))))));
    }

    public static String getLastPlayedAgo(Track track) {
        return track.getPlayCounter() <= 0
                ? mContext.getString(R.string.trackNeverPlayed)
                : String.format(Locale.getDefault(),
                "%s %s (%dx). ", //NON-NLS
                mContext.getString(R.string.trackPlayed),
                prettyTime.format(track.getLastPlayed()),
                track.getPlayCounter());
    }

    public static String getAddedDateAgo(Track track) { //NON-NLS
        return String.format(
                "%s %s.", //NON-NLS
                mContext.getString(R.string.trackAdded),
                prettyTime.format(track.getAddedDate()));
    }

    private void displayTrack() {
        if (displayedTrack != null) {
            runOnUiThread(() -> {
                setTextView(textViewFileInfo1, trimTrailingWhitespace(Html.fromHtml(
                        "<html><b>" +
                                displayedTrack.getTitle()
                                        .concat("</b></html>"))));
                setTextView(textViewFileInfo2, trimTrailingWhitespace(Html.fromHtml(
                        "<html><b>" +
                                displayedTrack.getArtist()
                                        .concat("</b></html>"))));
                setTextView(textViewFileInfo3, trimTrailingWhitespace(Html.fromHtml(
                        "<html>" +
                                displayedTrack.getAlbum()
                                        .concat("</html>"))));
                displayTrackDetails();
                ratingBar.setEnabled(false);
                ratingBar.setRating((float) displayedTrack.getRating());
                ratingBar.setEnabled(true);
                setupSpinnerGenre(RepoGenres.get(), displayedTrack.getGenre());
                displayTrackTags();
            });

            if (displayedTrack.getIdFileRemote() >= 0) {
                displayImage(RepoCovers.getCoverIcon(displayedTrack, RepoCovers.IconSize.COVER, true));
                bluetoothNotifyChange(AVRCP_META_CHANGED);
            } else if (displayedTrack.getCoverHash().equals("welcomeHash")) {
                displayImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_startup_cover_foreground));
            } else {
                displayCover();
            }
        }
    }

    private void displayTrackTags() {
        ArrayList<String> fileTags = displayedTrack.getTags(false);
        if (fileTags == null) {
            fileTags = new ArrayList<>();
        }
        for (Map.Entry<Integer, String> tag : RepoTags.get().entrySet()) {
            ToggleButton button = layoutTags.findViewById(tag.getKey());
            if (button != null && button.isChecked() != fileTags.contains(tag.getValue())) {
                button.setChecked(fileTags.contains(tag.getValue()));
                setTagButtonTextColor(button);
            }
        }
    }

    //private static final String AVRCP_PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
    private static final String AVRCP_META_CHANGED = "com.android.music.metachanged";

    private void bluetoothNotifyChange(String what) {
        Intent i = new Intent(what);
        i.putExtra("id", Long.valueOf(displayedTrack.getIdFileRemote())); //NON-NLS
        i.putExtra("artist", displayedTrack.getArtist()); //NON-NLS //NON-NLS //NON-NLS //NON-NLS //NON-NLS
        i.putExtra("album", displayedTrack.getAlbum()); //NON-NLS
        i.putExtra("track", displayedTrack.getTitle()); //NON-NLS
        i.putExtra("playing", "true"); //NON-NLS
        i.putExtra("ListSize", "99");
        i.putExtra("duration", "20"); //NON-NLS
        i.putExtra("position", "0"); //NON-NLS
        sendBroadcast(i);
    }

    //Display cover from cache or ask for it
    private void displayCover() {
        Bitmap coverIcon = RepoCovers.getCoverIcon(displayedTrack, RepoCovers.IconSize.COVER, false);
        if (coverIcon != null) {
            displayImage(coverIcon);
        } else { //Ask cover
            int maxWidth = this.getWindow().getDecorView().getWidth();
            if (maxWidth <= 0) {
                maxWidth = RepoCovers.IconSize.COVER.getSize();
            }
            if (clientRemote != null) {
                clientRemote.send("sendCover" + maxWidth);
            }
        }
    }

    private void displayImage(Bitmap bitmap) {
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_startup_cover_foreground);
        }
        Bitmap finalBitmap = bitmap;
        runOnUiThread(() -> {
            imageViewCover.setImageBitmap(finalBitmap);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getApplicationContext().getResources(), finalBitmap);
            bitmapDrawable.setAlpha(50);
            layoutMain.setBackground(bitmapDrawable);
        });
    }

    class ListenerRemote implements IListenerRemote {

        private final String TAG = ListenerRemote.class.getName();

        @Override
        public void onReceivedJson(final String json) {
            try {
                JSONObject jObject = new JSONObject(json);
                String type = jObject.getString("type"); //NON-NLS //NON-NLS
                switch (type) {
                    case "playlists": //NON-NLS
                        String selectedPlaylist = jObject.getString("selectedPlaylist"); //NON-NLS
                        Playlist temp = new Playlist(selectedPlaylist, false);
                        final JSONArray jsonPlaylists = (JSONArray) jObject.get("playlists"); //NON-NLS
                        final List<Playlist> playlists = new ArrayList<>();
                        for (int i = 0; i < jsonPlaylists.length(); i++) {
                            String playlist = (String) jsonPlaylists.get(i);
                            Playlist playList = new Playlist(playlist, false);
                            if (playlist.equals(selectedPlaylist)) {
                                playList = temp;
                            }
                            playlists.add(playList);
                        }
                        ArrayAdapter<Playlist> arrayAdapter =
                                new ArrayAdapter<>(ActivityMain.this,
                                        R.layout.spinner_item, playlists);
                        setupPlaylistSpinner(arrayAdapter, temp);
                        enablePlaylistEdit(false);
                        break;
                    case "currentPosition":
                        final int currentPosition = jObject.getInt("currentPosition"); //NON-NLS
                        final int total = jObject.getInt("total"); //NON-NLS
                        if (isRemoteConnected()) {
                            setSeekBar(currentPosition, total);
                        }
                        break;
                    case "fileInfoInt":
                        displayedTrack = new Track(jObject, new File(""), false);
                        //TODO: Display Playlist name and nbFiles
                        displayTrack();
                        break;
                }
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onReceivedBitmap(final Bitmap bitmap) { //NON-NLS
            Log.d(TAG, "onReceivedBitmap: callback"); //NON-NLS //NON-NLS
            Log.d(TAG, bitmap == null ? "null" : bitmap.getWidth() + "x" + bitmap.getHeight()); //NON-NLS //NON-NLS
            if (bitmap != null && !RepoCovers.contains(displayedTrack.getCoverHash(), RepoCovers.IconSize.COVER)) {
                RepoCovers.writeIconToCache(displayedTrack.getCoverHash(), bitmap);
            }
            displayCover();
        }

        @Override
        public void onDisconnected(final String msg) {
            if (!msg.equals("")) {
                runOnUiThread(() -> helperToast.toastShort(msg));
            }
            enableClientRemote(buttonRemote);
            setupLocalPlaylistSpinner();
            displayedTrack = localTrack;
            displayTrack();
        }
    }

    private void stopRemote() { //NON-NLS
        if (clientRemote != null) {
            clientRemote.close();
            clientRemote = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "ActivityMain onCreateOptionsMenu"); //NON-NLS
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long //NON-NLS
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(TAG, "ActivityMain onOptionsItemSelected"); //NON-NLS
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true; //NON-NLS
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() { //NON-NLS
        Log.i(TAG, "ActivityMain onBackPressed"); //NON-NLS
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.mainAlertDialogClosingApplicationTitle)
                .setMessage(R.string.mainAlertDialogClosingApplicationMessage)
                .setPositiveButton(R.string.globalLabelYes, (dialog, which) -> finish())
                .setNegativeButton(R.string.globalLabelNo, null)
                .show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.VIEW")) {
            getFromQRcode(intent.getDataString());
        }
    }

    //Receivers
    ComponentName receiverMediaButtonName;
    ReceiverHeadSetPlugged receiverHeadSetPlugged = new ReceiverHeadSetPlugged();

    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothHeadset mBluetoothHeadset;

    protected BluetoothProfile.ServiceListener mHeadsetProfileListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {
            try {
                unregisterReceiver(mHeadsetBroadcastReceiver);
            } catch (IllegalArgumentException ex) {
                //java.lang.IllegalArgumentException: Receiver not registered
                //TODO: We don't care but why does this happen ?
            }
            mBluetoothHeadset = null;
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            mBluetoothHeadset = (BluetoothHeadset) proxy;

            registerReceiver(mHeadsetBroadcastReceiver,
                    new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED));

            //This is triggered on phone calls, already received in ReceiverPhoneCall
            /*registerReceiver(mHeadsetBroadcastReceiver,
                    new IntentFilter(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED));*/
        }
    };

    protected BroadcastReceiver mHeadsetBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction())
                    .equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED);
                if (state == BluetoothHeadset.STATE_CONNECTED) { //NON-NLS
                    Log.i(TAG, "BT onConnected. Waiting 4s"); //NON-NLS
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace(); //NON-NLS
                    }
                    audioPlayer.play(); //NON-NLS
                } else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                    Log.i(TAG, "BT DISconnected"); //NON-NLS
                    audioPlayer.pause();

                    //Somehow, this situation (at least) (can) endup with other receivers (headsethook at least)
                    //not to trigger anymore => Why ?
                    //So re-registering button receiver. Seems to work
                    audioManager.unregisterMediaButtonEventReceiver(receiverMediaButtonName);
                    registerButtonReceiver();
                }
            }/*
            else // BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED
            {
                //This is triggered on phone calls, already received in ReceiverPhoneCall

                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_AUDIO_DISCONNECTED);
                if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED)
                {
                    Log.d(TAG, "BT AUDIO onConnected");

                }
                else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED)
                {
                    Log.d(TAG, "BT AUDIO DISconnected");

                }
            }*/
        }
    };

    public static Playlist clonePlaylist(Playlist playlist) {
        //Save to Json
        Gson gson = new Gson();
        String json = gson.toJson(playlist);
        //Create a new from json
        Type mapType = new TypeToken<Playlist>() {
        }.getType();
        Playlist newPlaylist = null;
        try {
            newPlaylist = gson.fromJson(json, mapType);
        } catch (JsonSyntaxException ex) {
            Log.e(TAG, "", ex);
        }
        return newPlaylist;
    }

    enum AudioOutput {
        LOCAL,
        RASPBERRY
    }
}
