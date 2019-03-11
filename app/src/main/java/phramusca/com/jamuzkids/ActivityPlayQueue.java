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

public class ActivityPlayQueue extends AppCompatActivity implements AdapterTrack.TrackAdapterListener {

    AdapterTrack trackAdapter;
    SwipeActionAdapter trackSwipeAdapter;
    private int offset=0;
    private static final int QUEUE_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        Button button_exit_queue = findViewById(R.id.button_exit_queue);
        button_exit_queue.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        @SuppressWarnings("unchecked")
        final ArrayList<Track> queue = (ArrayList<Track>) intent.getSerializableExtra("queueArrayList");

        if(queue!=null) {
            int position = intent.getIntExtra("queueArrayPosition", 0);
            offset = intent.getIntExtra("queueArrayOffset", 0);
            position = position - offset;
            ListView listView = findViewById(R.id.list_queue);
            trackAdapter = new AdapterTrack(this, queue, position);
            trackAdapter.addListener(this);
            trackSwipeAdapter = new SwipeActionAdapter(trackAdapter);
            trackSwipeAdapter.setListView(listView);
            listView.setAdapter(trackSwipeAdapter);
            listView.setSelection(position);
            trackSwipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.queue_slide_play)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.queue_slide_add)
                    .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.queue_slide_remove)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.queue_slide_down);
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
                                PlayQueue.insert(position+offset);
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
        builder.setTitle(R.string.confirmPlayNow);
        builder.setMessage(Html.fromHtml(
                "<html>".concat(track.toString()).concat("</html>")));
        builder.setPositiveButton(R.string.confirmYes, (dialog, which) -> {
            if(PlayQueue.insert(position)) {
                Intent intent = new Intent();
                intent.putExtra("action", "playNext");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.confirmNo, (dialog, which) -> {
        });
        builder.setCancelable(true);
        builder.show();
    }

    private void confirmRemoval(Track track, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmRemove);
        builder.setMessage(Html.fromHtml(
                "<html>".concat(track.toString()).concat("</html>")));
        builder.setPositiveButton(R.string.confirmYes, (dialog, which) -> {
            PlayQueue.remove(position+offset);
            trackAdapter.remove(position);
            trackAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton(R.string.confirmNo, (dialog, which) -> {
        });
        builder.setCancelable(true);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QUEUE_REQUEST_CODE && resultCode == RESULT_OK) {
            //Redirects intent as-is from ActivityAlbumTracks to ActivityMain
            setResult(RESULT_OK, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(final Track track, final int position) {

        //Get album tracks
        Playlist playlist = new Playlist(track.getAlbum(), true);
        playlist.setAlbum(track.getAlbum());
        ArrayList<Track> tracks = (ArrayList<Track>) playlist.getTracks();
        //Open album tracks layout
        Intent intent = new Intent(getApplicationContext(), ActivityAlbumTracks.class);
        intent.putExtra("tracksList", tracks);
        startActivityForResult(intent, QUEUE_REQUEST_CODE);


        /*Intent intent = new Intent();
        intent.putExtra("action", "openAlbum");
        intent.putExtra("track", track);
        setResult(RESULT_OK, intent);
        finish();*/

        //FIXME Offer user a choice (and delete above code):
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make your choice");
        CharSequence[] choices = new CharSequence[4];
        choices[0]="Pistes de \""+track.getAlbum()+"\"";
        choices[1]="Albums de \""+track.getArtist()+"\"";
        builder.setSingleChoiceItems(choices, -1, (dialog, which) -> {
            switch (which) {
                case 0:
                    //Album
                    Intent intent = new Intent();
                    intent.putExtra("action", "openAlbum");
                    intent.putExtra("track", track);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case 1:
                case 2:
                case 3:
                    new HelperToast(this).toastShort(choices[which]+" : "+track.toString());
                    dialog.cancel();
                    break;
            }
        });
        builder.setNegativeButton(R.string.confirmNo, (dialog, which) -> {
        });
        builder.setCancelable(true);
        builder.show();*/
    }
}
