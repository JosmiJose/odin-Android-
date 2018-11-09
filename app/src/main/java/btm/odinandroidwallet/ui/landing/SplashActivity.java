package btm.odinandroidwallet.ui.landing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.ui.home.MainActivity;
import btm.odinandroidwallet.util.PrefsUtil;
import btm.odinandroidwallet.util.RootUtil;
import btm.odinandroidwallet.util.ViewUtils;


public class SplashActivity extends AppCompatActivity {
 PrefsUtil mPrefUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefUtils=new PrefsUtil(this);
        if(RootUtil.isDeviceRooted())
        {
           closeApp();
        }

        boolean loggedOut= mPrefUtils.getValue(PrefsUtil.LOGGED_OUT,true);
        if(loggedOut)
        {
            Intent intent = new Intent(this, LandingActivity.class);
            startActivity(intent);
            finish();

        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void closeApp() {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.rooted_device_title)
                .setMessage(R.string.rooted_device_body)
                .setPositiveButton(R.string.close_app, (dialog, which) -> finish())
                .show();

    }
}