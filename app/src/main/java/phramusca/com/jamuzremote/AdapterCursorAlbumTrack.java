package phramusca.com.jamuzremote;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdapterCursorAlbumTrack extends CursorRecyclerViewAdapter<AdapterLoad.UserViewHolder> {

    private ViewGroup parent;

    // Trick to get track new status after download as cursor is not updated
    private final Map<Integer, Track.Status> newStatuses;

    public AdapterCursorAlbumTrack(Context context, Cursor cursor) {
        super(context, cursor);
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
        AdapterLoad.UserViewHolder userViewHolder = (AdapterLoad.UserViewHolder) viewHolder;

        if (track.getStatus().equals(Track.Status.INFO)) {
            userViewHolder.item_line1.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorAccent));
            userViewHolder.item_line2.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorAccent));
        } else if (track.getStatus().equals(Track.Status.ERROR)) {
            userViewHolder.item_line1.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.lightYellow));
            userViewHolder.item_line2.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.lightYellow));
        } else {
            userViewHolder.item_line1.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.textColor));
            userViewHolder.item_line2.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.textColor));
        }

        userViewHolder.item_line1.setText(track.getTitle());
        userViewHolder.item_line2.setText(track.getArtist());
        userViewHolder.item_line3.setText(String.format(Locale.ENGLISH, "%d/5 %s",
                (int) track.getRating(), track.getGenre()));
        userViewHolder.item_line4.setText(String.format(Locale.ENGLISH, "%s\n%s %s",
                track.getTags(), track.getLastPlayedAgo(), track.getAddedDateAgo()));

        Bitmap bitmap = IconBufferCover.getCoverIcon(track, IconBufferCover.IconSize.THUMB, true);
        if (bitmap == null) {
            bitmap = HelperBitmap.getEmptyThumb();
        }

        if (track.getStatus().equals(Track.Status.NEW)) {
            bitmap = AdapterTrack.overlayIcon(bitmap, R.drawable.ic_download, parent.getContext());
        } else if (track.getStatus().equals(Track.Status.ERROR)) {
            bitmap = AdapterTrack.overlayIcon(bitmap, R.drawable.ic_error, parent.getContext());
        }

        userViewHolder.imageViewCover.setImageBitmap(bitmap);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(parent.getContext().getResources(), bitmap);
        bitmapDrawable.setAlpha(50);

        if (track.isHistory()) {
            userViewHolder.layout_item.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimary));
        } else {
            userViewHolder.layout_item.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.background_color));
        }
    }
}