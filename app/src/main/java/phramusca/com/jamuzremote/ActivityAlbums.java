package phramusca.com.jamuzremote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;

public class ActivityAlbums extends AppCompatActivity {

    AdapterAlbum trackAdapter;
    SwipeActionAdapter swipeActionAdapter;

    //FIXME !!! Implement pagination for albums
    //https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView
    private int offset=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        Button button_exit_albums = (Button) findViewById(R.id.button_exit_albums);
        button_exit_albums.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        @SuppressWarnings("unchecked")
        final ArrayList<Track> albums = (ArrayList<Track>) intent.getSerializableExtra("albumArrayList");

        if(albums!=null) {
            int position = intent.getIntExtra("albumArrayPosition", 0);
            offset = intent.getIntExtra("albumArrayOffset", 0);
            position = position - offset;
            ListView listView = (ListView) findViewById(R.id.list_albums);
            trackAdapter = new AdapterAlbum(this, albums, position);
            swipeActionAdapter = new SwipeActionAdapter(trackAdapter);
            swipeActionAdapter.setListView(listView);
            listView.setAdapter(swipeActionAdapter);
            listView.setSelection(position);
            swipeActionAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
                    .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.row_bg_right_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);
            swipeActionAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener(){
                @Override
                public boolean hasActions(int position, SwipeDirection direction){
                    /*if(direction.isLeft()) return true; // Change this to false to disable left swipes
                    if(direction.isRight()) return true;
                    return false;*/
                    return true;
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
                        Track track = (Track) swipeActionAdapter.getItem(position);
                        switch (direction) {
                            case DIRECTION_FAR_LEFT:
                                //FIXME !!!!
                                break;
                            case DIRECTION_NORMAL_LEFT:
                                trackAdapter.insertNext(position);
                                break;
                            case DIRECTION_FAR_RIGHT:
                                //FIXME !!!!
                                break;
                            case DIRECTION_NORMAL_RIGHT:
                                PlayQueue.moveDown(position+offset);
                                trackAdapter.moveDown(position);
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
                    for(Track track : albums) {
                        if(track.getTumb(true)!=null) {
                            runOnUiThread(() -> trackAdapter.notifyDataSetChanged());
                        }
                    }
                }
            }.start();
        }
    }

}
