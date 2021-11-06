package phramusca.com.jamuzremote;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdapterCursorAlbumTrack extends AdapterCursor<AdapterLoad.UserViewHolder> {

    private ViewGroup parent;

    // Trick to get track new status after download as cursor is not updated
    private final Map<Integer, Track.Status> newStatuses;
    private final String searchQuery;

    public AdapterCursorAlbumTrack(Context context, Cursor cursor, String searchQuery) {
        super(context, cursor);
        this.searchQuery = searchQuery;
        newStatuses = new HashMap<>();
    }

    @Override
    @NotNull
    public AdapterLoad.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item_album, parent, false);
        return new AdapterLoad.UserViewHolder(itemView);
    }

    public Track getTrack(int position) {
        Track track = null;
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            track = HelperLibrary.musicLibrary.cursorToTrack(cursor, false);
            if(newStatuses.containsKey(position)) {
                track.setStatus(newStatuses.get(position));
            }
        }
        return track;
    }

    public void updateStatus(Track.Status status, int position) {
        newStatuses.put(position, status);
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(AdapterLoad.UserViewHolder viewHolder, Cursor cursor, int position) {
        Track track = HelperLibrary.musicLibrary.cursorToTrack(cursor, false);
        track.getTags(false);

        if(newStatuses.containsKey(position)) {
            track.setStatus(newStatuses.get(position));
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        AdapterLoad.UserViewHolder userViewHolder = viewHolder;

        Bitmap bitmap = RepoCovers.getCoverIcon(track, RepoCovers.IconSize.THUMB, true);
        if (bitmap == null) {
            bitmap = HelperBitmap.getEmptyThumb(parent.getContext());
        }

        if (track.getStatus().equals(Track.Status.INFO) || track.getStatus().equals(Track.Status.NEW)) {
            userViewHolder.item_line1.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimaryDark));
            userViewHolder.item_line2.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimaryDark));
            userViewHolder.item_line3.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimaryDark));
            userViewHolder.item_line4.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimaryDark));
            if(track.getStatus().equals(Track.Status.NEW)) {
                bitmap = HelperBitmap.overlayIcon(bitmap, R.drawable.ic_download, parent.getContext());
            } else {
                bitmap = HelperBitmap.overlayIcon(bitmap, R.drawable.ic_info, parent.getContext());
            }
        } else if (track.getStatus().equals(Track.Status.ERROR)) {
            userViewHolder.item_line1.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.lightYellow));
            userViewHolder.item_line2.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.lightYellow));
            userViewHolder.item_line3.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.lightYellow));
            userViewHolder.item_line4.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.lightYellow));
            bitmap = HelperBitmap.overlayIcon(bitmap, R.drawable.ic_error, parent.getContext());
        } else {
            userViewHolder.item_line1.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.textColor));
            userViewHolder.item_line2.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.textColor));
            userViewHolder.item_line3.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.textColor));
            userViewHolder.item_line4.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.textColor));
        }

        userViewHolder.item_line1.setText(String.format(Locale.ENGLISH,"%s%d %s", //NON-NLS
                track.getDiscTotal() > 1 ? "[" + track.getDiscNo() + "/" + track.getDiscTotal() + "] " : "",
                track.getTrackNo(),
                track.getTitle()));
        userViewHolder.item_line2.setText(track.getArtist());
        if(searchQuery!=null && !searchQuery.isEmpty()) {
            userViewHolder.item_line1.setTextToHighlight(searchQuery);
            userViewHolder.item_line1.setTextHighlightColor(R.color.colorAccent);
            userViewHolder.item_line1.setCaseInsensitive(true);
            userViewHolder.item_line1.highlight();
            userViewHolder.item_line2.setTextToHighlight(searchQuery);
            userViewHolder.item_line2.setTextHighlightColor(R.color.colorAccent);
            userViewHolder.item_line2.setCaseInsensitive(true);
            userViewHolder.item_line2.highlight();
        }

        userViewHolder.item_line3.setText(String.format(Locale.ENGLISH,
                "%d/5 %s %s", //NON-NLS
                (int) track.getRating(),
                track.getGenre(),
                track.getYear()));
        userViewHolder.item_line4.setText(String.format(Locale.ENGLISH,
                "%s\n%s %s", //NON-NLS
                track.getTags(),
                ActivityMain.getLastPlayedAgo(track),
                ActivityMain.getAddedDateAgo(track)));

        userViewHolder.imageViewCover.setImageBitmap(bitmap);

        if (track.isHistory()) {
            userViewHolder.layout_item.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimary));
        } else {
            userViewHolder.layout_item.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.background_color));
        }
    }
}