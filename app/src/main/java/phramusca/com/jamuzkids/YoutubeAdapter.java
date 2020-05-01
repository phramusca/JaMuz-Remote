package phramusca.com.jamuzkids;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

// Adapted from https://github.com/abhi5658/search-youtube

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.MyViewHolder> {

    private Context mContext;
    private List<YouTubeVideoItem> mVideoList;
    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView thumbnail;
        public TextView video_title, video_id, video_description;
        public RelativeLayout video_view;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.video_thumbnail);
            video_title = view.findViewById(R.id.video_title);
            video_id = view.findViewById(R.id.video_id);
            video_description = view.findViewById(R.id.video_description);
            video_view = view.findViewById(R.id.video_view);
        }
    }

    YoutubeAdapter(Context mContext, List<YouTubeVideoItem> mVideoList) {
        this.mContext = mContext;
        this.mVideoList = mVideoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final YouTubeVideoItem singleVideo = mVideoList.get(position);
        holder.video_id.setText("Video ID : "+singleVideo.getId()+" ");
        holder.video_title.setText(singleVideo.getTitle());
        holder.video_description.setText(singleVideo.getDescription());

        Picasso.with(mContext)
                .load(singleVideo.getThumbnailURL())
                .resize(480,270)
                .centerCrop()
                .into(holder.thumbnail);

        holder.video_view.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ActivityYouTubePlayer.class);
            intent.putExtra("VIDEO_ID", singleVideo.getId());
            intent.putExtra("VIDEO_TITLE",singleVideo.getTitle());
            intent.putExtra("VIDEO_DESC",singleVideo.getDescription());

            //Flags define hot the activity should behave when launched
            //FLAG_ACTIVITY_NEW_TASK flag if set, the activity will become the start of a new task on this history stack.
            //adding flag as it is required for YoutubePlayerView Activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return mVideoList.size();
    }
}