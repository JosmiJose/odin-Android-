package btm.odinandroidwallet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionHistoryValue;

public class SubscriptionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context ctx;
    List<SubscriptionHistoryValue>items=new ArrayList<SubscriptionHistoryValue>();
    class ViewHolder0 extends RecyclerView.ViewHolder {
        public TextView sDate, eDate,type,points;

        public ViewHolder0(View itemView){
            super(itemView);
            sDate = (TextView) itemView.findViewById(R.id.start_date);
            eDate = (TextView) itemView.findViewById(R.id.end_date);
            points = (TextView) itemView.findViewById(R.id.points);

        }
    }


    public SubscriptionHistoryAdapter(Context ctx, List<SubscriptionHistoryValue> items)
    {
        this.ctx=ctx;
        this.items=items;
    }
    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(position==0)
        {
            return 0;
        }
        else
        {
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size()+1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==0)
        {
            View headerView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subscription_history_header_item_row, parent, false);
            return new ViewHolder0(headerView);
        }
        else
        {
            View normalView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subscription_history_normal_item_row, parent, false);
            return new ViewHolder0(normalView);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder.getItemViewType()!=0)
        {
            SubscriptionHistoryValue item = items.get(position-1);
            ((ViewHolder0) holder).sDate.setText(""+item.subscriptionDate);
            ((ViewHolder0) holder).eDate.setText(""+item.subscriptionEndDate);
            ((ViewHolder0) holder).points.setText(""+item.pointsUsed);
        }
    }
}