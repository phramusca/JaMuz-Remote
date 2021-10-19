package phramusca.com.jamuzremote;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActivityAlbums extends AppCompatActivity implements IListenerAlbumAdapter {

    private static final int ALBUM_TRACK_REQUEST_CODE = 100;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        Button button_exit_albums = findViewById(R.id.button_exit_albums);
        button_exit_albums.setOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Cursor newAlbums = HelperLibrary.musicLibrary.getAlbums();
        AlbumCursorAdapter listCursorAdapter = new AlbumCursorAdapter(this, newAlbums);
        recyclerView.setAdapter(listCursorAdapter);
        listCursorAdapter.addListener(this);

        new SwipeHelper(this, recyclerView, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        ButtonInfo.PLAY,
                        pos -> {
                            AlbumListItem albumListItem = listCursorAdapter.getAlbumListItem(pos);
                            insertAndSetResult(albumListItem.getAlbum(), true);
                        },
                        getApplicationContext()));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        ButtonInfo.QUEUE,
                        pos -> {
                            AlbumListItem albumListItem = listCursorAdapter.getAlbumListItem(pos);
                            insertAndSetResult(albumListItem.getAlbum(), false);
                        },
                        getApplicationContext()));
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ALBUM_TRACK_REQUEST_CODE && resultCode == RESULT_OK) {
            //Redirects intent as-is from ActivityAlbumTracks to ActivityMain
            setResult(RESULT_OK, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void insertAndSetResult(String album, boolean playNext) {
        Playlist playlist = new Playlist(album, true);
        playlist.setAlbum(album);
        PlayQueue.queue.insert(playlist);

        Intent data = new Intent();
        data.putExtra("action", playNext ? "playNextAndDisplayQueue" : "displayQueue");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(AlbumListItem albumListItem) {
        //Get album tracks
        Playlist playlist = new Playlist(albumListItem.getAlbum(), true);
        playlist.setAlbum(albumListItem.getAlbum());
        ArrayList<Track> tracks = (ArrayList<Track>) playlist.getTracks(new ArrayList<Track.Status>() {
            {
                add(Track.Status.REC);
                add(Track.Status.LOCAL);
                add(Track.Status.INFO);
                add(Track.Status.NEW);
                add(Track.Status.ERROR);
            }
        });
        //Open album tracks layout
        Intent intent = new Intent(getApplicationContext(), ActivityAlbumTracks.class);
        intent.putExtra("tracksList", tracks);
        startActivityForResult(intent, ALBUM_TRACK_REQUEST_CODE);
    }
}
