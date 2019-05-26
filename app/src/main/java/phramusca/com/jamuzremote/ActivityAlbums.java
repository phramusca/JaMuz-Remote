package phramusca.com.jamuzremote;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityAlbums extends AppCompatActivity implements AdapterTrack.TrackAdapterListener {

    private static final int ALBUM_TRACK_REQUEST_CODE = 100;

    private List<Track> albums;
    private AdapterAlbum adapterAlbum; //http://www.devexchanges.info/2017/02/android-recyclerview-dynamically-load.html
    private boolean complete;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        Button button_exit_albums = findViewById(R.id.button_exit_albums);
        button_exit_albums.setOnClickListener(v -> onBackPressed());

        albums = new ArrayList<>();
        complete=false;
        if(addMore()) {
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapterAlbum = new AdapterAlbum(this, recyclerView, albums, this);
            recyclerView.setAdapter(adapterAlbum);
            adapterAlbum.addListener(this);
            //set load more listener for the RecyclerView adapter
            adapterAlbum.setOnLoadMoreListener(() -> {
                if (!complete) {
                    albums.add(null);
                    adapterAlbum.notifyItemInserted(albums.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            albums.remove(albums.size() - 1);
                            adapterAlbum.notifyItemRemoved(albums.size());
                            complete=!addMore();
                            adapterAlbum.notifyDataSetChanged();
                            adapterAlbum.setLoaded();
                        }
                    }, 5000);
                } else {
                    Toast.makeText(ActivityAlbums.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        SwipeHelper swipeHelper = new SwipeHelper(this, recyclerView, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "",
                        R.drawable.ic_slide_queue_play,
                        Color.parseColor("#FF3C30"),
                        pos -> {
                            Track album = (Track) albums.get(pos);
                            insertAndSetResult(album, true);
                        },
                        getApplicationContext()));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "",
                        R.drawable.ic_slide_queue_add,
                        Color.parseColor("#FF9502"),
                        pos -> {
                            Track album = (Track)albums.get(pos);
                            insertAndSetResult(album, false);
                        },
                        getApplicationContext()));
            }
        };

    }

    private boolean addMore() {
        List<Track> newAlbums = HelperLibrary.musicLibrary.getAlbums(albums.size());
        this.albums.addAll(newAlbums);
        readCovers(newAlbums);
        return newAlbums.size()>0;
    }

    private void readCovers(List<Track> tracks) {
        new Thread() {
            @Override
            public void run() {
                for(Track track : tracks) {
                    if(track.getTumb(true)!=null) {
                        runOnUiThread(() -> adapterAlbum.notifyDataSetChanged());
                    }
                }
            }
        }.start();
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
        PlayQueue.insert(playlist);

        Intent data = new Intent();
        data.putExtra("action", playNext?"playNextAndDisplayQueue":"displayQueue");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(Track track, int position) {
        //Get album tracks
        Playlist playlist = new Playlist(track.getAlbum(), true);
        playlist.setAlbum(track.getAlbum());
        ArrayList<Track> tracks = (ArrayList<Track>) playlist.getTracks();
        //Open album tracks layout
        Intent intent = new Intent(getApplicationContext(), ActivityAlbumTracks.class);
        intent.putExtra("tracksList", tracks);
        startActivityForResult(intent, ALBUM_TRACK_REQUEST_CODE);
    }
}
