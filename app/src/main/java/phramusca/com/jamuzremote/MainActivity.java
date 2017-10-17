package phramusca.com.jamuzremote;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//TODO: Support Themes
//http://www.hidroh.com/2015/02/25/support-multiple-themes-android-app-part-2/

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Client clientRemote;
    private Client clientSync;
    private Track displayedTrack;
    private Track localTrack;
    private Map coverMap = new HashMap();
    private Intent service; //Not yet used
    private AudioManager audioManager;
    public static AudioPlayer audioPlayer;
    public static MusicLibrary musicLibrary;

    //In internal SD emulated storage:
    //TODO: Change folder as we now have rights
    //In external SD. Does not seem to work !
    //private static final String DB_PATH =
    //      "/storage/3515-1C15/Android/data/"+BuildConfig.APPLICATION_ID;
    public static File musicLibraryDbFile = new File(
            Environment.getExternalStorageDirectory()+"/JaMuz/JaMuzRemote.db");

    private int nbFiles=0;
    private int nbFilesTotal = 0;
    private List<Track> queue = new ArrayList<>();
    private List<Track> queueHistory = new ArrayList<>();
    private List<PlayList> localPlaylists = new ArrayList<PlayList>();
    private PlayList localSelectedPlaylist;
    private Map<Integer, String> tags = new HashMap<>();
    private List<String> genres = new ArrayList<>();

    private ProcessAbstract scanLibray;
    private ProcessAbstract processBrowseFS;
    private ProcessAbstract processBrowseFScount;

    // GUI elements
    private TextView textViewFileInfo;
    private EditText editTextConnectInfo;
    private TextView textViewPath;
    private Button buttonConfigConnection;
    private Button buttonRemote;
    private Button buttonSync;
    private ToggleButton toggleButtonDimMode;
    private ToggleButton toggleButtonControls;
    private ToggleButton toggleButtonTags;
    private ToggleButton toggleButtonPlaylist;
    private ToggleButton toggleButtonOptions;
    private Button buttonPrevious;
    private Button buttonPlay;
    private Button buttonNext;
    private Button buttonRewind;
    private Button buttonPullup;
    private Button buttonForward;
    private Button buttonVolUp;
    private Button buttonVolDown;
    private Button button_speech;
    private SeekBar seekBarPosition;
    private Spinner spinnerPlaylist;
    private Spinner spinnerGenre;
    private static boolean spinnerPlaylistSend=false;
    private static boolean spinnerGenreSend=false;
    private RatingBar ratingBar;
    private ImageView imageViewCover;
    private LinearLayout layoutTrackInfo;
    private LinearLayout layoutMain;
    private LinearLayout layoutControls;
    private FlexboxLayout layoutTags;
    private FlexboxLayout layoutTagsPlaylist;
    private LinearLayout layoutAttributes;
    private LinearLayout layoutPlaylist;
    private GridLayout layoutOptions;
    private SeekBar seekBarReplayGain;

    //Notifications
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilderSync;
    private NotificationCompat.Builder mBuilderScan;
    private static final int ID_NOTIFIER_SYNC = 1;
    private static final int ID_NOTIFIER_SCAN = 2;
    private String m_chosenDir = "/";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "MainActivity onCreate");
        setContentView(R.layout.activity_main);

        textToSpeech =new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.FRENCH);
                }
            }
        });

        //Read FilesToKeep file to get list of files to maintain in db
        String readJson = HelperTextFile.read(this, "FilesToKeep.txt");
        if(!readJson.equals("")) {
            filesToKeep = new HashMap<>();
            Gson gson = new Gson();
            Type mapType = new TypeToken<HashMap<String, FileInfoReception>>(){}.getType();
            filesToKeep = gson.fromJson(readJson,mapType);
        }
        //Read filesToGet file to get list of files to retrieve
        readJson = HelperTextFile.read(this, "filesToGet.txt");
        if(!readJson.equals("")) {
            filesToGet = new HashMap<>();
            Gson gson = new Gson();
            Type mapType = new TypeToken<HashMap<Integer, FileInfoReception>>(){}.getType();
            filesToGet = gson.fromJson(readJson, mapType);
        }

        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilderSync = new NotificationCompat.Builder(this);
        mBuilderSync.setContentTitle("JaMuz Sync")
                .setContentText("Download in progress")
                .setUsesChronometer(true)
                .setSmallIcon(R.drawable.gears_normal);

        mBuilderScan = new NotificationCompat.Builder(this);
        mBuilderScan.setContentTitle("JaMuz Scan")
                .setContentText("Scan in progress")
                .setUsesChronometer(true)
                .setSmallIcon(R.drawable.gears_normal);

        layoutTags = (FlexboxLayout) findViewById(R.id.panel_tags);
        layoutTagsPlaylist = (FlexboxLayout) findViewById(R.id.panel_tags_playlist);
        layoutAttributes = (LinearLayout) findViewById(R.id.panel_attributes);
        layoutPlaylist = (LinearLayout) findViewById(R.id.panel_playlist);
        layoutControls = (LinearLayout) findViewById(R.id.panel_controls);
        layoutOptions = (GridLayout) findViewById(R.id.panel_options);

        textViewFileInfo = (TextView) findViewById(R.id.textFileInfo);

        editTextConnectInfo = (EditText) findViewById(R.id.editText_info);
        editTextConnectInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dimOn();
                return false;
            }
        });

        preferences = getPreferences(MODE_PRIVATE);
        editTextConnectInfo.setText(preferences.getString("connectionString", "192.168.0.10:2013"));

        textViewPath = (TextView) findViewById(R.id.textViewPath);

        buttonConfigConnection = (Button) findViewById(R.id.button_config_connection);
        buttonConfigConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setConfig("connectionString", editTextConnectInfo.getText().toString());
            }
        });

        m_chosenDir = preferences.getString("m_chosenDir", "/");
        String display = m_chosenDir.equals("/")?
                "Choose an additional local path for music. Note that it is recommended to use JaMuz though."
                :m_chosenDir;
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
                                new DirectoryChooserDialog.ChosenDirectoryListener()
                                {
                                    @Override
                                    public void onChosenDir(String chosenDir)
                                    {
                                        m_chosenDir = chosenDir;
                                        textViewPath.setText(trimTrailingWhitespace(Html.fromHtml("<html>"
                                                .concat(chosenDir)
                                                .concat("</html>"))));
                                        setConfig("m_chosenDir", m_chosenDir);
                                        checkPermissionsThenScanLibrary();

                                    }
                                });
                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                directoryChooserDialog.chooseDirectory(m_chosenDir);
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                if(fromUser) { //as it is also set when server sends file info (and it can be 0)
                    dimOn();
                    ratingBar.setEnabled(false);
                    displayedTrack.setRating(Math.round(rating));
                    if(!isRemoteConnected()) {
                        musicLibrary.updateTrack(displayedTrack);
                        //Queue may not be valid as value changed
                        queue.clear();
                    } else {
                        clientRemote.send("setRating".concat(String.valueOf(Math.round(rating))));
                    }
                    ratingBar.setEnabled(true);
                }
            }
        });

        seekBarPosition = (SeekBar) findViewById(R.id.seekBar);
        seekBarPosition.setEnabled(false);

        seekBarReplayGain = (SeekBar) findViewById(R.id.seekBarReplayGain);
        seekBarReplayGain.setProgress(70);
        seekBarReplayGain.setMax(100); //default, but still
        seekBarReplayGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = ((float)progress / 100.0f);
                Log.i(TAG, "seekBarReplayGain: "+value);
                String msg = audioPlayer.setVolume(value, displayedTrack);
                if(!msg.equals("")) {
                    toastLong(msg);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        spinnerPlaylist = (Spinner) findViewById(R.id.spinner_playlist);
        spinnerPlaylist.setOnItemSelectedListener(spinnerListener);
        spinnerPlaylist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dimOn();
                return false;
            }
        });

        spinnerGenre = (Spinner) findViewById(R.id.spinner_genre);
        spinnerGenre.setOnItemSelectedListener(spinnerGenreListener);
        spinnerGenre.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dimOn();
                return false;
            }
        });

        buttonPrevious = setupButton(buttonPrevious, R.id.button_previous, "previousTrack");
        buttonPlay = setupButton(buttonPlay, R.id.button_play, "playTrack");
        buttonNext = setupButton(buttonNext, R.id.button_next, "nextTrack");
        buttonRewind = setupButton(buttonRewind, R.id.button_rewind, "rewind");
        buttonPullup = setupButton(buttonPullup, R.id.button_pullup, "pullup");
        buttonForward = setupButton(buttonForward, R.id.button_forward, "forward");
        buttonVolUp = setupButton(buttonVolUp, R.id.button_volUp, "volUp");
        buttonVolDown = setupButton(buttonVolDown, R.id.button_volDown, "volDown");

        toggleButtonDimMode = (ToggleButton) findViewById(R.id.button_dim_mode);
        toggleButtonDimMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDimMode(toggleButtonDimMode.isChecked());
            }
        });

        toggleButtonControls = (ToggleButton) findViewById(R.id.button_controls_toggle);
        toggleButtonControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimOn();
                toggle(layoutControls, !toggleButtonControls.isChecked());
            }
        });

        toggleButtonTags = (ToggleButton) findViewById(R.id.button_tags_toggle);
        toggleButtonTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimOn();
                toggle(layoutAttributes, !toggleButtonTags.isChecked());
            }
        });

        toggleButtonPlaylist = (ToggleButton) findViewById(R.id.button_playlist_toggle);
        toggleButtonPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimOn();
                toggle(layoutPlaylist, !toggleButtonPlaylist.isChecked());
            }
        });

        toggleButtonOptions = (ToggleButton) findViewById(R.id.button_connect_toggle);
        toggleButtonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimOn();
                toggle(layoutOptions, !toggleButtonOptions.isChecked());
            }
        });

        buttonRemote = (Button) findViewById(R.id.button_connect);
        buttonRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimOn();
                enableGUI(buttonRemote, false);
                buttonRemote.setBackgroundResource(R.drawable.remote_ongoing);
                if(buttonRemote.getText().equals("Connect")) {
                    String infoConnect = editTextConnectInfo.getText().toString();
                    String[] split = infoConnect.split(":");  //NOI18N
                    if(split.length<2) {
                        enableConnect(true);
                        toastLong("Bad format:\t"+infoConnect+"" +
                                "\nExpected:\t\t<IP>:<Port>" +
                                "\nEx:\t\t\t\t\t\t\t192.168.0.12:2013");
                        return;
                    }
                    String address = split[0];
                    int port;
                    try {
                        port = Integer.parseInt(split[1]);
                    } catch(NumberFormatException ex) {
                        port=2013;
                    }
                    CallBackRemote callBackRemote = new CallBackRemote();
                    clientRemote = new Client(address, port,
                            Settings.Secure.getString(MainActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID), "tata", callBackRemote);
                   new Thread() {
                        public void run() {
                            if(clientRemote.connect()) {
                                setConfig("connectionString", editTextConnectInfo.getText().toString());
                                enableGUI(buttonRemote, true);
                                enableConnect(false);
                            }
                            else {
                                enableConnect(true);
                            }
                        }
                    }.start();
                }
                else {
                    enableConnect(true);
                    stopRemote();
                }
            }
        });

        button_speech = (Button) findViewById(R.id.button_speech);
        button_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });

        buttonSync = (Button) findViewById(R.id.button_sync);
        buttonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimOn();
                enableGUI(buttonSync, false);
                buttonSync.setBackgroundResource(R.drawable.connect_ongoing);
                if(buttonSync.getText().equals("Connect")) {
                    String infoConnect = editTextConnectInfo.getText().toString();
                    String[] split = infoConnect.split(":");  //NOI18N
                    if(split.length<2) {
                        enableSync(true);
                        return;
                    }
                    String address = split[0];
                    int port;
                    try {
                        port = Integer.parseInt(split[1]);
                    } catch(NumberFormatException ex) {
                        port=2013;
                    }
                    CallBackSync callBackSync = new CallBackSync();
                    clientSync = new Client(address, port,
                            Settings.Secure.getString(MainActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID)+"-data", "tata", callBackSync);
                    new Thread() {
                        public void run() {
                            if(clientSync.connect()) {
                                setConfig("connectionString", editTextConnectInfo.getText().toString());
                                enableGUI(buttonSync, true);
                                enableSync(false);
                                requestNextFile(false);
                            }
                            else {
                                enableSync(true);
                            }
                        }
                    }.start();
                }
                else {
                    enableSync(true);
                    stopSync();
                }
            }
        });

        imageViewCover = (ImageView) findViewById(R.id.imageView);
        layoutMain = (LinearLayout) findViewById(R.id.panel_main);

        layoutTrackInfo = (LinearLayout) findViewById(R.id.trackInfo);
        layoutTrackInfo.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                Log.v(TAG, "onSwipeTop");
                if(!isRemoteConnected()) {
                    audioPlayer.forward();
                } else {
                    clientRemote.send("forward");
                }

            }
            @Override
            public void onSwipeRight() {
                Log.v(TAG, "onSwipeRight");
                if(!isRemoteConnected()) {
                    playPrevious();
                } else {
                    clientRemote.send("previousTrack");
                }
            }
            @Override
            public void onSwipeLeft() {
                Log.v(TAG, "onSwipeLeft");
                if(!isRemoteConnected()) {
                    playNext();
                } else {
                    clientRemote.send("nextTrack");
                }
            }
            @Override
            public void onSwipeBottom() {
                Log.v(TAG, "onSwipeBottom");
                if(!isRemoteConnected()) {
                    audioPlayer.rewind();
                } else {
                    clientRemote.send("rewind");
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
        });

        localTrack = new Track(-1, 0, "Welcome to", "2017", "JaMuz Remote", "coverHash",
                "relativeFullPath", "---", new Date(0), new Date(0), 0);
        displayedTrack = localTrack;
        setTextView(textViewFileInfo, Html.fromHtml("<html><h1>"
                .concat(displayedTrack.toString())
                .concat("<BR/></h1></html>")), false);

        enableGUI(buttonSync, false);
        enableGUI(buttonRemote, false);
        getFromQRcode(getIntent().getDataString());
        editTextConnectInfo.setEnabled(true);
        buttonRemote.setEnabled(true);
        buttonSync.setEnabled(true);

        //TODO: MAke this an option somehow

        //The following call creates default application folder
        // - in "external" card, the emulated one : /storage/emulated/0/Android//com.phramusca.jamuz/files
        // - and in real removable sd card : /storage/xxxx-xxxx/Android/com.phramusca.jamuz/files
        externalFilesDir = getExternalFilesDirs(null);
        checkPermissionsThenScanLibrary();

        CallBackPlayer callBackPlayer = new CallBackPlayer();
        audioPlayer = new AudioPlayer(callBackPlayer);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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

        receiverMediaButtonName = new ComponentName(getPackageName(),
                ReceiverMediaButton.class.getName());
        audioManager.registerMediaButtonEventReceiver(receiverMediaButtonName);

        //TODO: Why this one needs registerReceiver whereas ReceiverPhoneCall does not
        registerReceiver(receiverHeadSetPlugged,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        //Start background service
        //Not yet used but can be used to scan library
        //What is the benefit ??
        //service = new Intent(this, MyService.class);
        //startService(service);

        toggle(layoutOptions, true);
        toggle(layoutControls, true);
        toggle(layoutAttributes, true);
        toggle(layoutPlaylist, true);
        setDimMode(toggleButtonDimMode.isChecked());
    }

    private void setConfig(String id, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(id, value);
        editor.commit();
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dimOn();
                ToggleButton button = (ToggleButton)view;
                String buttonText = button.getText().toString();
                displayedTrack.toggleTag(buttonText);
                setTagButtonTextColor(button);
            }
        });
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        layoutTags.addView(button, lp);
    }

    private void makeButtonTagPlaylist(int key, String value) {
        ToggleButton button = getButtonTag(key, value);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dimOn();
                ToggleButton button = (ToggleButton)view;
                String buttonText = button.getText().toString();
                //FIXME: Do sthg with buttonText !!
                /*displayedTrack.toggleTag(buttonText);*/
                setTagButtonTextColor(button);
            }
        });
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        layoutTagsPlaylist.addView(button, lp);
    }

    //This is a trick since the following (not in listner) is not working:
    //button.setTextColor(ContextCompat.getColor(this, R.color.toggle_text));
    private void setTagButtonTextColor(ToggleButton b) {
        boolean checked = b.isChecked();
        b.setTextColor(ContextCompat.getColor(this, checked?R.color.textColor:R.color.colorPrimaryDark));
    }


    private void applyPlaylist(PlayList playList) {
        if(!isRemoteConnected()) {
            if(musicLibrary!=null) { //Happens before write permission allowed so db not accessed
                queue = musicLibrary.getTracks(playList);
            }
            localSelectedPlaylist = playList;
            playNext();
        } else {
            clientRemote.send("setPlaylist".concat(playList.toString()));
        }
        dimOn();
    }

    Spinner.OnItemSelectedListener spinnerListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
        int pos, long id) {
            if (spinnerPlaylistSend) {
                PlayList playList = (PlayList) parent.getItemAtPosition(pos);
                applyPlaylist(playList);
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
                String genre = (String) parent.getItemAtPosition(pos);
                if(!isRemoteConnected()) {
                    if(musicLibrary!=null) { //Happens before write permission allowed so db not accessed
                        displayedTrack.setGenre(genre);
                        musicLibrary.updateGenre(displayedTrack);
                    }
                }
                dimOn();
            }
            spinnerGenreSend=true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            dimOn();
        }
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

        if(toggleButtonDimMode.isChecked()) {
            dimOn();
        }
        else if(wasRemoteConnected && !audioPlayer.isPlaying()) {
            enableGUI(buttonRemote, false);
            getFromQRcode(getIntent().getDataString());
            buttonRemote.performClick();
        }

        receiverMediaButtonName = new ComponentName(getPackageName(), ReceiverMediaButton.class.getName());
        audioManager.registerMediaButtonEventReceiver(receiverMediaButtonName);
    }

    //https://developer.android.com/training/wearables/apps/voice.html
    private static final int SPEECH_REQUEST_CODE = 0;
    public void displaySpeechRecognizer() {
        textToSpeech.speak("Je vous écoute.", TextToSpeech.QUEUE_FLUSH, null);
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
            String spokenText = results.get(0);
            boolean foundPlaylist=false;
            for(PlayList playList : localPlaylists) {
                if(playList.getName().equalsIgnoreCase(spokenText)) {
                    localSelectedPlaylist = playList;
                    setupSpinner(localPlaylists, localSelectedPlaylist);
                    applyPlaylist(localSelectedPlaylist);
                    foundPlaylist=true;
                    break;
                }
            }
            if(!foundPlaylist) {
                toastLong("Playlist not found:\n\""+spokenText+"\"");
                textToSpeech.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    TextToSpeech textToSpeech;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity onDestroy");

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
        if(service!=null) {
            stopService(service);
        }

        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        stopRemote();
        stopSync();

        //Abort and wait scanLibrayInThread is aborted
        //So it does not crash if scanLib not completed
        if(processBrowseFS!=null) {
            processBrowseFS.abort();
        }
        if(processBrowseFScount!=null) {
            processBrowseFScount.abort();
        }
        if(scanLibray!=null) {
            scanLibray.abort();
        }
        try {
            if(processBrowseFS!=null) {
                processBrowseFS.join();
            }
            if(processBrowseFScount!=null) {
                processBrowseFScount.join();
            }
            if(scanLibray!=null) {
                scanLibray.join();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "MainActivity onDestroy: UNEXPECTED InterruptedException", e);
        }

        if(musicLibrary!=null) {
            musicLibrary.close();
        }

        saveFilesLists();

        //Close all notifications
        mNotifyManager.cancelAll();
    }

    //TODO: Do not saveFilesLists ALL everytime !! (not in receivedFile at least)
    private void saveFilesLists() {
        //Write list of files to maintain in db
        if(filesToKeep!=null) {
            Gson gson = new Gson();
            HelperTextFile.write(this, "FilesToKeep.txt", gson.toJson(filesToKeep));
        }
        //Write list of files to retrieve
        if(filesToGet!=null) {
            Gson gson = new Gson();
            HelperTextFile.write(this, "filesToGet.txt", gson.toJson(filesToGet));
        }
    }

    public static File getExtSDcard(String path) {
        String removableStoragePath;
        File fileList[] = new File("/storage/").listFiles();
        for (File file : fileList)
        {
            if(!file.getAbsolutePath().equalsIgnoreCase(
                    Environment.getExternalStorageDirectory().getAbsolutePath())
                    && file.isDirectory() && file.canRead())
                return new File(file.getAbsolutePath()+File.separator+path);
        }
        //If not found, use external storage which turns out to be ... internal SD card + internal phone memory
        //filtered and emulated
        // and not the actual external SD card as any could expect
        return new File(Environment.getExternalStorageDirectory()+File.separator+"JaMuz");
                //+File.separator+"Android/data/"+BuildConfig.APPLICATION_ID);
    }

    private static File[] externalFilesDir;

    public static File getAppDataPath() {

        File path =  externalFilesDir[1];
        /*
        File path = getExtSDcard("Android/data/org.phramusca.jamuz/");
        //File path = new File("/storage/3515-1C15/Android/data/org.phramusca.jamuz/files/");
        if(!path.exists()) {
            path.mkdir();
        }
        path = getExtSDcard("Android/data/org.phramusca.jamuz/files/");*/
        if(!path.exists()) {
            path.mkdirs();
        }
        return path;
    }

    private void scanLibrayInThread() {
        scanFolder(getAppDataPath());
        new Thread() {
            public void run() {
                try {
                    if(scanLibray!=null) {
                        scanLibray.join();
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "MainActivity onDestroy: UNEXPECTED InterruptedException", e);
                }
                if(!m_chosenDir.equals("/")) {
                    File folder = new File(m_chosenDir);
                    scanFolder(folder);
                }
            }
        }.start();
    }

    private void scanFolder(final File path) {
        scanLibray = new ProcessAbstract("Thread.MainActivity.scanLibrayInThread") {
            public void run() {
                try {
                    checkAbort();
                    nbFiles=0;
                    nbFilesTotal=0;
                    checkAbort();
                    //Scan android filesystem for files
                    processBrowseFS = new ProcessAbstract("Thread.MainActivity.browseFS") {
                        public void run() {
                            try {
                                browseFS(path);
                            } catch (IllegalStateException | InterruptedException e) {
                                Log.w(TAG, "Thread.MainActivity.browseFS InterruptedException");
                                scanLibray.abort();
                            }
                        }
                    };
                    processBrowseFS.start();
                    //Get total number of files
                    processBrowseFScount = new ProcessAbstract("Thread.MainActivity.browseFScount") {
                        public void run() {
                            try {
                                browseFScount(path);
                            } catch (InterruptedException e) {
                                Log.w(TAG, "Thread.MainActivity.browseFScount InterruptedException");
                                scanLibray.abort();
                            }
                        }
                    };
                    processBrowseFScount.start();

                    try {
                        processBrowseFS.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        processBrowseFScount.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkAbort();
                    //Scan deleted files
                    //TODO: No need to check what scanned previously ...
                    List<Track> tracks = musicLibrary.getTracks(new PlayList("All"));
                    nbFilesTotal = tracks.size();
                    nbFiles=0;
                    for(Track track : tracks) {
                        checkAbort();
                        File file = new File(track.getPath());
                        if(!file.exists()) {
                            Log.d(TAG, "Remove track from db: "+track);
                            musicLibrary.deleteTrack(track.getPath());
                        }
                        notifyScan("JaMuz is scanning deleted files ... ", 200);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String msg = "Database updated.";
                            toastLong(msg);
                            notifyBar(mBuilderScan, ID_NOTIFIER_SCAN, msg, 5000);

                        }
                    });
                } catch (InterruptedException e) {
                    Log.w(TAG, "Thread.MainActivity.scanLibrayInThread InterruptedException");
                }
            }

            private void browseFS(File path) throws InterruptedException {
                checkAbort();
                if (path.isDirectory()) {
                    File[] files = path.listFiles();
                    if (files != null) {
                        if(files.length>0) {
                            for (File file : files) {
                                checkAbort();
                                if (file.isDirectory()) {
                                    browseFS(file);
                                }
                                else {
                                    String absolutePath=file.getAbsolutePath();
                                    if(absolutePath.startsWith(getAppDataPath().getAbsolutePath())) {
                                        //Scanning private sd card path
                                        //=> Files from JaMuz Sync
                                        String fileKey = absolutePath.substring(
                                                getAppDataPath().getAbsolutePath().length()+1);
                                        if(filesToKeep!=null && !filesToKeep.containsKey(fileKey)) {
                                            Log.i(TAG, "Deleting file "+absolutePath);
                                            file.delete();
                                        } else if(filesToKeep!=null && filesToKeep.containsKey(fileKey)) {
                                            FileInfoReception fileInfoReception=filesToKeep.get(fileKey);
                                            insertOrUpdateTrackInDatabase(absolutePath, fileInfoReception);
                                        } else {
                                            insertOrUpdateTrackInDatabase(absolutePath, null);
                                        }
                                    }
                                    else {
                                        //Scanning extra local folder
                                        List<String> audioExtenstions = new ArrayList<>();
                                        audioExtenstions.add("mp3");
                                        audioExtenstions.add("flac");
                                        /*audioFiles.add("ogg");*/
                                        String ext = absolutePath.substring(absolutePath.lastIndexOf(".")+1);
                                        if(audioExtenstions.contains(ext)) {
                                            insertOrUpdateTrackInDatabase(absolutePath, null);
                                        }
                                    }
                                    notifyScan("JaMuz is scanning files ... ", 13);
                                }
                            }
                        } else {
                            Log.i(TAG, "Deleting empty folder "+path.getAbsolutePath());
                            path.delete();
                        }
                    }
                }
            }

            private void browseFScount(File path) throws InterruptedException {
                checkAbort();
                if (path.isDirectory()) {
                    File[] files = path.listFiles();
                    if (files != null) {
                        if(files.length>0) {
                            for (File file : files) {
                                checkAbort();
                                if (file.isDirectory()) {
                                    browseFScount(file);
                                }
                                else {
                                    nbFilesTotal++;
                                }
                            }
                        }
                    }
                }
            }
        };
        scanLibray.start();
    }

    //Ends a notification
    private void notifyBar(NotificationCompat.Builder builder, int id, String msg, long millisInFuture) {
        notifyBar(builder, id, msg, 0, 0, false, true, false);
        disableNotificationIn(millisInFuture, id);
    }

    private void notifyBar(NotificationCompat.Builder builder, int id, String msg,
                           int max, int progress, boolean indeterminate,boolean setWhen,
                           boolean usesChronometer) {
        builder.setContentText(msg);
        if(setWhen) {
            builder.setWhen(System.currentTimeMillis());
        }
        builder.setUsesChronometer(usesChronometer);
        builder.setProgress(max, progress, indeterminate);
        builder.setContentIntent(getApplicationIntent());
        mNotifyManager.notify(id, builder.build());
    }

    //This is to have application opened when clicking on notification
    private PendingIntent getApplicationIntent() {
        Intent notificationIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);
        return intent;
    }

    private boolean insertOrUpdateTrackInDatabase(String absolutePath,
                                                  FileInfoReception fileInfoReception) {
        boolean result=true;
        int id = musicLibrary.getTrack(absolutePath);
        if(id>=0) {
            Log.d(TAG, "browseFS updateTrack " + absolutePath);
            //TODDO: Update if file is modified only:
            //based on lastModificationDate and/or size (not on content as longer than updateTrack)
            //musicLibrary.updateTrack(id, track, false);
            //Warning with genre now that it is part of merge
        } else {
            Track track = getTrack(absolutePath);
            if(track!=null) {
                Log.d(TAG, "browseFS insertTrack " + absolutePath);
                if(fileInfoReception!=null) {
                    track.setRating(fileInfoReception.rating);
                    track.setAddedDate(fileInfoReception.addedDate);
                    track.setLastPlayed(fileInfoReception.lastPlayed);
                    track.setPlayCounter(fileInfoReception.playCounter);

                    //FIXME TAGS & GENRE : test insertion from FileInfoReception
                    track.setTags(fileInfoReception.tags);
                    track.setGenre(fileInfoReception.genre); //TODO Do not if genre read from file is better
                }
                musicLibrary.insertTrack(track);
            } else {
                //FIXME: Delete track ONLY if it is a song track
                //that appears to be corrupted
                Log.w(TAG, "browseFS delete file because cannot read tags of " + absolutePath);
                new File(absolutePath).delete();
                result=false;
            }
        }
        return result;
    }

    private void connectDatabase() {
        musicLibrary = new MusicLibrary(this);
        musicLibrary.open();
    }

    private Track getTrack(final String absolutePath) {
        Track track=null;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(absolutePath);

            String album =
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String artist =
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String title =
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String genre =
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

            int rating = 0;
            String coverHash="";
            track = new Track(-1, rating, title, album, artist, coverHash, absolutePath, genre ,
                    new Date(), new Date(0), 0);
        } catch (final RuntimeException ex) {
            Log.e(TAG, "Error reading file tags "+absolutePath, ex);
        }
        return track;
    }

    private void playHistory() {
        displayedTrack = queueHistory.get(queueHistoryIndex);
        Log.i(TAG, "playHistory("+(queueHistoryIndex+1)+"/"+queueHistory.size()+")");
        playAudio();
    }

    private void play() {
        File file = new File(displayedTrack.getPath());
        if(file.exists()) {
            queueHistory.add(displayedTrack);
            queueHistoryIndex = queueHistory.size()-1;
            playAudio();
        } else {
            Log.d(TAG, "play(): Remove track from db:"+displayedTrack);
            musicLibrary.deleteTrack(displayedTrack.getPath());
            playNext();
        }
    }

    private int queueHistoryIndex =-1;

    private void playPrevious() {
        if(queueHistory.size()>0 && queueHistoryIndex>0 && queueHistoryIndex<queueHistory.size()) {
            queueHistoryIndex--;
            playHistory();
        } else {
            toastLong("No tracks beyond.");
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
            musicLibrary.updateTrack(displayedTrack);
            //Fill the queue
            if(queue.size()<5) {
                if(musicLibrary!=null) { //Happens before write permission allowed so db not accessed
                    List<Track> addToQueue = musicLibrary.getTracks((PlayList) spinnerPlaylist.getSelectedItem());
                    queue.addAll(addToQueue);
                }
            }
            //Play first track in queue
            if(queue.size()>0) {
                displayedTrack = queue.get(0);
                queue.remove(0);
                Log.i(TAG, "playQueue(1/"+queue.size()+")");
                play();
            } else {
                //setupSpinner(localPlaylists, localSelectedPlaylist);
                toastLong("Empty Playlist.");
            }
        }
    }

    class CallBackPlayer implements ICallBackPlayer {

        private final String TAG = MainActivity.class.getSimpleName()+"."+CallBackPlayer.class.getSimpleName();

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
            displayTrack();
        }

    }

    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String msg = (String) message.obj;
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

    public void playAudio(){
        localTrack = displayedTrack;
        audioPlayer.stop(false);
        String msg = audioPlayer.play(displayedTrack);
        if(!msg.equals("")) {
            toastLong(msg);
        }
        dimOn();
    }

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = brightness;
                getWindow().setAttributes(params);
            }
        });
    }

    private void enableConnect(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!enable) {
                    buttonRemote.setText("Close");
                    buttonRemote.setBackgroundResource(R.drawable.remote_on);
                } else {
                    buttonRemote.setBackgroundResource(R.drawable.remote_off);
                    setupSpinner(localPlaylists, localSelectedPlaylist);
                }
                editTextConnectInfo.setEnabled(enable);
                buttonRemote.setEnabled(true);
            }
        });
    }

    private void enableSync(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!enable) {
                    buttonSync.setText("Close");
                    buttonSync.setBackgroundResource(R.drawable.connect_on);
                } else {
                    buttonSync.setBackgroundResource(R.drawable.connect_off);
                }
                editTextConnectInfo.setEnabled(enable);
                buttonSync.setEnabled(true);
            }
        });
    }

    private void enableGUI(final Button button, final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editTextConnectInfo.setEnabled(enable);
                button.setEnabled(enable);
            }
        });
    }

    private void getFromQRcode(String content) {
        if(content!=null) {
            if(!content.equals("")) {
                content=content.substring("JaMuzRemote://".length());
                content=Encryption.decrypt(content, "NOTeBrrhzrtestSecretK");
                editTextConnectInfo.setText(content);
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

            //TODO: Translate
            String msgStr = "<html><b>For a full JaMuz experience</b>, please consider " +
                    "allowing permissions that you will be asked for: <BR/><BR/>" +
                    "<i>- <u>Multimedia files</u></i> : Allows application to:<BR/> " +
                    "- Write files received from JaMuz to application folder on external SD card " +
                        "(\"" + getAppDataPath() +"\") <BR/> " +
                    "- Read-only user selected folder on external SD card <BR/>" +
                    "- Store database in JaMuz folder on internal SD card " +
                        "(\""+musicLibraryDbFile.getAbsolutePath()+"\").<BR/><BR/>" +
                    "<i>- <u>Phone calls</u></i> : Simply to be able to pause and resume audio on phone calls.";

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Warning !");
            alertDialog.setMessage(Html.fromHtml(msgStr));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            askPermissions();
                        }
                    });
            alertDialog.show();
        } else {
            connectDatabase();
            setupTags();
            setupGenres();
            scanLibrayInThread();
            setupLocalPlaylists();
        }
    }

    private void setupTags() {
        if(tags.size()<=0) {
            tags = new HashMap<>();
            tags = musicLibrary.getTags();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(Map.Entry<Integer, String> tag : tags.entrySet()) {
                        makeButtonTag(tag.getKey(), tag.getValue());
                        makeButtonTagPlaylist(tag.getKey(), tag.getValue());
                    }
                }
            });
        }
    }

    private void setupGenres() {
        genres = new ArrayList<>();
        genres = musicLibrary.getGenres();
        setupSpinnerGenre(genres, displayedTrack.getGenre());
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, REQUEST );
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
                    setupTags();
                    setupGenres();
                    scanLibrayInThread();
                    setupLocalPlaylists();
                }
            }
        }
    }

    private Button setupButton(Button button, final int buttonName, final String msg) {
        button = (Button) findViewById(buttonName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(msg);
            }
        });
        return button;
    }

    protected void doAction(String msg) {
        if(!isRemoteConnected()) {
            switch (msg) {
                case "previousTrack":
                    playPrevious();
                    break;
                case "nextTrack":
                    playNext();
                    break;
                case "playTrack":
                    audioPlayer.togglePlay();
                    dimOn();
                    break;
                case "pullup":
                    audioPlayer.pullUp();
                    dimOn();
                    break;
                case "rewind":
                    audioPlayer.rewind();
                    dimOn();
                    break;
                case "forward":
                    audioPlayer.forward();
                    dimOn();
                    break;
                case "volUp":
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    dimOn();
                    break;
                case "volDown":
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    dimOn();
                    break;
                default:
                    //Popup("Error", "Not implemented");
                    toastLong("Not implemented");
                    break;
            }
        } else {
            dimOn();
            clientRemote.send(msg);
        }
    }

    private void setupLocalPlaylists() {
        localPlaylists = new ArrayList<PlayList>();
        addToPlaylists("Top", "rating=5", "rating>2", "playCounter, lastPlayed");
        addToPlaylists("Discover","rating=0", "rating=0", "RANDOM()");
        addToPlaylists("More","rating>2 AND rating<5", "rating>0 AND rating<3", "playCounter, lastPlayed");
        localPlaylists.add(new PlayList("All", "1", "", musicLibrary));
        localSelectedPlaylist = localPlaylists.get(0);
        setupSpinner(localPlaylists, localSelectedPlaylist);
    }

    private void addToPlaylists(String name, String where, String whereEnfantin, String order) {
        String enfantin = "Enfantin";

        addToPlaylists(name, "genre!=\""+enfantin+"\" AND " + where, order);
        addToPlaylists(name+" "+enfantin, "genre=\""+enfantin+"\" AND " + whereEnfantin, order);
        LinkedHashMap<String, Integer> genres = new LinkedHashMap();
        genres = musicLibrary.getGenres("genre!=\""+enfantin+"\" AND " + where);
        for(Map.Entry<String, Integer> entry : genres.entrySet()) {
            localPlaylists.add(new PlayList(name + " " + entry.getKey(),
                    "genre=\"" + entry.getKey() + "\" AND " + where,
                    order,
                    musicLibrary));
        }
        String in = getInSqlList(genres);
        if(!in.equals("")) {
            in+=",\""+enfantin+"\"";
            addToPlaylists(name+" Autre", "genre NOT IN ("+in+") AND " + where, order);
        }
    }

    private void addToPlaylists(String name, String where, String order) {
        localPlaylists.add(new PlayList(name, where, order, musicLibrary));
    }

    private String getInSqlList(Map<String, Integer> list) {
        String in = "";
        if(list.size()>0) {
            for(String entry : list.keySet()) {
                in+="\""+entry+"\",";
            }
            in = in.substring(0, in.length()-1);
        }
        return in;
    }

    ///TODO: Detect WIFI connection to allow/disallow "Connect" buttons
    //https://stackoverflow.com/questions/5888502/how-to-detect-when-wifi-connection-has-been-established-in-android

    private boolean checkConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    private void setupSpinner(final List<PlayList> playlists, final PlayList selectedPlaylist) {

        final ArrayAdapter<PlayList> arrayAdapter =
                new ArrayAdapter<PlayList>(this, android.R.layout.simple_spinner_item, playlists);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinnerPlaylistSend =false;
                spinnerPlaylist.setAdapter(arrayAdapter);
                spinnerPlaylist.setSelection(arrayAdapter.getPosition(selectedPlaylist));
            }
        });
    }

    private void setupSpinnerGenre(final List<String> genres, final String genre) {

        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genres);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinnerGenreSend=false;
                spinnerGenre.setAdapter(arrayAdapter);
                if(!genre.equals("")) {
                    spinnerGenre.setSelection(arrayAdapter.getPosition(genre));
                }
            }
        });
    }

    private void setTextView(final TextView textview, final Spanned msg, final boolean append) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(append) {
                    textview.append(msg);
                }
                else {
                    textview.setText(msg);
                }
            }
        });
    }

    private void toastLong(final String msg) {
        toast(msg, Toast.LENGTH_LONG);
    }

    private void toastShort(final String msg) {
        toast(msg, Toast.LENGTH_SHORT);
    }

    private void toast(final String msg, int duration) {
        if(!msg.equals("")) {
            Log.i(TAG, "Toast makeText "+msg);
            Toast.makeText(MainActivity.this, msg, duration).show();
        }
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }


    private void notifyScan(final String action, int every) {
        nbFiles++;
        if(((nbFiles-1) % every) == 0) { //To prevent UI from freezing
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = nbFiles + "/" + nbFilesTotal + " " + action;
                    notifyBar(mBuilderScan, ID_NOTIFIER_SCAN, msg, nbFilesTotal, nbFiles, false, false, false);
                }
            });
        }
    }

    private void setSeekBar(final int currentPosition, final int total) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBarPosition.setMax(total);
                seekBarPosition.setProgress(currentPosition);
            }
        });
    }

    public void popup(final String title, final CharSequence msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(msg);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private void displayTrack() {
        if(displayedTrack!=null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTextView(textViewFileInfo, trimTrailingWhitespace(Html.fromHtml("<html><h1>"
                            .concat(displayedTrack.toString())
                            .concat("</h1></html>"))), false);
                    ratingBar.setEnabled(false);
                    ratingBar.setRating(displayedTrack.getRating());
                    ratingBar.setEnabled(true);
                    setupSpinnerGenre(genres, displayedTrack.getGenre());

                    //Display file tags
                    if(musicLibrary!=null) {
                        musicLibrary.getTags(displayedTrack.getId()); //To refresh after merge
                        ArrayList<String> fileTags = displayedTrack.getTags();
                        for(Map.Entry<Integer, String> tag : tags.entrySet()) {
                            ToggleButton button = (ToggleButton) layoutTags.findViewById(tag.getKey());
                            if(button.isChecked()!=fileTags.contains(tag.getValue())) {
                                button.setChecked(fileTags.contains(tag.getValue()));
                            }
                            setTagButtonTextColor(button);
                        }
                    }
                }
            });

            if(displayedTrack.getId()>=0) {
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
        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return new SpannableString(source.subSequence(0, i+1));
    }

    private static final String AVRCP_PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
    private static final String AVRCP_META_CHANGED = "com.android.music.metachanged";

    private void bluetoothNotifyChange(String what) {
        Intent i = new Intent(what);
        i.putExtra("id", Long.valueOf(displayedTrack.getId()));
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
            bitmap = (Bitmap) coverMap.get(displayedTrack.getCoverHash());
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
        runOnUiThread(new Runnable() {
            public void run() {
                imageViewCover.setImageBitmap(finalBitmap);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(finalBitmap);
                bitmapDrawable.setAlpha(50);
                layoutMain.setBackground(bitmapDrawable);
            }
        });
    }

    private void displayImage(byte[] art) {
        if( art != null ){
            displayImage( BitmapFactory.decodeByteArray(art, 0, art.length));
        } else {
            displayImage(getEmptyCover());
        }
    }

    private Bitmap getEmptyCover() {
        return textAsBitmap("No cover");
    }

    public Bitmap textAsBitmap(String text) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(35);
        paint.setColor(Color.rgb(192, 192, 192));
        int size = 500;
        Bitmap image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(Color.rgb(64, 64, 64));
        canvas.drawText(text, (size/2)-(text.length()*12),
                (size/2)+20, paint);
        return image;
    }

    class CallBackRemote implements ICallBackReception {

        private final String TAG = MainActivity.class.getSimpleName()+"."+CallBackRemote.class.getSimpleName();

        @Override
        public void received(final String msg) {
            if(msg.startsWith("MSG_")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastShort(msg);
                        }
                    });
            }
            else {
                try {
                    JSONObject jObject = new JSONObject(msg);
                    String type = jObject.getString("type");
                    switch(type) {
                        case "playlists":
                            String selectedPlaylist = jObject.getString("selectedPlaylist");
                            PlayList temp = new PlayList(selectedPlaylist);
                            final JSONArray jsonPlaylists = (JSONArray) jObject.get("playlists");
                            final List<PlayList> playlists = new ArrayList<PlayList>();
                            for(int i=0; i<jsonPlaylists.length(); i++) {
                                String playlist = (String) jsonPlaylists.get(i);
                                PlayList playList = new PlayList(playlist);
                                if(playlist.equals(selectedPlaylist)) {
                                    playList=temp;
                                }
                                playlists.add(playList);
                            }
                            setupSpinner(playlists, temp);
                            break;
                        case "currentPosition":
                            final int currentPosition = jObject.getInt("currentPosition");
                            final int total = jObject.getInt("total");
                            if(isRemoteConnected()) {
                                setSeekBar(currentPosition, total);
                            }
                            break;
                        case "fileInfoInt":
                            displayedTrack = new Track(-1,
                                    jObject.getInt("rating"),
                                    jObject.getString("title"),
                                    jObject.getString("album"),
                                    jObject.getString("artist"),
                                    jObject.getString("coverHash"), "",
                                    jObject.getString("genre"),
                                    new Date(),
                                    new Date(0),0);
                            displayTrack();
                            break;
                    }
                } catch (JSONException e) {
                    setTextView(textViewFileInfo, Html.fromHtml(e.toString()), false);
                }
            }
        }

        @Override
        public void receivedFile(final FileInfoReception fileInfoReception) { }
        @Override
        public void receivingFile(FileInfoReception fileInfoReception) { }
        @Override
        public void receivedDatabase() { }

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toastShort(msg);
                }
            });
            stopRemote();
            setupSpinner(localPlaylists, localSelectedPlaylist);
            displayedTrack = localTrack;
            displayTrack();
        }
    }

    class CallBackSync implements ICallBackReception {

        private final String TAG = MainActivity.class.getSimpleName()+"."+CallBackSync.class.getSimpleName();

        @Override
        public void received(final String msg) {
            if(msg.equals("MSG_SEND_DB")) {
                clientSync.sendDatabase();
            }
            else if(msg.startsWith("MSG_")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastShort(msg);
                    }
                });
            }
            else {
                try {
                    JSONObject jObject = new JSONObject(msg);
                    String type = jObject.getString("type");
                    switch(type) {
                        case "FilesToGet":
                            filesToGet = new HashMap<>();
                            filesToKeep = new HashMap<>();
                            JSONArray files = (JSONArray) jObject.get("files");
                            for(int i=0; i<files.length(); i++) {
                                FileInfoReception fileReceived = new FileInfoReception((JSONObject) files.get(i));
                                filesToKeep.put(fileReceived.relativeFullPath, fileReceived);
                                File localFile = new File(getAppDataPath(), fileReceived.relativeFullPath);
                                if(!localFile.exists()) {
                                    filesToGet.put(fileReceived.idFile, fileReceived);
                                } else {
                                    clientSync.send("insertDeviceFile"+fileReceived.idFile);
                                }
                            }
                            requestNextFile(true);
                            break;
                        case "tags":
                            final JSONArray jsonTags = (JSONArray) jObject.get("tags");
                            for(int i=0; i<jsonTags.length(); i++) {
                                final String tag = (String) jsonTags.get(i);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!tags.values().contains(tag)) {
                                            if(musicLibrary!=null) { //Happens before write permission allowed so db not accessed
                                                int idTag = musicLibrary.addTag(tag);
                                                if(idTag>0) {
                                                    tags.put(idTag, tag);
                                                    makeButtonTag(idTag, tag);
                                                    makeButtonTagPlaylist(idTag, tag);
                                                }
                                            }
                                        }
                                    }
                                });

                            }
                            break;
                        case "genres":
                            final JSONArray jsonGenres = (JSONArray) jObject.get("genres");
                            for(int i=0; i<jsonGenres.length(); i++) {
                                final String genre = (String) jsonGenres.get(i);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!genres.contains(genre)) {
                                            if(musicLibrary!=null) { //Happens before write permission allowed so db not accessed
                                                if (musicLibrary.addGenre(genre)) {
                                                    genres.add(genre);
                                                }
                                            }
                                        }
                                    }
                                });

                            }
                            setupSpinnerGenre(genres, displayedTrack.getGenre());
                            break;
                    }
                } catch (JSONException e) {
                    setTextView(textViewFileInfo, Html.fromHtml(e.toString()), false);
                }
            }
        }

        @Override
        public void receivedFile(final FileInfoReception fileInfoReception) {
            cancelWatchTimeOut();
            Log.i(TAG, "Received file\n"+fileInfoReception+"\nRemaining : "+filesToGet.size()+"/"+filesToKeep.size());
            File path = getAppDataPath();
            File receivedFile = new File(path.getAbsolutePath()+File.separator
                    +fileInfoReception.relativeFullPath);
            if(filesToGet.containsKey(fileInfoReception.idFile)) {
                if(receivedFile.exists()) {
                    if (receivedFile.length() == fileInfoReception.size) {
                        Log.i(TAG, "Saved file size: " + receivedFile.length());
                        if(insertOrUpdateTrackInDatabase(receivedFile.getAbsolutePath(), fileInfoReception)) {
                            filesToGet.remove(fileInfoReception.idFile);
                            clientSync.send("insertDeviceFile" + fileInfoReception.idFile);
                        } else {
                            Log.w(TAG, "File tags could not be read. Deleting " + receivedFile.getAbsolutePath());
                            receivedFile.delete();
                        }
                    } else {
                        Log.w(TAG, "File has wrong size. Deleting " + receivedFile.getAbsolutePath());
                        receivedFile.delete();
                    }
                } else {
                    Log.w(TAG, "File does not exits. "+receivedFile.getAbsolutePath());
                }
            } else {
                Log.w(TAG, "File not requested. Deleting "+receivedFile.getAbsolutePath());
                receivedFile.delete();
            }
            requestNextFile(true);
        }

        @Override
        public void receivingFile(final FileInfoReception fileInfoReception) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = filesToGet.size() + "/" + filesToKeep.size()
                            + " remaining. Receiving: "+fileInfoReception.relativeFullPath;
                    int max=filesToKeep.size();
                    int progress=filesToKeep.size()-filesToGet.size();
                    notifyBar(mBuilderSync, ID_NOTIFIER_SYNC, msg, max, progress, false, true, true);
                }
            });
        }

        @Override
        public void receivedDatabase() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = "Statistics merged.";
                    toastLong(msg);
                    notifyBar(mBuilderSync, ID_NOTIFIER_SYNC, msg, 5000);
                }
            });

            // TODO MERGE: Update FilesToKeep and FilesToGet
            // as received merged db is the new reference
            // (not urgent since values should only be
            // used again if file has been removed from db
            // somehow, as if db crashes and remade)
        }

        @Override
        public void receivedBitmap(final Bitmap bitmap) {
        }

        @Override
        public void disconnected(final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toastShort(msg);
                }
            });
            stopSync();
        }
    }

    private CountDownTimer timerWatchTimeout= new CountDownTimer(0, 0) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {

        }
    };

    private void cancelWatchTimeOut() {
        Log.i(TAG, "timerWatchTimeout.cancel()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized(timerWatchTimeout) {
                    if(timerWatchTimeout!=null) {
                        timerWatchTimeout.cancel(); //Cancel previous if any
                    }
                }
            }
        });
    }

    private void watchTimeOut(final long size) {
        cancelWatchTimeOut();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized(timerWatchTimeout) {

                    long minTimeout =  10 * 1000;  //Min timeout 10s (+ 10s by Mo)
                    long maxTimeout =  120 * 1000; //Max timeout 2 min

                    long timeout = size<1000000?minTimeout:((size / 1000000) * minTimeout);
                    timeout = timeout>maxTimeout?maxTimeout:timeout;
                    timerWatchTimeout = new CountDownTimer(timeout, timeout/10) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.i(TAG, "Seconds Remaining: "+ (millisUntilFinished/1000));
                        }

                        @Override
                        public void onFinish() {
                            Log.w(TAG, "Timeout. Dis-connecting");
                            stopSync();
                            Log.i(TAG, "Re-connecting");
                            buttonSync.performClick();
                        }
                    };
                    Log.i(TAG, "timerWatchTimeout.start()");
                    timerWatchTimeout.start();
                }
            }
        });
    }

    private void disableNotificationIn(final long millisInFuture, final int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CountDownTimer timer = new CountDownTimer(millisInFuture, millisInFuture/10) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.i(TAG, (millisUntilFinished/1000)+"s remaining before " +
                                "disabling notification");
                    }

                    @Override
                    public void onFinish() {
                        mNotifyManager.cancel(id);
                    }
                };
                timer.start();
            }
        });
    }

    private Map<Integer, FileInfoReception> filesToGet = null;
    private Map<String, FileInfoReception> filesToKeep = null;

    private void requestNextFile(final boolean scanLibrary) {
        if(filesToKeep!=null) {
            saveFilesLists();
            if(filesToGet.size()>0) {
                final FileInfoReception fileInfoReception = filesToGet.entrySet().iterator().next().getValue();
                File receivedFile = new File(getAppDataPath(), fileInfoReception.relativeFullPath);
                if(receivedFile.exists() && receivedFile.length() == fileInfoReception.size) {
                    Log.i(TAG, "File already exists. Remove from filesToGet list: "+fileInfoReception);
                    clientSync.send("insertDeviceFile"+fileInfoReception.idFile);
                    //No need to request this one, it already exists (how can this be ?)
                    filesToGet.remove(fileInfoReception.idFile);
                    //Request next one then
                    requestNextFile(scanLibrary);
                } else {
                    //Wait (after connection) and send request
                    //final int id = filesToGet.entrySet().iterator().next().getKey();
                    //final FileInfoReception id = filesToGet.entrySet().iterator().next().getValue();
                    new Thread() {
                        @Override
                        public void run() {
                            if(!scanLibrary) {
                                try {
                                    //Waits a little after connection
                                    Log.i(TAG, "Waiting 2s");
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                }
                            }
                            watchTimeOut(fileInfoReception.size);
                            synchronized(timerWatchTimeout) {
                                clientSync.send("sendFile"+fileInfoReception.idFile);
                            }
                        }
                    }.start();
                }
            } else {
                final String msg = "No more files to download.\n\nAll "+filesToKeep.size()+" files" +
                        " have been retrieved successfully.";
                Log.i(TAG, msg+" Updating library:"+scanLibrary);
                notifyBar(mBuilderSync, ID_NOTIFIER_SYNC, msg, 5000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastLong(msg);
                    }
                });
                //Not disconnecting to be able to receive a new list
                //sent by the server. User can still close
                //enableSync(true);
                //stopClient(clientSync,buttonSync, R.drawable.connect_off, true);

                //Resend add request in case missed for some reason
                if(filesToKeep!=null) {
                    for(FileInfoReception file : filesToKeep.values()) {
                        if(!filesToGet.containsKey(file.idFile)) {
                            clientSync.send("insertDeviceFile"+file.idFile);
                        }
                    }
                }
                if(scanLibrary) {
                    checkPermissionsThenScanLibrary();
                }
            }
        } else {
            Log.i(TAG, "filesToKeep is null");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toastLong("No files to download.\n\nYou can use JaMuz (Linux/Windows) to " +
                            "export a list of files to retrieve, based on playlists.");
                }
            });
        }
    }

    private void stopRemote() {
        stopClient(clientRemote, buttonRemote, R.drawable.remote_off, true);
        setupSpinner(localPlaylists, localSelectedPlaylist);
        displayedTrack = localTrack;
        displayTrack();
    }

    private void stopSync() {
        stopClient(clientSync,buttonSync, R.drawable.connect_off, true);
        cancelWatchTimeOut();
    }

    private void stopClient(Client client, final Button button, final int resId, final boolean enable) {
        if(client!=null) {
            client.close();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableGUI(button, false);
                button.setText("Connect");
                button.setBackgroundResource(resId);
                button.setEnabled(enable);
                editTextConnectInfo.setEnabled(enable);
            }
        });
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
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent.getAction().equals("android.intent.action.VIEW")) {
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
            if (intent.getAction().equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED))
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
                    //TODO: This situation (at least) can endup with other receivers (headsethook at least)
                    //not to trigger anymore => Why ?
                    Log.i(TAG, "BT DISconnected");
                    audioPlayer.pause();
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
}
