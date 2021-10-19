package phramusca.com.jamuzremote;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActivityAlbums extends AppCompatActivity implements IListenerTrackAdapter {

    private static final int ALBUM_TRACK_REQUEST_CODE = 100;

    private List<Track> albums;
    //private AdapterAlbum adapterAlbum;
    private CursorRecyclerViewAdapter cursorRecyclerViewAdapter;
    private boolean complete;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        Button button_exit_albums = findViewById(R.id.button_exit_albums);
        button_exit_albums.setOnClickListener(v -> onBackPressed());

        albums = new ArrayList<>();
        complete = false;
//        if (addMore()) {
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //adapterAlbum = new AdapterAlbum(this, recyclerView, albums);
            Cursor newAlbums = HelperLibrary.musicLibrary.getAlbums();
            cursorRecyclerViewAdapter = new MyListCursorAdapter(this, newAlbums);
            recyclerView.setAdapter(cursorRecyclerViewAdapter);
            //adapterAlbum.addListener(this); //FIXME NOW add back ?
            //adapterAlbum.setOnLoadListener(new IListenerOnLoad() {
//                @Override
//                public void onLoadMore() {
//                    if (!complete) {
//                        albums.add(null);
//                        adapterAlbum.notifyItemInserted(albums.size() - 1);
//                        new Handler().post(() -> {
//                            int loaderPos = albums.size() - 1;
//                            complete = !addMore();
//                            albums.remove(loaderPos);
//                            adapterAlbum.notifyItemRemoved(loaderPos);
//                            adapterAlbum.setLoaded();
//                        });
//                    }
//                }
//
//                @Override
//                public void onLoadTop() {
//                }
//            });
//        }

        //FIXME NOW add back !
//        new SwipeHelper(this, recyclerView, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
//            @Override
//            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
//
//                underlayButtons.add(new SwipeHelper.UnderlayButton(
//                        ButtonInfo.PLAY,
//                        pos -> {
//                            Track album = albums.get(pos);
//                            insertAndSetResult(album, true);
//                        },
//                        getApplicationContext()));
//
//                underlayButtons.add(new SwipeHelper.UnderlayButton(
//                        ButtonInfo.QUEUE,
//                        pos -> {
//                            Track album = albums.get(pos);
//                            insertAndSetResult(album, false);
//                        },
//                        getApplicationContext()));
//            }
//        };
    }

    private boolean addMore() {
        List<Track> newAlbums = HelperLibrary.musicLibrary.getAlbums(albums.size());
        this.albums.addAll(newAlbums);
        return newAlbums.size() > 0;
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

    private void insertAndSetResult(Track track, boolean playNext) {
        Playlist playlist = new Playlist(track.getAlbum(), true);
        playlist.setAlbum(track.getAlbum());
        PlayQueue.queue.insert(playlist);

        Intent data = new Intent();
        data.putExtra("action", playNext ? "playNextAndDisplayQueue" : "displayQueue");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(Track track, int position) {
        //Get album tracks
        Playlist playlist = new Playlist(track.getAlbum(), true);
        playlist.setAlbum(track.getAlbum());
        ArrayList<Track> tracks = (ArrayList<Track>) playlist.getTracks(new ArrayList<Track.Status>() {
            { add(Track.Status.REC); }
            { add(Track.Status.LOCAL); }
            { add(Track.Status.INFO); }
            { add(Track.Status.NEW); }
            { add(Track.Status.ERROR); }
        });
        //Open album tracks layout
        Intent intent = new Intent(getApplicationContext(), ActivityAlbumTracks.class);
        intent.putExtra("tracksList", tracks);
        startActivityForResult(intent, ALBUM_TRACK_REQUEST_CODE);
    }
}
