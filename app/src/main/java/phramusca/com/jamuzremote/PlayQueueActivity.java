package phramusca.com.jamuzremote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ListView;

import java.util.ArrayList;

public class PlayQueueActivity extends AppCompatActivity implements TrackAdapter.TrackAdapterListener {

    TrackAdapter adapter;
    int histSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_queue);
        Intent intent = getIntent();
        final ArrayList<Track> queue = (ArrayList<Track>) intent.getSerializableExtra("queueArrayList");
        if(queue!=null) {
            //Display tracks
            ListView listView = (ListView) findViewById(R.id.list_queue);
            adapter = new TrackAdapter(this, queue);
            adapter.addListener(this);
            listView.setAdapter(adapter);
            //Set "selected"
            int histIndex = intent.getIntExtra("histIndex", 0);
            histSize = intent.getIntExtra("histSize", 0);
            int position = histIndex<=0?0:histIndex;
            listView.setSelection(position);

            //Reads thumbnails in background
            new Thread() {
                @Override
                public void run() {
                    for(Track track : queue) {
                        if(track.getTumb(true)!=null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }.start();
        }
    }

    @Override
    public void onClick(final Track item, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Play ?");
        builder.setMessage(Html.fromHtml(
                "<html>".concat(item.toString()).concat("</html>")));
        builder.setPositiveButton("NOW !", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent data = new Intent();
                data.putExtra("queueItem", false);
                data.putExtra("positionPlay", position);
                data.putExtra("histSize", histSize);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        if(!item.isHistory()) {
            builder.setNegativeButton("AFTER CURRENT.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent data = new Intent();
                    data.putExtra("queueItem", true);
                    data.putExtra("positionPlay", position);
                    data.putExtra("histSize", histSize);
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        }

        builder.setCancelable(true);

        builder.show();
    }
}
