package phramusca.com.jamuzremote;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
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

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static phramusca.com.jamuzremote.Playlist.Order.PLAYCOUNTER_LASTPLAYED;
import static phramusca.com.jamuzremote.Playlist.Order.RANDOM;

//FIXME: Submit to f-droid.org
//https://gitlab.com/fdroid/fdroiddata/blob/master/CONTRIBUTING.md
//https://f-droid.org/

// TODO: This class is far too big: move some out
// FIXME: Move audio to a service
// Why not using the standard android player by the way ? (less control for replaygain ?)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private SharedPreferences preferences;
    private HelperToast helperToast = new HelperToast(this);
    private ClientRemote clientRemote;
    private Track displayedTrack;
    private Track localTrack;
    protected static Map<String, Bitmap> coverMap = new HashMap<>();
    private AudioManager audioManager;
    public static AudioPlayer audioPlayer;

    //In internal SD emulated storage:
    //TODO: Possibly, change database location to external SD as we now have rights
    //In external SD. Does not seem to work !
    //private static final String DB_PATH =
    //      "/storage/3515-1C15/Android/data/"+BuildConfig.APPLICATION_ID;
    public static File musicLibraryDbFile = new File(
            Environment.getExternalStorageDirectory()+"/JaMuz/JaMuzRemote.db");

    private List<Track> queue = new ArrayList<>();
    private ArrayList<Track> queueHistory = new ArrayList<>();
    private Map<String, Playlist> localPlaylists = new LinkedHashMap<>();
    private ArrayAdapter<Playlist> playListArrayAdapter;
    private Playlist localSelectedPlaylist;

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int QUEUE_REQUEST_CODE = 1;
    private static final int QR_REQUEST_CODE = 49374;

    private static final int MAX_QUEUE = 10;

    // GUI elements
    private TextView textViewFileInfo;
    private EditText editTextConnectInfo;
    private TextView textViewPath;
    private TextView textViewPlaylist;
    private Button buttonRemote;
    private Button buttonSync;
    private ToggleButton toggleButtonDimMode;
    private ToggleButton toggleButtonControls;
    private ToggleButton toggleButtonTagsPanel;
    private ToggleButton toggleButtonRatingPanel;
    private ToggleButton toggleButtonOrderPanel;
    private ToggleButton toggleButtonGenresPanel;
    private ToggleButton toggleButtonEditTags;
    private ToggleButton toggleButtonPlaylist;
    private ToggleButton toggleButtonOptions;
    private Button buttonRatingOperator;
    private Button button_save;
    private SeekBar seekBarPosition;
    private Spinner spinnerPlaylist;
    private Spinner spinnerGenre;
    private Spinner spinnerPlaylistLimitUnit;
    private Spinner spinnerPlaylistLimitValue;
    private static boolean spinnerPlaylistSend=false;
    private static boolean spinnerGenreSend=false;
    private static boolean spinnerLimitUnitSend =false;
    private static boolean spinnerLimitValueSend=false;
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
    private LinearLayout layoutAttributes;
    private LinearLayout layoutPlaylist;
    private LinearLayout layoutPlaylistEditBar;
    private GridLayout layoutPlaylistToolBar;
    private GridLayout layoutOptions;

    private IntentIntegrator qrScan;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "MainActivity onCreate");
        setContentView(R.layout.activity_main);

        textToSpeech =new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(textToSpeech.getDefaultVoice().getLocale());
            }
        });

        layoutTags = (FlexboxLayout) findViewById(R.id.panel_tags);
        layoutTagsPlaylist = (FlexboxLayout) findViewById(R.id.panel_tags_playlist);
        layoutGenrePlaylist = (FlexboxLayout) findViewById(R.id.panel_genre_playlist);
        layoutTagsPlaylistLayout = (LinearLayout) findViewById(R.id.panel_tags_playlist_layout);
        layoutRatingPlaylistLayout = (LinearLayout) findViewById(R.id.panel_rating_playlist_layout);
        layoutOrderPlaylistLayout = (LinearLayout) findViewById(R.id.panel_order_playlist_layout);

        playListOrderRadio = (RadioGroup) findViewById(R.id.playlist_order_radio);

        layoutGenrePlaylistLayout = (LinearLayout) findViewById(R.id.panel_genre_playlist_layout);
        layoutAttributes = (LinearLayout) findViewById(R.id.panel_attributes);
        layoutPlaylist = (LinearLayout) findViewById(R.id.panel_playlist);

        layoutPlaylistToolBar = (GridLayout) findViewById(R.id.panel_playlist_toolbar);
        layoutPlaylistEditBar = (LinearLayout) findViewById(R.id.panel_playlist_editbar);

        layoutControls = (LinearLayout) findViewById(R.id.panel_controls);
        layoutOptions = (GridLayout) findViewById(R.id.panel_options);

        textViewFileInfo = (TextView) findViewById(R.id.textFileInfo);

        editTextConnectInfo = (EditText) findViewById(R.id.editText_info);
        editTextConnectInfo.setOnTouchListener(dimOnTouchListener);

        preferences = getPreferences(MODE_PRIVATE);
        editTextConnectInfo.setText(preferences.getString("connectionString", "192.168.0.11:2013"));

        buttonRemote = (Button) findViewById(R.id.button_connect);
        buttonRemote.setOnClickListener(v -> {
            dimOn();
            buttonRemote.setEnabled(false);
            buttonRemote.setBackgroundResource(R.drawable.remote_ongoing);
            if(buttonRemote.getText().equals("Connect")) {
                ClientInfo clientInfo = getClientInfo(true);
                if(clientInfo!=null) {
                    clientRemote =  new ClientRemote(clientInfo, new CallBackRemote());
                } else {
                    enableRemote(true);
                    return;
                }
                new Thread() {
                    public void run() {
                        if(clientRemote.connect()) {
                            enableRemote(false);
                        }
                        else {
                            enableRemote(true);
                        }
                    }
                }.start();
            }
            else {
                enableRemote(true);
                stopRemote();
            }
        });

        buttonSync = (Button) findViewById(R.id.button_sync);
        buttonSync.setOnClickListener(v -> {
            dimOn();
            buttonSync.setBackgroundResource(R.drawable.connect_ongoing);
            if(buttonSync.getText().equals("Connect")) {
                enableSync(false);
                ClientInfo clientInfo = getClientInfo(false);
                if(clientInfo!=null) {
                    if(!isMyServiceRunning(ServiceSync.class)) {
                        Intent service = new Intent(getApplicationContext(), ServiceSync.class);
                        service.putExtra("clientInfo", clientInfo);
                        service.putExtra("getAppDataPath", getAppDataPath());
                        startService(service);
                    }
                }
            }
            else {
                Log.i(TAG, "Broadcast("+ServiceSync.USER_STOP_SERVICE_REQUEST+")");
                sendBroadcast(new Intent(ServiceSync.USER_STOP_SERVICE_REQUEST));
                enableSync(true);
            }
        });

        getFromQRcode(getIntent().getDataString());

        textViewPath = (TextView) findViewById(R.id.textViewPath);

        textViewPlaylist = (TextView) findViewById(R.id.textViewPlaylist);

        Button buttonSaveConnectionString = (Button) findViewById(R.id.button_save_connectionString);
        buttonSaveConnectionString.setOnClickListener(view ->
                setConfig("connectionString", editTextConnectInfo.getText().toString())
        );

        qrScan = new IntentIntegrator(this);
        Button button_scan_QR = (Button) findViewById(R.id.button_scan_QR);
        button_scan_QR.setOnClickListener(view ->
                qrScan.initiateScan()
        );

        String userPath = preferences.getString("userPath", "/");
        String display = userPath.equals("/")?
                getString(R.string.pathInfo)
                :userPath;
        textViewPath.setText(trimTrailingWhitespace(Html.fromHtml("<html>"
                .concat(display)
                .concat("</html>"))));
        Button dirChooserButton = (Button) findViewById(R.id.button_browse);
        dirChooserButton.setOnClickListener(new View.OnClickListener()
        {
            private boolean m_newFolderEnabled = false;
            @Override
            public void onClick(View v)
            {
                DirectoryChooserDialog directoryChooserDialog =
                        new DirectoryChooserDialog(MainActivity.this,
                                chosenDir -> {
                                    textViewPath.setText(trimTrailingWhitespace(Html.fromHtml("<html>"
                                            .concat(chosenDir)
                                            .concat("</html>"))));
                                    setConfig("userPath", chosenDir);
                                    checkPermissionsThenScanLibrary();
                                });
                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                directoryChooserDialog.chooseDirectory(preferences.getString("userPath", "/"));
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if(fromUser) { //as it is also set when server sends file info (and it can be 0)
                dimOn();
                ratingBar.setEnabled(false);
                displayedTrack.setRating(Math.round(rating));
                if (isRemoteConnected()) {
                    clientRemote.send("setRating".concat(String.valueOf(Math.round(rating))));
                } else {
                    displayedTrack.update();
                    refreshQueueAndPlaylistSpinner(true);
                }
                ratingBar.setEnabled(true);
            }
        });

        ratingBarPlaylist = (RatingBar) findViewById(R.id.ratingBarPlaylist);
        ratingBarPlaylist.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if(fromUser) {
                dimOn();
                ratingBarPlaylist.setEnabled(false);
                if(localSelectedPlaylist!=null) {
                    localSelectedPlaylist.setRating(Math.round(rating));
                    refreshQueueAndPlaylistSpinner();
                }
                ratingBarPlaylist.setEnabled(true);
            }
        });

        Button buttonClearRating = (Button) findViewById(R.id.button_clear_rating);
        buttonClearRating.setOnClickListener(v -> {
            ratingBarPlaylist.setRating(0F);
            if(localSelectedPlaylist!=null) {
                localSelectedPlaylist.setRating(0);
                refreshQueueAndPlaylistSpinner();
            }
        });

        buttonRatingOperator = (Button) findViewById(R.id.button_rating_operator);
        buttonRatingOperator.setOnClickListener(v -> {
            if(localSelectedPlaylist!=null) {
                buttonRatingOperator.setText(localSelectedPlaylist.setRatingOperator());
                refreshQueueAndPlaylistSpinner();
            }
        });

        playListLimitUnitArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.limitUnits, android.R.layout.simple_spinner_item);
        playListLimitUnitArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaylistLimitUnit = (Spinner) findViewById(R.id.spinner_playlist_limit_unit);
        spinnerPlaylistLimitUnit.setAdapter(playListLimitUnitArrayAdapter);
        spinnerPlaylistLimitUnit.setOnItemSelectedListener(spinnerLimitUnitListener);
        spinnerPlaylistLimitUnit.setOnTouchListener(dimOnTouchListener);

        Integer[] limitValues = new Integer[100];
        for (int i=0; i<100; i++) {
            limitValues[i] = i;
        }
        playListLimitValueArrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, limitValues);
        playListLimitValueArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaylistLimitValue = (Spinner) findViewById(R.id.numberPicker_playlist_limit_value);
        spinnerPlaylistLimitValue.setAdapter(playListLimitValueArrayAdapter);
        spinnerPlaylistLimitValue.setOnItemSelectedListener(spinnerLimitValueListener);
        spinnerPlaylistLimitValue.setOnTouchListener(dimOnTouchListener);

        seekBarPosition = (SeekBar) findViewById(R.id.seekBar);
        seekBarPosition.setEnabled(false);

        SeekBar seekBarReplayGain = (SeekBar) findViewById(R.id.seekBarReplayGain);
        seekBarReplayGain.setProgress(70);
        seekBarReplayGain.setMax(100); //default, but still
        seekBarReplayGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = ((float)progress / 100.0f);
                Log.i(TAG, "seekBarReplayGain: "+value);
                String msg = audioPlayer.setVolume(value, displayedTrack);
                if(!msg.equals("")) {
                    helperToast.toastLong(msg);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        spinnerPlaylist = (Spinner) findViewById(R.id.spinner_playlist);
        spinnerPlaylist.setOnItemSelectedListener(spinnerPlaylistListener);
        spinnerPlaylist.setOnTouchListener(dimOnTouchListener);

        spinnerGenre = (Spinner) findViewById(R.id.spinner_genre);
        spinnerGenre.setOnItemSelectedListener(spinnerGenreListener);
        spinnerGenre.setOnTouchListener(dimOnTouchListener);

        setupButton(R.id.button_previous, "previousTrack");
        setupButton(R.id.button_play, "playTrack");
        setupButton(R.id.button_next, "nextTrack");
        setupButton(R.id.button_rewind, "rewind");
        setupButton(R.id.button_pullup, "pullup");
        setupButton(R.id.button_forward, "forward");
        setupButton(R.id.button_volUp, "volUp");
        setupButton(R.id.button_volDown, "volDown");

        toggleButtonDimMode = (ToggleButton) findViewById(R.id.button_dim_mode);
        toggleButtonDimMode.setOnClickListener(v -> setDimMode(toggleButtonDimMode.isChecked()));

        toggleButtonControls = (ToggleButton) findViewById(R.id.button_controls_toggle);
        toggleButtonControls.setOnClickListener(v -> {
            dimOn();
            toggle(layoutControls, !toggleButtonControls.isChecked());
        });

        toggleButtonTagsPanel = (ToggleButton) findViewById(R.id.button_tags_panel_toggle);
        toggleButtonTagsPanel.setOnClickListener(v -> {
            dimOn();
            toggle(layoutTagsPlaylistLayout, !toggleButtonTagsPanel.isChecked());
            if(toggleButtonTagsPanel.isChecked()) {
                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            }
        });

        toggleButtonRatingPanel = (ToggleButton) findViewById(R.id.button_rating_layout);
        toggleButtonRatingPanel.setOnClickListener(v -> {
            dimOn();
            toggle(layoutRatingPlaylistLayout, !toggleButtonRatingPanel.isChecked());
            if(toggleButtonRatingPanel.isChecked()) {
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            }
        });

        toggleButtonOrderPanel = (ToggleButton) findViewById(R.id.button_order_panel_toggle);
        toggleButtonOrderPanel.setOnClickListener(v -> {
            dimOn();
            toggle(layoutOrderPlaylistLayout, !toggleButtonOrderPanel.isChecked());
            if(toggleButtonOrderPanel.isChecked()) {
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
            }
        });

        toggleButtonGenresPanel = (ToggleButton) findViewById(R.id.button_genres_panel_toggle);
        toggleButtonGenresPanel.setOnClickListener(v -> {
            dimOn();
            toggle(layoutGenrePlaylistLayout, !toggleButtonGenresPanel.isChecked());
            if(toggleButtonGenresPanel.isChecked()) {
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            }
        });

        toggleButtonEditTags = (ToggleButton) findViewById(R.id.button_edit_toggle);
        toggleButtonEditTags.setOnClickListener(v -> {
            dimOn();
            toggle(layoutAttributes, !toggleButtonEditTags.isChecked());
            if(toggleButtonEditTags.isChecked()) {
                toggleOff(toggleButtonPlaylist, layoutPlaylist);
                toggleOff(toggleButtonOptions, layoutOptions);

                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            }
        });

        toggleButtonPlaylist = (ToggleButton) findViewById(R.id.button_playlist_toggle);
        toggleButtonPlaylist.setOnClickListener(v -> {
            dimOn();
            toggle(layoutPlaylist, !toggleButtonPlaylist.isChecked());
            if(toggleButtonPlaylist.isChecked()) {
                toggleOff(toggleButtonEditTags, layoutAttributes);
                toggleOff(toggleButtonOptions, layoutOptions);
            } else {
                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);
            }
        });

        toggleButtonOptions = (ToggleButton) findViewById(R.id.button_connect_toggle);
        toggleButtonOptions.setOnClickListener(v -> {
            dimOn();
            toggle(layoutOptions, !toggleButtonOptions.isChecked());
            if(toggleButtonOptions.isChecked()) {
                toggleOff(toggleButtonGenresPanel, layoutGenrePlaylistLayout);
                toggleOff(toggleButtonRatingPanel, layoutRatingPlaylistLayout);
                toggleOff(toggleButtonTagsPanel, layoutTagsPlaylistLayout);
                toggleOff(toggleButtonOrderPanel, layoutOrderPlaylistLayout);

                toggleOff(toggleButtonEditTags, layoutAttributes);
                toggleOff(toggleButtonPlaylist, layoutPlaylist);
            }
        });

        Button button_new = (Button) findViewById(R.id.button_new);
        button_new.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.playlistName);
            final EditText input = new EditText(MainActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            builder.setView(input);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                String text = input.getText().toString().trim();
                if(!localPlaylists.containsKey(text)) {
                    Playlist newPlaylist=null;
                    if(localSelectedPlaylist!=null) {
                        newPlaylist = clonePlaylist(localSelectedPlaylist);
                        newPlaylist.setName(text);
                    }
                    if(newPlaylist==null) {
                        newPlaylist = new Playlist(text, true);
                    }
                    localPlaylists.put(newPlaylist.getName(), newPlaylist);
                    setupLocalPlaylistSpinner(newPlaylist);
                } else {
                    helperToast.toastLong(getString(R.string.playlist)+" \""+text+"\" "+getString(R.string.alreadyExists));
                }
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();
        });

        button_save = (Button) findViewById(R.id.button_save);
        button_save.setOnClickListener(v -> {
            if(localSelectedPlaylist!=null) {
                StringBuilder msg= new StringBuilder().append(getString(R.string.playlist))
                        .append(" \"")
                        .append(localSelectedPlaylist.getName())
                        .append("\" ")
                        .append(getString(R.string.saved));
                if(localSelectedPlaylist.save()) {
                    button_save.setBackgroundResource(localSelectedPlaylist.isModified()?
                            R.drawable.ic_button_save_red:R.drawable.ic_button_save);
                    msg.append(" ").append(getString(R.string.successfully));
                } else {
                    msg.append(" ").append(getString(R.string.withErrors));
                }
                helperToast.toastShort(msg.toString());
            }
        });

        Button button_restore = (Button) findViewById(R.id.button_restore);
        button_restore.setOnClickListener(v -> {
            if(localSelectedPlaylist!=null) {
                StringBuilder msg= new StringBuilder().append(getString(R.string.playlist))
                        .append(" \"")
                        .append(localSelectedPlaylist.getName())
                        .append("\" ")
                        .append(getString(R.string.restored));
                HelperFile.createFolder(getString(R.string.playlistsFolder));
                Playlist playlist = readPlaylist(localSelectedPlaylist.getName()+".plli");
                if(playlist!=null) {
                    msg.append(" ").append(getString(R.string.successfully));
                    playlist.setModified(false);
                    setupLocalPlaylistSpinner(playlist, false);
                } else {
                    msg.append(" ").append(getString(R.string.withErrors));
                }
                helperToast.toastShort(msg.toString());
            }
        });

        Button button_delete = (Button) findViewById(R.id.button_delete);
        button_delete.setOnClickListener(v -> {
            if(localSelectedPlaylist!=null) {
                new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.deletePlaylist)
                .setMessage(getString(R.string.sureDelete) +
                        " \""+localSelectedPlaylist.getName()+"\" "+getString(R.string.playlistQuestion))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if(localPlaylists.size()>1) {
                        HelperFile.delete("Playlists", localSelectedPlaylist.getName() + ".plli");
                        localPlaylists.remove(localSelectedPlaylist.getName());
                        setupLocalPlaylistSpinner(null);
                    } else {
                        helperToast.toastShort(getString(R.string.cannotDeleteLastPlaylist));
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
            }
        });

        Button button_queue = (Button) findViewById(R.id.button_queue);
        button_queue.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PlayQueueActivity.class);
            ArrayList<Track> list = new ArrayList<>();
            //TODO: Implement pagination
            // https://stackoverflow.com/questions/16661662/how-to-implement-pagination-in-android-listview
            if(queueHistory!=null) {
                list.addAll(queueHistory.size()> MAX_QUEUE ?
                        new ArrayList<>(queueHistory.subList(queueHistory.size() - MAX_QUEUE, queueHistory.size()))
                        :queueHistory);
            }
            if(queue!=null) {
                list.addAll(queue.size()> MAX_QUEUE ? new ArrayList<>(queue.subList(0, MAX_QUEUE)):queue);
            }
            int histSize = 0;
            int offset = 0;
            if (queueHistory != null) {
                histSize = queueHistory.size()> MAX_QUEUE ? MAX_QUEUE:queueHistory.size();
                offset = queueHistory.size()-histSize;
            }
            intent.putExtra("queueArrayList", list);
            intent.putExtra("histIndex", queueHistoryIndex-offset);
            intent.putExtra("histSize", histSize);
            startActivityForResult(intent, QUEUE_REQUEST_CODE);
        });

        Button button_speech = (Button) findViewById(R.id.button_speech);
        button_speech.setOnClickListener(v -> displaySpeechRecognizer());

        imageViewCover = (ImageView) findViewById(R.id.imageView);
        layoutMain = (LinearLayout) findViewById(R.id.panel_main);

        LinearLayout layoutTrackInfo = (LinearLayout) findViewById(R.id.trackInfo);
        layoutTrackInfo.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                Log.v(TAG, "onSwipeTop");
                if (isRemoteConnected()) {
                    clientRemote.send("forward");
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
                    clientRemote.send("rewind");
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
                if(isDimOn) {
                    if(isRemoteConnected()) {
                        clientRemote.send("playTrack");
                    }
                    else {
                        audioPlayer.togglePlay();
                    }
                }
            }
            @Override
            public void onDoubleTapUp() {
                if(!isRemoteConnected() && isDimOn) {
                    audioPlayer.pullUp();
                    audioPlayer.resume(); //As toggled by simple Tap
                }
                //TODO: Do the same for remote when fixed on JaMuz
            }

            @Override
            public void onLongPressed() {
                displaySpeechRecognizer();
            }
        });

        localTrack = new Track(0, getString(R.string.welcomeTitle),
                getString(R.string.welcomeYear), getString(R.string.app_name), "coverHash", "---");
        displayedTrack = localTrack;
        displayTrack(false);

        //TODO: MAke this an option somehow

        //The following call creates default application folder
        // - in "external" card, the emulated one : /storage/emulated/0/Android//com.phramusca.jamuz/files
        // - and in real removable sd card : /storage/xxxx-xxxx/Android/com.phramusca.jamuz/files
        externalFilesDir = getExternalFilesDirs(null);
        checkPermissionsThenScanLibrary();

        CallBackPlayer callBackPlayer = new CallBackPlayer();
        audioPlayer = new AudioPlayer(callBackPlayer);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        registerButtonReceiver();

        //Start BT HeadSet connexion detection
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null)
        {
           if (audioManager.isBluetoothScoAvailableOffCall())
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    mBluetoothAdapter.getProfileProxy(this, mHeadsetProfileListener,
                            BluetoothProfile.HEADSET);
                }
            }
        }

        //TODO: Why this one needs registerReceiver whereas ReceiverPhoneCall does not
        registerReceiver(receiverHeadSetPlugged,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        if(isMyServiceRunning(ServiceSync.class)) {
            enableSync(false);
        }

        toggle(layoutControls, true);
        toggle(layoutOptions, true);
        toggle(layoutAttributes, true);
        toggle(layoutOrderPlaylistLayout, true);
        toggle(layoutGenrePlaylistLayout, true);
        toggle(layoutTagsPlaylistLayout, true);
        toggle(layoutRatingPlaylistLayout, true);
        //toggle(layoutPlaylist, true);

        setDimMode(toggleButtonDimMode.isChecked());
    }


    private ClientInfo getClientInfo(boolean isRemote) {
        if(!checkConnectedViaWifi())  {
            helperToast.toastLong("You must connect to WiFi network.");
            return null;
        }
        String infoConnect = editTextConnectInfo.getText().toString();
        String[] split = infoConnect.split(":");  //NOI18N
        if(split.length<2) {
            helperToast.toastLong("Bad format:\t"+infoConnect+"" +
                    "\nExpected:\t\t<IP>:<Port>" +
                    "\nEx:\t\t\t\t\t\t\t192.168.0.11:2013");
            return null;
        }
        String address = split[0];
        int port;
        try {
            port = Integer.parseInt(split[1]);
        } catch(NumberFormatException ex) {
            port=2013;
        }
        return new ClientInfo(address, port,
                Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID),
                "tata", isRemote,
                "jamuz", getAppDataPath().getAbsolutePath());
    }

    private void toggleOff(ToggleButton button, View layout) {
        button.setChecked(false);
        toggle(layout, true);
    }

    private void setConfig(String id, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(id, value);
        editor.apply();
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
            ToggleButton button1 = (ToggleButton)view;
            setTagButtonTextColor(button1);
            String buttonText = button1.getText().toString();
            if(!isRemoteConnected()) {
                displayedTrack.toggleTag(buttonText);
                refreshQueueAndPlaylistSpinner(true);
            } else {
                //displayedTrack.toggleTag(buttonText); //TODO: Manage this too
                //clientRemote.send("setTag".concat(String.valueOf(Math.round(rating)))); //TODO
            }
        });
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        layoutTags.addView(button, lp);
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
            TriStateButton button1 = (TriStateButton)view;
            TriStateButton.STATE state = button1.getState();
            setTagButtonTextColor(button1, state);
            if(localSelectedPlaylist!=null) {
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
            TriStateButton button1 = (TriStateButton)view;
            TriStateButton.STATE state = button1.getState();
            setTagButtonTextColor(button1, state);
            String buttonText = button1.getText().toString();
            if(localSelectedPlaylist!=null) {
                localSelectedPlaylist.toggleGenre(buttonText, state);
                refreshQueueAndPlaylistSpinner();

            }
        });
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        layoutGenrePlaylist.addView(button, lp);
    }

    private void refreshQueueAndPlaylistSpinner() {
        refreshQueueAndPlaylistSpinner(false);
        textViewPlaylist.setText(localSelectedPlaylist.getSummary());
    }

    private void refreshQueueAndPlaylistSpinner(final boolean refreshAll) {
        queue.clear();
        refreshLocalPlaylistSpinner(refreshAll);
        fillQueue(10, new ArrayList<>());
        button_save.setBackgroundResource(localSelectedPlaylist.isModified()?
                R.drawable.ic_button_save_red:R.drawable.ic_button_save);
    }

    //This is a trick since the following (not in listener) is not working:
    //button.setTextColor(ContextCompat.getColor(this, R.color.toggle_text));
    private void setTagButtonTextColor(ToggleButton b) {
        if(b!=null) {
            boolean checked = b.isChecked();
            b.setTextColor(ContextCompat.getColor(this, checked?R.color.textColor:R.color.colorPrimaryDark));
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

    private void applyPlaylist(Playlist playlist, boolean playNext) {
        dimOn();
        if (isRemoteConnected()) {
            clientRemote.send("setPlaylist".concat(playlist.toString()));
        } else {
            displayPlaylist(playlist);
            localSelectedPlaylist = playlist;
            if(playNext) {
                queue = playlist.getTracks(10);
                playNext();
            } else {
                refreshQueueAndPlaylistSpinner(false);
            }
        }
    }

    Spinner.OnItemSelectedListener spinnerPlaylistListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
        int pos, long id) {
            if (spinnerPlaylistSend) {
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
            if(spinnerGenreSend) {
                dimOn();
                String genre = (String) parent.getItemAtPosition(pos);
                if(!isRemoteConnected()) {
                    displayedTrack.updateGenre(genre);
                    refreshQueueAndPlaylistSpinner(true);
                }
            }
            spinnerGenreSend=true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            dimOn();
        }
    };

    Spinner.OnItemSelectedListener spinnerLimitUnitListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if(spinnerLimitUnitSend) {
                dimOn();
                String value = (String) parent.getItemAtPosition(pos);
                if(!isRemoteConnected() && localSelectedPlaylist!=null) {
                    localSelectedPlaylist.setLimitUnit(value);
                    refreshQueueAndPlaylistSpinner();
                }
            }
            spinnerLimitUnitSend =true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            dimOn();
        }
    };

    Spinner.OnItemSelectedListener spinnerLimitValueListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if(spinnerLimitValueSend) {
                dimOn();
                Integer value = (Integer) parent.getItemAtPosition(pos);
                if(!isRemoteConnected() && localSelectedPlaylist!=null) {
                    localSelectedPlaylist.setLimitValue(value);
                    refreshQueueAndPlaylistSpinner();
                }
            }
            spinnerLimitValueSend =true;
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
        return (clientRemote!=null && clientRemote.isConnected());
    }

    private void setDimMode(boolean enable) {
        if(enable) {
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
        if(collapse) {
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
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    //https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "MainActivity onPause");
        wasRemoteConnected=isRemoteConnected();
        stopRemote();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        try
        {
            if(hasFocus) {
                setDimMode(toggleButtonDimMode.isChecked());
            }
            else {
                setDimMode(true);
            }
        }
        catch(Exception ex)
        {
            Log.e(TAG, "onWindowFocusChanged", ex);
        }
    }

    private boolean wasRemoteConnected=false;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "MainActivity onResume");

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                        new IntentFilter("ServiceBase"));

        getFromQRcode(getIntent().getDataString());

        if(toggleButtonDimMode.isChecked()) {
            dimOn();
        }

        if(wasRemoteConnected && !audioPlayer.isPlaying()) {
            buttonRemote.setEnabled(false);
            buttonRemote.performClick();
        }

        //Only re-enable the following if loosing media button receiver again
        /*audioManager.unregisterMediaButtonEventReceiver(receiverMediaButtonName);
        registerButtonReceiver();*/
    }

    private void registerButtonReceiver() {
        receiverMediaButtonName = new ComponentName(getPackageName(),
                ReceiverMediaButton.class.getName());
        audioManager.registerMediaButtonEventReceiver(receiverMediaButtonName);
    }

    //https://developer.android.com/training/wearables/apps/voice.html
    public void displaySpeechRecognizer() {
        textToSpeech.speak(getString(R.string.TTSlistening), TextToSpeech.QUEUE_FLUSH, null,
                this.hashCode() + "listening");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            //Search
            String spokenText = results.get(0);
            Search.SearchKeyWord searchKeyWord = Search.get(spokenText);
            String searchValue = searchKeyWord.getKeyword();
            String msg= getString(R.string.unknownCommand) + " \"" + searchValue + "\".";
            switch (searchKeyWord.getType()) {
                case PLAYLIST:
                    msg = getString(R.string.playlist)+" \"" + searchValue + "\" "+getString(R.string.notfound);
                    for(Playlist playlist : localPlaylists.values()) {
                        if(playlist.getName().equalsIgnoreCase(searchValue)) {
                            applyPlaylist(playlist, true);
                            setupLocalPlaylistSpinner();
                            msg = "";
                            break;
                        }
                    }
                    break;
                case ARTIST_ONGOING:
                    searchValue = displayedTrack.getArtist();
                case ARTIST:
                    msg = getString(R.string.artist)+" \"" + searchValue + "\" "+getString(R.string.notfound);
                    if(searchValue.equals("")) {
                        //TODO: Actually it can happen, but needs to change playlist query (like "%blaBla%" curently)
                        msg = getString(R.string.specifyArtist);
                    }
                    else if(HelperLibrary.musicLibrary.getArtist(searchValue)) {
                        Playlist playlist =new Playlist(searchValue, true);
                        playlist.setArtist(searchValue);
                        setupLocalPlaylistSpinner(playlist, true);
                        msg = "";
                    }
                    break;
                case ALBUM_ONGOING:
                    searchValue = displayedTrack.getAlbum();
                case ALBUM:
                    msg = getString(R.string.album)+" \"" + searchValue + "\" "+getString(R.string.notfound);
                    if(searchValue.equals("")) {
                        //TODO: Actually it can happen, but needs to change playlist query (like "%blaBla%" curently)
                        msg = getString(R.string.specifyAlbum);
                    }
                    else if(HelperLibrary.musicLibrary.getAlbum(searchValue)) {
                        Playlist playlist =new Playlist(searchValue, true);
                        playlist.setAlbum(searchValue);
                        setupLocalPlaylistSpinner(playlist, true);
                        msg = "";
                    }
                    break;
            }

            if(!msg.equals("")) {
                helperToast.toastLong(msg);
                textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null,
                        this.hashCode() + "commandResult");
            }

        } else if (requestCode == QUEUE_REQUEST_CODE && resultCode == RESULT_OK) {
            boolean enqueue = data.getBooleanExtra("queueItem", false);

            int position = data.getIntExtra("positionPlay", -1);
            int histSize = data.getIntExtra("histSize", -1);

            if (position >= 0) {
                int offset = queueHistory.size()-histSize;
                position = position + offset;

                if(position<queueHistory.size()) {
                    queueHistoryIndex=position;
                    playHistory();
                } else {
                    position = position-queueHistory.size();
                    if(position<queue.size()) {
                        Track track = queue.get(position);
                        for(int i=0;i<=position;i++) {
                            queue.remove(0);
                        }
                        Log.i(TAG, "playQueue(1/"+queue.size()+")");
                        File file = new File(displayedTrack.getPath());
                        if(file.exists()) {
                            track.setHistory(true);
                            queueHistory.add(track);
                            queueHistoryIndex = queueHistory.size() - 1;
                            if(!enqueue) {
                                displayedTrack = track;
                                playAudio("");
                            } else {
                                queueHistoryIndex--;
                            }
                        }
                    }
                }
            }
        } else if (requestCode == QR_REQUEST_CODE && resultCode == RESULT_OK) {
            //https://www.simplifiedcoding.net/android-qr-code-scanner-tutorial/
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result == null || result.getContents() == null) {
                Toast.makeText(this, "Problem reading QR code", Toast.LENGTH_LONG).show();
            } else {
                getFromQRcode(result.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    TextToSpeech textToSpeech;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity onDestroy");
        stopRemote();

        //Better unregister as it does not trigger anyway + raises exceptions if not
        unregisterReceiver(receiverHeadSetPlugged);
        try {
            unregisterReceiver(mHeadsetBroadcastReceiver);
        } catch(IllegalArgumentException ex) {
            //TODO: Why does this occurs in Galaxy tablet
            //TODO: Test mHeadsetBroadcastReceiver in Galaxy tablet
        }

        //Note: receiverMediaButtonName remains active if not unregistered
        //but causes issues
        audioManager.unregisterMediaButtonEventReceiver(receiverMediaButtonName);

        audioPlayer.stop(true);

        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        HelperLibrary.close();
    }

    private static File[] externalFilesDir;
    public static File getAppDataPath() {
        File path =  externalFilesDir[1];
        if(!path.exists()) {
            path.mkdirs();
        }
        return path;
    }

    private void connectDatabase() {
        HelperLibrary.open(getAppDataPath(), this);

        new Thread() {
            public void run() {
                setupTags();
                setupGenres();
                setupLocalPlaylists();
            }
        }.start();

        //Start Scan Service
        if(!isMyServiceRunning(ServiceScan.class)) {
            Intent service = new Intent(getApplicationContext(), ServiceScan.class);
            service.putExtra("userPath", preferences.getString("userPath", "/"));
            service.putExtra("getAppDataPath", getAppDataPath());
            startService(service);
        }
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

    public void playAudio(String source){
        dimOn();
        localTrack = displayedTrack;
        refreshLocalPlaylistSpinner(false);
        audioPlayer.stop(false);
        displayedTrack.setSource(source.equals("")?localSelectedPlaylist.toString():source);
        String msg = audioPlayer.play(displayedTrack);
        if(!msg.equals("")) {
            helperToast.toastLong(msg);
        }
    }

    private void playHistory() {
        displayedTrack = queueHistory.get(queueHistoryIndex);
        Log.i(TAG, "playHistory("+(queueHistoryIndex+1)+"/"+queueHistory.size()+")");
        playAudio(getString(R.string.history)+" "+(queueHistoryIndex+1)+"/"+queueHistory.size()+"");
    }

    private int queueHistoryIndex =-1;

    private void playPrevious() {
        if(queueHistory.size()>0 && queueHistoryIndex>0 && queueHistoryIndex<queueHistory.size()) {
            queueHistoryIndex--;
            playHistory();
        } else {
            helperToast.toastLong("No tracks beyond.");
        }
    }

    private void fillQueue(int number, List<Integer> IDs) {
        if(queue.size()<number && localSelectedPlaylist!=null) {
            List<Track> addToQueue = localSelectedPlaylist.getTracks(number-queue.size(), IDs);
            queue.addAll(addToQueue);
        }
    }

    private void playNext() {
        if(queueHistoryIndex>=0 && (queueHistoryIndex+1)<queueHistory.size()) {
            queueHistoryIndex++;
            playHistory();
        }
        else {
            //Update lastPlayed and playCounter
            displayedTrack.setPlayCounter(displayedTrack.getPlayCounter()+1);
            displayedTrack.setLastPlayed(new Date());
            displayedTrack.update();
            //TODO: On tablet : java.lang.NoSuchMethodError: No interface method stream()Ljava/util/stream/Stream;
            // in class Ljava/util/List; or its super classes
            // (declaration of 'java.util.List' appears in /system/framework/core-libart.jar)
            /*List<Integer> queueIds = queue.stream().map(Track::getIdFileRemote).collect(Collectors.toList());*/

            List<Integer> queueIds = new ArrayList<>();
            for(Track track : queue) {
                queueIds.add(track.getIdFileRemote());
            }

            fillQueue(11, queueIds); // So it remains 10 after
            //Play first track in queue
            if(queue.size()>0) {
                displayedTrack = queue.get(0);
                queue.remove(0);
                Log.i(TAG, "playQueue(1/"+queue.size()+")");
                File file = new File(displayedTrack.getPath());
                if(file.exists()) {
                    displayedTrack.setHistory(true);
                    queueHistory.add(displayedTrack);
                    queueHistoryIndex = queueHistory.size()-1;
                    playAudio("");
                } else {
                    Log.d(TAG, "play(): Remove track from db:"+displayedTrack);
                    displayedTrack.delete();
                    playNext();
                }
            } else {
                refreshLocalPlaylistSpinner(false);
                helperToast.toastLong("Empty Playlist.");
            }
        }
    }

    class CallBackPlayer implements ICallBackPlayer {

        @Override
        public void onPlayBackEnd() {
            if(!isRemoteConnected()) {
                playNext();
            }
        }

        @Override
        public void onPositionChanged(int position, int duration) {
            if(!isRemoteConnected()) {
                setSeekBar(position, duration);
                if ((duration - position) < 5001 && (duration - position) > 4501) {
                    //setBrightness(1);
                    dimOn();
                }
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

        @Override
        public void speech() {
            displaySpeechRecognizer();
        }

        @Override
        public void onPlayBackStart() {
            displayTrack(true);
        }

    }

    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String msg = (String) message.obj;
            Log.i(TAG, "handleMessage("+msg+")");
            switch (msg) {
                case "play":
                    audioPlayer.play();
                    break;
                case "pause":
                    audioPlayer.pause();
                    break;
                case "togglePlay":
                    audioPlayer.togglePlay();
                    break;
                case "playNext":
                    audioPlayer.playNext();
                    break;
                case "playPrevious":
                    audioPlayer.playPrevious();
                    break;
            }
        }
    };

    //TODO: Use BroadcastReceiver (same or new ones)
    //to handle messages from other threads or services
    //Especially for audioPlayer (some weird message back&forwarding occuring)

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("message");
            Log.i(TAG, "Broadcast.onReceive("+msg+")");
            switch (msg) {
                case "enableSync":
                    enableSync(true);
                    break;
                case "refreshSpinner(true)":
                    refreshLocalPlaylistSpinner(true);
                    break;
                case "connectedSync":
                    //setConfig("connectionString", editTextConnectInfo.getText().toString());
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
        new CountDownTimer(500,50) {
            private float brightness=on?0:1;
            @Override
            public void onTick(long millisUntilFinished_) {
                if(on) {
                    setBrightness(brightness+=0.1);
                } else {
                    setBrightness(brightness-=0.1);
                }
            }
            @Override
            public void onFinish() {
                setBrightness(on?1:0);
                isDimOn = true;
            }
        }.start();
    }

    private static Timer timer = new Timer();
    private boolean isDimOn = true;

    private void dimOn() {
        editTextConnectInfo.clearFocus();

        if(toggleButtonDimMode.isChecked()) {
            if (!isDimOn) {
                dim(true);
            }
            timer.cancel();
            timer.purge();
            Log.v(TAG, "timerTask cancelled");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.v(TAG, "timerTask performed");
                    setBrightness(0);
                    //dim(false);
                    isDimOn = false;
                }
            }, 5 * 1000);
            Log.v(TAG, "timerTask scheduled");
        }
    }

    private void setBrightness(final float brightness) {
        Log.v(TAG, "setBrightness("+brightness+");");
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
                buttonRemote.setText("Close");
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
                buttonSync.setText("Connect");
                buttonSync.setBackgroundResource(R.drawable.connect_off_new);
            } else {
                buttonSync.setText("Close");
                buttonSync.setBackgroundResource(R.drawable.connect_on);
            }
            buttonSync.setEnabled(true);
        });
    }

    @SuppressLint("SetTextI18n")
    private void enableClientRemote(final Button button, final int resId) {
        runOnUiThread(() -> {
            button.setEnabled(false);
            button.setText("Connect");
            button.setBackgroundResource(resId);
            button.setEnabled(true);
        });
    }

    private void enablePlaylistEdit(final boolean enable) {
        runOnUiThread(() -> {
            toggle(layoutPlaylistEditBar, !enable);
            layoutPlaylistToolBar.setVisibility(enable?View.VISIBLE:View.GONE);
        });
    }

    private void getFromQRcode(String content) {
        if(content!=null) {
            if(!content.equals("")) {
                content=content.substring("jamuzremote://".length());
                content=Encryption.decrypt(content, "NOTeBrrhzrtestSecretK");

                buttonRemote.setEnabled(false);
                buttonSync.setEnabled(false);
                editTextConnectInfo.setText(content);
                setConfig("connectionString", editTextConnectInfo.getText().toString());
                buttonRemote.setEnabled(true);
                buttonSync.setEnabled(true);
            }
        }
    }

    private static final int REQUEST = 112;

    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS
    };

    public void checkPermissionsThenScanLibrary() {
        if (!hasPermissions(this, PERMISSIONS)) {

            String msgStr = "<html><b>"+getString(R.string.permissionMsg_1)+"</b>"+getString(R.string.permissionMsg_2)
                    +"<BR/><BR/>" +
                    "<i>- <u>"+getString(R.string.permissionMsg_3)+"</u></i> "+getString(R.string.permissionMsg_4)
                    +"<BR/> " +
                    getString(R.string.permissionMsg_5) + " (\"" + getAppDataPath() +"\")."
                    +"<BR/>" +
                    getString(R.string.permissionMsg_6)
                    +"<BR/>" +
                    getString(R.string.permissionMsg_7) + " (\""+musicLibraryDbFile.getAbsolutePath()+"\")."
                    +"<BR/><BR/>" +
                    "<i>- <u>"+getString(R.string.permissionMsg_8)+"</u></i> "+getString(R.string.permissionMsg_9)
                    +"</html>";

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.warning));
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
            layoutTagsPlaylist.removeAllViews();
            makeButtonTagPlaylist(Integer.MAX_VALUE, "null");
            if(RepoTags.get()!=null) {
                for(Map.Entry<Integer, String> tag : RepoTags.get().entrySet()) {
                    makeButtonTag(tag.getKey(), tag.getValue());
                    makeButtonTagPlaylist(tag.getKey(), tag.getValue());
                }
                //Re-display track and playlist
                displayTrack(false);
                displayPlaylist(localSelectedPlaylist);
            }
        });
    }

    private void setupGenres() {
        runOnUiThread(() -> {
            layoutGenrePlaylist.removeAllViews();
            for(String genre : RepoGenres.get()) {
                makeButtonGenrePlaylist(-1, genre);
            }
            //Re-display track and playlist
            displayTrack(false); //spinner genre is re-set in there
            displayPlaylist(localSelectedPlaylist);
        });
    }

    private void setupSpinnerGenre(final List<String> genres, final String genre) {
        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genres);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        runOnUiThread(() -> {
            spinnerGenreSend=false;
            spinnerGenre.setAdapter(arrayAdapter);
            if(!genre.equals("")) {
                spinnerGenre.setSelection(arrayAdapter.getPosition(genre));
            }
        });
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST );
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
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connectDatabase();
                }
            }
        }
    }

    private void setupButton(final int buttonName, final String msg) {
        Button button = (Button) findViewById(buttonName);
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
                    audioPlayer.togglePlay();
                    break;
                case "pullup":
                    audioPlayer.pullUp();
                    break;
                case "rewind":
                    audioPlayer.rewind();
                    break;
                case "forward":
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
                    helperToast.toastLong("Not implemented");
                    break;
            }
        }
    }

    private void setupLocalPlaylists() {
        localPlaylists = new HashMap<>();
        File playlistFolder = HelperFile.createFolder("Playlists");
        if (playlistFolder != null) {
            for(String file : playlistFolder.list()) {
                if(file.endsWith(".plli")) {
                    Playlist playlist = readPlaylist(file);
                    if(playlist != null) {
                        playlist.getNbFiles();
                        localPlaylists.put(playlist.getName(), playlist);
                    }
                }
            }
        }
        setupLocalPlaylistSpinner(null);
    }

    private void setupLocalPlaylistSpinner(Playlist playlist) {
        if (localPlaylists.size() > 0) {
            localPlaylists = sortHashMapByValues(localPlaylists);
        } else {
            Playlist playlistAll = new Playlist("All", true);
            localPlaylists.put(playlistAll.getName(), playlistAll);
            playlist=playlistAll;
        }

        if(playlist!=null && localPlaylists.containsKey(playlist.getName())) {
            localSelectedPlaylist=playlist;
        } else {
            localSelectedPlaylist = localPlaylists.values().iterator().next();
        }

        playListArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                new ArrayList<>(localPlaylists.values()));
        setupLocalPlaylistSpinner();
        runOnUiThread(() -> displayPlaylist(localSelectedPlaylist));
    }

    private void setupLocalPlaylistSpinner(Playlist playlist, boolean playNext) {
        applyPlaylist(playlist, playNext);
        localPlaylists.put(playlist.getName(), playlist);
        playListArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                new ArrayList<>(localPlaylists.values()));
        setupLocalPlaylistSpinner();
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
            if(selectedPlaylist!=null) {
                spinnerPlaylist.setSelection(arrayAdapter.getPosition(selectedPlaylist));
            }
        });
    }

    private void refreshLocalPlaylistSpinner(final boolean refreshAll) {
        if(localSelectedPlaylist!=null) {
            new Thread() {
                public void run() {
                    if(refreshAll) {
                        for(Playlist playlist : localPlaylists.values()) {
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
        if(playlist!=null) {
            for(int i=0; i<layoutTagsPlaylist.getFlexItemCount();i++) {
                TriStateButton button = (TriStateButton)layoutTagsPlaylist.getFlexItemAt(i);
                if(button!=null) {
                    button.setState(TriStateButton.STATE.ANY);
                    setTagButtonTextColor(button, TriStateButton.STATE.ANY);
                }
            }
            for(int i=0; i<layoutGenrePlaylist.getFlexItemCount();i++) {
                TriStateButton button = (TriStateButton)layoutGenrePlaylist.getFlexItemAt(i);
                if(button!=null) {
                    button.setState(TriStateButton.STATE.ANY);
                    setTagButtonTextColor(button, TriStateButton.STATE.ANY);
                }
            }
            TriStateButton nullButton = layoutTagsPlaylist.findViewWithTag("null");
            if(nullButton!=null) {
                nullButton.setState(playlist.getUnTaggedState());
                setTagButtonTextColor(nullButton, playlist.getUnTaggedState());
            }
            for(Map.Entry<String, TriStateButton.STATE> entry : playlist.getTags()) {
                TriStateButton button = layoutTagsPlaylist.findViewWithTag(entry.getKey());
                if(button!=null) {
                    button.setState(entry.getValue());
                    setTagButtonTextColor(button, entry.getValue());
                }
            }
            for(Map.Entry<String, TriStateButton.STATE> entry : playlist.getGenres()) {
                TriStateButton button = layoutGenrePlaylist.findViewWithTag(entry.getKey());
                if(button!=null) {
                    button.setState(entry.getValue());
                    setTagButtonTextColor(button, entry.getValue());
                }
            }
            buttonRatingOperator.setText(playlist.getRatingOperator());
            ratingBarPlaylist.setRating(playlist.getRating());
            switch(playlist.getOrder()) {
                case RANDOM:
                    playListOrderRadio.check(R.id.order_random);
                    break;
                case PLAYCOUNTER_LASTPLAYED:
                    playListOrderRadio.check(R.id.order_playCounter_lastPlayed);
                    break;
            }

            spinnerLimitUnitSend = false;
            spinnerPlaylistLimitUnit.setAdapter(playListLimitUnitArrayAdapter);
            spinnerPlaylistLimitUnit.setSelection(playListLimitUnitArrayAdapter.getPosition(playlist.getLimitUnit()));

            spinnerLimitValueSend = false;
            spinnerPlaylistLimitValue.setAdapter(playListLimitValueArrayAdapter);
            spinnerPlaylistLimitValue.setSelection(playlist.getLimitValue());
            textViewPlaylist.setText(playlist.getSummary());
            button_save.setBackgroundResource(playlist.isModified()?
                    R.drawable.ic_button_save_red:R.drawable.ic_button_save);
        }
    }

    public Playlist readPlaylist(String filename) {
        String readJson = HelperFile.read("Playlists", filename);
        if(!readJson.equals("")) {
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

        LinkedHashMap<String, Playlist> sortedMap =
                new LinkedHashMap<>();

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
        if(checked && localSelectedPlaylist!=null) {
            switch(view.getId()) {
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
        });
    }

    private void displayTrack(boolean forceReadTags) {
        if(displayedTrack!=null) {
            if(forceReadTags) {
                displayedTrack.getTags(true);
            }
            runOnUiThread(() -> {
                setTextView(textViewFileInfo, trimTrailingWhitespace(Html.fromHtml(
                        "<html>"+
                        (displayedTrack.getSource().equals("")?""
                                :"<u>".concat(displayedTrack.getSource()).concat("</u>:"))
                        +""
                        .concat("<h1>")
                        .concat(displayedTrack.toString())
                        .concat("</h1></html>"))));
                ratingBar.setEnabled(false);
                ratingBar.setRating(displayedTrack.getRating());
                ratingBar.setEnabled(true);
                setupSpinnerGenre(RepoGenres.get(), displayedTrack.getGenre());

                //Display file tags
                ArrayList<String> fileTags = displayedTrack.getTags(false);
                for(Map.Entry<Integer, String> tag : RepoTags.get().entrySet()) {
                    ToggleButton button = layoutTags.findViewById(tag.getKey());
                    if(button!=null && button.isChecked()!=fileTags.contains(tag.getValue())) {
                        button.setChecked(fileTags.contains(tag.getValue()));
                        setTagButtonTextColor(button);
                    }
                }
            });

            if(displayedTrack.getIdFileRemote()>=0) {
                displayImage(displayedTrack.getArt());
                bluetoothNotifyChange(AVRCP_META_CHANGED);

            } else {
                displayCover();
            }
        }
    }

    /**
     * Trims trailing whitespace. Removes any of these characters:
     * https://stackoverflow.com/questions/9589381/remove-extra-line-breaks-after-html-fromhtml
     * 0009, HORIZONTAL TABULATION
     * 000A, LINE FEED
     * 000B, VERTICAL TABULATION
     * 000C, FORM FEED
     * 000D, CARRIAGE RETURN
     * 001C, FILE SEPARATOR
     * 001D, GROUP SEPARATOR
     * 001E, RECORD SEPARATOR
     * 001F, UNIT SEPARATOR
     * @return "" if source is null, otherwise string with all trailing whitespace removed
     */
    public static Spanned trimTrailingWhitespace(Spanned source) {

        if(source == null)
            return new SpannableString("");

        int i = source.length();

        // loop back to the first non-whitespace character
        while(true) {
            if (!(--i >= 0 && Character.isWhitespace(source.charAt(i)))) break;
        }

        return new SpannableString(source.subSequence(0, i+1));
    }

    //private static final String AVRCP_PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
    private static final String AVRCP_META_CHANGED = "com.android.music.metachanged";

    private void bluetoothNotifyChange(String what) {
        Intent i = new Intent(what);
        i.putExtra("id", Long.valueOf(displayedTrack.getIdFileRemote()));
        i.putExtra("artist", displayedTrack.getArtist());
        i.putExtra("album",displayedTrack.getAlbum());
        i.putExtra("track", displayedTrack.getTitle());
        i.putExtra("playing", "true");
        i.putExtra("ListSize", "99");
        i.putExtra("duration", "20");
        i.putExtra("position", "0");
        sendBroadcast(i);
    }

    //Display cover from cache or ask for it
    private void displayCover() {
        Bitmap bitmap = null;
        if (coverMap.containsKey(displayedTrack.getCoverHash())) {
            bitmap = coverMap.get(displayedTrack.getCoverHash());
        } else { //Ask cover
            int maxWidth = this.getWindow().getDecorView().getWidth();
            if(maxWidth<=0) {
                maxWidth=250;
            }
            if(clientRemote!=null) {
                clientRemote.send("sendCover"+maxWidth);
            }
        }
        displayImage(bitmap);
    }

    private void displayImage(final Bitmap finalBitmap) {
        //final Bitmap finalBitmap = bitmap;
        runOnUiThread(() -> {
            imageViewCover.setImageBitmap(finalBitmap);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(finalBitmap);
            bitmapDrawable.setAlpha(50);
            layoutMain.setBackground(bitmapDrawable);
        });
    }

    private void displayImage(byte[] art) {
        if( art != null ){
            displayImage( BitmapFactory.decodeByteArray(art, 0, art.length));
        } else {
            displayImage(HelperBitmap.getEmptyCover());
        }
    }

    class CallBackRemote implements ICallBackRemote {

        private final String TAG = CallBackRemote.class.getName();

        @Override
        public void receivedJson(final String json) {
            try {
                JSONObject jObject = new JSONObject(json);
                String type = jObject.getString("type");
                switch(type) {
                    case "playlists":
                        String selectedPlaylist = jObject.getString("selectedPlaylist");
                        Playlist temp = new Playlist(selectedPlaylist, false);
                        final JSONArray jsonPlaylists = (JSONArray) jObject.get("playlists");
                        final List<Playlist> playlists = new ArrayList<>();
                        for(int i=0; i<jsonPlaylists.length(); i++) {
                            String playlist = (String) jsonPlaylists.get(i);
                            Playlist playList = new Playlist(playlist, false);
                            if(playlist.equals(selectedPlaylist)) {
                                playList=temp;
                            }
                            playlists.add(playList);
                        }
                        ArrayAdapter<Playlist> arrayAdapter =
                                new ArrayAdapter<>(MainActivity.this,
                                        R.layout.spinner_item, playlists);
                        setupPlaylistSpinner(arrayAdapter, temp);
                        enablePlaylistEdit(false);
                        break;
                    case "currentPosition":
                        final int currentPosition = jObject.getInt("currentPosition");
                        final int total = jObject.getInt("total");
                        if(isRemoteConnected()) {
                            setSeekBar(currentPosition, total);
                        }
                        break;
                    case "fileInfoInt":
                        displayedTrack = new Track(
                                jObject.getInt("rating"),
                                jObject.getString("title"),
                                jObject.getString("album"),
                                jObject.getString("artist"),
                                jObject.getString("coverHash"),
                                jObject.getString("genre"));
                        //FIXME: Display user tags from remote file
                        //TODO: Display Playlist name and nbFiles
                        displayTrack(false);
                        break;
                }
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void receivedBitmap(final Bitmap bitmap) {
            Log.d(TAG, "receivedBitmap: callback");
            Log.d(TAG, bitmap == null ? "null" : bitmap.getWidth() + "x" + bitmap.getHeight());

            if (!coverMap.containsKey(displayedTrack.getCoverHash())) {
                if (bitmap != null) { //Save to cache
                    coverMap.put(displayedTrack.getCoverHash(), bitmap);
                }
            }
            displayCover();
        }

        @Override
        public void disconnected(final String msg) {
            if(!msg.equals("")) {
                runOnUiThread(() -> helperToast.toastShort(msg));
            }
            stopRemote();
            setupLocalPlaylistSpinner();
            displayedTrack = localTrack;
            displayTrack(false);
        }
    }

    private void stopRemote() {
        if(clientRemote!=null) {
            clientRemote.close();
            clientRemote=null;
        }
        enableClientRemote(buttonRemote, R.drawable.remote_off);
        setupLocalPlaylistSpinner();
        displayedTrack = localTrack;
        displayTrack(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "MainActivity onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(TAG, "MainActivity onOptionsItemSelected");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "MainActivity onBackPressed");
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing JaMuz")
                .setMessage("Are you sure you want to exit and stop playback ?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent.getAction()!=null && intent.getAction().equals("android.intent.action.VIEW")) {
            getFromQRcode(intent.getDataString());
        }
    }

    //Receivers
    ComponentName receiverMediaButtonName;
    ReceiverHeadSetPlugged receiverHeadSetPlugged = new ReceiverHeadSetPlugged();

    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothHeadset mBluetoothHeadset;

    protected BluetoothProfile.ServiceListener mHeadsetProfileListener = new BluetoothProfile.ServiceListener()
    {
        @Override
        public void onServiceDisconnected(int profile)
        {
            try {
                unregisterReceiver(mHeadsetBroadcastReceiver);
            } catch(IllegalArgumentException ex) {
                //java.lang.IllegalArgumentException: Receiver not registered
                //TODO: We don't care but why does this happen ?
            }
            mBluetoothHeadset = null;
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy)
        {
            mBluetoothHeadset = (BluetoothHeadset) proxy;

            registerReceiver(mHeadsetBroadcastReceiver,
                    new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED));

            //This is triggered on phone calls, already received in ReceiverPhoneCall
            /*registerReceiver(mHeadsetBroadcastReceiver,
                    new IntentFilter(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED));*/
        }
    };

    protected BroadcastReceiver mHeadsetBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (Objects.requireNonNull(intent.getAction())
                    .equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED))
            {
                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED);
                if (state == BluetoothHeadset.STATE_CONNECTED)
                {
                    Log.i(TAG, "BT connected. Waiting 4s");
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    audioPlayer.play();
                }
                else if (state == BluetoothHeadset.STATE_DISCONNECTED)
                {
                    Log.i(TAG, "BT DISconnected");
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
                    Log.d(TAG, "BT AUDIO connected");

                }
                else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED)
                {
                    Log.d(TAG, "BT AUDIO DISconnected");

                }
            }*/
        }
    };

    @SuppressWarnings("unchecked")
    public static Playlist clonePlaylist(Playlist playlist) {
        //Save to Json
        Gson gson = new Gson();
        String json = gson.toJson(playlist);
        //Create a new from json
        Type mapType = new TypeToken<Playlist>(){}.getType();
        Playlist newPlaylist=null;
        try {
            newPlaylist = gson.fromJson(json, mapType);
        } catch (JsonSyntaxException ex) {
            Log.e(TAG, "", ex);
        }
        return newPlaylist;
    }
}
