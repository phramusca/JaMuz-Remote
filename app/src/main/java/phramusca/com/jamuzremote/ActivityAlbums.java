package phramusca.com.jamuzremote;

import android.content.Intent;
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
    private AdapterAlbum adapterAlbum;
    private boolean complete;
    private boolean completeTop;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        Button button_exit_albums = findViewById(R.id.button_exit_albums);
        button_exit_albums.setOnClickListener(v -> onBackPressed());

        albums = new ArrayList<>();
        complete=false;
        completeTop=false;
        if(addMore()) {
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapterAlbum = new AdapterAlbum(this, recyclerView, albums);
            recyclerView.setAdapter(adapterAlbum);
            adapterAlbum.addListener(this);
            adapterAlbum.setOnLoadListener(new OnLoadListener() {
                @Override
                public void onLoadMore() {
                    if (!complete) {
                        albums.add(null);
                        adapterAlbum.notifyItemInserted(albums.size() - 1);
                        new Handler().post(() -> {
                            int loaderPos = albums.size() - 1;
                            complete=!addMore();
                            albums.remove(loaderPos);
                            adapterAlbum.notifyDataSetChanged();
                            adapterAlbum.setLoaded();
                        });
                    } else {
                        Toast.makeText(ActivityAlbums.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onLoadTop() {
                   /* if(!completeTop) {
                        albums.add(0, null);
                        adapterAlbum.notifyItemInserted(0);
                        recyclerView.getLayoutManager().scrollToPosition(0);
                        new Handler().postDelayed(() -> {
                            int nbAdded = addTop();
                            completeTop=nbAdded<=0;
                            albums.remove(nbAdded);
                            adapterAlbum.notifyDataSetChanged();
                            adapterAlbum.setLoadedTop();
                        }, 4000);
                    } else {
                        Toast.makeText(ActivityAlbums.this, "Loading data ON TOP completed", Toast.LENGTH_SHORT).show();
                    }*/
                }
            });
        }

        SwipeHelper swipeHelper = new SwipeHelper(this, recyclerView, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        ButtonInfo.PLAY,
                        pos -> {
                            Track album = (Track) albums.get(pos);
                            insertAndSetResult(album, true);
                        },
                        getApplicationContext()));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        ButtonInfo.QUEUE,
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

    private int addTop() {
        List<Track> newAlbums = HelperLibrary.musicLibrary.getAlbums(albums.size());
        this.albums.addAll(0, newAlbums);
        readCovers(newAlbums);
        return newAlbums.size();
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
