package phramusca.com.jamuzkids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ActivityAlbumTracks extends AppCompatActivity {

    AdapterAlbumTrack trackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_tracks);

        Button button_exit_albums = findViewById(R.id.button_exit_album_tracks);
        button_exit_albums.setOnClickListener(v -> onBackPressed());

        TextView title = findViewById(R.id.album_tracks_title);

        Intent intent = getIntent();
        @SuppressWarnings("unchecked")
        final ArrayList<Track> tracks = (ArrayList<Track>) intent.getSerializableExtra("tracksList");

        if(tracks!=null) {
            RecyclerView recyclerView = findViewById(R.id.list_album_tracks);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            trackAdapter = new AdapterAlbumTrack(this, tracks, -1, recyclerView) {
                @Override
                List<Track> getMore() {
                    return new ArrayList<>();
                }

                @Override
                List<Track> getTop() {
                    return new ArrayList<>();
                }
            };
            title.setText(tracks.get(0).getAlbum());

            new SwipeHelper(this, recyclerView, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
                @Override
                public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            ButtonInfo.PLAY,
                            pos -> {
                                Track track = tracks.get(pos);
                                insertAndSetResult(track, true);
                            },
                            getApplicationContext()));

                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            ButtonInfo.QUEUE,
                            pos -> {
                                Track track = tracks.get(pos);
                                insertAndSetResult(track, false);
                            },
                            getApplicationContext()));
                }
            };
        }
    }

    private void insertAndSetResult(Track track, boolean playNext) {
        PlayQueue.queue.insert(track);

        Intent data = new Intent();
        data.putExtra("action", playNext?"playNextAndDisplayQueue":"displayQueue");
        setResult(RESULT_OK, data);
        finish();
    }
}
