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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_queue);
        Intent intent = getIntent();
        final ArrayList<Track> queue = (ArrayList<Track>) intent.getSerializableExtra("queue");
        if(queue!=null) {
            //Display tracks
            ListView listView = (ListView) findViewById(R.id.list_queue);
            adapter = new TrackAdapter(this, queue);
            adapter.addListener(this);
            listView.setAdapter(adapter);

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
    public void onClick(final Track item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Play ?");
        builder.setMessage(Html.fromHtml(
                "<html>".concat(item.toString()).concat("</html>")));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent data = new Intent();

                //FIXME: BitMap (thumb) not serializable !
                //+ only Need to send index in history or queue

                //data.putExtra("track", item);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}
