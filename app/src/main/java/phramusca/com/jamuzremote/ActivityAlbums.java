package phramusca.com.jamuzremote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActivityAlbums extends AppCompatActivity {

    private static final int ALBUM_TRACK_REQUEST_CODE = 100;
    RecyclerView recyclerView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        Button button_exit_albums = findViewById(R.id.button_exit_albums);
        button_exit_albums.setOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.activityAlbumsLoading));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getString(R.string.activityAlbumsLoadingPleaseWait));
        mProgressDialog.show();
        loadItems();
    }

    private void loadItems() {
        new Thread() {
            public void run() {
                Cursor cursor = RepoAlbums.get();
                runOnUiThread(() -> setupList(cursor));
            }
        }.start();
    }

    private String searchQuery="";

    private void setupList(Cursor cursor) {
        AdapterCursorAlbum adapterCursorAlbum = new AdapterCursorAlbum(getApplicationContext(), cursor);
        recyclerView.setAdapter(adapterCursorAlbum);
        adapterCursorAlbum.addListener(adapterListItemAlbum -> {
            //Open album tracks layout
            Intent intent = new Intent(getApplicationContext(), ActivityAlbumTracks.class);
            intent.putExtra("album", adapterListItemAlbum.getAlbum()); //NON-NLS
            intent.putExtra("searchQuery", searchQuery);
            startActivityForResult(intent, ALBUM_TRACK_REQUEST_CODE);
        });

        EditText queryText = findViewById(R.id.filter_album);
        queryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                adapterCursorAlbum.getFilter().filter(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        queryText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

        Button button_search = findViewById(R.id.button_search);
        button_search.setOnClickListener(v -> {
            queryText.setVisibility(View.VISIBLE);
            queryText.requestFocus();
            final InputMethodManager inputMethodManager = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(queryText, InputMethodManager.SHOW_IMPLICIT);
        });

        new SwipeHelper(getApplicationContext(), recyclerView, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
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
        mProgressDialog.dismiss();
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
        data.putExtra("action", playNext ? "playNextAndDisplayQueue" : "displayQueue"); //NON-NLS
        setResult(RESULT_OK, data);
        finish();
    }
}
