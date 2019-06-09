package phramusca.com.jamuzkids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ActivityPlayQueue extends AppCompatActivity implements IListenerTrackAdapter {

    AdapterTrack trackAdapter;
    RecyclerView recyclerView;
    private int offset=0;
    private static final int QUEUE_REQUEST_CODE = 200;

    //FIXME Issue when list is small
    // => need to scroll down (fake since it should not be necessary in real)
    // BEFORE swiping to make swipe to work

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        Button button_exit_queue = findViewById(R.id.button_exit_queue);
        button_exit_queue.setOnClickListener(v -> onBackPressed());
        Intent intent = getIntent();
        final Playlist playlist = (Playlist) intent.getSerializableExtra("SelectedPlaylist");

        PlayQueueRelative playQueueRelative = PlayQueue.queue.getActivityList();
        if(playQueueRelative.getTracks()!=null) {
            int position = playQueueRelative.getPosition();
            offset = playQueueRelative.getOffset();
            position = position - offset;
            recyclerView = findViewById(R.id.list_queue);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            trackAdapter = new AdapterTrack(this, playQueueRelative.getTracks(), position, recyclerView) {
                @Override
                List<Track> getMore() {
                    return PlayQueue.queue.getMore(trackAdapter.getItemCount()-1+offset, playlist);
                }

                @Override
                List<Track> getTop() {
                    return new ArrayList<>();
                }
            };
            trackAdapter.addListener(this);
            recyclerView.getLayoutManager().scrollToPosition(position-1>=0?position-1:position);

            PlayQueue.queue.addListener(positionPlaying -> {
                trackAdapter.trackList.setPositionPlaying(positionPlaying-offset);
                trackAdapter.notifyDataSetChanged();
            });

            new SwipeHelper(this, recyclerView, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
                @Override
                public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder,
                                                      List<UnderlayButton> underlayButtons) {

                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            SwipeHelper.ButtonInfo.DEL,
                            position -> {
                                PlayQueue.queue.remove(position+offset);
                                trackAdapter.trackList.remove(position);
                                trackAdapter.notifyDataSetChanged();
                            },
                            getApplicationContext()));

                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            ButtonInfo.DOWN,
                            position -> {
                                PlayQueue.queue.moveDown(position+offset);
                                trackAdapter.trackList.moveDown(position);
                                trackAdapter.notifyDataSetChanged();
                            },
                            getApplicationContext()));

                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            ButtonInfo.PLAY,
                            position -> {
                                if(PlayQueue.queue.insertNext(position+offset)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("action", "playNext");
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            },
                            getApplicationContext()));

                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            ButtonInfo.QUEUE,
                            position -> {
                                PlayQueue.queue.insertNext(position+offset);
                                trackAdapter.trackList.insertNext(position);
                                trackAdapter.notifyDataSetChanged();
                            },
                            getApplicationContext()));
                }
            };
        }
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

        //TODO Offer user a choice (and delete above code):
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
