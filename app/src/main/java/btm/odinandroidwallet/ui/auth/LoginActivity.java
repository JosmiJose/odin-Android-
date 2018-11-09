package btm.odinandroidwallet.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.EmailResetBody;
import btm.odinandroidwallet.networking.retrofit.pojo.GenericResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.LoginResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.User;
import btm.odinandroidwallet.ui.customviews.MaterialProgressDialog;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Waleed on 6/3/2018.
 */

public class LoginActivity extends AppCompatActivity {
    TextInputEditText emailEdit;
    TextInputEditText passwordEdit;
    private MaterialProgressDialog mProgressDialog;
    APIInterface apiInterface;
    private PrefsUtil mPrefsUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefsUtil = new PrefsUtil(LoginActivity.this);
        apiInterface = APIClient.getClient(LoginActivity.this).create(APIInterface.class);
        setContentView(R.layout.activity_manual_pairing);
        Toolbar toolbar = findViewById(R.id.toolbar_general);
        emailEdit = findViewById(R.id.wallet_id);
        passwordEdit = findViewById(R.id.wallet_pass);
        setupToolbar();
        TextView title = findViewById(R.id.title);
        title.setText(R.string.login);
        TextView commandNext = findViewById(R.id.command_next);
        commandNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onContinueClicked();
            }
        });
        TextView forgot = findViewById(R.id.forgot_password);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogResetPassword();
            }
        });

    }

    private void showDialogResetPassword() {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout pwLayout = (LinearLayout) inflater.inflate(R.layout.model_reset_password, null);

        AppCompatEditText currentEmail = pwLayout.findViewById(R.id.current_email);


        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.reset_password)
                .setCancelable(false)
                .setView(pwLayout)
                .setPositiveButton(R.string.update, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        alertDialog.setOnShowListener(dialog -> {
            Button buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            buttonPositive.setOnClickListener(view -> {
                dialog.dismiss();
                String currentEm = currentEmail.getText().toString();
                resetPasswordNetworkCall(currentEm);
            });
        });
        alertDialog.show();
    }

    private void resetPasswordNetworkCall(String email) {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(this);
        mProgressDialog.show();
        Call<GenericResponse> call = apiInterface.resetPassword(new EmailResetBody(email));
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Log.d("TAG", response.code() + "");

                String displayResponse = "";
                try {
                    GenericResponse resource = response.body();
                    if (resource.status) {
                        new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogStyle)
                                .setTitle(R.string.email_sent)
                                .setMessage(R.string.email_sent_msg)
                                .setCancelable(true)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    } else {
                        showToast(R.string.reset_password_error, ToastCustom.TYPE_ERROR);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showToast(R.string.reset_password_error, ToastCustom.TYPE_ERROR);
                }

                AppUtil.hideProgressDialog(mProgressDialog, LoginActivity.this);

            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                }
                else {
                    showToast(R.string.reset_password_error, ToastCustom.TYPE_ERROR);
                }
                call.cancel();
                AppUtil.hideProgressDialog(mProgressDialog, LoginActivity.this);

            }
        });
    }

    void onContinueClicked() {
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if (email == null || email.isEmpty()) {
            showErrorToast(R.string.invalid_email);
        } else if (password == null || password.isEmpty()) {
            showErrorToast(R.string.invalid_password);
        } else {
            verifyPassword(password, email);
        }
    }

    private void verifyPassword(String password, String guid) {
        User user = new User(guid, password);
        loginNetworkCall(user);
    }

    private void loginNetworkCall(User user) {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(this);
        mProgressDialog.show();
        Call<LoginResponse> call = apiInterface.login(user);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("TAG", response.code() + "");

                String displayResponse = "";
                try {
                    LoginResponse resource = response.body();
                    if(resource.isEmailVerified != null)
                    {
                        if(resource.isEmailVerified) {
                            checkStatus(resource);
                        }else
                        {
                            new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogStyle)
                                    .setTitle(R.string.email_not_verified)
                                    .setMessage(R.string.email_not_verified_msg)
                                    .setCancelable(true)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        }
                    }else
                    {
                        checkStatus(resource);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    showToast(R.string.login_error, ToastCustom.TYPE_ERROR);
                }

                AppUtil.hideProgressDialog(mProgressDialog, LoginActivity.this);

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                }
                else {
                    showToast(R.string.login_error, ToastCustom.TYPE_ERROR);
                }
                call.cancel();
                AppUtil.hideProgressDialog(mProgressDialog, LoginActivity.this);

            }
        });
    }

    private void checkStatus(LoginResponse resource) {
        if (resource.status) {
            mPrefsUtil.setValue(PrefsUtil.USER_TOKEN, resource.result.key);
            mPrefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
            goToPinPage();
        } else {
            if(resource.result.message != null && !TextUtils.isEmpty(resource.result.message))
            {
                showStringToast(resource.result.message, ToastCustom.TYPE_ERROR);
            }
            else {
                showToast(R.string.login_error, ToastCustom.TYPE_ERROR);
            }
        }
    }

    public void goToPinPage() {
        startActivity(new Intent(this, PinEntryActivity.class));
    }

    private void showErrorToast(@StringRes int message) {
        dismissProgressDialog();
        resetPasswordField();
        showToast(message, ToastCustom.TYPE_ERROR);
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void resetPasswordField() {
        if (!isFinishing()) passwordEdit.setText("");
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(this, getString(message), ToastCustom.LENGTH_SHORT, toastType);
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
    public void showStringToast(String message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(LoginActivity.this, message, ToastCustom.LENGTH_SHORT, toastType);
    }
}
