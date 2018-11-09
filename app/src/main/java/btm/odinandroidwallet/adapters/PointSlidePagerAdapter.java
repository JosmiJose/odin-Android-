package btm.odinandroidwallet.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import btm.odinandroidwallet.ui.points.InfoFragment;
import btm.odinandroidwallet.ui.points.PointsHistoryFragment;


public class PointSlidePagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Fragment> mFragments;

    public PointSlidePagerAdapter(FragmentManager fm) {
        super(fm);
        InfoFragment infoFragment = new InfoFragment();
        PointsHistoryFragment pointsHistory = new PointsHistoryFragment();
        mFragments = new ArrayList<>();
        mFragments.add(infoFragment);
        mFragments.add(pointsHistory);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}

