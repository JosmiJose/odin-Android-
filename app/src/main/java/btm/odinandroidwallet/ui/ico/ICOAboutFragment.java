package btm.odinandroidwallet.ui.ico;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.PreIcoCompanyListValue;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.util.PrefsUtil;
import safety.com.br.progressimageview.ProgressImageView;
import safety.com.br.progressimageview.animations.CircleTransform;

public class ICOAboutFragment extends Fragment {
    PreIcoCompanyListValue value;
    private PrefsUtil mPrefsUtil;
    UserProfileResponse obj;
    private ProgressImageView progressImageColorAndSize;

    public static ICOAboutFragment newInstance(PreIcoCompanyListValue value) {
        ICOAboutFragment fragment = new ICOAboutFragment();
        fragment.value=value;
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.ico_about_fragment,container,false);

        mPrefsUtil = new PrefsUtil(getActivity());
        Gson gson = new Gson();
        String json = mPrefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        obj = gson.fromJson(json, UserProfileResponse.class);
        LinearLayout industryBack=rootView.findViewById(R.id.industry_back);
      /*  industryBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        if(!TextUtils.isEmpty(value.companyLogo)) {
            progressImageColorAndSize = (ProgressImageView) rootView.findViewById(R.id.logo);

            progressImageColorAndSize.showLoading().withAutoHide(true).withBorderColor(getResources().getColor(R.color.colorPrimary)).withBorderSize(10);

            Picasso picasso = Picasso.with(getActivity());
            picasso.load(value.companyLogo)
                    .transform(new CircleTransform())
                    .into(progressImageColorAndSize);

        }
        TextView companyName=rootView.findViewById(R.id.name);
        companyName.setText(value.companyName);
        TextView rate=rootView.findViewById(R.id.rate);
        rate.setText("1 ODIN = "+1 / value.odinEquivalent+" "+value.preIcoTicker);
        TextView industryTxt=rootView.findViewById(R.id.industry_txt);
        industryTxt.setText(value.industryType);
        TextView websiteTxt=rootView.findViewById(R.id.website);
        websiteTxt.setText(value.website);
        TextView tickerTxt=rootView.findViewById(R.id.ticker);
        tickerTxt.setText(value.preIcoTicker);
        TextView supplyTxt=rootView.findViewById(R.id.total_supply);
        if(obj != null) {
            if (obj.result.isSubscribed) {
                int supply = value.totalSupply + value.premiumSupply;
                supplyTxt.setText("" + supply);
            } else {
                supplyTxt.setText(""+value.totalSupply);
            }
        }

        TextView startDate=rootView.findViewById(R.id.start_date);
        startDate.setText(value.icoStartDate);
        TextView endDate=rootView.findViewById(R.id.end_date);
        endDate.setText(value.icoEndDate);
        TextView overview=rootView.findViewById(R.id.overview);
        overview.setText(value.companyOverview);
        TextView address=rootView.findViewById(R.id.address);
        address.setText(value.contractAddress);
        TextView releaseDate=rootView.findViewById(R.id.release_date);
        releaseDate.setText(value.dateOfTokenRelease);
        return rootView;
    }
}