package phramusca.com.jamuzremote;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterAlbum extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private boolean isLoading;
    private Activity activity;
    private List<Track> albums;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    private Context mContext;

    AdapterAlbum(Context context, RecyclerView recyclerView, List<Track> albums, Activity activity) {
        this.mContext = context;
        this.albums = albums;
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    // "Loading item" ViewHolder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar1);
        }
    }

    // "Normal item" ViewHolder
    private class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView item_line1;
        public TextView item_line2;
        public TextView item_line3;
        public TextView item_line4;
        public ImageView imageViewCover;

        UserViewHolder(View view) {
            super(view);
            item_line1 = view.findViewById(R.id.item_line1);
            item_line2 = view.findViewById(R.id.item_line2);
            item_line3 = view.findViewById(R.id.item_line3);
            item_line4 = view.findViewById(R.id.item_line4);
            imageViewCover = view.findViewById(R.id.imageView);
        }
    }

    private OnLoadMoreListener onLoadMoreListener;
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return albums.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.queue_item_album, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.queue_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            Track track = albums.get(position);
            UserViewHolder userViewHolder = (UserViewHolder) holder;

            userViewHolder.item_line1.setText(track.getAlbum());
            userViewHolder.item_line2.setText(track.getArtist());
            userViewHolder.item_line3.setText(String.format(Locale.ENGLISH,"%d %s.",
                    track.getPlayCounter(), //Includes nb of albums
                    mContext.getString(R.string.nbTracks)));
            userViewHolder.item_line4.setText(String.format(Locale.ENGLISH,"%d/5 %s",
                    track.getRating(),
                    track.getGenre()));

            //TODO: Make a Repo in ActivityPlayQueue to speed even further
            //Or make it global as for Remote
            //TODO: Add a limit (FIFO) to those repos not to overload android memory
            Bitmap bitmap = albums.get(position).getTumb(false);
            if (bitmap == null) {
                bitmap = HelperBitmap.getEmptyThumb();
            }

            userViewHolder.imageViewCover.setImageBitmap(bitmap);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
            bitmapDrawable.setAlpha(50);

            if (albums.get(position).isHistory()) {
                userViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            } else {
                userViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background_color));
            }

            userViewHolder.itemView.setTag(position);
            userViewHolder.itemView.setOnClickListener(view -> {
                Integer position1 = (Integer)view.getTag();
                sendListener(albums.get(position1), position1);
            });


        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return albums == null ? 0 : albums.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public interface TrackAdapterListener {
        void onClick(Track item, int position);
    }

    private ArrayList<AdapterTrack.TrackAdapterListener> mListListener = new ArrayList<>();

    public void addListener(AdapterTrack.TrackAdapterListener aListener) {
        mListListener.add(aListener);
    }

    private void sendListener(Track item, int position) {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onClick(item, position);
        }
    }
}
