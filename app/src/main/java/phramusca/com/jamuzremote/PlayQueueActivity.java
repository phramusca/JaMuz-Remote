package phramusca.com.jamuzremote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class PlayQueueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_queue);
        Intent intent = getIntent();
        ArrayList<Track> queue = (ArrayList<Track>) intent.getSerializableExtra("queue");
        if(queue!=null) {
            ListView listView = (ListView) findViewById(R.id.list_queue);
            TrackAdapter adapter = new TrackAdapter(this, queue);
            listView.setAdapter(adapter);
        }
    }
}
