package phramusca.com.jamuzremote;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

/**
 * Created by raph on 03/03/18.
 */

public class AdapterAlbumTrack extends AdapterAbstract {

    AdapterAlbumTrack(Context context, List<Track> tracks, int positionPlaying) {
        super(context, tracks, positionPlaying);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Track track = tracks.get(position);
        track.getTags(true);
        return getLayout(position, convertView, parent,
                track.getTitle(),
                String.format(Locale.ENGLISH,"%s %d/5 %s",
                        track.getTags(),
                        track.getRating(),
                        track.getGenre()),
                track.getArtist());
    }
}
