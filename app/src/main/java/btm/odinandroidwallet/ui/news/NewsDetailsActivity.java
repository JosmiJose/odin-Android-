package btm.odinandroidwallet.ui.news;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.pojo.NewsListValue;
import btm.odinandroidwallet.ui.settings.WebViewClientImpl;
import btm.odinandroidwallet.ui.views.PrefixRttv;
import safety.com.br.progressimageview.ProgressImageView;


public class NewsDetailsActivity extends AppCompatActivity {

    private static final String ITEM_KEY = "Item";

    private ProgressImageView progressImageColorAndSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_details);
        setupToolbar();
        Intent i = getIntent();
        NewsListValue item = (NewsListValue) i.getSerializableExtra("newsItem");
        WebView bodyText = (WebView) findViewById(R.id.body);
        WebSettings webSettings = bodyText.getSettings();
        webSettings.setJavaScriptEnabled(true);

        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        bodyText.setWebViewClient(webViewClient);


        TextView titleText = (TextView) findViewById(R.id.title);
        PrefixRttv relativeTimeText = (PrefixRttv) findViewById(R.id.relativeTime);
       if (!TextUtils.isEmpty(item.uploadImage.toString())) {
            progressImageColorAndSize = (ProgressImageView) findViewById(R.id.featureImage);

            progressImageColorAndSize.showLoading().withAutoHide(true).withBorderColor(getResources().getColor(R.color.colorPrimary)).withBorderSize(10);

            Picasso picasso = Picasso.with(this);
            picasso.load(item.uploadImage.toString())
                    .placeholder(getResources().getDrawable(R.drawable.news_placeholder))
                    .into(progressImageColorAndSize);

        }

        bodyText.loadData(item.body, "text/html", null);
      //  bodyText.setText(item.description);
        titleText.setText(item.title);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try {
            date = format.parse(item.updatedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        relativeTimeText.setReferenceTime(date.getTime());

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
    }
}
