package phramusca.com.jamuzremote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by raph on 03/03/18.
 */

public abstract class AdapterTrack extends AdapterLoad {

    private Context mContext;
    List<Track> tracks;
    private int positionPlaying;
    private boolean complete;
    private boolean completeTop;

    AdapterTrack(Context context, List<Track> tracks, int positionPlaying, RecyclerView recyclerView) {
        super(context, recyclerView);
        mContext = context;
        this.tracks = tracks;
        readCovers(tracks);
        this.positionPlaying = positionPlaying;
        complete=false;
        completeTop=false;
        setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoadMore() {
                if (!complete) {
                    tracks.add(null);
                    notifyItemInserted(tracks.size() - 1);
                    new Handler().post(() -> {
                        int loaderPos = tracks.size() - 1;
                        complete=!addMore();
                        remove(loaderPos);
                        notifyDataSetChanged();
                        setLoaded();
                    });
                } else {
                    Toast.makeText(mContext, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoadTop() {
                    if(!completeTop) {
                        tracks.add(0, null);
                        notifyItemInserted(0);
                        recyclerView.getLayoutManager().scrollToPosition(0);
                        new Handler().post(() -> {
                            int nbAdded = addTop();
                            completeTop=nbAdded<=0;
                            tracks.remove(nbAdded);
                            notifyDataSetChanged();
                            setLoadedTop();
                        });
                    } else {
                        Toast.makeText(mContext, "Loading data ON TOP completed", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        //if(addMore()) {

            recyclerView.setAdapter(this);
        //}
    }

    abstract List<Track> getMore();
    abstract List<Track> getTop();

    boolean addMore() {
        List<Track> newTracks = getMore();
        this.tracks.addAll(newTracks);
        readCovers(newTracks);
        return newTracks.size()>0;
    }

    int addTop() {
        List<Track> newTracks = getTop();
        this.tracks.addAll(0, newTracks);
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
        return tracks.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return tracks == null ? 0 : tracks.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            UserViewHolder userViewHolder = (UserViewHolder) holder;
            Track track = tracks.get(position);
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

        //TODO: Make a Repo in ActivityPlayQueue to speed even further
        //Or make it global as for Remote
        //TODO: Add a limit (FIFO) to those repos not to overload android memory
        Bitmap bitmap = tracks.get(position).getTumb(false);
        if (bitmap == null) {
            bitmap = HelperBitmap.getEmptyThumb();
        }
        if(position==positionPlaying) {
            bitmap = overlayPlayingIcon(bitmap, 15);
        }

        userViewHolder.imageViewCover.setImageBitmap(bitmap);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
        bitmapDrawable.setAlpha(50);

        if (tracks.get(position).isHistory()) {
            userViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        } else {
            userViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background_color));
        }

        userViewHolder.itemView.setTag(position);
        userViewHolder.itemView.setOnClickListener(view -> {
            Integer position1 = (Integer)view.getTag();
            sendListener(tracks.get(position1), position1);
        });
    }

    private Bitmap overlayPlayingIcon(Bitmap bitmap, int margin) {
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

    //TODO: Merge with the same ones on PlayQueue
    public void insertNext(int oldPosition) {
        if(oldPosition!=positionPlaying) {
            Track track = tracks.get(oldPosition);
            if(track!=null) {
                tracks.remove(oldPosition);
                if(oldPosition<positionPlaying) {
                    positionPlaying--;
                }
                tracks.add(positionPlaying+1, track);
            }
        }
    }

    public void moveDown(int oldPosition) {
        if(oldPosition!=positionPlaying
                && oldPosition<tracks.size()) {
            Track track = tracks.get(oldPosition);
            if(track!=null) {
                tracks.remove(oldPosition);
                oldPosition++;
                if(oldPosition==positionPlaying) {
                    positionPlaying--;
                }
                tracks.add(oldPosition, track);
            }
        }
    }


    public void remove(int position) {
        if(position!= positionPlaying) {
            Track track = tracks.get(position);
            if(track!=null) {
                tracks.remove(position);
                if(position<positionPlaying) {
                    positionPlaying--;
                }
            }
        }
    }

    public interface TrackAdapterListener {
        void onClick(Track item, int position);
    }
}
