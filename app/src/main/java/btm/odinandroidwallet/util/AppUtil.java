package btm.odinandroidwallet.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.ui.home.MainActivity;
import btm.odinandroidwallet.ui.landing.LandingActivity;
import btm.odinandroidwallet.ui.profile.model.AbstractWizardModel;
import btm.odinandroidwallet.ui.profile.model.Page;
import btm.odinandroidwallet.ui.profile.model.ReviewItem;
import btm.odinandroidwallet.ui.settings.GenericWebviewActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Waleed on 2/3/2018.
 */

public class AppUtil {
    private Context context;
    PrefsUtil prefs;
    public static double minEthBalance = 0.0001;
    //public static String url="http://13.231.250.133/";
    public static String url = "https://www.odinwallet.io/";
    public static String pin = "sha256/08W1cJUyQm09wnhVPLx1If8ZV/PfN1lHnmyyC/iPneQ=";
    public static String pin2= "sha256/0Fx1KZHVJatO6W3zu8mBrdZKXtvqgZjc0WEguOSZtzU=";
    public static int inputLength = 50;
    public static int minimumAge = 15;

    public AppUtil(Context context) {
        this.context = context;
        prefs = new PrefsUtil(context);
    }
    /**
     Function/Module Name : clearCredentials
     Purpose : is to clear user login credentials
     **/
    public void clearCredentials() {
        prefs.clear();
    }
    /**
     Function/Module Name : clearCredentials
     Purpose : is to clear user login credentials and restart the app
     **/
    public void clearCredentialsAndRestart() {
        clearCredentials();
        restartApp();
    }
    /**
     Function/Module Name : restartApp
     Purpose : is to restart the app
     **/
    public void restartApp() {
        Intent intent = new Intent(context, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    /**
     Function/Module Name : SHA256
     Purpose : is to hash password/pin number .
     Input: user's pin or password
     Output :  Hexa Hashed pin or password
     **/
    public static String SHA256(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(Charset.forName("UTF-8")));
        digest.update(hash, 0, hash.length);
        byte[] sha1hash = digest.digest();
        return convertToHex(sha1hash);
    }
    /**
     Function/Module Name : convertToHex
     Purpose : is to convert shal hash to hexa decimal for encryption purpose
     Input: shal hash bytes
     Output :  hexa hashed pin or password
     **/
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
    /**
     Function/Module Name : startMainActivity
     Purpose : is to start the main activity
     **/
    public void startMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        prefs.logIn();
    }
    /**
     Function/Module Name : stringToDate
     Purpose : Convert String to Date ( the timestamp coming from Restful services)
     Input: timestamp
     Output :  Date
     **/
    public static Date stringToDate(String timeStamp) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        try {
            date = format.parse(timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     Function/Module Name : getProgressDialog
     Purpose : Method to show progressDialog wherever needed in the application
     Input: context
     Output :  ProgressDialog
     **/
    public static ProgressDialog getProgressDialog(Context context) {
        ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        return mProgressDialog;
    }
    /**
     Function/Module Name : hideProgressDialog
     Purpose : Method to hide progressDialog wherever needed in the application
     Input: ProgressDialog, context
     **/
    public static void hideProgressDialog(ProgressDialog mProgressDialog, Context context) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }
    /**
     Function/Module Name : getReviewItems
     Purpose : Method get the items from the wizard for displaying them in the review page
     Input: AbstractWizardModel
     Output :  reviewItems
     **/
    @NonNull
    public static ArrayList<ReviewItem> getReviewItems(AbstractWizardModel mWizardModel) {
        ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
        for (Page page : mWizardModel.getCurrentPageSequence()) {
            page.getReviewItems(reviewItems);
        }
        Collections.sort(reviewItems, new Comparator<ReviewItem>() {
            @Override
            public int compare(ReviewItem a, ReviewItem b) {
                return a.getWeight() > b.getWeight() ? +1 : a.getWeight() < b.getWeight() ? -1 : 0;
            }
        });
        return reviewItems;
    }

    /**
     Function/Module Name : toRequestBody
     Purpose : Method to get convert string to request body
     Input: string
     Output :  requestBody
     **/
    public static RequestBody toRequestBody(String value) {
        int hello;
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    public static enum Mode {
        RECEIVED,
        SENT
    }

    public static boolean isCameraOpen() {
        Camera camera = null;

        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            return true;
        } finally {
            if (camera != null) {
                camera.release();
            }
        }

        return false;
    }

    public static void showWebPage(Context ctx, String url, int title) {
        Intent i = new Intent(ctx, GenericWebviewActivity.class);
        i.putExtra("header", ctx.getResources().getString(title));
        i.putExtra("url", url);
        ctx.startActivity(i);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public static String formatDoubleToGivenDecimals(double number,int minDecimals, int maxDecimals){
        DecimalFormat decimalFormat = new DecimalFormat();
       // decimalFormat.setDecimalSeparatorAlwaysShown(true);
        decimalFormat.setMinimumFractionDigits(minDecimals);
        decimalFormat.setMaximumFractionDigits(maxDecimals);
        return decimalFormat.format(number);
    }

    public static boolean isMyServiceRunning(Context ctx, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isEnglishAlphabets(char word) {
            Pattern ps = Pattern.compile("^[0-9a-zA-Z ]+$");
            Matcher ms = ps.matcher(String.valueOf(word));
            return ms.matches();
    }
}
