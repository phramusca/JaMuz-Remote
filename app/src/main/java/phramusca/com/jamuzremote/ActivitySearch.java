package phramusca.com.jamuzremote;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// Adapted from https://github.com/abhi5658/search-youtube

public class ActivitySearch extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private Handler handler;

    private List<YouTubeVideoItem> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_main);
        mProgressDialog = new ProgressDialog(this);
        EditText searchInput = findViewById(R.id.search_input);
        mRecyclerView = findViewById(R.id.videos_recycler_view);
        mProgressDialog.setTitle("Searching...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        handler = new Handler();
        //onEditorAction method called when user clicks ok button or any custom
        //button set on the bottom right of keyboard
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){

                //FIXME: Enable search on sqlite library
                //+ review what options offer youtube
                mProgressDialog.setMessage("Finding videos for "+v.getText().toString());
                mProgressDialog.show();
                //searchOnYoutube(v.getText().toString());
                searchOnJaMuz(v.getText().toString());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                return false;
            }
            return true;
        });
    }

    private void searchOnJaMuz(String keywords) {
        new Thread(){
            public void run(){
                Playlist playlist = new Playlist("Search", true);
                playlist.setSearchValue(keywords);
                ArrayList<Track> tracks = (ArrayList<Track>) playlist.getTracks();
                searchResults = new ArrayList<>();
                for (Track track : tracks) {
                    YouTubeVideoItem item = new YouTubeVideoItem();
                    item.setId(String.valueOf(track.getIdFileServer()));
                    item.setTitle(track.getArtist()+" / "+track.getTitle());
                    item.setDescription(track.toString());
                    item.setThumbnailURL(track.getCoverHash());
                    searchResults.add(item);
                }
                handler.post(() -> {
                    fillYoutubeVideos();
                    mProgressDialog.dismiss();
                });
            }
        }.start();
    }

    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){
                YoutubeConnector yc = new YoutubeConnector(ActivitySearch.this);
                searchResults = yc.search(keywords);
                if(searchResults!=null) {
                    handler.post(() -> {
                        fillYoutubeVideos();
                        mProgressDialog.dismiss();
                    });
                } else {
                    mProgressDialog.dismiss();
                    runOnUiThread(() -> {
                        Toast.makeText(ActivitySearch.this, "No results !!", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    });
                }
            }
        }.start();
    }

    private void fillYoutubeVideos(){
        YoutubeAdapter youtubeAdapter = new YoutubeAdapter(getApplicationContext(), searchResults);
        mRecyclerView.setAdapter(youtubeAdapter);
        youtubeAdapter.notifyDataSetChanged();
    }
}