package phramusca.com.jamuzremote;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ActivityAlbumTracks extends AppCompatActivity {

    AdapterCursorAlbumTrack adapterCursorAlbumTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_tracks);

        Button button_exit_albums = findViewById(R.id.button_exit_album_tracks);
        button_exit_albums.setOnClickListener(v -> onBackPressed());

        TextView title = findViewById(R.id.album_tracks_title);

        Intent intent = getIntent();
        final String album = (String) intent.getSerializableExtra("album"); //NON-NLS
        String searchQuery = intent.getStringExtra("searchQuery");

        Playlist playlist = new Playlist(album, true);
        playlist.setAlbum(album);
        Cursor cursor = playlist.getTracks();

        Track track = null;
        if (cursor != null && cursor.moveToPosition(0)) {
            track = HelperLibrary.musicLibrary.cursorToTrack(cursor, false);
        }
        if (track == null) {
            return;
        }

        Button button_queue_album = findViewById(R.id.button_queue_album);
        Track finalTrack = track;
        button_queue_album.setOnClickListener(v -> insertAndSetResult(finalTrack, false));

        Button button_download = findViewById(R.id.button_download);
        button_download.setOnClickListener(v -> {
            for (int i = 0; i < adapterCursorAlbumTrack.getItemCount(); i++) {
                Track track1 = adapterCursorAlbumTrack.getTrack(i);
                if (Arrays.asList(Track.Status.INFO, Track.Status.ERROR).contains(track1.getStatus())) {
                    downloadFile(track1, i);
                }
            }
        });

        Button button_queue_play_album = findViewById(R.id.button_queue_play_album);
        button_queue_play_album.setOnClickListener(v -> insertAndSetResult(finalTrack, true));

        RecyclerView recyclerView = findViewById(R.id.list_album_tracks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterCursorAlbumTrack = new AdapterCursorAlbumTrack(this, cursor, searchQuery);
        recyclerView.setAdapter(adapterCursorAlbumTrack);

        title.setText(finalTrack.getAlbum());

        new SwipeHelper(this, recyclerView, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        ButtonInfo.PLAY,
                        pos -> {
                            Track track = adapterCursorAlbumTrack.getTrack(pos);
                            insertAndSetResult(track, true, pos);
                        },
                        getApplicationContext()));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        ButtonInfo.QUEUE,
                        pos -> {
                            Track track = adapterCursorAlbumTrack.getTrack(pos);
                            insertAndSetResult(track, false, pos);
                        },
                        getApplicationContext()));
            }
        };
    }

    private void insertAndSetResult(Track track, boolean playNext) {
        Playlist playlist = new Playlist(track.getAlbum(), true);
        playlist.setAlbum(track.getAlbum());
        PlayQueue.queue.insert(playlist);

        Intent data = new Intent();
        data.putExtra("action", playNext ? "playNextAndDisplayQueue" : "displayQueue");
        setResult(RESULT_OK, data);
        finish();
    }

    private void insertAndSetResult(Track track, boolean playNext, int position) {
        if (Arrays.asList(Track.Status.REC, Track.Status.LOCAL).contains(track.getStatus())) {
            //Insert in queue
            PlayQueue.queue.insert(track);
            Intent data = new Intent();
            data.putExtra("action", playNext ? "playNextAndDisplayQueue" : "displayQueue");
            setResult(RESULT_OK, data);
            finish();
        } else if (Arrays.asList(Track.Status.INFO, Track.Status.ERROR).contains(track.getStatus())) {
            downloadFile(track, position);
        }
    }

    private void downloadFile(Track track, int position) {
        track.getTags(true);
        track.setStatus(Track.Status.NEW);
        adapterCursorAlbumTrack.updateStatus(track.getStatus(), position);
        HelperToast helperToast = new HelperToast(getApplicationContext());
        ClientInfo clientInfo = ActivityMain.getClientInfo(ClientCanal.SYNC, helperToast);
        ServiceSync.DownloadTask downloadTask = new ServiceSync.DownloadTask(track, track1 -> updateStatus(track1.getStatus(), position), clientInfo);
        downloadTask.start();
    }

    private void updateStatus(Track.Status status, int position) {
        runOnUiThread(() -> {
            adapterCursorAlbumTrack.updateStatus(status, position);
        });
    }
}
