package btm.odinandroidwallet.ui.points;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.PointSlidePagerAdapter;

public class PointsDetailsActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {
    private TabLayout mTabLayout;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);


        mTabLayout = setupTabLayout();
        mPagerAdapter = createPageAdapter(getSupportFragmentManager());
        mPager = setupPaging(mPagerAdapter);

        setupToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        TextView title=findViewById(R.id.title);
        title.setText(R.string.odin_points);


    }

    private TabLayout setupTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.points_info));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.points_history));
        tabLayout.setOnTabSelectedListener(this);

        return tabLayout;
    }

    private PointSlidePagerAdapter createPageAdapter(FragmentManager fragmentManager) {
        return new PointSlidePagerAdapter(fragmentManager);
    }

    private ViewPager setupPaging(PagerAdapter pagerAdapter) {
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(this);

        return pager;
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mTabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
       }

        return true;
    }



}
