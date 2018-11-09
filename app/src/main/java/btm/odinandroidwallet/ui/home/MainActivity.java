package btm.odinandroidwallet.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.gson.Gson;


import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Arrays;

import btm.odinandroidwallet.App;
import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.GenericResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.auth.LoginActivity;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.dashboard.DashboardFragment;
import btm.odinandroidwallet.ui.help.HelpActivity;
import btm.odinandroidwallet.ui.history.HistoryFragment;
import btm.odinandroidwallet.ui.history.HistoryMainFragment;
import btm.odinandroidwallet.ui.news.NewsActivity;
import btm.odinandroidwallet.ui.points.PointsDetailsActivity;
import btm.odinandroidwallet.ui.profile.ProfileWizardActivity;
import btm.odinandroidwallet.ui.request.RequestFragment;
import btm.odinandroidwallet.ui.request.RequestMainFragment;
import btm.odinandroidwallet.ui.send.SendFragment;
import btm.odinandroidwallet.ui.settings.GenericWebviewActivity;
import btm.odinandroidwallet.ui.settings.SettingsActivity;
import btm.odinandroidwallet.ui.subscription.SubscriptionActivity;
import btm.odinandroidwallet.ui.zxing.CaptureActivity;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;
import btm.odinandroidwallet.util.ViewUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;


/**
 * Created by Waleed on 5/3/2018.
 */

public class MainActivity extends AppCompatActivity {

