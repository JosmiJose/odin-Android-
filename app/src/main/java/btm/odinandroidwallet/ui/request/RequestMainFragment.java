package btm.odinandroidwallet.ui.request;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.ViewPagerAdapter;
import btm.odinandroidwallet.ui.history.HistoryFragment;

/**
 * Created by Waleed on 6/3/2018.
 */

public class RequestMainFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static RequestMainFragment newInstance() {
        RequestMainFragment fragment = new RequestMainFragment();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dashboard,container,false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = view.findViewById(R.id.tabs_dashboard);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(RequestFragment.newInstance("odin"), getString(R.string.odin));
        adapter.addFragment(RequestFragment.newInstance("ether"),getString(R.string.etherum));
        viewPager.setAdapter(adapter);
    }
}
