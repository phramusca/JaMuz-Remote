package phramusca.com.jamuzremote;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by raph on 03/03/18.
 */

public class AdapterAlbum extends AdapterAbstract {

    AdapterAlbum(Context context, List<Track> tracks, int positionPlaying) {
        super(context, tracks, positionPlaying);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getLayout(position, convertView, parent,
                tracks.get(position).getAlbum(),
                tracks.get(position).getArtist(),
                tracks.get(position).getGenre() + " | " + tracks.get(position).getPlayCounter() + " " + parent.getContext().getString(R.string.nbTracks));
    }
}
