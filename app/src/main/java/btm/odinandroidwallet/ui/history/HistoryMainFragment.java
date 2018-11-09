package btm.odinandroidwallet.ui.history;

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
import btm.odinandroidwallet.util.AppUtil;

/**
 * Created by Waleed on 6/3/2018.
 */

public class HistoryMainFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static HistoryMainFragment newInstance() {
        HistoryMainFragment fragment = new HistoryMainFragment();
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
        adapter.addFragment(HistoryFragment.newInstance(AppUtil.Mode.SENT), getString(R.string.sent));
        adapter.addFragment(HistoryFragment.newInstance(AppUtil.Mode.RECEIVED),getString(R.string.received));
        viewPager.setAdapter(adapter);
    }
}