    //used to fix the setNavigationIcon issue on android system before lolipop
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    NavigationView navigationView;
    AHBottomNavigation bottomNavigation;
    PrefsUtil prefs;
    AppUtil util;
    private Typeface typeface;
    private DrawerLayout drawerLayout;
    APIInterface apiInterface;
    private PrefsUtil mPrefsUtil;
    private String KYCNOTSTARTED = "NOT_STARTED";
    private String APPROVEDSTATUS = "ACCEPTED";
    String kycFlag = KYCNOTSTARTED;
    TextView title;
    int balanceMode = 1;
    UserProfileResponse resource;
    LinearLayout parent;
    public static final int PERMISSION_REQUEST_CAMERA = 161;
    public static final int SCAN_URI = 2007;
    MenuItem profileItem;
    String scannedResult = "";
    private static final float ROTATE_FROM = 30.0f;
    private static final float ROTATE_TO = 360.0f;
    RotateAnimation r;
    ImageView sync;
    private AHBottomNavigation.OnTabSelectedListener tabSelectedListener = (position, wasSelected) -> {

        switch (position) {

            case 0:
                startSendFragment(scannedResult);

                break;
            case 1:
                startDashboardFragment();
                break;
            case 2:
                startHistoryFragment();

                break;
            case 3:
                startRecieveFragment();

                break;

        }

        return true;
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent = findViewById(R.id.parent);
        mPrefsUtil = new PrefsUtil(MainActivity.this);
        apiInterface = APIClient.getClient(this).create(APIInterface.class);
        kycFlag = mPrefsUtil.getValue(PrefsUtil.KYC_STATUS, KYCNOTSTARTED);


        prefs = new PrefsUtil(this);
        util = new AppUtil(this);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_general);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.vector_menu));
        toolbar.setTitle("");
        title = findViewById(R.id.title);
        ImageView scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestScan();
            }
        });
        sync = findViewById(R.id.sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                r.setDuration((long) 2 * 500);
                r.setRepeatCount(Animation.INFINITE);
                sync.startAnimation(r);
                getProfileNetworkCall();
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resource != null) {
                    if (balanceMode == 1) {
                        balanceMode = 2;
                        //request to show only up to for decimal places for balance no rounding
                        String ethBalanceDisplay = AppUtil.formatDoubleToGivenDecimals(resource.result.etherBalance, 2, 4);
                        title.setText("Ether " + ethBalanceDisplay);
                    } else if (balanceMode == 2) {
                        balanceMode = 1;
                        //request to show only up to for decimal places for balance no rounding
                        String odinBalanceDisplay = AppUtil.formatDoubleToGivenDecimals(resource.result.odinHolding, 2, 4);
                        title.setText("ODIN " + odinBalanceDisplay);
                    }
                }
            }
        });
        setSupportActionBar(toolbar);
        ViewUtils.setElevation(toolbar, 0F);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.send_odin, R.drawable.send_token, R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.dashboard_title, R.drawable.vector_home, R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.overview, R.drawable.vector_transactions, R.color.white);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.receive_odin, R.drawable.recieve_token, R.color.white);

        // Add items
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.addItems(Arrays.asList(item1, item2, item3, item4));

        // Styling
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.primary_blue_accent));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.primary_gray_dark));
        bottomNavigation.setForceTint(true);
        bottomNavigation.setUseElevation(true);
        Typeface typeface = TypefaceUtils.load(getAssets(), "fonts/Montserrat-Regular.ttf");
        bottomNavigation.setTitleTypeface(typeface);

        // Select Dashboard by default
        bottomNavigation.setOnTabSelectedListener(tabSelectedListener);
        bottomNavigation.setCurrentItem(1);

        // handleIncomingIntent();
        applyFontToNavDrawer();


        //   else if(kycFlag.equals(APPROVEDSTATUS))
        // {
        getProfileNetworkCall();
        //}

    }

    private void askToUpdateProfile(int msg) {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.update_profile)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, whichButton) -> {
                    Intent i = new Intent(MainActivity.this, ProfileWizardActivity.class);
                    i.putExtra("kycStatus", kycFlag);
                    startActivity(i);

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanActivity();
            } else {
                // Permission request was denied.
            }
        }
    }

    void requestScan() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionFromActivity(parent, MainActivity.this);
        } else {
            startScanActivity();
        }
    }

    void startScanActivity() {
        if (!AppUtil.isCameraOpen()) {
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, SCAN_URI);
        } else {
            ToastCustom.makeText(MainActivity.this, getString(R.string.camera_unavailable), ToastCustom.LENGTH_SHORT, ToastCustom.TYPE_ERROR);
        }
    }

    public static void requestCameraPermissionFromActivity(View parentView, final Activity activity) {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {

            Snackbar.make(parentView, activity.getString(R.string.request_camera_permission),
                    Snackbar.LENGTH_INDEFINITE).setAction(activity.getString(R.string.ok_cap), view -> {
                // Request the permission
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA);
            }).setActionTextColor(ContextCompat.getColor(parentView.getContext(), R.color.primary_blue_accent))
                    .show();

        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        }
    }

    private void getProfileNetworkCall() {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(this);
        mProgressDialog.show();
        Call<UserProfileResponse> call = apiInterface.getUserProfile(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {

                resource = response.body();
                try {
                    mPrefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                    kycFlag = resource.kycStatus;
                    if (balanceMode == 1) {
                        //request to show only up to for decimal places for balance no rounding
                        String odinBalanceDisplay = AppUtil.formatDoubleToGivenDecimals(resource.result.odinHolding, 2, 4);
                        title.setText("ODIN " + odinBalanceDisplay);
                    } else if (balanceMode == 2) {
                        //request to show only up to for decimal places for balance no rounding
                        String ethBalanceDisplay = AppUtil.formatDoubleToGivenDecimals(resource.result.etherBalance, 2, 4);
                        title.setText("Ether " + ethBalanceDisplay);
                    }
                    ContinueAnyWay(resource, mProgressDialog);
                } catch (Exception execption) {
                    execption.printStackTrace();
                    AppUtil.hideProgressDialog(mProgressDialog, MainActivity.this);
                    unPair();

                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {

                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                } else {
                    showToast(R.string.get_profile_error, ToastCustom.TYPE_ERROR);
                }

                ContinueAnyWay(null, mProgressDialog);
            }

        });

    }

    private void ContinueAnyWay(UserProfileResponse resource, ProgressDialog mProgressDialog) {
        if (resource != null) {
            saveProfileObject(resource);
            if (kycFlag.equals(APPROVEDSTATUS)) {
                profileItem.setVisible(false);
            } else {
                profileItem
                        .setVisible(true);
            }
        }
        if (sync != null) {
            sync.clearAnimation();
        }
        AppUtil.hideProgressDialog(mProgressDialog, this);
        if (kycFlag.equals(KYCNOTSTARTED)) {
            askToUpdateProfile(R.string.update_profile_reminder);
        }
    }

    private void saveProfileObject(UserProfileResponse resource) {
        Gson gson = new Gson();
        String json = gson.toJson(resource);
        mPrefsUtil.setValue(PrefsUtil.USER_PROFILE, json);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void applyFontToNavDrawer() {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            applyFontToMenuItem(menuItem);
            if (menuItem.getItemId() == R.id.nav_profile) {
                profileItem = menuItem;
                if (kycFlag.equals(APPROVEDSTATUS)) {
                    menuItem.setVisible(false);
                } else {
                    menuItem.setVisible(true);
                }
            }
        }
    }

    private void applyFontToMenuItem(MenuItem menuItem) {
        if (typeface == null) {
            typeface = TypefaceUtils.load(getAssets(), "fonts/Montserrat-Regular.ttf");
        }
        menuItem.setTitle(CalligraphyUtils.applyTypefaceSpan(
                menuItem.getTitle(),
                typeface));
    }


    private void startDashboardFragment() {
        DashboardFragment fragment = DashboardFragment.newInstance();
        addFragmentToBackStack(fragment);
    }

    private void startSendFragment(String scanData) {
        SendFragment fragment = SendFragment.newInstance(scanData);
        addFragmentToBackStack(fragment);
    }

    private void startRecieveFragment() {
        RequestMainFragment fragment = RequestMainFragment.newInstance();
        addFragmentToBackStack(fragment);
    }

    private void startHistoryFragment() {
        HistoryMainFragment fragment = HistoryMainFragment.newInstance();
        addFragmentToBackStack(fragment);
    }

    private void addFragmentToBackStack(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.content_frame, fragment, fragment.getClass().getSimpleName())
                .commitAllowingStateLoss();
    }

    Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content_frame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetNavigationDrawer();
    }

    public void resetNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });

        // Set selected appropriately.
        if (getCurrentFragment() instanceof DashboardFragment) {
            bottomNavigation.setCurrentItem(1);
        } else if (getCurrentFragment() instanceof HistoryFragment) {
            bottomNavigation.setCurrentItem(2);
        } else if (getCurrentFragment() instanceof SendFragment) {
            bottomNavigation.setCurrentItem(0);
        } else if (getCurrentFragment() instanceof RequestFragment) {
            bottomNavigation.setCurrentItem(3);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SCAN_URI
                && data != null && data.getStringExtra(CaptureActivity.SCAN_RESULT) != null) {
            String strResult = data.getStringExtra(CaptureActivity.SCAN_RESULT);
            scannedResult = strResult;

            if (!TextUtils.isEmpty(strResult)) {
                doScanInput(strResult);
            }

        }
    }

    private void doScanInput(String scanData) {
        bottomNavigation.setCurrentItem(0);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_logout:
                new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                        .setTitle(R.string.logout_title)
                        .setMessage(R.string.logout_confirm_msg)
                        .setPositiveButton(R.string.logout_title, (dialog, which) -> logoutNetworkCall())
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                break;
            case R.id.nav_news:
                Intent i = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_points:
                i = new Intent(MainActivity.this, PointsDetailsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_subscription:
                i = new Intent(MainActivity.this, SubscriptionActivity.class);
                startActivity(i);
                break;
            case R.id.nav_settings:
                i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_profile:
                i = new Intent(MainActivity.this, ProfileWizardActivity.class);
                i.putExtra("kycStatus", kycFlag);
                startActivity(i);
                break;
            case R.id.nav_faq:
                String url = AppUtil.url + "api/faq/";
                AppUtil.showWebPage(MainActivity.this, url, R.string.faq);
                break;
            case R.id.nav_help:
                url = AppUtil.url + "api/help/";
                AppUtil.showWebPage(MainActivity.this, url, R.string.help_center);
                break;
        }
        drawerLayout.closeDrawers();
    }

    void unPair() {
        prefs.clear();
        util.restartApp();
    }

    private void logoutNetworkCall() {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(this);
        mProgressDialog.show();
        Call<GenericResponse> call = apiInterface.logout(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                AppUtil.hideProgressDialog(mProgressDialog, MainActivity.this);
                unPair();
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {

                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                } else {
                    showToast(R.string.logout_error, ToastCustom.TYPE_ERROR);
                }
                call.cancel();
                AppUtil.hideProgressDialog(mProgressDialog, MainActivity.this);
            }
        });
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(this, getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }
}
