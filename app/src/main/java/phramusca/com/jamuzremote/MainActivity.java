package phramusca.com.jamuzremote;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private Client client;
    private Track displayedTrack;
    private Track localTrack;
    private Map coverMap = new HashMap();
    private Intent service; //Not yet used
    private AudioManager audioManager;
    public static AudioPlayer audioPlayer;
    private MusicLibrary musicLibrary;
    private int nbFiles=0;
    private int nbFilesTotal = 0;
    private List<Track> queue = new ArrayList<>();
    private List<Track> queueHistory = new ArrayList<>();
    private boolean local = true;
    final List<PlayList> localPlaylists = new ArrayList<PlayList>();
    private PlayList localSelectedPlaylist = new PlayList("All", null);

    // GUI elements
    private TextView textViewReceived;
    private EditText editTextConnectInfo;
    private Button buttonConnect;
    private ToggleButton buttonSetDimMode;
    private ToggleButton buttonControlsToggle;
    private ToggleButton buttonConnectToggle;
    private Button buttonPrevious;
    private Button buttonPlay;
    private Button buttonNext;
    private Button buttonRewind;
    private Button buttonPullup;
    private Button buttonForward;
    private Button buttonVolUp;
    private Button buttonVolDown;
    private SeekBar seekBar;
    private Spinner spinner;
    private boolean spinnerSend=true;
    private RatingBar ratingBar;
    private ImageView image;
    private LinearLayout trackInfo;
    private LinearLayout controls;
    private GridLayout connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "MainActivity onCreate");
        setContentView(R.layout.activity_main);

        textViewReceived = (TextView) findViewById(R.id.textView_conv);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                if(fromUser) { //as it is also set when server sends file info (and it can be 0)
                    dimOn();
                    ratingBar.setEnabled(false);
                    displayedTrack.setRating(Math.round(rating));
                    if(local) {
                        musicLibrary.updateTrack(displayedTrack.getId(), displayedTrack, true);
                    } else {
                        client.send("setRating".concat(String.valueOf(Math.round(rating))));
                    }
                    ratingBar.setEnabled(true);
                }
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setEnabled(false);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                PlayList item = (PlayList) parent.getItemAtPosition(pos);
                if(spinnerSend) {
                    if(local) {
                        if(musicLibrary!=null) { //Happens before write permission allowed so db not accessed
                            queue = musicLibrary.getTracks(item);
                        }
                        localSelectedPlaylist = item;
                    } else {
                        client.send("setPlaylist".concat(item.toString()));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Another interface callback
            }
        });
        spinner.setOnTouchListener(new View.OnTouchListener() {
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

        editTextConnectInfo = (EditText) findViewById(R.id.editText_info);
        editTextConnectInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dimOn();
                return false;
            }
        });

        buttonSetDimMode = (ToggleButton) findViewById(R.id.button_dim_mode);
        buttonSetDimMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDimMode(!buttonSetDimMode.isChecked());
            }
        });

        buttonControlsToggle = (ToggleButton) findViewById(R.id.button_controls_toggle);
        buttonControlsToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimOn();
                toggleControls(!buttonControlsToggle.isChecked());
            }
        });

        buttonConnectToggle = (ToggleButton) findViewById(R.id.button_connect_toggle);
        buttonConnectToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimOn();
                toggleConnect(!buttonConnectToggle.isChecked());
            }
        });

        buttonConnect = (Button) findViewById(R.id.button_connect);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimOn();
                enableGUI(false);
                if(buttonConnect.getText().equals("Connect")) {
                    CallBackReception callBackReception = new CallBackReception();

                    String infoConnect = editTextConnectInfo.getText().toString();
                    String[] split = infoConnect.split(":");  //NOI18N
                    if(split.length<2) {
                        enableConnect(true);
                        return;
                    }
                    String address = split[0];
                    int port;
                    try {
                        port = Integer.parseInt(split[1]);
                    } catch(NumberFormatException ex) {
                        port=2013;
                    }

                    client = new Client(address, port, Settings.Secure.getString(MainActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID), "tata", callBackReception);

                    //Must start networking in a thread or getting NetworkOnMainThreadException
                    new Thread() {
                        public void run() {
                            if(client.connect()) {
                                enableGUI(true);
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

                    displayedTrack = localTrack;
                    displayTrack();
                }
            }
        });

        image = (ImageView) findViewById(R.id.imageView);

        trackInfo = (LinearLayout) findViewById(R.id.trackInfo);

        controls = (LinearLayout) findViewById(R.id.controls);
        connect = (GridLayout) findViewById(R.id.connect);

        trackInfo.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                Log.i(TAG, "onSwipeTop");
                if(local) {
                    audioPlayer.forward();
                } else {
                    client.send("forward");
                }

            }
            @Override
            public void onSwipeRight() {
                Log.i(TAG, "onSwipeRight");
                if(local) {
                    playPrevious();
                } else {
                    client.send("previousTrack");
                }
            }
            @Override
            public void onSwipeLeft() {
                Log.i(TAG, "onSwipeLeft");
                if(local) {
                    playNext();
                } else {
                    client.send("nextTrack");
                }
            }
            @Override
            public void onSwipeBottom() {
                Log.i(TAG, "onSwipeBottom");
                if(local) {
                    audioPlayer.rewind();
                } else {
                    client.send("rewind");
                }
            }
            @Override
            public void onTouch() {
                dimOn();
            }

        });



        localTrack = new Track(-1, 0, "Welcome to", "2017", "JaMuz Remote", "coverHash", "path", "---");
        displayedTrack = localTrack;
        setTextView(textViewReceived, Html.fromHtml("<html><h1>".concat(displayedTrack.toString()).concat("<BR/></h1></html>")), false);

        enableGUI(false);
        getFromQRcode();
        editTextConnectInfo.setEnabled(true);
        buttonConnect.setEnabled(true);

        //TODO: MAke this an option somehow
        pathToFiles = getExtSDcard("/storage/", "Android/data/com.theolivetree.sshserver/files/");
        //  /storage/3515-1C15/Android/data/com.theolivetree.sshserver/files/";

        checkPermissions();

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
                    mBluetoothAdapter.getProfileProxy(this, mHeadsetProfileListener, BluetoothProfile.HEADSET);
                }
            }
        }

        receiverMediaButtonName = new ComponentName(getPackageName(), ReceiverMediaButton.class.getName());
        audioManager.registerMediaButtonEventReceiver(receiverMediaButtonName);

        //TODO: Why this one needs registerReceiver whereas ReceiverPhoneCall does not
        registerReceiver(receiverHeadSetPlugged,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        //Start background service
        //Not yet used but can be used to scan library
        //What is the benefit ??
        //service = new Intent(this, MyService.class);
        //startService(service);

        toggleControls(true);
        toggleConnect(true);
    }

    private void setDimMode(boolean enable) {
        if(enable) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            dimOn();
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            timer.cancel();
            timer.purge();
            setBrightness(-1);
        }
    }

    private void toggleConnect(boolean collapse) {
        if(collapse) {
            collapse(connect);
        } else {
            expand(connect);
        }
    }

    private void toggleControls(boolean collapse) {
        //https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
        if(collapse) {
            collapse(controls);
        } else {
            expand(controls);
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
        stopRemote();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "MainActivity onResume");

        if(!buttonSetDimMode.isChecked()) {
            dimOn();
        }
        else if(!audioPlayer.isPlaying()) {
            enableGUI(false);
            getFromQRcode();
            buttonConnect.performClick();
        }

        //TODO: Check if this solves the issue with buttons
        receiverMediaButtonName = new ComponentName(getPackageName(), ReceiverMediaButton.class.getName());
        audioManager.registerMediaButtonEventReceiver(receiverMediaButtonName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity onDestroy");
        //Better unregister as it does not trigger anyway + raises exceptions if not
        unregisterReceiver(receiverHeadSetPlugged);
        unregisterReceiver(mHeadsetBroadcastReceiver);
        //Note: receiverMediaButtonName remains active if not unregistered
        //but causes issues
        audioManager.unregisterMediaButtonEventReceiver(receiverMediaButtonName);

        audioPlayer.stop(true);
        if(service!=null) {
            stopService(service);
        }

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
            Log.i(TAG, "MainActivity onDestroy: UNEXPECTED InterruptedException");
        }



        if(musicLibrary!=null) {
            musicLibrary.close();
        }
    }

    ProcessAbstract scanLibray;
    ProcessAbstract processBrowseFS;
    ProcessAbstract processBrowseFScount;

    public static File pathToFiles;

    private File getExtSDcard(String path, String search) {
        File f = new File(path);
        File[] files = f.listFiles();

        if (files != null) {
            if(files.length>0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        File checkFolder = new File(file.getAbsolutePath()+File.separator+search);
                        if(checkFolder.exists()) {
                            return checkFolder;
                        }
                    }
                }
            }
        }
        //If not found, use external storage which turns out to be ... internal SD card + internal phone memory
        //filtered and emulated
        // and not the actual external SD card as any could expect
        return new File(Environment.getExternalStorageDirectory()+"/JaMuz");
                //+File.separator+"Android/data/"+BuildConfig.APPLICATION_ID);
    }

    private void scanLibrayInThread() {
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
                                browseFS(pathToFiles);
                            } catch (InterruptedException e) {
                                Log.i(TAG, "Thread.MainActivity.browseFS InterruptedException");
                                scanLibray.abort();
                            }
                        }
                    };
                    processBrowseFS.start();
                    //Get total number of files
                    processBrowseFScount = new ProcessAbstract("Thread.MainActivity.browseFScount") {
                        public void run() {
                            try {
                                browseFScount(pathToFiles);
                            } catch (InterruptedException e) {
                                Log.i(TAG, "Thread.MainActivity.browseFScount InterruptedException");
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
                    List<Track> tracks = musicLibrary.getTracks();
                    nbFiles=0;
                    for(Track track : tracks) {
                        checkAbort();
                        File file = new File(track.getPath());
                        if(!file.exists()) {
                            musicLibrary.deleteTrack(track.getPath());
                        }
                        toastNbFile("JaMuz scan deleted ", 1000);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastLong("Database updated.");
                        }
                    });
                } catch (InterruptedException e) {
                    Log.i(TAG, "Thread.MainActivity.scanLibrayInThread InterruptedException");
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

                                    int id = musicLibrary.getTrack(absolutePath);
                                    if(id>=0) {
                                        Log.v(TAG, "browseFS updateTrack " + absolutePath);
                                        //FIXME: Update if file is modified only:
                                        //based on lastModificationDate and/or size (not on content as longer than updateTrack)
                                        //musicLibrary.updateTrack(id, track, false);
                                    } else {
                                        Log.v(TAG, "browseFS insertTrack " + absolutePath);
                                        musicLibrary.insertTrack(getTrack(file));
                                    }
                                    toastNbFile("JaMuz scan ", 200);
                                }
                            }
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

    private void connectDatabase() {
        musicLibrary = new MusicLibrary(this);
        musicLibrary.open();
    }

    private Track getTrack(File file) {
        String absolutePath=file.getAbsolutePath();

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

        return new Track(-1, rating, title, album, artist, coverHash, absolutePath, genre);
    }


    private void playRandom() {
        //Fill the queue
        if(queue.size()<5) {
            if(musicLibrary!=null) { //Happens before write permission allowed so db not accessed
                List<Track> addToQueue = musicLibrary.getTracks((PlayList) spinner.getSelectedItem());
                queue.addAll(addToQueue);
            }
        }
        //Play a random track
        if(queue.size()>0) {
            int index=new Random().nextInt(queue.size());
            displayedTrack = queue.get(index);
            queue.remove(displayedTrack);
            Log.i(TAG, "playQueue("+(index+1)+"/"+queue.size()+")");
            play();

        } else {
            toastLong("Empty Playlist.");
        }
    }

    private void playHistory() {
        displayedTrack = queueHistory.get(queueHistoryIndex);
        Log.i(TAG, "playHistory("+(queueHistoryIndex+1)+"/"+queueHistory.size()+")");
        playAudio(displayedTrack.getPath());
    }

    private void play() {
        File file = new File(displayedTrack.getPath());
        if(file.exists()) {
            queueHistory.add(displayedTrack);
            queueHistoryIndex = queueHistory.size()-1;
            playAudio(displayedTrack.getPath());
        } else {
            musicLibrary.deleteTrack(displayedTrack.getPath());
            playRandom();
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
            playRandom();
        }
    }

    class CallBackPlayer implements ICallBackPlayer {

        @Override
        public void onPlayBackEnd() {
            if(local) {
                playNext();
            }
        }

        @Override
        public void onPositionChanged(int position, int duration) {
            if(local) {
                setSeekBar(position, duration);
                if ((duration - position) < 5001 && (duration - position) > 4501) {
                    //setBrightness(1);
                    dimOn();
                }
            }
        }

        @Override
        public void doPlayRandom() {
            playRandom();
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
        public void onPlayBackStart() {
            displayTrack();
        }

    }

    public void playAudio(String path){
        localTrack = displayedTrack;
        audioPlayer.stop(false);
        audioPlayer.play(path);
        dimOn();
    }

    //FIXME: Try http://android.okhelp.cz/turn-screen-on-off-android-sample-code/

    private void dim(final boolean on) {
        CountDownTimer countDownTimer = new CountDownTimer(500,50) {
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
            }
        }.start();
    }

    private Timer timer = new Timer();
    private boolean isDimOn = false;
    private void dimOn() {
        editTextConnectInfo.clearFocus();

        if(!buttonSetDimMode.isChecked()) {
            if (!isDimOn) {
                //setBrightness(1);
                dim(true);
                isDimOn = true;
            }
            timer.cancel();
            timer.purge();
            Log.i(TAG, "timerTask cancelled");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "timerTask performed");
                    setBrightness(0);
                    //dim(false);
                    isDimOn = false;
                }
            }, 5 * 1000);
            Log.i(TAG, "timerTask scheduled");
        }
    }

    private void setBrightness(final float brightness) {
        Log.i(TAG, "setBrightness("+brightness+");");
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
                local=enable;
                if(!enable) {
                    buttonConnect.setText("Close");
                } else {
                    setupSpinner(localPlaylists, localSelectedPlaylist);
                }
                editTextConnectInfo.setEnabled(enable);
                buttonConnect.setEnabled(true);
            }
        });
    }

    private void enableGUI(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editTextConnectInfo.setEnabled(enable);
                buttonConnect.setEnabled(enable);
            }
        });
    }

    private void getFromQRcode() {
        String content = getIntent().getDataString();
        if(content!=null) {
            if(!content.equals("")) {
                content=content.substring("JaMuzRemote://".length());
                content=Encryption.decrypt(content, "NOTeBrrhzrtestSecretK");
                editTextConnectInfo.setText(content);
            }
        }
    }

    private static final int REQUEST= 112;

    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS
    };

    public void checkPermissions() {
        if (!hasPermissions(this, PERMISSIONS)) {

            //TODO: Translate
            String msgStr = "<html><b>For a full JaMuz experience</b>, please consider " +
                    "allowing permissions that you will be asked for: <BR/><BR/>" +
                    "<i>- <u>Mutimedia files</u></i> : Needed to be able to read files in \"" +
                    pathToFiles +
                    "\" which is the root of \"SSH Server\" from \"The Olive Tree\" where music files are expected to be.<BR/>" +
                    "It is also needed to store database in JaMuz folder on internal SD card.<BR/><BR/>" +
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
            scanLibrayInThread();
            setupLocalPlaylists();
        }
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
        if(local) {
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
                    //mediaPlayer.setVolume(20, 20);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    dimOn();
                    break;
                case "volDown":
                    //mediaPlayer.setVolume(1, 1);
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
            client.send(msg);

        }
    }

    private void setupLocalPlaylists() {

        String genreCol = "genre"; // TODO: Use musicLibraryDb.COL_GENRE
        String ratingCol = "rating"; // TODO: Use musicLibraryDb.COL_RATING

        //TODO: sync playlists with JaMuz

        localPlaylists.add(localSelectedPlaylist);
        localPlaylists.add(new PlayList("Top", ratingCol + "=5"));
        LinkedHashMap<String, String> genres = new LinkedHashMap();
        if(musicLibrary!=null) { //Happens before write permission allowed so db not accessed
            genres = musicLibrary.getGenres(ratingCol + "=5");
        }

        for(Map.Entry<String, String> entry : genres.entrySet()) {
            localPlaylists.add(new PlayList("Top " + entry.getValue(), genreCol + "=\"" + entry.getKey() + "\" AND " + ratingCol + "=5"));
        }
        String in = getInSqlList(genres);
        if(!in.equals("")) {
            localPlaylists.add(new PlayList("Top Autre", genreCol + " NOT IN ("+in+") AND " + ratingCol + "=5"));
        }
        localPlaylists.add(new PlayList("Discover", genreCol + "!=\"Enfantin\" AND " + ratingCol + "=0"));
        if(musicLibrary!=null) { //Happens before write permission allowed so db not accessed
            genres = musicLibrary.getGenres(ratingCol + "=0");
            in = getInSqlList(genres);
        }
        for(Map.Entry<String, String> entry : genres.entrySet()) {
            localPlaylists.add(new PlayList("Discover "+entry.getValue(), genreCol + "=\""+entry.getKey()+"\" AND " + ratingCol + "=0"));
        }
        if(!genres.containsKey("Enfantin")) {
            localPlaylists.add(new PlayList("Discover Enfantin", genreCol + "=\"Enfantin\" AND " + ratingCol + "=0"));
        }
        if(!in.equals("")) {
            in+=",\"Enfantin\"";
            localPlaylists.add(new PlayList("Discover Autre", genreCol + " NOT IN ("+in+") AND " + ratingCol + "=0"));
        }
        //localPlaylists.add(new PlayList("Empty playlist (test)", genreCol + "=\"TUcroisVRaimentQUEceGENRE" +
         //       "PEUXexister????\" AND " + ratingCol + ">10000000"));

        setupSpinner(localPlaylists, localSelectedPlaylist);
    }

    private String getInSqlList(Map<String, String> list) {
        String in = "";
        if(list.size()>0) {
            for(String entry : list.values()) {
                in+="\""+entry+"\",";
            }
            in = in.substring(0, in.length()-1);
        }
        return in;
    }

    ///TODO: Detect WIFI connection to allow/disallow "Connect" button
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
                // attaching data adapter to spinner
                spinnerSend=false;
                spinner.setAdapter(arrayAdapter);
                spinner.setSelection(arrayAdapter.getPosition(selectedPlaylist));
                spinnerSend=true;
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
        Log.i(TAG, "Toast makeText "+msg);
        Toast.makeText(this, msg, duration).show();
    }

    private void toastNbFile(final String action, final int every) {
        nbFiles++;
        if(((nbFiles-1) % every) == 0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toastShort("#"+(nbFiles)+"/"+nbFilesTotal+" "+action);
                }
            });
        }
    }

    private void setSeekBar(final int currentPosition, final int total) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBar.setMax(total);
                seekBar.setProgress(currentPosition);
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
                    setTextView(textViewReceived, Html.fromHtml("<html><h1>".concat(displayedTrack.toString()).concat("<BR/></h1></html>")), false);
                    ratingBar.setEnabled(false);
                    ratingBar.setRating(displayedTrack.getRating());
                    ratingBar.setEnabled(true);
                }
            });

            if(displayedTrack.getId()>=0) {
                displayImage(displayedTrack.getArt());
            } else {
                displayCover();
            }
        }
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
            client.send("sendCover"+maxWidth);
        }
        displayImage(bitmap);
    }

    private void displayImage(final Bitmap finalBitmap) {
        //final Bitmap finalBitmap = bitmap;
        runOnUiThread(new Runnable() {
            public void run() {
                image.setImageBitmap(finalBitmap);
            }
        });
    }

    private void displayImage(byte[] art) {
        if( art != null ){
            displayImage( BitmapFactory.decodeByteArray(art, 0, art.length));
        }
    }

    class CallBackReception implements ICallBackReception {
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
                            PlayList temp = new PlayList(selectedPlaylist, "");
                            final JSONArray jsonPlaylists = (JSONArray) jObject.get("playlists");
                            final List<PlayList> playlists = new ArrayList<PlayList>();
                            for(int i=0; i<jsonPlaylists.length(); i++) {
                                String playlist = (String) jsonPlaylists.get(i);
                                PlayList playList = new PlayList(playlist, "");
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
                            if(!local) {
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
                                    jObject.getString("genre"));
                            displayTrack();
                            break;
                    }
                } catch (JSONException e) {
                    setTextView(textViewReceived, Html.fromHtml(e.toString()), false);
                }
            }
        }

        @Override
        public void receivedBitmap(final Bitmap bitmap) {
            System.out.println("receivedBitmap: callback");
            System.out.println(bitmap == null ? "null" : bitmap.getWidth() + "x" + bitmap.getHeight());

            if (!coverMap.containsKey(displayedTrack.getCoverHash())) {
                if (bitmap != null) { //Save to cache
                    coverMap.put(displayedTrack.getCoverHash(), bitmap);
                }
            }
            displayCover();
        }

        @Override
        public void disconnected() {
            stopRemote();
        }
    }

    private void stopRemote() {
        if(client!=null) {
            client.close();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableGUI(false);
                buttonConnect.setText("Connect");
                buttonConnect.setEnabled(true);
                editTextConnectInfo.setEnabled(true);
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
            unregisterReceiver(mHeadsetBroadcastReceiver);
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
                    //FIXME: This situation (at least) can endup with other receivers (headsethook at least)
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
