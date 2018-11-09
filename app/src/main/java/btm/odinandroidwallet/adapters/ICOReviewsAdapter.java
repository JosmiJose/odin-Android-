package btm.odinandroidwallet.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.security.PublicKey;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.objects.Review;


public class ICOReviewsAdapter extends RecyclerView.Adapter<ICOReviewsAdapter.MyView> {

    private List<Review> list;

    public class MyView extends RecyclerView.ViewHolder {

        public TextView title,desc;
        public RatingBar rating;

        public MyView(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            desc = (TextView) view.findViewById(R.id.desc);
            rating=view.findViewById(R.id.rating);

        }
    }


    public ICOReviewsAdapter(List<Review> list) {
        this.list = list;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        holder.title.setText(list.get(position).title);
        holder.desc.setText(list.get(position).desc);
        holder.rating.setRating((float)list.get(position).rating);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}