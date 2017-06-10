package phramusca.com.jamuzremote;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    private Client client;
    private Track displayedTrack;
    private Map coverMap = new HashMap();
    private static final String TAG = "=====> JaMuz";
    private Intent service; //Not yet used
    private AudioManager audioManager; //Used to set volume

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

    private Button setButton(Button button, final int buttonName, final String msg) {
        button = (Button) findViewById(buttonName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(local) {
                    switch (msg) {
                        case "previousTrack":
                            playRandom();
                            break;
                        case "nextTrack":
                            playRandom();
                            break;
                        case "playTrack":
                            if(mp==null) {
                                playRandom();
                            }
                            else if(mp.isPlaying()) {
                                mp.pause();
                                stopTimer();
                            } else {
                                mp.start();
                                startTimer();
                            }
                            break;
                        case "pullup":
                            mp.seekTo(0);
                            break;
                        case "rewind":
                            mp.seekTo(mp.getCurrentPosition()-mp.getDuration()/10);
                            break;
                        case "forward":
                            mp.seekTo(mp.getCurrentPosition()+mp.getDuration()/10);
                            break;
                        case "volUp":
                            //mp.setVolume(20, 20);
                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                            break;
                        case "volDown":
                            //mp.setVolume(1, 1);
                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                            break;
                        default:
                            //Popup("Error", "Not implemented");
                            toast("Not implemented");
                            break;
                    }

                } else {
                    client.send(msg);
                }
            }
        });
        return button;
    }

    private void toast(final String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "MainActivity onCreate");
        setContentView(R.layout.activity_main);

        //TODO: Make this an option AND alow a timeout
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textViewReceived = (TextView) findViewById(R.id.textView_conv);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                if(fromUser) { //as it is also set when server sends file info (and it can be 0)
                    Log.i(TAG, "ratingBar Disabled");
                    ratingBar.setEnabled(false);
                    Log.i(TAG, "ratingBar setIndeterminate");
                    ratingBar.setIndeterminate(true);
                    Log.i(TAG, "displayedTrack setRating "+Math.round(rating));
                    displayedTrack.setRating(Math.round(rating));
                    if(local) {
                        Log.i(TAG, "musicLibrary updateTrack "+displayedTrack.getRating());
                        musicLibrary.updateTrack(displayedTrack.getId(), displayedTrack, true);
                        Log.i(TAG, "ratingBar setEnabled");
                        ratingBar.setEnabled(true);
                        Log.i(TAG, "ratingBar UNset Indeterminate");
                        ratingBar.setIndeterminate(false);
                    } else {
                        Log.i(TAG, "client send setRating"+displayedTrack.getRating());
                        client.send("setRating".concat(String.valueOf(Math.round(rating))));
                    }
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
                if(spinnerSend) {
                    client.send("setPlaylist".concat((String) parent.getItemAtPosition(pos)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Another interface callback

            }
        });

        buttonPrevious = setButton(buttonPrevious, R.id.button_previous, "previousTrack");
        buttonPlay = setButton(buttonPlay, R.id.button_play, "playTrack");
        buttonNext = setButton(buttonNext, R.id.button_next, "nextTrack");
        buttonRewind = setButton(buttonRewind, R.id.button_rewind, "rewind");
        buttonPullup = setButton(buttonPullup, R.id.button_pullup, "pullup");
        buttonForward = setButton(buttonForward, R.id.button_forward, "forward");
        buttonVolUp = setButton(buttonVolUp, R.id.button_volUp, "volUp");
        buttonVolDown = setButton(buttonVolDown, R.id.button_volDown, "volDown");

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
                //textViewReceived.setText("");
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
                    closeRemote();
                }
            }
        });

        enableGUI(false);
        getFromQRcode();
        editTextConnectInfo.setEnabled(true);
        buttonConnect.setEnabled(true);

        verifyStoragePermissions(this);

        new Thread() {
            public void run() {

                connectDatabase();
                browseFS(new File("/storage/3515-1C15/Android/data/com.theolivetree.sshserver/files/"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast("Database updated.");
                    }
                });

            }
        }.start();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        service = new Intent(this, MyService.class);
        startService(service);

        //browseLibrary(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        //browseLibrary(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);

        //if(songs.size()>0) {
        //    for(Song song : songs) {
