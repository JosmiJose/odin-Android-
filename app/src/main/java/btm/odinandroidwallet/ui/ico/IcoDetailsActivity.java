package btm.odinandroidwallet.ui.ico;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.ViewPagerAdapter;
import btm.odinandroidwallet.networking.retrofit.pojo.NewsListValue;
import btm.odinandroidwallet.networking.retrofit.pojo.PreIcoCompanyListValue;

/**
 * Created by user on 6/4/2018.
 */

public class IcoDetailsActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView download ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ico_details);
        setupToolbar();
        Intent i = getIntent();
        PreIcoCompanyListValue item = (PreIcoCompanyListValue) i.getSerializableExtra("ico");
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewPager,item);
        tabLayout = findViewById(R.id.tabs_dashboard);
        tabLayout.setupWithViewPager(viewPager);

    }
    private void setupViewPager(ViewPager viewPager,PreIcoCompanyListValue value) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ICOAboutFragment.newInstance(value), getString(R.string.ico_about));
        adapter.addFragment(ICOReviewsFragment.newInstance(value),getString(R.string.ico_reviews));
        adapter.addFragment(ICOTeamFragment.newInstance(value),getString(R.string.ico_team));
        viewPager.setAdapter(adapter);
    }
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        TextView title = findViewById(R.id.title);
        title.setText("ICO Details");
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
