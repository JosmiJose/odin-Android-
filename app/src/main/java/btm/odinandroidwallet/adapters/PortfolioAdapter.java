package btm.odinandroidwallet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.pojo.PortfolioValue;
import safety.com.br.progressimageview.ProgressImageView;
import safety.com.br.progressimageview.animations.CircleTransform;

public class PortfolioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context ctx;
    List<PortfolioValue> items=new ArrayList<PortfolioValue>();
    private ProgressImageView progressImageColorAndSize;
    class ViewHolder0 extends RecyclerView.ViewHolder {
        public TextView name, value, holdings;


        public ViewHolder0(View itemView){
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            value = (TextView) itemView.findViewById(R.id.value);
            holdings = (TextView) itemView.findViewById(R.id.holdings);


        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        public TextView name, value, holdings;
        public ProgressImageView progressImageColorAndSize;
        public ViewHolder2(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            value = (TextView) itemView.findViewById(R.id.value);
            holdings = (TextView) itemView.findViewById(R.id.holdings);
            progressImageColorAndSize = (ProgressImageView) itemView.findViewById(R.id.icon);
        }

    }

    public PortfolioAdapter(Context ctx, List<PortfolioValue>items)
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
                    .inflate(R.layout.portfolio_header_item_row, parent, false);
            return new ViewHolder0(headerView);
        }
        else
        {
            View normalView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.portfolio_normal_item_row, parent, false);
            return new ViewHolder2(normalView);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder.getItemViewType()!=0)
        {
             PortfolioValue item = items.get(position-1);
            ((ViewHolder2) holder).name.setText(item.tokenLongName);
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(8);
            ((ViewHolder2) holder).value.setText(""+df.format(item.odinValue));
            ((ViewHolder2) holder).holdings.setText(""+item.holdings+" "+item.tokenName);

            progressImageColorAndSize =((ViewHolder2) holder).progressImageColorAndSize;
            progressImageColorAndSize.showLoading().withAutoHide(true).withBorderColor(ctx.getResources().getColor(R.color.colorPrimary)).withBorderSize(10);
             if(!TextUtils.isEmpty(item.tokenImage)) {
                 Picasso picasso = Picasso.with(ctx);

                 picasso.load(item.tokenImage)
                         .transform(new CircleTransform())
                         .into(progressImageColorAndSize);
             }
             else
             {
                 progressImageColorAndSize.hideLoading();
             }
        }
    }
}