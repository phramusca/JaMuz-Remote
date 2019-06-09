package phramusca.com.jamuzkids;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class AdapterAlbum extends AdapterLoad {

    private List<Track> albums;
    private Context mContext;

    AdapterAlbum(Context context, RecyclerView recyclerView, List<Track> albums) {
        super(context, recyclerView);
        this.mContext = context;
        this.albums = albums;
    }

    @Override
    public int getItemViewType(int position) {
        return albums.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return albums == null ? 0 : albums.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            Track track = albums.get(position);
            UserViewHolder userViewHolder = (UserViewHolder) holder;

            userViewHolder.item_line1.setText(track.getAlbum());
            userViewHolder.item_line2.setText(track.getArtist());
            userViewHolder.item_line3.setText(String.format(Locale.ENGLISH,"%d %s.",
                    track.getPlayCounter(), //Includes nb of albums
                    mContext.getString(R.string.nbTracks)));
            userViewHolder.item_line4.setText(String.format(Locale.ENGLISH,"%d/5 %s",
                    track.getRating(),
                    track.getGenre()));

            Bitmap bitmap = albums.get(position).getTumb(false);
            if (bitmap == null) {
                bitmap = HelperBitmap.getEmptyThumb();
            }

            userViewHolder.imageViewCover.setImageBitmap(bitmap);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
            bitmapDrawable.setAlpha(50);

            if (albums.get(position).isHistory()) {
                userViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            } else {
                userViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background_color));
            }

            userViewHolder.itemView.setTag(position);
            userViewHolder.itemView.setOnClickListener(view -> {
                Integer position1 = (Integer)view.getTag();
                sendListener(albums.get(position1), position1);
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
}
