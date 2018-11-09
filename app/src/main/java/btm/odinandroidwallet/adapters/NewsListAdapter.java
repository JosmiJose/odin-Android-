package btm.odinandroidwallet.adapters;

import android.content.Context;
import android.net.IpPrefix;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.pojo.NewsListValue;
import btm.odinandroidwallet.ui.listener.ClickListener;
import btm.odinandroidwallet.ui.views.PrefixRttv;
import safety.com.br.progressimageview.ProgressImageView;
import safety.com.br.progressimageview.animations.CircleTransform;


public class NewsListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final List<NewsListValue> mDataset;
    private final ClickListener mClickListener;
    private Context mContext;
    private ProgressImageView progressImageColorAndSize;
    public NewsListAdapter(List<NewsListValue> myDataset, ClickListener clickListener) {
        mDataset = myDataset;
        mClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        RelativeLayout v = (RelativeLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.story_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsListValue item = mDataset.get(position);

        TextView bodyText = (TextView) holder.mView.findViewById(R.id.body);
        TextView titleText = (TextView) holder.mView.findViewById(R.id.title);
        PrefixRttv relativeTimeText = (PrefixRttv) holder.mView.findViewById(R.id.relativeTime);
        ImageView featureImage = (ImageView) holder.mView.findViewById(R.id.featureImage);

        holder.setClickListener(mClickListener);
        bodyText.setText(item.description);
      //  bodyText.setText(item.body);
        titleText.setText(item.title);
        Date date=new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try {
            date = format.parse(item.updatedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        relativeTimeText.setReferenceTime(date.getTime());
        if(!TextUtils.isEmpty(item.uploadImage.toString())) {
            progressImageColorAndSize = (ProgressImageView) holder.mView.findViewById(R.id.featureImage);

            progressImageColorAndSize.showLoading().withAutoHide(true).withBorderColor(mContext.getResources().getColor(R.color.colorPrimary)).withBorderSize(10);

            Picasso picasso = Picasso.with(mContext);
            picasso.load(item.uploadImage.toString())
                    .placeholder(mContext.getResources().getDrawable(R.drawable.news_placeholder))
                    .into(progressImageColorAndSize);

        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
