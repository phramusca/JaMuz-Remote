package phramusca.com.jamuzkids;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

//http://www.devexchanges.info/2017/02/android-recyclerview-dynamically-load.html
public class AdapterLoad extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int VIEW_TYPE_ITEM = 0;
    final int VIEW_TYPE_LOADING = 1;
    private final Context mContext;
    private boolean isLoading;
    private boolean isLoadingTop;
    private int visibleThreshold = 3;
    private int lastVisibleItem, firstVisibleItem, totalItemCount;

    AdapterLoad(Context context, RecyclerView recyclerView) {
        mContext = context;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if(totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        isLoading = true;
                        if (onLoadListener != null) {
                            onLoadListener.onLoadMore();
                        }
                    }
                }

                if(!isLoadingTop) {
                    // TODO !!! Load on top: on page init, add a load item on top
                    // and select next one (so that loader at pos 0 is hidden)
                    // So there is no need  to scroll down then scroll up to search up
                    // as no more need to check dY
                    if(firstVisibleItem <= visibleThreshold && dy < 0) {
                        isLoadingTop = true;
                        if (onLoadListener != null) {
                            onLoadListener.onLoadTop();
                        }
                    }
                }
            }
        });
    }

    private IListenerOnLoad onLoadListener;
    public void setOnLoadListener(IListenerOnLoad mOnLoadListener) {
        this.onLoadListener = mOnLoadListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setLoadedTop() {
        isLoadingTop = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.queue_item_album, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.queue_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    // "Loading item" ViewHolder
    class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar1);
        }
    }

    // "Normal item" ViewHolder
    class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView item_line1;
        public TextView item_line2;
        public TextView item_line3;
        public TextView item_line4;
        public ImageView imageViewCover;
        public LinearLayout layout_item;

        UserViewHolder(View view) {
            super(view);
            layout_item = view.findViewById(R.id.layout_item);
            item_line1 = view.findViewById(R.id.item_line1);
            item_line2 = view.findViewById(R.id.item_line2);
            item_line3 = view.findViewById(R.id.item_line3);
            item_line4 = view.findViewById(R.id.item_line4);
            imageViewCover = view.findViewById(R.id.imageView);
        }
    }

    private ArrayList<IListenerTrackAdapter> mListListener = new ArrayList<>();

    public void addListener(IListenerTrackAdapter aListener) {
        mListListener.add(aListener);
    }

    void sendListener(Track item, int position) {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onClick(item, position);
        }
    }
}
