package phramusca.com.jamuzkids;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by raph on 03/03/18.
 */

public class AdapterTrack extends BaseAdapter {

    private Context mContext;
    List<Track> tracks;
    private LayoutInflater mInflater;
    private int positionPlaying;

    AdapterTrack(Context context, List<Track> tracks, int positionPlaying) {
        mContext = context;
        this.tracks = tracks;
        mInflater = LayoutInflater.from(context);
        this.positionPlaying = positionPlaying;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Track track = tracks.get(position);
        track.getTags(false);
        return getLayout(position, convertView, parent,
                track.getTitle(),
                track.getArtist(),
                track.getAlbum(),
                String.format(Locale.ENGLISH,
                        "%s %d/5 %s",
                        track.getTags(),
                        track.getRating(),
                        track.getGenre()
                ));
    }

    LinearLayout getLayout(int position, View convertView, ViewGroup parent,
                           String line1, String line2, String line3, String line4) {
        LinearLayout layoutItem;
        if (convertView == null) {
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.queue_item, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        TextView item_line1 = layoutItem.findViewById(R.id.item_line1);
        TextView item_line2 = layoutItem.findViewById(R.id.item_line2);
        TextView item_line3 = layoutItem.findViewById(R.id.item_line3);
        TextView item_line4 = layoutItem.findViewById(R.id.item_line4);

        item_line1.setText(line1);
        item_line2.setText(line2);
        item_line3.setText(line3);
        item_line4.setText(line4);

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

        ImageView imageViewCover = layoutItem.findViewById(R.id.imageView);
        imageViewCover.setImageBitmap(bitmap);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
        bitmapDrawable.setAlpha(50);

        if (tracks.get(position).isHistory()) {
            layoutItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        } else {
            layoutItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background_color));
        }

        layoutItem.setTag(position);
        layoutItem.setOnClickListener(view -> {
            Integer position1 = (Integer)view.getTag();
            sendListener(tracks.get(position1), position1);
        });

        return layoutItem;
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
        if(oldPosition!=positionPlaying) {
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

    private ArrayList<TrackAdapterListener> mListListener = new ArrayList<>();

    public void addListener(TrackAdapterListener aListener) {
        mListListener.add(aListener);
    }

    private void sendListener(Track item, int position) {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onClick(item, position);
        }
    }
}
