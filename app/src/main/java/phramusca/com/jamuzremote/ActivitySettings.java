package phramusca.com.jamuzremote;

import static phramusca.com.jamuzremote.StringManager.trimTrailingWhitespace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.util.ArrayList;

public class ActivitySettings extends AppCompatActivity {

    private static final String TAG = ActivitySettings.class.getName();
    private EditText editTextConnectInfo;
    private SharedPreferences preferences;
    private IntentIntegrator qrScan;
    private TextView textViewPath;
    private static final int QR_REQUEST_CODE = 49374;
    private static final int DIRECTORY_REQUEST_CODE = 591534;

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
        textViewPath.setText(trimTrailingWhitespace(Html.fromHtml("<html>"
                .concat(userPath)
                .concat("</html>"))));
        Button dirChooserButton = findViewById(R.id.button_browse);

        dirChooserButton.setOnClickListener(v -> {
            final Intent chooserIntent = new Intent(getApplicationContext(), DirectoryChooserActivity.class);
            DirectoryChooserConfig.Builder builder = DirectoryChooserConfig.builder()
                    .newDirectoryName("DirChooserSample")
                    .allowReadOnlyDirectory(true)
                    .allowNewDirectoryNameModification(true);
            if(!userPath.equals("/")) {
                builder.initialDirectory(userPath);
            }
            chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, builder.build());
            startActivityForResult(chooserIntent, DIRECTORY_REQUEST_CODE);
        });

        SeekBar seekBarReplayGain = findViewById(R.id.seekBarReplayGain);
        seekBarReplayGain.setProgress(preferences.getInt("baseVolume", 70));
        seekBarReplayGain.setMax(100);
        seekBarReplayGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "seekBarReplayGain: " + progress); //NON-NLS
                setConfig("baseVolume", progress);
                Intent data = new Intent();
                data.putExtra("volume", progress); //NON-NLS
                setResult(RESULT_OK, data);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        CheckBox kidsplaceAllowAddNewDel = findViewById(R.id.settingsCheckBoxKidsPlaceAllowAddNewDel);
        kidsplaceAllowAddNewDel.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        setConfig("kidsplaceAllowAddNewDel", isChecked)
        );
        kidsplaceAllowAddNewDel.setChecked(
                preferences.getBoolean("kidsplaceAllowAddNewDel", false));

        Spinner kidsplaceLimitPlaylist = findViewById(R.id.kidsPlaceLimitPlaylist);
        CheckBox kidsplaceLimit = findViewById(R.id.settingsCheckBoxKidsPlaceLimit);
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
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayList<Parcelable> localPlaylists = getIntent()
                .getParcelableArrayListExtra("localPlaylists");
        ArrayAdapter playListArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                localPlaylists);
        kidsplaceLimitPlaylist.setAdapter(playListArrayAdapter);

        String selectedPlaylist = preferences.getString("kidsplaceLimitPlaylist", null);
        if (selectedPlaylist != null) {
            kidsplaceLimitPlaylist.setSelection(playListArrayAdapter.getPosition(
                    new Playlist(selectedPlaylist, true)));
        }

        boolean isKidsPlaceLimit = preferences.getBoolean("kidsplaceLimit", false);
        kidsplaceLimit.setChecked(isKidsPlaceLimit);
        kidsplaceLimitPlaylist.setEnabled(isKidsPlaceLimit);

        CheckBox kidsplaceOnStartup = findViewById(R.id.settingsCheckBoxKidsPlaceOnStartup);
        kidsplaceOnStartup.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        setConfig("kidsplaceOnStartup", isChecked)
        );
        kidsplaceOnStartup.setChecked(
                preferences.getBoolean("kidsplaceOnStartup", false));

        CheckBox kidsplaceAllowEdition = findViewById(R.id.settingsCheckBoxKidsPlaceAllowEdition);
        kidsplaceAllowEdition.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        setConfig("kidsplaceAllowEdition", isChecked)
        );
        kidsplaceAllowEdition.setChecked(
                preferences.getBoolean("kidsplaceAllowEdition", false));

        ActivityMain.SpeechFlavor speechFavor = ActivityMain.SpeechFlavor.valueOf(preferences.getString("speechFavor", ActivityMain.SpeechFlavor.PAUSE.name()));
        switch (speechFavor) {
            case NONE:
                ((RadioButton)findViewById(R.id.settingsRadioSpeechNone)).setChecked(true);
                break;
            case LOWER_VOLUME:
                ((RadioButton)findViewById(R.id.settingsRadioSpeechLowerVolume)).setChecked(true);
                break;
            case PAUSE:
                ((RadioButton)findViewById(R.id.settingsRadioSpeechPause)).setChecked(true);
                break;
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        ActivityMain.SpeechFlavor speechFlavor = ActivityMain.SpeechFlavor.PAUSE;
        switch(view.getId()) {
            case R.id.settingsRadioSpeechLowerVolume:
                if(checked)
                    speechFlavor= ActivityMain.SpeechFlavor.LOWER_VOLUME;
                break;
            case R.id.settingsRadioSpeechPause:
                if(checked)
                    speechFlavor= ActivityMain.SpeechFlavor.PAUSE;
                break;
            case R.id.settingsRadioSpeechNone:
                if(checked)
                    speechFlavor= ActivityMain.SpeechFlavor.NONE;
                break;
        }
        setConfig("speechFavor", speechFlavor.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QR_REQUEST_CODE && resultCode == RESULT_OK) {
            //https://www.simplifiedcoding.net/android-qr-code-scanner-tutorial/
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result == null || result.getContents() == null) {
                Toast.makeText(this, getString(R.string.settingsServerToastProblemQR), Toast.LENGTH_LONG).show();
            } else {
                data.putExtra("QRcode", result.getContents());
                setResult(RESULT_OK, data);
                finish();
            }
        } else if (requestCode == DIRECTORY_REQUEST_CODE
                        && resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
            String chosenDir = data
                    .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
            textViewPath.setText(trimTrailingWhitespace(Html.fromHtml("<html>"
                .concat(chosenDir)
                .concat("</html>"))));
            setConfig("userPath", chosenDir);
            Intent dataAction = new Intent();
            dataAction.putExtra("action", "checkPermissionsThenScanLibrary"); //NON-NLS
            setResult(RESULT_OK, dataAction);
            finish();
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