//
        //    }
        //}

    }

    private MusicLibrary musicLibrary;

    private void connectDatabase() {
        musicLibrary = new MusicLibrary(this);
        musicLibrary.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "MainActivity onPause");
        closeRemote();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "MainActivity onResume");
        getFromQRcode();
        if(mp==null || !mp.isPlaying()) {
            buttonConnect.performClick();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity onDestroy");
        stopMediaPlayer(true);
        stopService(service);
        musicLibrary.close();
    }

    private void browseLibrary(Uri songUri){
        ContentResolver contentResolver = getContentResolver();
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst())
        {
            int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                long currentId = songCursor.getLong(songId);
                String currentTitle = songCursor.getString(songTitle);
                String currentAlbum = songCursor.getString(songAlbum);
                String currentArtist = songCursor.getString(songArtist);
                String currentData = songCursor.getString(songData);

                //songs.add(new Song(currentId, currentData, currentTitle, currentAlbum, currentArtist));
            } while(songCursor.moveToNext());
        }
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

                            int rating = -1;
                            String coverHash="";

                            Track track = new Track(-1, rating, title, album, artist, coverHash, absolutePath, genre);

                            int id = musicLibrary.getTrack(absolutePath);
                            if(id>=0) {
                                Log.d(TAG, "browseFS updateTrack " + absolutePath);
                                musicLibrary.updateTrack(id, track, false);
                            } else {
                                Log.d(TAG, "browseFS insertTrack " + absolutePath);
                                musicLibrary.insertTrack(track);
                            }


                        }
                    }
                }
            }
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void playRandom() {
        stopMediaPlayer(false);
        Random generator = new Random();

        List<Track> songs = musicLibrary.getTracks();

        int index = generator.nextInt(songs.size());
        displayedTrack = songs.get(index);
        playAudio(displayedTrack.getPath());
        displayTrack();
    }

    MediaPlayer mp;
    CountDownTimer timer;

    public void playAudio(String path){
        try {
            mp = new MediaPlayer();
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
            startTimer();

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(local) {
                        buttonNext.performClick();
                    }
                }
            });

            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    startTimer();
                }
            });

            //FIXME: MAke this an option
            //mp.setScreenOnWhilePlaying(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        Log.d(TAG, "CountDownTimer startTimer");
        timer = new CountDownTimer(mp.getDuration()-mp.getCurrentPosition()-1,500) {
            @Override
            public void onTick(long millisUntilFinished_) {
                if(mp!=null) {
                    Log.d(TAG, "CountDownTimer onTick "+mp.getCurrentPosition()/100+"/"+mp.getDuration()/1000);
                    setSeekBar(mp.getCurrentPosition(), mp.getDuration());
                }
                if(mp==null || !mp.isPlaying()) {
                    Log.d(TAG, "CountDownTimer onTick NOT isPlaying => cancel");
                    this.cancel();
                }
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "CountDownTimer onFinish");
            }
        }.start();
    }

    private void stopTimer() {
        if(timer!=null) {
            Log.d(TAG, "CountDownTimer cancel");
            timer.cancel();
            timer=null;
        }
    }

    private boolean local = true;

    private void enableConnect(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                local=enable;
                if(!enable) {
                    buttonConnect.setText("Close");
                    stopMediaPlayer(false);
                }
                editTextConnectInfo.setEnabled(enable);
                buttonConnect.setEnabled(true);
            }
        });
    }

    private void stopMediaPlayer(boolean release) {
        if (mp!=null && mp.isPlaying()) {
            mp.stop();
            if(release) {
                mp.release();
            }
            setSeekBar(0, 1);
        }
        stopTimer();
    }

    private void enableGUI(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editTextConnectInfo.setEnabled(enable);
                buttonConnect.setEnabled(enable);
                buttonPrevious.setEnabled(enable);
                buttonPlay.setEnabled(true);
                buttonNext.setEnabled(true);
                buttonRewind.setEnabled(true);
                buttonPullup.setEnabled(true);
                buttonForward.setEnabled(true);
                buttonVolUp.setEnabled(true);
                buttonVolDown.setEnabled(true);
                Log.d(TAG, "ratingBar setEnabled");
                ratingBar.setEnabled(true);

                //seekBar.setEnabled(enable);
                spinner.setEnabled(enable);
                //if(!enable) {
                //    spinner.setAdapter(null);
                 //   ImageView image = (ImageView) findViewById(R.id.imageView);
                 //   image.setImageResource(0);
                //}
            }
        });
    }

    private void setupSpinner(final List<String> playlists, final String selectedPlaylist) {
        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, playlists);
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

    private void displayTrack() {
        if(displayedTrack!=null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTextView(textViewReceived, Html.fromHtml("<html><h1>".concat(displayedTrack.toString()).concat("<BR/></h1></html>")), false);
                    Log.i(TAG, "ratingBar UNset Indeterminate");
                    ratingBar.setIndeterminate(false);
                    Log.i(TAG, "ratingBar setRating "+displayedTrack.getRating());
                    ratingBar.setRating(displayedTrack.getRating());
                    Log.i(TAG, "ratingBar setEnabled");
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

    public void Popup(final String title, final String msg) {
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



    class CallBackReception implements ICallBackReception {
        @Override
        public void received(final String msg) {
            if(msg.startsWith("MSG_")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //textViewReceived.setText(msg);
                            //Popup("Error", msg);
                            toast(msg);
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
                            final JSONArray jsonPlaylists = (JSONArray) jObject.get("playlists");
                            final List<String> playlists = new ArrayList<String>();
                            for(int i=0; i<jsonPlaylists.length(); i++) {
                                String playlist = (String) jsonPlaylists.get(i);
                                playlists.add(playlist);
                            }
                            setupSpinner(playlists, selectedPlaylist);
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
            closeRemote();
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

    //FIXME: Battery consumption
    //- Close connection & dim after a delay (in settings)
    //- Eventually simulate position set (seekBar) and sync only every x (say 10) seconds.

    //FIXME: Make a Settings activity

    private void closeRemote() {
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
}
