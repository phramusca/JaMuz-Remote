package phramusca.com.jamuzremote;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterCursorAlbum extends CursorRecyclerViewAdapter<AdapterLoad.UserViewHolder> {

    private ViewGroup parent;

    public AdapterCursorAlbum(Context context, Cursor cursor) {
        super(context, cursor);
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
    public void onBindViewHolder(AdapterLoad.UserViewHolder viewHolder, Cursor cursor, int position) {
        AdapterListItemAlbum adapterListItemAlbum = AdapterListItemAlbum.fromCursor(cursor);

        @SuppressWarnings("UnnecessaryLocalVariable")
        AdapterLoad.UserViewHolder userViewHolder = (AdapterLoad.UserViewHolder) viewHolder;

        userViewHolder.item_line1.setText(adapterListItemAlbum.getAlbum());
        userViewHolder.item_line2.setText(adapterListItemAlbum.getArtist());
        userViewHolder.item_line3.setText(String.format(Locale.ENGLISH, "%d %s.",
                adapterListItemAlbum.getNbTracks(), //Includes nb of albums
                parent.getContext().getString(R.string.nbTracks)));
        userViewHolder.item_line4.setText(String.format(Locale.ENGLISH, "%.1f/5 %s",
                adapterListItemAlbum.getRating(),
                adapterListItemAlbum.getGenre()));

        Bitmap bitmap = IconBufferCover.getCoverIcon(adapterListItemAlbum.getCoverHash(), adapterListItemAlbum.getPath(), IconBufferCover.IconSize.THUMB, false);
        if (bitmap == null) {
            bitmap = HelperBitmap.getEmptyThumb();
            readIconInThread(adapterListItemAlbum, userViewHolder);
        }
        userViewHolder.imageViewCover.setImageBitmap(bitmap);
        userViewHolder.itemView.setOnClickListener(view -> sendListener(adapterListItemAlbum));
    }

    private void readIconInThread(AdapterListItemAlbum adapterListItemAlbum, AdapterLoad.UserViewHolder userViewHolder) {
        new Thread(() -> {
            Bitmap readBitmap = IconBufferCover.getCoverIcon(adapterListItemAlbum.getCoverHash(), adapterListItemAlbum.getPath(), IconBufferCover.IconSize.THUMB, true);
            if (readBitmap != null) {
                Bitmap finalReadBitmap = readBitmap;
                new Handler(Looper.getMainLooper()).post(() -> userViewHolder.imageViewCover.setImageBitmap(finalReadBitmap));
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
                swapCursor(cursor);
            }
        }
    };
}