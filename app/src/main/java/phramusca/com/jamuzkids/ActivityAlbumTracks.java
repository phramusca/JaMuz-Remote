package phramusca.com.jamuzkids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;

public class ActivityAlbumTracks extends AppCompatActivity {

    AdapterAlbumTrack trackAdapter;
    SwipeActionAdapter swipeActionAdapter;

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
            ListView listView = findViewById(R.id.list_album_tracks);
            trackAdapter = new AdapterAlbumTrack(this, tracks, -1);
            title.setText(tracks.get(0).getAlbum());
            swipeActionAdapter = new SwipeActionAdapter(trackAdapter);
            swipeActionAdapter.setListView(listView);
            listView.setAdapter(swipeActionAdapter);
            swipeActionAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.queue_slide_play)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.queue_slide_add);
            swipeActionAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener(){
                @Override
                public boolean hasActions(int position, SwipeDirection direction){
                    if(direction.isLeft()) return true;
                    if(direction.isRight()) return false; //Disabling right swipes
                    return false;
                }

                @Override
                public boolean shouldDismiss(int position, SwipeDirection direction){
                    return false; //direction == SwipeDirection.DIRECTION_NORMAL_LEFT;
                }

                @Override
                public void onSwipe(int[] positionList, SwipeDirection[] directionList){
                    for(int i=0;i<positionList.length;i++) {
                        SwipeDirection direction = directionList[i];
                        int position = positionList[i];
                        Track track = (Track) swipeActionAdapter.getItem(position);
                        switch (direction) {
                            case DIRECTION_FAR_LEFT:
                                insertAndSetResult(track, true);
                                break;
                            case DIRECTION_NORMAL_LEFT:
                                insertAndSetResult(track, false);
                                break;
                        }
                        swipeActionAdapter.notifyDataSetChanged();
                    }
                }
            });
            //Reads thumbnails in background
            new Thread() {
                @Override
                public void run() {
                    for(Track track : tracks) {
                        if(track.getTumb(true)!=null) {
                            runOnUiThread(() -> trackAdapter.notifyDataSetChanged());
                        }
                    }
                }
            }.start();
        }
    }

    private void insertAndSetResult(Track track, boolean playNext) {
        PlayQueue.insert(track);

        Intent data = new Intent();
        data.putExtra("action", playNext?"playNextAndDisplayQueue":"displayQueue");
        setResult(RESULT_OK, data);
        finish();
    }
}
