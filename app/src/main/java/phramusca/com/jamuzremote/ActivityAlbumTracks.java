package phramusca.com.jamuzremote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

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
        final String idPath = (String) intent.getSerializableExtra("idPath"); //NON-NLS
        String searchQuery = intent.getStringExtra("searchQuery");

        Playlist playlist = new Playlist(idPath, true);
        playlist.setIdPath(idPath);
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
            List<Track> newTracks = new ArrayList<>();
            List<Track.Status> statuses = Arrays.asList(Track.Status.INFO, Track.Status.ERROR);
            for (int i = 0; i < adapterCursorAlbumTrack.getItemCount(); i++) {
                Track track1 = adapterCursorAlbumTrack.getTrack(i);
                if (statuses.contains(track1.getStatus())) {
                    track1.getTags(true);
                    track1.setStatus(Track.Status.NEW);
                    adapterCursorAlbumTrack.updateStatus(track1.getStatus(), i, "");
                    newTracks.add(track1);
                }
            }
            startDownloads(newTracks);
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

    private ProcessDownload processDownload;

    private void startDownloads(List<Track> newTracks) {
        if ((processDownload == null || !processDownload.isAlive()) && newTracks.size() > 0) {
            //Log.i(TAG, "START ProcessDownload"); //NON-NLS
            OkHttpClient clientDownload = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
            NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            HelperNotification helperNotification = new HelperNotification(PendingIntent.getActivity(getApplicationContext(), 1, getIntent(), PendingIntent.FLAG_UPDATE_CURRENT), mNotifyManager);
            HelperToast helperToast = new HelperToast(getApplicationContext());
            ClientInfo clientInfo = ActivityMain.getClientInfo(ClientCanal.SYNC, helperToast);
            processDownload = new ProcessDownload("ActivityAlbumTracks.ProcessDownload", newTracks, getApplicationContext(), helperNotification, clientInfo, clientDownload);
            processDownload.start();
        }
    }

    private void insertAndSetResult(Track track, boolean playNext) {
        Playlist playlist = new Playlist(track.getAlbum(), true);
        playlist.setIdPath(track.getIdPath());
        PlayQueue.queue.insert(playlist);

        Intent data = new Intent();
        data.putExtra("action", playNext ? "playNextAndDisplayQueue" : "displayQueue"); //NON-NLS
        setResult(RESULT_OK, data);
        finish();
    }

    private void insertAndSetResult(Track track, boolean playNext, int position) {
        if (Arrays.asList(Track.Status.REC, Track.Status.LOCAL).contains(track.getStatus())) {
            //Insert in queue
            PlayQueue.queue.insert(track);
            Intent data = new Intent();
            data.putExtra("action", playNext ? "playNextAndDisplayQueue" : "displayQueue"); //NON-NLS
            setResult(RESULT_OK, data);
            finish();
        } else if (Arrays.asList(Track.Status.INFO, Track.Status.ERROR).contains(track.getStatus())) {
            List<Track> list = new ArrayList<>();
            list.add(track);
            startDownloads(list);
        }
    }

    private void updateStatus(Track.Status status, int position, String msg) {
        runOnUiThread(() -> {
            adapterCursorAlbumTrack.updateStatus(status, position, msg);
        });
    }
}
