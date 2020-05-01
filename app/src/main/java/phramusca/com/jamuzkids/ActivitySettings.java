package phramusca.com.jamuzkids;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import static phramusca.com.jamuzkids.StringManager.trimTrailingWhitespace;

public class ActivitySettings extends AppCompatActivity {

    private static final String TAG = ActivitySettings.class.getName();
    private EditText editTextConnectInfo;
    private SharedPreferences preferences;
    private IntentIntegrator qrScan;
    private TextView textViewPath;
    private static final int QR_REQUEST_CODE = 49374;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Button button_exit_settings = findViewById(R.id.button_exit_settings);
        button_exit_settings.setOnClickListener(v -> onBackPressed());

        editTextConnectInfo = findViewById(R.id.editText_info);

        editTextConnectInfo.setText(preferences.getString("connectionString", "192.168.0.11:2013"));

        Button buttonSaveConnectionString = findViewById(R.id.button_save_connectionString);
        buttonSaveConnectionString.setOnClickListener(view ->
                setConfig("connectionString", editTextConnectInfo.getText().toString())
        );

        qrScan = new IntentIntegrator(this);
        qrScan.setRequestCode(QR_REQUEST_CODE);
        Button button_scan_QR = findViewById(R.id.button_scan_QR);
        button_scan_QR.setOnClickListener(view ->
                qrScan.initiateScan()
        );

        textViewPath = findViewById(R.id.textViewPath);

        String userPath = preferences.getString("userPath", "/");
        String display = userPath.equals("/")?
                getString(R.string.pathInfo)
                :userPath;
        textViewPath.setText(trimTrailingWhitespace(Html.fromHtml("<html>"
                .concat(display)
                .concat("</html>"))));
        Button dirChooserButton = findViewById(R.id.button_browse);
        dirChooserButton.setOnClickListener(new View.OnClickListener()
        {
            private boolean m_newFolderEnabled = false;
            @Override
            public void onClick(View v)
            {
                DirectoryChooserDialog directoryChooserDialog =
                        new DirectoryChooserDialog(ActivitySettings.this,
                                chosenDir -> {
                                    textViewPath.setText(trimTrailingWhitespace(Html.fromHtml("<html>"
                                            .concat(chosenDir)
                                            .concat("</html>"))));
                                    setConfig("userPath", chosenDir);
                                    Intent data = new Intent();
                                    data.putExtra("action", "checkPermissionsThenScanLibrary");
                                    setResult(RESULT_OK, data);
                                    finish();
                                });
                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                directoryChooserDialog.chooseDirectory(preferences.getString("userPath", "/"));
            }
        });

        SeekBar seekBarReplayGain = findViewById(R.id.seekBarReplayGain);
        seekBarReplayGain.setProgress(preferences.getInt("baseVolume", 70));
        seekBarReplayGain.setMax(100);
        seekBarReplayGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "seekBarReplayGain: "+progress);
                setConfig("baseVolume", progress);
                Intent data = new Intent();
                data.putExtra("volume", progress);
                setResult(RESULT_OK, data);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        CheckBox kidsplaceAllowAddNewDel = findViewById(R.id.kidsplaceAllowAddNewDel);
        kidsplaceAllowAddNewDel.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        setConfig("kidsplaceAllowAddNewDel", isChecked)
        );
        kidsplaceAllowAddNewDel.setChecked(
                preferences.getBoolean("kidsplaceAllowAddNewDel", false));

        Spinner kidsplaceLimitPlaylist = findViewById(R.id.kidsplaceLimitPlaylist);
        CheckBox kidsplaceLimit = findViewById(R.id.kidsplaceLimit);
        kidsplaceLimit.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                {
                    kidsplaceLimitPlaylist.setEnabled(isChecked);
                    kidsplaceAllowAddNewDel.setEnabled(!isChecked);
                    setConfig("kidsplaceLimit", isChecked);
                }
        );
        kidsplaceLimitPlaylist.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                Playlist playlist = (Playlist) parent.getItemAtPosition(pos);
                setConfig("kidsplaceLimitPlaylist", playlist.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ArrayList<Parcelable> localPlaylists = getIntent()
                .getParcelableArrayListExtra("localPlaylists");
        ArrayAdapter playListArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                localPlaylists);
        kidsplaceLimitPlaylist.setAdapter(playListArrayAdapter);

        String selectedPlaylist = preferences.getString("kidsplaceLimitPlaylist", null);
        if(selectedPlaylist!=null) {
            kidsplaceLimitPlaylist.setSelection(playListArrayAdapter.getPosition(
                    new Playlist(selectedPlaylist, true)));
        }

        boolean isKidsPlaceLimit = preferences.getBoolean("kidsplaceLimit", false);
        kidsplaceLimit.setChecked(isKidsPlaceLimit);
        kidsplaceLimitPlaylist.setEnabled(isKidsPlaceLimit);

        CheckBox kidsplaceOnStartup = findViewById(R.id.kidsplaceOnStartup);
        kidsplaceOnStartup.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        setConfig("kidsplaceOnStartup", isChecked)
        );
        kidsplaceOnStartup.setChecked(
                preferences.getBoolean("kidsplaceOnStartup", false));

        CheckBox kidsplaceAllowEdition = findViewById(R.id.kidsplaceAllowEdition);
        kidsplaceAllowEdition.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        setConfig("kidsplaceAllowEdition", isChecked)
        );
        kidsplaceAllowEdition.setChecked(
                preferences.getBoolean("kidsplaceAllowEdition", false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QR_REQUEST_CODE && resultCode == RESULT_OK) {
            //https://www.simplifiedcoding.net/android-qr-code-scanner-tutorial/
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result == null || result.getContents() == null) {
                Toast.makeText(this, "Problem reading QR code", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "QR code: "+result.getContents(), Toast.LENGTH_LONG).show();
                data.putExtra("QRcode", result.getContents());
                setResult(RESULT_OK, data);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void setConfig(String id, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(id, value);
        editor.apply();
    }

    private void setConfig(String id, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(id, value);
        editor.apply();
    }

    private void setConfig(String id, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(id, value);
        editor.apply();
    }
}
