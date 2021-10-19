package phramusca.com.jamuzremote;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class AlbumCursorAdapter extends CursorRecyclerViewAdapter<AdapterLoad.UserViewHolder> {

    private ViewGroup parent;

    public AlbumCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView mTextView;
//
//        public ViewHolder(View view) {
//            super(view);
//            mTextView = view.findViewById(R.id.text);
//        }
//    }

    @Override
    @NotNull
    public AdapterLoad.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item_album, parent, false);
        return new AdapterLoad.UserViewHolder(itemView);
    }

    public AlbumListItem getAlbumListItem(int position) {
        AlbumListItem albumListItem = null;
        Cursor cursor = getCursor();
        if(cursor.moveToPosition(position)) {
            albumListItem = AlbumListItem.fromCursor(cursor);
        }
        return albumListItem;
    }

    @Override
    public void onBindViewHolder(AdapterLoad.UserViewHolder viewHolder, Cursor cursor) {
        AlbumListItem albumListItem = AlbumListItem.fromCursor(cursor);

        AdapterLoad.UserViewHolder userViewHolder = (AdapterLoad.UserViewHolder) viewHolder;

        userViewHolder.item_line1.setText(albumListItem.getAlbum());
        userViewHolder.item_line2.setText(albumListItem.getArtist());
        userViewHolder.item_line3.setText(String.format(Locale.ENGLISH, "%d %s.",
                albumListItem.getNbTracks(), //Includes nb of albums
                parent.getContext().getString(R.string.nbTracks)));
        userViewHolder.item_line4.setText(String.format(Locale.ENGLISH, "%.1f/5 %s",
                albumListItem.getRating(),
                albumListItem.getGenre()));

        Bitmap bitmap = IconBufferCover.readIconFromCache(albumListItem.getCoverHash(), IconBufferCover.IconSize.THUMB);
        if (bitmap == null) {
            bitmap = HelperBitmap.getEmptyThumb();
        }

        userViewHolder.imageViewCover.setImageBitmap(bitmap);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(parent.getContext().getResources(), bitmap);
        bitmapDrawable.setAlpha(50);

        userViewHolder.itemView.setOnClickListener(view -> {
            sendListener(albumListItem);
        });
    }
}