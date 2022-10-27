package phramusca.com.jamuzremote;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class ActivitySettings extends AppCompatActivity {

    private static final String TAG = ActivitySettings.class.getName();
    private EditText editTextConnectInfo;
    private SharedPreferences preferences;
    private IntentIntegrator qrScan;
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
        qrScan.setOrientationLocked(true);
        qrScan.setPrompt(getString(R.string.qrScanPromptMessage));
        qrScan.setCaptureActivity(ActivityCapturePortrait.class);
        Button button_scan_QR = findViewById(R.id.button_scan_QR);
        button_scan_QR.setOnClickListener(view ->
                qrScan.initiateScan()
        );

        Button buttonRefresh = findViewById(R.id.button_refresh);
        buttonRefresh.setOnClickListener(v -> {
            if (!isMyServiceRunning(ServiceScan.class)) {
                Intent service = new Intent(getApplicationContext(), ServiceScan.class);
                service.putExtra("getAppDataPath", HelperFile.getAudioRootFolder());
                service.putExtra("forceRefresh", true);
                startService(service);
            }
        });

        CheckBox displayServer = findViewById(R.id.settingsCheckBoxDisplayServer);
        displayServer.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    setConfig("displayServer", isChecked);
                    RepoAlbums.reset();
                    //TODO: Remove tracks from queue that do not match Activity.getScope()
                }
        );
        displayServer.setChecked(preferences.getBoolean("displayServer", true));

        CheckBox displayMediaStore = findViewById(R.id.settingsCheckBoxDisplayMediaStore);
        displayMediaStore.setOnCheckedChangeListener(
                (buttonView, isChecked) ->  {
                    setConfig("displayMediaStore", isChecked);
                    RepoAlbums.reset();
                    //TODO: Remove tracks from queue that do not match Activity.getScope()
                }
        );
        displayMediaStore.setChecked(preferences.getBoolean("displayMediaStore", true));

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

        ActivityMain.AudioOutput audioOutput = ActivityMain.AudioOutput.valueOf(preferences.getString("audioOutput", ActivityMain.AudioOutput.LOCAL.name()));
        switch (audioOutput) {
            case LOCAL:
                ((RadioButton)findViewById(R.id.settingsRadioAudioOutputLocal)).setChecked(true);
                break;
            case RASPBERRY:
                ((RadioButton)findViewById(R.id.settingsRadioAudioOutputRaspberry)).setChecked(true);
                break;
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

    public void onAudioOutputRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        ActivityMain.AudioOutput audioOutput = ActivityMain.AudioOutput.LOCAL;
        switch(view.getId()) {
            case R.id.settingsRadioAudioOutputLocal:
                if(checked)
                    audioOutput= ActivityMain.AudioOutput.LOCAL;
                break;
            case R.id.settingsRadioAudioOutputRaspberry:
                if(checked)
                    audioOutput= ActivityMain.AudioOutput.RASPBERRY;
                break;
        }
        setConfig("audioOutput", audioOutput.name());
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
