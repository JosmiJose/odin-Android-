package btm.odinandroidwallet.ui.subscription;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.PointsHistoryAdapter;
import btm.odinandroidwallet.adapters.RecyclerViewAdapter;
import btm.odinandroidwallet.adapters.ViewPagerAdapter;
import btm.odinandroidwallet.ui.ico.ICOAboutFragment;
import btm.odinandroidwallet.ui.ico.ICOReviewsFragment;
import btm.odinandroidwallet.ui.ico.ICOTeamFragment;
import btm.odinandroidwallet.util.AppUtil;

public class SubscriptionActivity extends AppCompatActivity  {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        setupToolbar();
        TextView title = findViewById(R.id.title);
        title.setText(R.string.subscription);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs_dashboard);
        tabLayout.setupWithViewPager(viewPager);


    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SubscribeFragment.newInstance(), getString(R.string.subscribe));
        adapter.addFragment(SubscriptionHistoryFragment.newInstance(),getString(R.string.subscription_history));
        viewPager.setAdapter(adapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


}