package phramusca.com.jamuzremote;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Client client;
    private TextView textViewReceived; //textView_conv
    private EditText editTextConnectInfo; //editText_info
    private Button buttonConnect; //button_connect

    private Button buttonPrevious; //button_previous
    private Button buttonPlay; //button_play
    private Button buttonNext; //button_next
    private Button buttonRewind; //button_rewind
    private Button buttonPullup; //button_pullup
    private Button buttonForward; //button_forward

    private SeekBar seekBar;
    private Spinner spinner;
    private boolean spinnerSend=true;

    private Button setButton(Button button, int buttonName, final String msg) {
        button = (Button) findViewById(buttonName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.send(msg);
            }
        });
        return button;
    }

    private RatingBar ratingBar;

    // See: http://developer.android.com/reference/android/app/Activity.html#ActivityLifecycle
    @Override
    protected void onPause() {
        super.onPause();
        close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: Re-connect
        buttonConnect.performClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Make this an option AND alow a timeout
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textViewReceived = (TextView) findViewById(R.id.textView_conv);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                if(fromUser) { //as it is also set when server sends file info (and it can be 0)
                    ratingBar.setEnabled(false);
                    ratingBar.setIndeterminate(true);
                    client.send("setRating".concat(String.valueOf(Math.round(rating))));
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

        editTextConnectInfo = (EditText) findViewById(R.id.editText_info);
        buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableGUI(false);
                textViewReceived.setText("");
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

                    client = new Client(address, port, "tata", "tata", callBackReception);

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
                    close();
                }
            }
        });

        enableGUI(false);
        editTextConnectInfo.setEnabled(true);
        buttonConnect.setEnabled(true);
    }

    private void enableConnect(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!enable) {
                    buttonConnect.setText("Close");
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

                buttonPrevious.setEnabled(enable);  //button_previous
                buttonPlay.setEnabled(enable);  //button_play
                buttonNext.setEnabled(enable);  //button_next
                buttonRewind.setEnabled(enable);  //button_rewind
                buttonPullup.setEnabled(enable);  //button_pullup
                buttonForward.setEnabled(enable);  //button_forward
                ratingBar.setEnabled(enable);
                //seekBar.setEnabled(enable);

                spinner.setEnabled(enable);
                if(!enable) {
                    spinner.setAdapter(null);
                    ImageView image = (ImageView) findViewById(R.id.imageView);
                    image.setImageResource(0);
                }
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

    class CallBackReception implements ICallBackReception {
        @Override
        public void received(final String msg) {
            if(msg.startsWith("MSG_")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewReceived.setText(msg);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setMax(total);
                                    seekBar.setProgress(currentPosition);
                                }
                            });
                            break;
                        case "fileInfoInt":
                            final int rating = jObject.getInt("rating");
                            String title = jObject.getString("title");
                            String album = jObject.getString("album");
                            String artist = jObject.getString("artist");
                            final String msgHTML = "<b>" + title + "</b><BR/>" + //NOI18N
                                    "<i>" + album + "</i><BR/>" + //NOI18N
                                    "" + artist + ""; //NOI18N

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewReceived.setText(Html.fromHtml("<html><h1>".concat(msgHTML).concat("</h1></html>")));
                                    ratingBar.setIndeterminate(false);
                                    ratingBar.setRating(rating);
                                    ratingBar.setEnabled(true);
                                }
                            });
                            break;
                    }
                } catch (JSONException e) {
                    setTextView(textViewReceived, Html.fromHtml(e.toString()), false);
                }
            }
        }

        @Override
        public void receivedBitmap(final Bitmap bitmap) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView image = (ImageView) findViewById(R.id.imageView);
                    image.setImageBitmap(bitmap);
                }
            });
        }

        @Override
        public void disconnected() {
            close();
        }
    }

    //FIXME: Battery consumption
    //- Close connection & dim after a delay (in settings)
    //- Eventually simulate position set (seekBar) and sync only every x (say 10) seconds.

    //FIXME: Select source playlist (and refresh if not done by default) and add a "Clear queue" button (no, make it auto, better)

    //FIXME: Make a Settings activity

    private void close() {
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
