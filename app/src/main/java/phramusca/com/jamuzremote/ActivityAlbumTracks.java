package phramusca.com.jamuzremote;

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

        if(tracks!=null && tracks.size()>0) {
            Button button_queue_album = findViewById(R.id.button_queue_album);
            button_queue_album.setOnClickListener(v -> insertAndSetResult(tracks.get(0), false));

            Button button_queue_play_album = findViewById(R.id.button_queue_play_album);
            button_queue_play_album.setOnClickListener(v -> insertAndSetResult(tracks.get(0), true));

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
                                insertAndSetResult(track, true, pos);
                            },
                            getApplicationContext()));

                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            ButtonInfo.QUEUE,
                            pos -> {
                                Track track = tracks.get(pos);
                                insertAndSetResult(track, false, pos);
                            },
                            getApplicationContext()));
                }
            };
        }
    }

    private void insertAndSetResult(Track track, boolean playNext) {
        Playlist playlist = new Playlist(track.getAlbum(), true);
        playlist.setAlbum(track.getAlbum());
        PlayQueue.queue.insert(playlist);

        Intent data = new Intent();
        data.putExtra("action", playNext?"playNextAndDisplayQueue":"displayQueue");
        setResult(RESULT_OK, data);
        finish();
    }

    private void insertAndSetResult(Track track, boolean playNext, int pos) {
        if(!track.getStatus().equals(Track.Status.INFO)) {
            PlayQueue.queue.insert(track);
            Intent data = new Intent();
            data.putExtra("action", playNext?"playNextAndDisplayQueue":"displayQueue");
            setResult(RESULT_OK, data);
            finish();
        } else {
            track.getTags(true);
            track.setStatus(Track.Status.NEW);
            trackAdapter.notifyItemChanged(pos);
            HelperToast helperToast = new HelperToast(getApplicationContext());
            ClientInfo clientInfo = ActivityMain.getClientInfo(ClientCanal.SYNC, helperToast);
            ServiceSync.DownloadTask downloadTask = new ServiceSync.DownloadTask(track, track1 -> notifyDownload(track, pos), clientInfo);
            downloadTask.start();
        }
    }

    private void notifyDownload(Track track, int pos) {
        runOnUiThread(() -> trackAdapter.notifyItemChanged(pos));
    }
}
