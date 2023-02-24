package phramusca.com.jamuzremote;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterCursorAlbum extends AdapterCursor<AdapterLoad.UserViewHolder> implements Filterable {

    private ViewGroup parent;

    public AdapterCursorAlbum(Cursor cursor) {
        super(cursor);
        oriCursor = cursor;
    }

    @Override
    @NotNull
    public AdapterLoad.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item_album, parent, false);
        return new AdapterLoad.UserViewHolder(itemView);
    }

    public AdapterListItemAlbum getAlbumListItem(int position) {
        AdapterListItemAlbum adapterListItemAlbum = null;
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            adapterListItemAlbum = AdapterListItemAlbum.fromCursor(cursor);
        }
        return adapterListItemAlbum;
    }

    @Override
    public void onBindViewHolder(AdapterLoad.UserViewHolder userViewHolder, Cursor cursor, int position) {
        AdapterListItemAlbum adapterListItemAlbum = AdapterListItemAlbum.fromCursor(cursor);

        userViewHolder.item_line1.setText(adapterListItemAlbum.getAlbum());
        if(!searchQuery.isEmpty()) {
            userViewHolder.item_line1.setTextToHighlight(searchQuery);
            userViewHolder.item_line1.setTextHighlightColor(R.attr.colorAccent);
            userViewHolder.item_line1.setCaseInsensitive(true);
            userViewHolder.item_line1.highlight();
        }

        userViewHolder.item_line2.setText(adapterListItemAlbum.getArtist());
        if(!searchQuery.isEmpty()) {
            userViewHolder.item_line2.setTextToHighlight(searchQuery);
            userViewHolder.item_line2.setTextHighlightColor(R.attr.colorAccent);
            userViewHolder.item_line2.setCaseInsensitive(true);
            userViewHolder.item_line2.highlight();
        }

        userViewHolder.item_line3.setText(String.format(Locale.ENGLISH, "%d %s.", //NON-NLS
                adapterListItemAlbum.getNbTracks(), //Includes nb of albums
                parent.getContext().getString(R.string.adapterCursorAlbumLabelTracks)));

        userViewHolder.item_line4.setText(String.format(Locale.ENGLISH, "%.1f/5 %s", //NON-NLS
                adapterListItemAlbum.getRating(),
                adapterListItemAlbum.getGenre()));

        Bitmap bitmap = RepoCovers.getCoverIcon(adapterListItemAlbum.getCoverHash(), adapterListItemAlbum.getPath(), RepoCovers.IconSize.THUMB, false);
        if (bitmap == null) {
            bitmap = HelperBitmap.getEmptyThumb(parent.getContext());
            readIconInThread(adapterListItemAlbum, userViewHolder);
        }
        userViewHolder.imageViewCover.setImageBitmap(bitmap);
        userViewHolder.itemView.setOnClickListener(view -> sendListener(adapterListItemAlbum));
    }

    private void readIconInThread(AdapterListItemAlbum adapterListItemAlbum, AdapterLoad.UserViewHolder userViewHolder) {
        new Thread(() -> {
            Bitmap readBitmap = RepoCovers.getCoverIcon(adapterListItemAlbum.getCoverHash(), adapterListItemAlbum.getPath(), RepoCovers.IconSize.THUMB, true);
            if (readBitmap != null) {
                new Handler(Looper.getMainLooper()).post(() -> userViewHolder.imageViewCover.setImageBitmap(readBitmap));
            }
        }).start();
    }

    private final ArrayList<IListenerAdapterAlbum> mListListener = new ArrayList<>();

    public void addListener(IListenerAdapterAlbum aListener) {
        mListListener.add(aListener);
    }

    void sendListener(AdapterListItemAlbum adapterListItemAlbum) {
        for (int i = mListListener.size() - 1; i >= 0; i--) {
            mListListener.get(i).onClick(adapterListItemAlbum);
        }
    }

    private final Cursor oriCursor;
    private String searchQuery="";

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Cursor cursor;
            if (constraint != null && constraint.length() != 0) {
                 cursor = HelperLibrary.musicLibrary.getAlbums(constraint.toString().toLowerCase().trim());
            } else {
                cursor = oriCursor;
            }
            FilterResults results = new FilterResults();
            results.values = cursor;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Cursor cursor = (Cursor) results.values;
            if(cursor!=null) {
                searchQuery = constraint.toString().toLowerCase().trim();
                swapCursor(cursor);
            }
        }
    };
}