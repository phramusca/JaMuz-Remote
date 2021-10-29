package phramusca.com.jamuzremote;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActivityAlbums extends AppCompatActivity implements IListenerAdapterAlbum {

    private static final int ALBUM_TRACK_REQUEST_CODE = 100;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        Button button_exit_albums = findViewById(R.id.button_exit_albums);
        button_exit_albums.setOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Cursor cursor = HelperLibrary.musicLibrary.getAlbums();
        AdapterCursorAlbum adapterCursorAlbum = new AdapterCursorAlbum(this, cursor);
        recyclerView.setAdapter(adapterCursorAlbum);
        adapterCursorAlbum.addListener(this);

        EditText queryText = (EditText) findViewById(R.id.filter_album);
        queryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterCursorAlbum.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        queryText.setOnEditorActionListener(new DoneOnEditorActionListener());

        new SwipeHelper(this, recyclerView, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        ButtonInfo.PLAY,
                        pos -> {
                            AdapterListItemAlbum adapterListItemAlbum = adapterCursorAlbum.getAlbumListItem(pos);
                            insertAndSetResult(adapterListItemAlbum.getAlbum(), true);
                        },
                        getApplicationContext()));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        ButtonInfo.QUEUE,
                        pos -> {
                            AdapterListItemAlbum adapterListItemAlbum = adapterCursorAlbum.getAlbumListItem(pos);
                            insertAndSetResult(adapterListItemAlbum.getAlbum(), false);
                        },
                        getApplicationContext()));
            }
        };
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

    private void insertAndSetResult(String album, boolean playNext) {
        Playlist playlist = new Playlist(album, true);
        playlist.setAlbum(album);
        PlayQueue.queue.insert(playlist);

        Intent data = new Intent();
        data.putExtra("action", playNext ? "playNextAndDisplayQueue" : "displayQueue");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(AdapterListItemAlbum adapterListItemAlbum) {
        //Open album tracks layout
        Intent intent = new Intent(getApplicationContext(), ActivityAlbumTracks.class);
        intent.putExtra("album", adapterListItemAlbum.getAlbum());
        startActivityForResult(intent, ALBUM_TRACK_REQUEST_CODE);
    }
}
