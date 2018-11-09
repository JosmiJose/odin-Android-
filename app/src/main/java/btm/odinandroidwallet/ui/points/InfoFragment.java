package btm.odinandroidwallet.ui.points;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;

public class InfoFragment extends Fragment {
    private PrefsUtil mPrefsUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.points_info_fragment,container,false);
        mPrefsUtil = new PrefsUtil(getActivity());
        TextView points=rootView.findViewById(R.id.points_no);

        Gson gson = new Gson();
        String json = mPrefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        UserProfileResponse obj = gson.fromJson(json, UserProfileResponse.class);
        if(obj!=null) {
            points.setText(""+obj.result.odinPoints);
        }
        else
        {
            points.setText("0");
        }

        TextView howToEarnPoints =rootView.findViewById(R.id.how_points);
        howToEarnPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url=AppUtil.url+"api/odin-points/";
                AppUtil.showWebPage(getActivity(),url,R.string.how_to_earn_points);
            }
        });
        return rootView;
    }
}