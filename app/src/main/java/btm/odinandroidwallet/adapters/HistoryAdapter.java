package btm.odinandroidwallet.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.pojo.HistoryValue;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.util.AppUtil;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context ctx;
    List<HistoryValue>items=new ArrayList<HistoryValue>();
    class ViewHolder0 extends RecyclerView.ViewHolder {
        public TextView date, investment, amount,tokenNo,status;

        public ViewHolder0(View itemView){
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            investment = (TextView) itemView.findViewById(R.id.investment);
            amount = (TextView) itemView.findViewById(R.id.amount);
            tokenNo = (TextView) itemView.findViewById(R.id.token_no);
            status = (TextView) itemView.findViewById(R.id.status);
        }
    }


    public HistoryAdapter(Context ctx, List<HistoryValue> items)
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
                    .inflate(R.layout.history_header_item_row, parent, false);
            return new ViewHolder0(headerView);
        }
        else
        {
            View normalView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_normal_item_row, parent, false);
            return new ViewHolder0(normalView);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder.getItemViewType()!=0)
        {
            HistoryValue item = items.get(position-1);
            Date date= AppUtil.stringToDate(item.txTimestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = sdf.format(date);
            ((ViewHolder0) holder).date.setText(""+dateString);
            if(item.txHash != null) {
                ((ViewHolder0) holder).investment.setText("" + item.txHash);
                ((ViewHolder0) holder).investment.setMovementMethod(LinkMovementMethod.getInstance());
                ((ViewHolder0) holder).investment.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse("https://etherscan.io/tx/"+item.txHash));
                        ctx.startActivity(browserIntent);
                    }
                });
            }else
            {
                ((ViewHolder0) holder).investment.setText(ctx.getResources().getString(R.string.not_available));
            }

            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(8);
            ((ViewHolder0) holder).amount.setText(""+df.format(item.amount));
            ((ViewHolder0) holder).tokenNo.setText(""+item.token);
            ((ViewHolder0) holder).status.setText(item.statusMsg);
           /* ((ViewHolder0) holder).investment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(item.txHash!=null) {
                        showDialogTrans(R.string.trans_to_clipboard, item.txHash.toString());
                    }
                }
            });*/
        }
    }
    private void showDialogTrans(int msg,String hash) {
        new AlertDialog.Builder(ctx, R.style.AlertDialogStyle)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, whichButton) -> {
                    ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("txhash", hash);
                    clipboard.setPrimaryClip(clip);
                    ToastCustom.makeText(ctx, ctx.getString(R.string.copied_to_clipboard), ToastCustom.LENGTH_SHORT, ToastCustom.TYPE_GENERAL);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

}