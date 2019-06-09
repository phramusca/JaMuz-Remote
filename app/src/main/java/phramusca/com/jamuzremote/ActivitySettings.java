package phramusca.com.jamuzremote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import static phramusca.com.jamuzremote.StringManager.trimTrailingWhitespace;

public class ActivitySettings extends AppCompatActivity {

    private static final String TAG = ActivitySettings.class.getName();
    private EditText editTextConnectInfo;
    private SharedPreferences preferences;
    private IntentIntegrator qrScan;
    private TextView textViewPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getPreferences(MODE_PRIVATE);

        Button button_exit_settings = findViewById(R.id.button_exit_settings);
        button_exit_settings.setOnClickListener(v -> onBackPressed());

        editTextConnectInfo = findViewById(R.id.editText_info);

        editTextConnectInfo.setText(preferences.getString("connectionString", "192.168.0.11:2013"));

        Button buttonSaveConnectionString = findViewById(R.id.button_save_connectionString);
        buttonSaveConnectionString.setOnClickListener(view ->
                setConfig("connectionString", editTextConnectInfo.getText().toString())
        );

        qrScan = new IntentIntegrator(this);
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
        seekBarReplayGain.setProgress(70); //FIXME: Save to preferences !
        seekBarReplayGain.setMax(100);
        seekBarReplayGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = ((float)progress / 100.0f);
                Log.i(TAG, "seekBarReplayGain: "+value);

                Intent data = new Intent();
                data.putExtra("volume", value);
                setResult(RESULT_OK, data);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        Spinner kidsplaceLimitPlaylist = findViewById(R.id.kidsplaceLimitPlaylist);

        CheckBox kidsplaceLimit = findViewById(R.id.kidsplaceLimit);
        kidsplaceLimit.setOnCheckedChangeListener(
                (buttonView, isChecked) -> kidsplaceLimitPlaylist.setEnabled(isChecked));

    }

    private void setConfig(String id, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(id, value);
        editor.apply();
    }
}
