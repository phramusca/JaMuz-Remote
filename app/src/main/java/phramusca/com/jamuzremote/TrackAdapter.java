package phramusca.com.jamuzremote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by raph on 03/03/18.
 */

public class TrackAdapter extends BaseAdapter {

    private List<Track> tracks;
    private LayoutInflater mInflater;

    TrackAdapter(Context context, List<Track> tracks) {
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

        /*if (tracks.get(position).getGenre().equals("Reggae")) {
            layoutItem.setBackgroundColor(Color.BLUE);
        } else {
            layoutItem.setBackgroundColor(Color.MAGENTA);
        }*/
        return layoutItem;
    }
}
