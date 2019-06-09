package phramusca.com.jamuzkids;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

/**
 * Created by raph on 03/03/18.
 */

public abstract class AdapterTrack extends AdapterLoad {

    private Context mContext;
    private boolean complete;
    private boolean completeTop;
    TrackList trackList;

    AdapterTrack(Context context, List<Track> tracks, int positionPlaying, RecyclerView recyclerView) {
        super(context, recyclerView);
        mContext = context;
        trackList = new TrackList(tracks, positionPlaying);
        readCovers(trackList.get());
        complete=false;
        completeTop=false;
        recyclerView.setAdapter(this);
        setOnLoadListener(new IListenerOnLoad() {
            @Override
            public void onLoadTop() {
                if(!completeTop) {
                    trackList.addLoaderTop();
                    notifyItemInserted(0);
                    new Handler().postDelayed(() -> {
                        completeTop=addTop()<=0;
                        trackList.removeLoader(0);
                        notifyDataSetChanged();
                        setLoadedTop();
                        /*if(completeTop) {
                            Toast.makeText(mContext, "Top of list", Toast.LENGTH_SHORT).show();
                        }*/
                    }, 500);
                }
            }

            @Override
            public void onLoadMore() {
                if (!complete) {
                    int loaderPos = trackList.addLoader();
                    notifyItemInserted(loaderPos);
                    new Handler().post(() -> {
                        complete=!addMore();
                        trackList.removeLoader(loaderPos);
                        notifyDataSetChanged();
                        setLoaded();
                        /*if(complete) {
                            Toast.makeText(mContext, "End of list", Toast.LENGTH_SHORT).show();
                        }*/
                    });
                }
            }
        });
    }

    abstract List<Track> getMore();
    abstract List<Track> getTop();

    private boolean addMore() {
        List<Track> newTracks = getMore();
        this.trackList.addBottom(newTracks);
        readCovers(newTracks);
        return newTracks.size()>0;
    }

    private int addTop() {
        List<Track> newTracks = getTop();
        this.trackList.addTop(newTracks);
        readCovers(newTracks);
        return newTracks.size();
    }

    private void readCovers(List<Track> tracks) {
        for(Track track : tracks) {
            if(track.getTumb(true)!=null) {
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return trackList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            UserViewHolder userViewHolder = (UserViewHolder) holder;
            Track track = trackList.get(position);
            track.getTags(false);
            setView(position, userViewHolder,
                    track.getTitle(),
                    track.getArtist(),
                    track.getAlbum(),
                    String.format(Locale.ENGLISH,
                            "%s %d/5 %s",
                            track.getTags(),
                            track.getRating(),
                            track.getGenre()
                    ));

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }


    void setView(int position, UserViewHolder userViewHolder,
                 String line1, String line2, String line3, String line4) {

        userViewHolder.item_line1.setText(line1);
        userViewHolder.item_line2.setText(line2);
        userViewHolder.item_line3.setText(line3);
        userViewHolder.item_line4.setText(line4);

        Bitmap bitmap = trackList.get(position).getTumb(false);
        if (bitmap == null) {
            bitmap = HelperBitmap.getEmptyThumb();
        }
        if(position==trackList.getPositionPlaying()) {
            bitmap = overlayPlayingIcon(bitmap);
        }

        userViewHolder.imageViewCover.setImageBitmap(bitmap);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
        bitmapDrawable.setAlpha(50);

        if (trackList.get(position).isHistory()) {
            userViewHolder.layout_item.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        } else {
            userViewHolder.layout_item.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background_color));
        }

        userViewHolder.itemView.setTag(position);
        userViewHolder.itemView.setOnClickListener(view -> {
            Integer position1 = (Integer)view.getTag();
            sendListener(trackList.get(position1), position1);
        });
    }

    private Bitmap overlayPlayingIcon(Bitmap bitmap) {
        int margin = 15;
        Bitmap bmOverlay = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmap, new Matrix(), null);
        Bitmap playingBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_playing);
        int newWidth = bmOverlay.getWidth()-(margin*2);
        int newHeight = bmOverlay.getHeight()-(margin*2);
        int width = playingBitmap.getWidth();
        int height = playingBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postTranslate(margin, margin);
        canvas.drawBitmap(playingBitmap, matrix, null);
        return bmOverlay;
    }
}
