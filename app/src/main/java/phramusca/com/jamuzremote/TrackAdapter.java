package phramusca.com.jamuzremote;

import android.content.Context;
import android.graphics.Bitmap;
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

/**
 * Created by raph on 03/03/18.
 */

public class TrackAdapter extends BaseAdapter {

    private Context mContext;
    private List<Track> tracks;
    private LayoutInflater mInflater;

    TrackAdapter(Context context, List<Track> tracks) {
        mContext = context;
        this.tracks = tracks;
        mInflater = LayoutInflater.from(context);
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
        LinearLayout layoutItem;
        if (convertView == null) {
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.queue_item, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        TextView tv_artist = layoutItem.findViewById(R.id.tv_artist);
        TextView tv_album = layoutItem.findViewById(R.id.tv_album);
        TextView tv_title = layoutItem.findViewById(R.id.tv_title);

        tv_artist.setText(tracks.get(position).getArtist());
        tv_album.setText(tracks.get(position).getAlbum());
        tv_title.setText(tracks.get(position).getTitle());

        //TODO: Make a Repo in PlayQueueActivity to speed even further
        //Or make it global as for Remote
        //TODO: Add a limit (FIFO) to those repos not to overload android memory
        Bitmap bitmap = tracks.get(position).getTumb(false);
        if (bitmap == null) {
            bitmap = BitMapHelper.getEmptyThumb();
        }
        ImageView imageViewCover = layoutItem.findViewById(R.id.imageView);
        imageViewCover.setImageBitmap(bitmap);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        bitmapDrawable.setAlpha(50);

        if (tracks.get(position).isHistory()) {
            layoutItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        } else {
            layoutItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background_color));
        }

        layoutItem.setTag(position);
        layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer position = (Integer)view.getTag();
                sendListener(tracks.get(position), position);
            }
        });

        return layoutItem;
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
