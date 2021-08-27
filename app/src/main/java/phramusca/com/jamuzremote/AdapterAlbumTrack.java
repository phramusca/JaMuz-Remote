package phramusca.com.jamuzremote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

/**
 * Created by raph on 03/03/18.
 */

public abstract class AdapterAlbumTrack extends AdapterTrack {

    AdapterAlbumTrack(Context context, List<Track> tracks, int positionPlaying, RecyclerView recyclerView) {
        super(context, tracks, positionPlaying, recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            UserViewHolder userViewHolder = (UserViewHolder) holder;
            Track track = trackList.get(position);
            track.getTags(false);
            setView(position, userViewHolder,
                    track.getTitle(),
                    track.getArtist(),
                    String.format(Locale.ENGLISH,"%d/5 %s",
                            (int)track.getRating(), track.getGenre()),
                    String.format(Locale.ENGLISH,"%s\n%s %s",
                            track.getTags(), track.getLastPlayedAgo(), track.getAddedDateAgo()));

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }


}
