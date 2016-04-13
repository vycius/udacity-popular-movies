package lt.vycius.movies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vycius.movies.entity.MovieReview;

public class ReviewsRecyclerViewAdapter
        extends RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ViewHolder> {

    private List<MovieReview> mValues;

    public ReviewsRecyclerViewAdapter(List<MovieReview> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MovieReview review = mValues.get(position);

        holder.mAuthor.setText(review.getAuthor());
        holder.mContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(android.R.id.text1)
        TextView mAuthor;

        @Bind(android.R.id.text2)
        TextView mContent;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    ;
}
