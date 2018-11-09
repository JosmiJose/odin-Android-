package btm.odinandroidwallet.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.util.Log;

import com.google.gson.Gson;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import devliving.online.securedpreferencestore.SecuredPreferenceStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrefsUtil implements PersistentPrefs {
    /**
     Function/Module Name : PrefsUtil
     Purpose : Class to handle storing and retrieving data from  shared preference (local storage)
     **/

    private SecuredPreferenceStore preferenceManager ;
    private Context ctx;
    APIInterface apiInterface;
    UserProfileResponse resource;
    public PrefsUtil(Context context) {
        preferenceManager = SecuredPreferenceStore.getSharedInstance();
        this.ctx=context;
        apiInterface= APIClient.getClient(ctx).create(APIInterface.class);
    }

    /**
     Function/Module Name : getValue
     Purpose : Method to get the value of stored data in shared preference (local storage) by its name
     Input: name which is the key and default value for handling nullable
     Output :  string
     **/
    @Override
    public String getValue(String name, String defaultValue) {
        return preferenceManager.getString(name,
                (defaultValue == null || defaultValue.isEmpty()) ? "" : defaultValue);
    }
    /**
     Function/Module Name : setValue
     Purpose : Method to store value in shared preference (local storage) by its name
     Input: name which is the key and calue which is string
     **/
    @Override
    public void setValue(String name, String value) {
        //checkKYCChange(name,value);
        Editor editor = preferenceManager.edit();
        editor.putString(name, (value == null || value.isEmpty()) ? "" : value);
        editor.apply();
    }
    /**
     Function/Module Name : getValue
     Purpose : Method to get the value of stored data in shared preference (local storage) by its name
     Input: name which is the key and default value for handling nullable
     Output :  Int
     **/
    @Override
    public int getValue(String name, int defaultValue) {
        return preferenceManager.getInt(name, defaultValue);
    }
    /**
     Function/Module Name : setValue
     Purpose : Method to store value in shared preference (local storage) by its name
     Input: name which is the key and value which is integer
     **/
    @Override
    public void setValue(String name, int value) {
        Editor editor = preferenceManager.edit();
        editor.putInt(name, (value < 0) ? 0 : value);
        editor.apply();
    }
    /**
     Function/Module Name : setValue
     Purpose : Method to store value in shared preference (local storage) by its name
     Input: name which is the key and value which is long type
     **/
    @Override
    public void setValue(String name, long value) {
        Editor editor = preferenceManager.edit();
        editor.putLong(name, (value < 0L) ? 0L : value);
        editor.apply();
    }
    /**
     Function/Module Name : getValue
     Purpose : Method to get the value of stored data in shared preference (local storage) by its name
     Input: name which is the key and default value for handling nullable
     Output :  long
     **/
    @Override
    public long getValue(String name, long defaultValue) {

        long result;
        try {
            result = preferenceManager.getLong(name, defaultValue);
        } catch (Exception e) {
            result = (long) preferenceManager.getInt(name, (int) defaultValue);
        }

        return result;
    }
    /**
     Function/Module Name : getValue
     Purpose : Method to get the value of stored data in shared preference (local storage) by its name
     Input: name which is the key and default value for handling nullable
     Output :  boolean
     **/
    @Override
    public boolean getValue(String name, boolean defaultValue) {
        return preferenceManager.getBoolean(name, defaultValue);
    }
    /**
     Function/Module Name : setValue
     Purpose : Method to store value in shared preference (local storage) by its name
     Input: name which is the key and value which is boolean
     **/
    @Override
    public void setValue(String name, boolean value) {

        Editor editor = preferenceManager.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }
    /**
     Function/Module Name : checkKYCChange
     Purpose : Not used
     **/
    private void checkKYCChange(String name , String value)
    {
        String currentStatus = getValue(name ,"");
        if(!currentStatus.equals(value))
        {
            if(value.equals("ACCEPTED"))
            {
               getProfileNetworkCall();
            }
        }
    }
    /**
     Function/Module Name : getProfileNetworkCall
     Purpose : is a get Api endpoint that get user profile
     Output :  Response is stored in the shared preference
     **/
    private  void getProfileNetworkCall()
    {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(ctx);
        mProgressDialog.show();
        Call<UserProfileResponse> call = apiInterface.getUserProfile(ctx.getString(R.string.auth_prefix) + " " + getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {

                Log.d("TAG",response.code()+"");
                resource = response.body();
                try {
                    setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                    ContinueAnyWay(resource,mProgressDialog);
                }
                catch (Exception execption)
                {
                    ContinueAnyWay(null,mProgressDialog);
                    showToast(R.string.get_profile_error, ToastCustom.TYPE_ERROR);
                }
            }
            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                ContinueAnyWay(null,mProgressDialog);
                showToast(R.string.get_profile_error, ToastCustom.TYPE_ERROR);
            }

        });

    }
    private void ContinueAnyWay(UserProfileResponse resource,  ProgressDialog mProgressDialog) {
        saveProfileObject(resource);
        AppUtil.hideProgressDialog(mProgressDialog, ctx);
    }
    private void saveProfileObject(UserProfileResponse resource)
    {
        Gson gson = new Gson();
        String json = gson.toJson(resource);
        setValue(PrefsUtil.USER_PROFILE,json);
    }
    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(ctx, ctx.getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }
    /**
     Function/Module Name : has
     Purpose : check if shared preference has data for the key provided
     input : name which is the key of the shared preference
     Output :  boolean
     **/
    @Override
    public boolean has(String name) {
        return preferenceManager.contains(name);
    }
    /**
     Function/Module Name : removeValue
     Purpose : to remove data from shared preference
     input : name which is the key of the shared preference
     **/
    @Override
    public void removeValue(String name) {
        Editor editor = preferenceManager.edit();
        editor.remove(name);
        editor.apply();
    }
    /**
     Function/Module Name : removeValue
     Purpose : to remove all data from shared preference
     **/
    @Override
    public void clear() {
        Editor editor = preferenceManager.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Clears everything but the GUID for logging back in
     */
    @Override
    public void logOut() {
        String guid = getValue(PrefsUtil.KEY_GUID, "");
        clear();

        setValue(PrefsUtil.LOGGED_OUT, true);
        setValue(PrefsUtil.KEY_GUID, guid);
    }

    /**
     * Reset value once user logged in
     */
    @Override
    public void logIn() {
        setValue(PrefsUtil.LOGGED_OUT, false);
    }

}
