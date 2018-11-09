package btm.odinandroidwallet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionListResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionValue;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyView> {

    private List<SubscriptionValue> list;
    Context ctx;

    public class MyView extends RecyclerView.ViewHolder {

        public TextView pointsTxt;
        public TextView durationTxt;
        public MyView(View view) {
            super(view);

            pointsTxt = (TextView) view.findViewById(R.id.points_no);
            durationTxt = (TextView) view.findViewById(R.id.duration);

        }
    }


    public RecyclerViewAdapter(List<SubscriptionValue> horizontalList,Context ctx) {
        this.list = horizontalList;
        this.ctx=ctx;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        holder.pointsTxt.setText(""+list.get(position).points);
        holder.durationTxt.setText(ctx.getResources().getString(R.string.subscription_duration_part_one)+" "+list.get(position).noOfDays+" "+ctx.getResources().getString(R.string.subscription_duration_part_two));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}