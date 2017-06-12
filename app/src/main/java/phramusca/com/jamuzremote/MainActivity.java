package phramusca.com.jamuzremote;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "=====> JaMuz";
    private Client client;
    private Track displayedTrack;
    private Map coverMap = new HashMap();
    private Intent service; //Not yet used
    private AudioManager audioManager; //Used to set volume
    private MusicLibrary musicLibrary;

    private int nbFiles=0;
    private int nbFilesTotal = 0;
    private List<Track> queue = new ArrayList<>();
    MediaPlayer mediaPlayer;
    CountDownTimer timer;
    private boolean local = true;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // GUI elements
    private TextView textViewReceived; //textView_conv
    private EditText editTextConnectInfo; //editText_info
    private Button buttonConnect; //button_connect
    private Button buttonPrevious; //button_previous
    private Button buttonPlay; //button_play
    private Button buttonNext; //button_next
    private Button buttonRewind; //button_rewind
    private Button buttonPullup; //button_pullup
    private Button buttonForward; //button_forward
    private Button buttonVolUp;
    private Button buttonVolDown;
    private SeekBar seekBar;
    private Spinner spinner;
    private boolean spinnerSend=true;
    private RatingBar ratingBar;

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
                        queue = musicLibrary.getTracks(item);
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

        buttonPrevious = setupButton(buttonPrevious, R.id.button_previous, "previousTrack");
        buttonPlay = setupButton(buttonPlay, R.id.button_play, "playTrack");
        buttonNext = setupButton(buttonNext, R.id.button_next, "nextTrack");
        buttonRewind = setupButton(buttonRewind, R.id.button_rewind, "rewind");
        buttonPullup = setupButton(buttonPullup, R.id.button_pullup, "pullup");
        buttonForward = setupButton(buttonForward, R.id.button_forward, "forward");
        buttonVolUp = setupButton(buttonVolUp, R.id.button_volUp, "volUp");
        buttonVolDown = setupButton(buttonVolDown, R.id.button_volDown, "volDown");

        editTextConnectInfo = (EditText) findViewById(R.id.editText_info);
        buttonConnect = (Button) findViewById(R.id.button_connect);

        textViewReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                }
            }
        });

        setupLocalPlaylists();

        enableGUI(false);
        getFromQRcode();
        editTextConnectInfo.setEnabled(true);
        buttonConnect.setEnabled(true);

        checkStoragePermissions(this);

        scanLibrayInThread();

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

        //https://stackoverflow.com/questions/32220498/registermediabuttoneventreceiver-alternative-setmediabuttonreceiver-pendinginte

        /*
        MediaSession mSession =  new MediaSession(this, getPackageName());
        Intent intent = new Intent(this, HeadsetReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setMediaButtonReceiver(pintent);
        mSession.setActive(true);
        mediaHandler.postDelayed(this, 1000L);
        */

        ComponentName rec = new ComponentName(getPackageName(), MediaButtonIntentReceiver.class.getName());
        audioManager.registerMediaButtonEventReceiver(rec);

        //registerReceiver(mMediaButtonReceiver,
        //       new IntentFilter(Intent.ACTION_MEDIA_BUTTON));


        //TODO: No more needed if above work
        //as it does work only if application is active
        takeKeyEvents(true);

        //TODO: Make this an option AND alow a timeout
        //Used until MediaButtonReceiver is finally implemented
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Start background service
        //Not yet used but can be used to scan library
        //What is the benefit ??
        service = new Intent(this, MyService.class);
        startService(service);
    }

    //FIXME: How to use this below instead of MediaButtonIntentReceiver
    protected BroadcastReceiver mMediaButtonReceiver = new BroadcastReceiver()
    {
        private static final String TAG = "JaMuz ButtonReceiver";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            KeyEvent keyEvent = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            String keyExtraEvent = KeyEvent.keyCodeToString(keyEvent.getKeyCode());

            int action = keyEvent.getAction();
            if (action == KeyEvent.ACTION_UP) {
                Log.i(TAG, intent.getAction()+" : "+keyExtraEvent);
                //doAction("playTrack");
                onKeyUp(keyEvent.getKeyCode(), keyEvent);
            }

        }
    };


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
        getFromQRcode();
        if(mediaPlayer ==null || !mediaPlayer.isPlaying()) {
            buttonConnect.performClick();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity onDestroy");
        stopMediaPlayer(true);
        stopService(service);
        musicLibrary.close();
    }

    private void scanLibrayInThread() {
        new Thread() {
            public void run() {

                connectDatabase();
                nbFiles=0;
                nbFilesTotal=0;
                final String path = "/storage/3515-1C15/Android/data/com.theolivetree.sshserver/files/";

                //Scan android filesystem for files
                Thread bfs = new Thread() {
                    public void run() {
                        browseFS(new File(path));
                    }
                };
                bfs.start();
                //Get total number of files
                Thread count = new Thread() {
                    public void run() {
                        browseFScount(new File(path));
                    }
                };
                count.start();

                try {
                    bfs.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    count.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Scan deleted files
                //FIXME: No need to check what scanned previously ...
                List<Track> tracks = musicLibrary.getTracks();
                nbFiles=0;
                for(Track track : tracks) {
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

            }
        }.start();
    }

    private void connectDatabase() {
        musicLibrary = new MusicLibrary(this);
        musicLibrary.open();
    }

    private void browseFS(File path) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
                if(files.length>0) {
                    for (File file : files) {
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

    private void browseFScount(File path) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
                if(files.length>0) {
                    for (File file : files) {
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
        stopMediaPlayer(false);

        if(queue.size()<5) {
            List<Track> addToQueue =musicLibrary.getTracks((PlayList) spinner.getSelectedItem());
            queue.addAll(addToQueue);
        }

        if(queue.size()>0) {
            Random generator = new Random();
            int index = generator.nextInt(queue.size());
            displayedTrack = queue.get(index);
            queue.remove(displayedTrack);
            File file = new File(displayedTrack.getPath());
            if(file.exists()) {
                playAudio(displayedTrack.getPath());
                displayTrack();
            } else {
                musicLibrary.deleteTrack(displayedTrack.getPath());
                playRandom();
            }

        } else {
            toastLong("Empty Playlist.");
        }
    }

    public void playAudio(String path){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            startTimer();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(local) {
                        buttonNext.performClick();
                    }
                }
            });

            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    startTimer();
                }
            });

            //FIXME: MAke this an option
            //mediaPlayer.setScreenOnWhilePlaying(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(mediaPlayer.getDuration()- mediaPlayer.getCurrentPosition()-1,500) {
            @Override
            public void onTick(long millisUntilFinished_) {
                if(mediaPlayer !=null) {
                    setSeekBar(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                }
                if(mediaPlayer ==null || !mediaPlayer.isPlaying()) {
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

    private void stopMediaPlayer(boolean release) {
        if (mediaPlayer !=null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            if(release) {
                mediaPlayer.release();
            }
            setSeekBar(0, 1);
        }
        stopTimer();
    }

    private void enableConnect(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                local=enable;
                if(!enable) {
                    buttonConnect.setText("Close");
                    stopMediaPlayer(false);
                } else {
                    //FIXME: Should not recreate playlists if were already good
                    //should replace only if we were connected but disconnected
                    setupLocalPlaylists();
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
                buttonPrevious.setEnabled(enable);
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

    public static void checkStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
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
                    playRandom();
                    break;
                case "nextTrack":
                    playRandom();
                    break;
                case "playTrack":
                    if(mediaPlayer ==null) {
                        playRandom();
                    }
                    else if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        stopTimer();
                    } else {
                        mediaPlayer.start();
                        startTimer();
                    }
                    break;
                case "pullup":
                    mediaPlayer.seekTo(0);
                    break;
                case "rewind":
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()- mediaPlayer.getDuration()/10);
                    break;
                case "forward":
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+ mediaPlayer.getDuration()/10);
                    break;
                case "volUp":
                    //mediaPlayer.setVolume(20, 20);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    break;
                case "volDown":
                    //mediaPlayer.setVolume(1, 1);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    break;
                default:
                    //Popup("Error", "Not implemented");
                    toastLong("Not implemented");
                    break;
            }

        } else {
            client.send(msg);
        }
    }

    private void setupLocalPlaylists() {
        final List<PlayList> playlists = new ArrayList<PlayList>();

        String genreCol = "genre"; // TODO: Use musicLibraryDb.COL_GENRE
        String ratingCol = "rating"; // TODO: Use musicLibraryDb.COL_RATING

        PlayList all = new PlayList("All", null);

        playlists.add(all);
        playlists.add(new PlayList("Discover", ratingCol + "=0"));
        playlists.add(new PlayList("Top", ratingCol + "=5"));
        playlists.add(new PlayList("Top Reggae", genreCol + "=\"Reggae\" AND " + ratingCol + "=5"));
        playlists.add(new PlayList("Top Rock", genreCol + "=\"Rock\" AND " + ratingCol + "=5"));

        setupSpinner(playlists, all);
    }

    ///FIXME: Detect WIFI connection to allow/disallow "Connect" button
    //https://stackoverflow.com/questions/5888502/how-to-detect-when-wifi-connection-has-been-established-in-android

    private boolean checkConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    private void setupSpinner(final List<PlayList> playlists, final PlayList selectedPlaylist) {
        final ArrayAdapter<PlayList> arrayAdapter =
                new ArrayAdapter<PlayList>(this, android.R.layout.simple_spinner_item, playlists);
        // Drop down layout style - list view with radio button
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

    public void popup(final String title, final String msg) {
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
                ImageView image = (ImageView) findViewById(R.id.imageView);
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
                            setSeekBar(currentPosition, total);
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

    //FIXME: Battery consumption
    //- Close connection & dim after a delay (in settings)
    //- Eventually simulate position set (seekBar) and sync only every x (say 10) seconds.

    //FIXME: Make a Settings activity

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
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                Log.d(TAG, "KEYCODE_MEDIA_PREVIOUS");
                toastShort("MEDIA_PREVIOUS : Play/Pause");
                //doAction("previousTrack");
                doAction("playTrack");

                return true;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                Log.d(TAG, "KEYCODE_MEDIA_NEXT");
                toastShort("MEDIA_NEXT : Next Track");
                doAction("nextTrack");

                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                Log.d(TAG, "KEYCODE_MEDIA_PLAY_PAUSE");
                toastShort("PLAY_PAUSE : Play/Pause");
                doAction("playTrack");
                //TODO: Not triggered or not available on my nissan
                return true;

            case KeyEvent.KEYCODE_HEADSETHOOK:
                //Play/Pause on Wired HeadSet
                Log.d(TAG, "KEYCODE_HEADSETHOOK");
                toastShort("HEADSETHOOK : Play/Pause");
                doAction("playTrack");
                return true;

            case KeyEvent.KEYCODE_MEDIA_PLAY:
                Log.d(TAG, "KEYCODE_MEDIA_PLAY");
                toastShort("MEDIA_PLAY : Play/Pause");
                doAction("playTrack");
                return true;

            case KeyEvent.KEYCODE_MEDIA_STOP:
                Log.d(TAG, "KEYCODE_MEDIA_STOP");
                toastShort("MEDIA_STOP : Play/Pause");
                doAction("playTrack");
                return true;

            default:
                String msg = keyCode+": "+String.valueOf(event.getKeyCode());
                Log.d(TAG, msg);
                toastShort(msg);
                return super.onKeyUp(keyCode, event);
        }
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

    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothHeadset mBluetoothHeadset;
    //protected BluetoothDevice mConnectedHeadset;

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

            registerReceiver(mHeadsetBroadcastReceiver,
                    new IntentFilter(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED));
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
                    Log.d(TAG, "BT connected. Waiting 4s");
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(mediaPlayer ==null) {
                        Log.d(TAG, "BT: playRandom()");
                        playRandom();
                    }
                    else if(!mediaPlayer.isPlaying()) {
                        Log.d(TAG, "BT: mediaPlayer.start()");
                        mediaPlayer.start();
                        startTimer();
                    }
                    else  {
                        Log.d(TAG, "BT: Already playing");
                    }
                }
                else if (state == BluetoothHeadset.STATE_DISCONNECTED)
                {
                    Log.d(TAG, "BT DISconnected");
                    if(mediaPlayer!=null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        stopTimer();
                    }
                }
            }
            else // BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED
            {
                //FIXME: Never reached. Something wrong
                //Check again with https://stackoverflow.com/questions/20398581/handle-bluetooth-headset-clicks-action-voice-command-and-action-web-search-on

                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_AUDIO_DISCONNECTED);
                if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED)
                {
                    Log.d(TAG, "BT AUDIO connected");

                }
                else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED)
                {
                    Log.d(TAG, "BT AUDIO DISconnected");

                }
            }
        }
    };
}
