package lt.vycius.movies.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vycius.movies.entity.Movie;
import lt.vycius.movies.entity.MovieVideo;

public class MovieVideoRecyclerViewAdapter
        extends RecyclerView.Adapter<MovieVideoRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<MovieVideo> mValues;

    public MovieVideoRecyclerViewAdapter(Context context, List<MovieVideo> items) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MovieVideo movieVideo = mValues.get(position);

        holder.mVideoName.setText(movieVideo.getName());
        holder.itemView.setOnClickListener(v ->
            watchYoutubeVideo(movieVideo.getKey())
        );
    }

    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(android.R.id.text1)
        TextView mVideoName;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
    ;
}
