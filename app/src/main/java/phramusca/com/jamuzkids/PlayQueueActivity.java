package phramusca.com.jamuzkids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.Button;
import android.widget.ListView;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;

public class PlayQueueActivity extends AppCompatActivity {

    TrackAdapter trackAdapter;
    SwipeActionAdapter trackSwipeAdapter;
    private int offset=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_queue);

        Button button_exit_queue = (Button) findViewById(R.id.button_exit_queue);
        button_exit_queue.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        @SuppressWarnings("unchecked")
        final ArrayList<Track> queue = (ArrayList<Track>) intent.getSerializableExtra("queueArrayList");

        if(queue!=null) {
            int position = intent.getIntExtra("queueArrayPosition", 0);
            offset = intent.getIntExtra("queueArrayOffset", 0);
            position = position - offset;
            ListView listView = (ListView) findViewById(R.id.list_queue);
            trackAdapter = new TrackAdapter(this, queue, position);
            trackSwipeAdapter = new SwipeActionAdapter(trackAdapter);
            trackSwipeAdapter.setListView(listView);
            listView.setAdapter(trackSwipeAdapter);
            listView.setSelection(position);
            trackSwipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
                    .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.row_bg_right_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);
            trackSwipeAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener(){
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
                        Track track = (Track) trackSwipeAdapter.getItem(position);
                        switch (direction) {
                            case DIRECTION_FAR_LEFT:
                                confirmPlayNext(track, position+offset);
                                break;
                            case DIRECTION_NORMAL_LEFT:
                                PlayQueue.insertNext(position+offset);
                                trackAdapter.insertNext(position);
                                break;
                            case DIRECTION_FAR_RIGHT:
                                confirmRemoval(track, position);
                                break;
                            case DIRECTION_NORMAL_RIGHT:
                                PlayQueue.moveDown(position+offset);
                                trackAdapter.moveDown(position);
                                break;
                        }
                        trackSwipeAdapter.notifyDataSetChanged();
                    }
                }
            });
            //Reads thumbnails in background
            new Thread() {
                @Override
                public void run() {
                    for(Track track : queue) {
                        if(track.getTumb(true)!=null) {
                            runOnUiThread(() -> trackAdapter.notifyDataSetChanged());
                        }
                    }
                }
            }.start();
        }
    }

    private void confirmPlayNext(Track track, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(phramusca.com.jamuzkids.R.string.confirmPlayNow);
        builder.setMessage(Html.fromHtml(
                "<html>".concat(track.toString()).concat("</html>")));
        builder.setPositiveButton(phramusca.com.jamuzkids.R.string.confirmYes, (dialog, which) -> {
            if(PlayQueue.insertNext(position)) {
                Intent data = new Intent();
                data.putExtra("queueItem", false);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        builder.setNegativeButton(phramusca.com.jamuzkids.R.string.confirmNo, (dialog, which) -> {
        });
        builder.setCancelable(true);
        builder.show();
    }

    private void confirmRemoval(Track track, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(phramusca.com.jamuzkids.R.string.confirmRemove);
        builder.setMessage(Html.fromHtml(
                "<html>".concat(track.toString()).concat("</html>")));
        builder.setPositiveButton(phramusca.com.jamuzkids.R.string.confirmYes, (dialog, which) -> {
            PlayQueue.remove(position+offset);
            trackAdapter.remove(position);
            trackAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton(phramusca.com.jamuzkids.R.string.confirmNo, (dialog, which) -> {
        });
        builder.setCancelable(true);
        builder.show();
    }
}
