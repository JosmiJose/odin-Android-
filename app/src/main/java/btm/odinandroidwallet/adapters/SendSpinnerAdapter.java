package btm.odinandroidwallet.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.pojo.PortfolioValue;
import btm.odinandroidwallet.networking.retrofit.pojo.PreIcoCompanyListValue;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import safety.com.br.progressimageview.ProgressImageView;
import safety.com.br.progressimageview.animations.CircleTransform;

public class SendSpinnerAdapter extends ArrayAdapter<PortfolioValue> {
    List<PortfolioValue> objects = new ArrayList<>();
    Context ctx;
    int resource;
    private ProgressImageView progressImageColorAndSize;
    public SendSpinnerAdapter(Context context, int textViewResourceId, List<PortfolioValue> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        this.ctx = context;
        this.resource = textViewResourceId;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
        //LayoutInflater inflater=getActivity.getLayoutInflater();//for fragment
        View row = inflater.inflate(resource, parent, false);
        final TextView label = (TextView) row.findViewById(R.id.tv_spinnervalue);
        label.setText(objects.get(position).tokenName);
        ImageView icon = row.findViewById(R.id.icon);
        if(objects.get(position).tokenName.equals("Eth")) {
            icon.setImageResource(R.drawable.ether);
        }
        else if(objects.get(position).tokenName.equals("ODIN")) {
            icon.setImageResource(R.drawable.mascot_landing);
        }else
        {
            progressImageColorAndSize = (ProgressImageView) row.findViewById(R.id.icon);

            progressImageColorAndSize.showLoading().withAutoHide(true).withBorderColor(ctx.getResources().getColor(R.color.colorPrimary)).withBorderSize(10);
            if(!TextUtils.isEmpty(objects.get(position).tokenImage)) {
                Picasso picasso = Picasso.with(ctx);
                picasso.load(objects.get(position).tokenImage)
                        .transform(new CircleTransform())
                        .into(progressImageColorAndSize);

            }else {
                progressImageColorAndSize.hideLoading();
            }
        }

        return row;
    }
}