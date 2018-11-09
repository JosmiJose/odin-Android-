package btm.odinandroidwallet.ui.settings;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.gson.Gson;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.ExchangeSpinnerAdapter;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.GenericResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.PasswordChange;
import btm.odinandroidwallet.networking.retrofit.pojo.PreIcoCompanyListResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.RegisterResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.User;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.auth.PinEntryActivity;
import btm.odinandroidwallet.ui.createwallet.CreateWalletActivity;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.home.MainActivity;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;
import btm.odinandroidwallet.util.StringUtils;
import btm.odinandroidwallet.util.ViewUtils;
import info.blockchain.wallet.util.FormatsUtil;
import info.blockchain.wallet.util.PasswordUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static btm.odinandroidwallet.ui.auth.PinEntryActivity.KEY_VALIDATING_PIN_FOR_RESULT;
import static btm.odinandroidwallet.ui.auth.PinEntryActivity.REQUEST_CODE_VALIDATE_PIN;

/**
 * Created by waleed on 20/3/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener {
    // Profile
    private Preference guidPref;
    private Preference emailPref;
    private Preference profileStatusPref;
    private StringUtils stringUtils;
    private PrefsUtil prefsUtil;
    private int pwStrength = 0;
    private String kycFlag="";
    private int pinValidateItem=0;
    APIInterface apiInterface;
    private String APPROVEDSTATUS="ACCEPTED";
    UserProfileResponse resource;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stringUtils=new StringUtils(getActivity());
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
        prefsUtil=new PrefsUtil(getActivity());
        PreferenceScreen prefScreen = getPreferenceScreen();
        if (prefScreen != null) prefScreen.removeAll();
        addPreferencesFromResource(R.xml.settings);
        kycFlag=prefsUtil.getValue(PrefsUtil.KYC_STATUS,"");
        initUi();
        getProfileNetworkCall();
    }
    private  void getProfileNetworkCall()
    {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(getActivity());
        mProgressDialog.show();
        Call<UserProfileResponse> call = apiInterface.getUserProfile(getString(R.string.auth_prefix) + " " + prefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {

                Log.d("TAG",response.code()+"");
                resource = response.body();
                try {
                    prefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                    ContinueAnyWay(resource, mProgressDialog);
                }
                catch (Exception execption)
                {
                    ContinueAnyWay(null,mProgressDialog);
                    showToast(R.string.get_profile_error, ToastCustom.TYPE_ERROR);
                }
            }
            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                }
                else {
                    showToast(R.string.get_profile_error, ToastCustom.TYPE_ERROR);
                }
                ContinueAnyWay(null,mProgressDialog);

            }

        });

    }
    private void ContinueAnyWay(UserProfileResponse resource,  ProgressDialog mProgressDialog) {
        saveProfileObject(resource);
        AppUtil.hideProgressDialog(mProgressDialog, getActivity());
        initUi();
    }
    private void saveProfileObject(UserProfileResponse resource)
    {
        Gson gson = new Gson();
        String json = gson.toJson(resource);
        prefsUtil.setValue(PrefsUtil.USER_PROFILE,json);
    }
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
          /*  case "email":
                showDialogEmail(preference.getSummary().toString());
                break;*/
            case "guid":
                showDialogGuid(R.string.guid_to_clipboard);
                break;
            case "pin":
                pinValidateItem=3;
                showDialogChangePin();
                break;
            case "change_pw":
                showDialogChangePassword();
                break;
            case "private_key":
                pinValidateItem=1;
                showDialogChangePin();
                break;
            case "seed":
                pinValidateItem=2;
                showDialogChangePin();
                break;
            case "about":
                String url=AppUtil.url+"api/about/";
                AppUtil.showWebPage(getActivity(),url,R.string.preferences_about_title);
                break;
            case "tos":
                String link=AppUtil.url+"api/tnc/";
                AppUtil.showWebPage(getActivity(),link,R.string.options_tos);
                break;
        }

        return true;
    }

    private void showKey(int mode)
    {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(getActivity());
        mProgressDialog.show();
        Call<GenericResponse> call;
        if(mode==2) {
            call = apiInterface.getSeed(getString(R.string.auth_prefix) + " " + prefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        }
        else
        {
            call = apiInterface.getPrivateKey(getString(R.string.auth_prefix) + " " + prefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        }
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {

                Log.d("TAG", response.code() + "");
                GenericResponse resource = response.body();
                try {
                    if (resource.status) {
                        prefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                        ContinueAnyWay(resource,mProgressDialog,mode);
                    } else {
                        if(resource.kycStatus.equals(APPROVEDSTATUS)) {
                            showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                        }
                        else
                        {
                            showToast(R.string.profile_should_be_updated, ToastCustom.TYPE_ERROR);
                        }
                        ContinueAnyWay(null,mProgressDialog,mode);
                    }
                }
                catch (Exception ex)
                {
                    showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                    ContinueAnyWay(null,mProgressDialog,mode);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                ContinueAnyWay(null,mProgressDialog,mode);
            }

        });

    }

    private void ContinueAnyWay(GenericResponse resource,ProgressDialog dialog,int mode)
    {
        if(resource != null)
        {


           String seed=resource.result.message;
            if(mode==2) {
                showGeneralDialog(getString(R.string.seed), seed);
            }else
            {
                showGeneralDialog(getString(R.string.private_key), seed);
            }
        }

        AppUtil.hideProgressDialog(dialog, getActivity());
    }
    private void showGeneralDialog(String title,String msg){
        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.copy_to_clipboard, (dialog1, which) -> {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(title,msg);
                    clipboard.setPrimaryClip(clip);
                    ToastCustom.makeText(getActivity(), getString(R.string.copied_to_clipboard), ToastCustom.LENGTH_SHORT, ToastCustom.TYPE_GENERAL);
                })
                .setNegativeButton(R.string.dialog_continue,null)
                .show();

    }

    private void showDialogChangePassword() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout pwLayout = (LinearLayout) inflater.inflate(R.layout.modal_change_password2, null);

        AppCompatEditText currentPassword = pwLayout.findViewById(R.id.current_password);
        AppCompatEditText newPassword = pwLayout.findViewById(R.id.new_password);
        AppCompatEditText newPasswordConfirmation = pwLayout.findViewById(R.id.confirm_password);

        RelativeLayout entropyMeter = pwLayout.findViewById(R.id.entropy_meter);
        ProgressBar passStrengthBar = pwLayout.findViewById(R.id.pass_strength_bar);
        passStrengthBar.setMax(100);
        TextView passStrengthVerdict = pwLayout.findViewById(R.id.pass_strength_verdict);

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                newPassword.postDelayed(() -> {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        entropyMeter.setVisibility(View.VISIBLE);
                        setPasswordStrength(passStrengthVerdict, passStrengthBar, editable.toString());
                    }
                }, 200);
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setTitle(R.string.change_password)
                .setCancelable(false)
                .setView(pwLayout)
                .setPositiveButton(R.string.update, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        alertDialog.setOnShowListener(dialog -> {
            Button buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            buttonPositive.setOnClickListener(view -> {

                String currentPw = currentPassword.getText().toString();
                String newPw = newPassword.getText().toString();
                String newConfirmedPw = newPasswordConfirmation.getText().toString();
                if (!currentPw.equals(newPw)) {
                        if (newPw.equals(newConfirmedPw)) {
                            if (newConfirmedPw.length() < 4 || newConfirmedPw.length() > 255) {
                                ToastCustom.makeText(getActivity(), getString(R.string.invalid_password), ToastCustom.LENGTH_SHORT, ToastCustom.TYPE_ERROR);
                            } else if (pwStrength < 50) {
                                new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                                        .setTitle(R.string.app_name)
                                        .setMessage(R.string.weak_password)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.yes, (dialog1, which) -> {
                                            newPasswordConfirmation.setText("");
                                            newPasswordConfirmation.requestFocus();
                                            newPassword.setText("");
                                            newPassword.requestFocus();
                                        })
                                        .setNegativeButton(R.string.polite_no, (dialog1, which) -> {
                                            alertDialog.dismiss();
                                            updatePassword(newConfirmedPw, currentPw);
                                        })
                                        .show();
                            } else {
                                alertDialog.dismiss();
                                updatePassword(newConfirmedPw, currentPw);
                            }
                        } else {
                            newPasswordConfirmation.setText("");
                            newPasswordConfirmation.requestFocus();
                            ToastCustom.makeText(getActivity(), getString(R.string.password_mismatch_error), ToastCustom.LENGTH_SHORT, ToastCustom.TYPE_ERROR);
                        }

                } else {
                    newPassword.setText("");
                    newPasswordConfirmation.setText("");
                    newPassword.requestFocus();
                    ToastCustom.makeText(getActivity(), getString(R.string.change_password_new_matches_current), ToastCustom.LENGTH_LONG, ToastCustom.TYPE_ERROR);
                }
            });
        });
        alertDialog.show();
    }
    void updatePassword(@NonNull String password, @NonNull String fallbackPassword) {

        updatePasswordNetworkCall(password,fallbackPassword);

    }
    private void updatePasswordNetworkCall(String password,String fallbackPassword) {

        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(getActivity());
        mProgressDialog.show();
        PasswordChange passwordChange =new PasswordChange(fallbackPassword,password,password);
        Call<RegisterResponse> call = apiInterface.changePassword(passwordChange,getString(R.string.auth_prefix) + " " + prefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.d("TAG", response.code() + "");

                try {
                    RegisterResponse resource = response.body();
                    if(resource.status) {
                        showDialogPasswordChanged();
                    }
                    else
                    {
                        if(resource.result.message.equals("{'old_password': ['Invalid password']}"))
                        {
                            showToast(R.string.prev_password_incorrect, ToastCustom.TYPE_ERROR);
                        }
                        else {
                            showToast(R.string.change_password_error, ToastCustom.TYPE_ERROR);
                        }
                    }
                } catch (Exception ex) {
                    showToast(R.string.change_password_error, ToastCustom.TYPE_ERROR);
                }

                AppUtil.hideProgressDialog(mProgressDialog, getActivity());

            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                call.cancel();
                AppUtil.hideProgressDialog(mProgressDialog, getActivity());
                showToast(R.string.registeration_error, ToastCustom.TYPE_ERROR);
            }
        });
    }
    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }

    void setPasswordStrength(TextView passStrengthVerdict, ProgressBar passStrengthBar, String pw) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            int[] strengthVerdicts = {R.string.strength_weak, R.string.strength_medium, R.string.strength_normal, R.string.strength_strong};
            int[] strengthColors = {R.drawable.progress_red, R.drawable.progress_orange, R.drawable.progress_blue, R.drawable.progress_green};
            pwStrength = (int) Math.round(PasswordUtil.getStrength(pw));


            // red
            int pwStrengthLevel = 0;

            if (pwStrength >= 75) {
                // green
                pwStrengthLevel = 3;
            } else if (pwStrength >= 50) {
                // green
                pwStrengthLevel = 2;
            } else if (pwStrength >= 25) {
                // orange
                pwStrengthLevel = 1;
            }

            passStrengthBar.setProgress(pwStrength);
            passStrengthBar.setProgressDrawable(ContextCompat.getDrawable(getActivity(), strengthColors[pwStrengthLevel]));
            passStrengthVerdict.setText(getResources().getString(strengthVerdicts[pwStrengthLevel]));
        }
    }

    private void showDialogChangePin() {
        Intent intent = new Intent(getActivity(), PinEntryActivity.class);
        intent.putExtra(KEY_VALIDATING_PIN_FOR_RESULT, true);
        startActivityForResult(intent, REQUEST_CODE_VALIDATE_PIN);
    }
    private void initUi() {
        // Profile

        Gson gson = new Gson();
        String json = prefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        UserProfileResponse obj = gson.fromJson(json, UserProfileResponse.class);
        guidPref = findPreference("guid");
        if(obj != null && obj.result.walletId != null) {
            guidPref.setOnPreferenceClickListener(this);
            guidPref.setSummary(""+obj.result.walletId);
        } else
        {
           guidPref.setVisible(false);
        }

        emailPref = findPreference("email");
        if (obj != null) {
            emailPref.setOnPreferenceClickListener(this);
            setEmailSummary(obj.result.email);
        } else
        {
            emailPref.setVisible(false);
        }


        profileStatusPref = findPreference("profile_status");

        if(kycFlag.equals("NOT_STARTED")) {
            profileStatusPref.setSummary(R.string.status_not_updated);
        }
        else  if(kycFlag.equals("PENDING")) {
            profileStatusPref.setSummary(R.string.status_pending);
        }
        else  if(kycFlag.equals("ACCEPTED")) {
            profileStatusPref.setSummary(R.string.status_approved);
        }
        else  if(kycFlag.equals("REJECTED")) {
            profileStatusPref.setSummary(R.string.status_rejected);
        }else
        {
            profileStatusPref.setVisible(false);
        }

        Preference pinPref = findPreference("pin");
        pinPref.setOnPreferenceClickListener(this);
        Preference privateKeyPref = findPreference("private_key");
        Preference seedPref = findPreference("seed");
        if(kycFlag.equals("ACCEPTED")) {

            privateKeyPref.setOnPreferenceClickListener(this);


            seedPref.setOnPreferenceClickListener(this);
        }
        else
        {
            privateKeyPref.setVisible(false);
            seedPref.setVisible(false);
        }

        Preference changePasswordPref = findPreference("change_pw");
        changePasswordPref.setOnPreferenceClickListener(this);

        Preference aboutPref = findPreference("about");
        aboutPref.setOnPreferenceClickListener(this);
        Preference tosPref = findPreference("tos");
        tosPref.setOnPreferenceClickListener(this);


    }

    private void setEmailSummary(String email) {
        emailPref.setSummary(email);
    }

    private void showDialogGuid(int msg) {
        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, whichButton) -> {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("guid", guidPref.getSummary());
                    clipboard.setPrimaryClip(clip);
                    ToastCustom.makeText(getActivity(), getString(R.string.copied_to_clipboard), ToastCustom.LENGTH_SHORT, ToastCustom.TYPE_GENERAL);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
    public void showDialogPasswordChanged() {
        // Slight delay to prevent UI blinking issues
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (getActivity() != null) {
                new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                        .setTitle(R.string.password_changed)
                        .setMessage(R.string.password_changed_notice)
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        }, 300);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VALIDATE_PIN && resultCode == RESULT_OK) {
            if(pinValidateItem==3) {
                pinCodeValidatedForChange();
            }else if(pinValidateItem==2)
            {
              showKey(pinValidateItem);
            }else if(pinValidateItem ==1)
            {
                showKey(pinValidateItem);
            }
        }
    }
    void pinCodeValidatedForChange() {
        prefsUtil.removeValue(PrefsUtil.KEY_PIN_FAILS);
        prefsUtil.removeValue(PrefsUtil.KEY_PIN_IDENTIFIER);

        goToPinEntryPage();
    }

    public void goToPinEntryPage() {
        Intent intent = new Intent(getActivity(), PinEntryActivity.class);
        intent.putExtra("change",true);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
