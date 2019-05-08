package phramusca.com.jamuzremote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityAlbums extends AppCompatActivity implements AdapterTrack.TrackAdapterListener {

    private static final int ALBUM_TRACK_REQUEST_CODE = 100;
    SwipeActionAdapter swipeActionAdapter;

    private List<Track> albums;
    private AdapterAlbum adapterAlbum; //http://www.devexchanges.info/2017/02/android-recyclerview-dynamically-load.html
    private boolean complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        Button button_exit_albums = findViewById(R.id.button_exit_albums);
        button_exit_albums.setOnClickListener(v -> onBackPressed());

        albums = new ArrayList<>();
        complete=false;
        if(addMore()) {
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
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
        //FIXME !!! Album pagination : SWipe
        
        //swipeActionAdapter = new SwipeActionAdapter(adapterAlbum);
        //swipeActionAdapter.setListView(recyclerView);
        //recyclerView.setAdapter(swipeActionAdapter);
        /*swipeActionAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.queue_slide_play)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.queue_slide_add)
                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.queue_slide_list)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.queue_slide_list);
        swipeActionAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener(){
            @Override
            public boolean hasActions(int position, SwipeDirection direction){
                if(direction.isLeft()) return true; // Change this to false to
                if(direction.isRight()) return false; //disable right swipes
                return false;
            }

            @Override
            public boolean shouldDismiss(int position, SwipeDirection direction){
                // Only dismiss an item when swiping normal left
                return false; //direction == SwipeDirection.DIRECTION_NORMAL_LEFT;
            }

            @Override
            public void onSwipe(int[] positionList, SwipeDirection[] directionList){
                for(int i=0;i<positionList.length;i++) {
                    SwipeDirection direction = directionList[i];
                    int position = positionList[i];
                    Track album = (Track) swipeActionAdapter.getItem(position);
                    switch (direction) {
                        case DIRECTION_FAR_LEFT:
                            insertAndSetResult(album, true);
                            break;
                        case DIRECTION_NORMAL_LEFT:
                            insertAndSetResult(album, false);
                            break;
                    }
                    swipeActionAdapter.notifyDataSetChanged();
                }
            }
        });*/
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
