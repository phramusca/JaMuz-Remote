package phramusca.com.jamuzremote;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Button;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityPlayQueue extends AppCompatActivity implements AdapterTrack.TrackAdapterListener {

    AdapterTrack trackAdapter;

    RecyclerView recyclerView;
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
            recyclerView = findViewById(R.id.list_queue);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            trackAdapter = new AdapterTrack(this, queue, position, recyclerView) {
                @Override
                List<Track> getMore() {
                    return new ArrayList<>();
                }

                @Override
                List<Track> getTop() {
                    return new ArrayList<>();
                }
            };
            trackAdapter.addListener(this);

            SwipeHelper swipeHelper = new SwipeHelper(this, recyclerView, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
                @Override
                public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            "",
                            R.drawable.ic_slide_queue_add,
                            Color.parseColor("#42f512"),
                            position -> {
                                PlayQueue.insert(position+offset);
                                trackAdapter.insertNext(position);
                            },
                            getApplicationContext()));

                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            "",
                            R.drawable.ic_slide_queue_play,
                            Color.parseColor("#36ff00"),
                            position -> {
                                if(PlayQueue.insert(position+offset)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("action", "playNext");
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            },
                            getApplicationContext()));

                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            "",
                            R.drawable.ic_slide_remove,
                            Color.parseColor("#FF3C30"),
                            position -> {
                                PlayQueue.remove(position+offset);
                                trackAdapter.remove(position);
                                trackAdapter.notifyDataSetChanged();
                            },
                            getApplicationContext()));

                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            "",
                            R.drawable.ic_slide_down,
                            Color.parseColor("#FF9502"),
                            position -> {
                                PlayQueue.moveDown(position+offset);
                                trackAdapter.moveDown(position);
                            },
                            getApplicationContext()));
                }
            };

        }
    }

 /*   private void confirmPlayNext(Track track, int position) {
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
    }*/

/*    private void confirmRemoval(Track track, int position) {
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
    }*/

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
