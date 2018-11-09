package btm.odinandroidwallet.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import btm.odinandroidwallet.App;
import btm.odinandroidwallet.R;
import btm.odinandroidwallet.ui.customviews.MaterialProgressDialog;
import btm.odinandroidwallet.ui.customviews.PinEntryKeypad;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.profile.ProfileWizardActivity;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.DialogButtonCallback;
import btm.odinandroidwallet.util.PersistentPrefs;
import btm.odinandroidwallet.util.PrefsUtil;
import btm.odinandroidwallet.util.ViewUtils;


public class PinEntryActivity extends AppCompatActivity
{
    private PrefsUtil mPrefsUtil;
    private ImageView[] pinBoxArray;
    private static final int PIN_LENGTH = 4;
    private static final int MAX_ATTEMPTS = 4;
    public static final String KEY_VALIDATING_PIN_FOR_RESULT = "validating_pin";
    boolean mValidatingPinForResult = false;
    private MaterialProgressDialog materialProgressDialog;
    private AppUtil mAppUtil;
    String mUserEnteredPin = "";
    private static final Handler HANDLER = new Handler();
    private ClearPinNumberRunnable clearPinNumberRunnable = new ClearPinNumberRunnable();
    String mUserEnteredConfirmationPin;
    TextView titleBox;
    public static final String KEY_VALIDATED_PIN = "validated_pin";
    public static final int REQUEST_CODE_VALIDATE_PIN = 88;
    boolean change=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppUtil=new AppUtil(PinEntryActivity.this);
        mPrefsUtil=new PrefsUtil(PinEntryActivity.this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pin_entry);
        titleBox=findViewById(R.id.titleBox);


        if (isCreatingNewPin()) {
            titleBox.setText(R.string.create_pin);
        } else {
            titleBox.setText(R.string.pin_entry);
           // fetchInfoMessage();
        }
        pinBoxArray = new ImageView[PIN_LENGTH];
        pinBoxArray[0] = findViewById(R.id.pinBox0);
        pinBoxArray[1] = findViewById(R.id.pinBox1);
        pinBoxArray[2] = findViewById(R.id.pinBox2);
        pinBoxArray[3] = findViewById(R.id.pinBox3);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            if (extras.containsKey(KEY_VALIDATING_PIN_FOR_RESULT)) {
                mValidatingPinForResult = extras.getBoolean(KEY_VALIDATING_PIN_FOR_RESULT);
            }

            if(extras.containsKey("change")) {
                change = extras.getBoolean("change");
            }
        }
        if(mValidatingPinForResult && mPrefsUtil.getValue(PrefsUtil.KEY_PIN_IDENTIFIER, "").isEmpty())
        {
            resetApp();
        }
        checkPinFails();
        PinEntryKeypad keyboard=findViewById(R.id.keyboard);
        keyboard.setPadClickedListener(new PinEntryKeypad.OnPinEntryPadClickedListener() {
            @Override
            public void onNumberClicked(String number) {
                onPadClicked(number);
            }

            @Override
            public void onDeleteClicked() {
                onDeleteClickedMethod();
            }
        });


    }
    void onDeleteClickedMethod() {
        if (!mUserEnteredPin.isEmpty()) {
            // Remove last char from pin string
            mUserEnteredPin = mUserEnteredPin.substring(0, mUserEnteredPin.length() - 1);

            // Clear last box
            pinBoxArray[mUserEnteredPin.length()].setImageResource(R.drawable.rounded_view_blue_white_border);
        }
    }
    void onPadClicked(String string) {
        if (mUserEnteredPin.length() == PIN_LENGTH) {
            return;
        }

        // Append tapped #
        mUserEnteredPin = mUserEnteredPin + string;

        for (int i = 0; i < mUserEnteredPin.length(); i++) {
            // Ensures that all necessary dots are filled
            pinBoxArray[i].setImageResource(R.drawable.rounded_view_dark_blue);
        }

        // Perform appropriate action if PIN_LENGTH has been reached
        if (mUserEnteredPin.length() == PIN_LENGTH) {

            // Throw error on '0000' to avoid server-side type issue
            if (mUserEnteredPin.equals("0000")) {
                showErrorToast(R.string.zero_pin);
                clearPinViewAndReset();
                if (isCreatingNewPin()) {
                    setTitleString(R.string.create_pin);
                }
                return;
            }

            // Only show warning on first entry and if user is creating a new PIN
            if (isCreatingNewPin() && isPinCommon(mUserEnteredPin) && mUserEnteredConfirmationPin == null) {
                showCommonPinWarning(new DialogButtonCallback() {
                    @Override
                    public void onPositiveClicked() {
                        clearPinViewAndReset();
                    }

                    @Override
                    public void onNegativeClicked() {
                        validateAndConfirmPin();
                    }
                });

                // If user is changing their PIN and it matches their old one, disallow it
            }  else {
                validateAndConfirmPin();
            }
        }
    }
    void validateAndConfirmPin() {
        // Validate

        if (!mPrefsUtil.getValue(PrefsUtil.KEY_PIN_IDENTIFIER, "").isEmpty()) {
            setTitleVisibility(View.INVISIBLE);
            validatePIN(mUserEnteredPin);
        } else if (mUserEnteredConfirmationPin == null) {
            // End of Create -  Change to Confirm
            mUserEnteredConfirmationPin = mUserEnteredPin;
            mUserEnteredPin = "";
            setTitleString(R.string.confirm_pin);
            clearPinBoxes();
        } else if (mUserEnteredConfirmationPin.equals(mUserEnteredPin)) {
            // End of Confirm - Pin is confirmed
            createNewPin(mUserEnteredPin);
        } else {
            // End of Confirm - Pin Mismatch
            showErrorToast(R.string.pin_mismatch_error);
            setTitleString(R.string.create_pin);
            clearPinViewAndReset();
        }
    }
    private void createNewPin(String pin) {
      //To be done hashing the pin and store it in prefs
        try {
            String token=mPrefsUtil.getValue(PrefsUtil.USER_TOKEN,"");
            String hash = AppUtil.SHA256(pin)+AppUtil.SHA256(token);
            mPrefsUtil.setValue(PrefsUtil.KEY_PIN_IDENTIFIER,hash);
            mPrefsUtil.setValue(PrefsUtil.KEY_PIN_FAILS, 0);
            if(change)
            {
                new AlertDialog.Builder(PinEntryActivity.this, R.style.AlertDialogStyle)
                        .setTitle(R.string.update_pin_success)
                        .setMessage(R.string.update_pin_success_msg)
                        .setPositiveButton(R.string.ok_cap, (dialog, which) -> finish())
                        .show();
            }
            else
            {
                getWalletAddressAndProceed();
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
    private void validatePIN(String pin) {
        //to be done hashing the pin and compare it with one in prefs
        try {
            String token=mPrefsUtil.getValue(PrefsUtil.USER_TOKEN,"");
            String hash = AppUtil.SHA256(pin)+ AppUtil.SHA256(token);
            String pinHash=mPrefsUtil.getValue(PrefsUtil.KEY_PIN_IDENTIFIER,"");
            if(hash.equals(pinHash))
            {
                if (mValidatingPinForResult) {
                    finishWithResultOk();
                }
                mPrefsUtil.setValue(PrefsUtil.KEY_PIN_FAILS, 0);
            }
            else
            {
                handleValidateFailure();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    private void handleValidateFailure() {
        checkPinFails();
        if (mValidatingPinForResult) {
            incrementFailureCount();
        } else {
            incrementFailureCountAndRestart();
        }
    }
    private void incrementFailureCount() {
        int fails = mPrefsUtil.getValue(PrefsUtil.KEY_PIN_FAILS, 0);
        mPrefsUtil.setValue(PrefsUtil.KEY_PIN_FAILS, ++fails);
        showErrorToast(R.string.invalid_pin);
        mUserEnteredPin = "";
        for (ImageView textView : getPinBoxArray()) {
            textView.setImageResource(R.drawable.rounded_view_blue_white_border);
        }
        setTitleVisibility(View.VISIBLE);
        setTitleString(R.string.pin_entry);
    }

    void incrementFailureCountAndRestart() {
        int fails = mPrefsUtil.getValue(PrefsUtil.KEY_PIN_FAILS, 0);
        mPrefsUtil.setValue(PrefsUtil.KEY_PIN_FAILS, ++fails);
        showErrorToast(R.string.invalid_pin);
        restartPageAndClearTop();
    }

    public void restartPageAndClearTop() {
        Intent intent = new Intent(this, PinEntryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public ImageView[] getPinBoxArray() {
        return pinBoxArray;
    }
    void getWalletAddressAndProceed() {
// Grab wallet address from server
            mAppUtil.startMainActivity();

    }
    public void finishWithResultOk() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
    public void setTitleVisibility(@ViewUtils.Visibility int visibility) {
        titleBox.setVisibility(visibility);
    }
    public void showCommonPinWarning(DialogButtonCallback callback) {
        new AlertDialog.Builder(PinEntryActivity.this, R.style.AlertDialogStyle)
                .setTitle(R.string.common_pin_dialog_title)
                .setMessage(R.string.common_pin_dialog_message)
                .setPositiveButton(R.string.common_pin_dialog_try_again, (dialogInterface, i) -> callback.onPositiveClicked())
                .setNegativeButton(R.string.common_pin_dialog_continue, (dialogInterface, i) -> callback.onNegativeClicked())
                .setCancelable(false)
                .create()
                .show();
    }
    private boolean isPinCommon(String pin) {
        List<String> commonPins = Arrays.asList("1234", "1111", "1212", "7777", "1004");
        return commonPins.contains(pin);
    }
    public void setTitleString(@StringRes int title) {

        HANDLER.postDelayed(() -> titleBox.setText(title), 200);
    }
    void clearPinViewAndReset() {
        clearPinBoxes();
        mUserEnteredConfirmationPin = null;
    }
    void clearPinBoxes() {
        mUserEnteredPin = "";
        HANDLER.postDelayed(clearPinNumberRunnable, 200);

    }
    private class ClearPinNumberRunnable implements Runnable {
        ClearPinNumberRunnable() {
            // Empty constructor
        }

        @Override
        public void run() {
            for (ImageView pinBox : pinBoxArray) {
                // Reset PIN buttons to blank
                pinBox.setImageResource(R.drawable.rounded_view_blue_white_border);
            }
        }
    }

    boolean isCreatingNewPin() {
        return mPrefsUtil.getValue(PrefsUtil.KEY_PIN_IDENTIFIER, "").isEmpty();
    }
    private void checkPinFails() {
        int fails = mPrefsUtil.getValue(PrefsUtil.KEY_PIN_FAILS, 0);
        if (fails >= MAX_ATTEMPTS) {
            showErrorToast(R.string.pin_4_strikes);
            showMaxAttemptsDialog();
        }
    }
    private void showErrorToast(@StringRes int message) {
        dismissProgressDialog();
        showToast(message, ToastCustom.TYPE_ERROR);
    }

    public void dismissProgressDialog() {
        if (materialProgressDialog != null && materialProgressDialog.isShowing()) {
            materialProgressDialog.dismiss();
            materialProgressDialog = null;
        }
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(PinEntryActivity.this, getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }
    public void showMaxAttemptsDialog() {
        new AlertDialog.Builder(PinEntryActivity.this, R.style.AlertDialogStyle)
                .setTitle(R.string.app_name)
                .setMessage(R.string.pin_4_strikes)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_cap, (dialog, whichButton) -> resetApp())
                .show();
    }


    public void showProgressDialog(@StringRes int messageId, @Nullable String suffix) {
        dismissProgressDialog();
        materialProgressDialog = new MaterialProgressDialog(PinEntryActivity.this);
        materialProgressDialog.setCancelable(false);
        if (suffix != null) {
            materialProgressDialog.setMessage(getString(messageId) + suffix);
        } else {
            materialProgressDialog.setMessage(getString(messageId));
        }

        if (PinEntryActivity.this != null && !PinEntryActivity.this.isFinishing()) materialProgressDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(isValidatingPinForResult()) {
            finishWithResultCanceled();
        }else
        {
            super.onBackPressed();
        }
    }
    private void finishWithResultCanceled() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    public boolean isValidatingPinForResult() {
        return mValidatingPinForResult;
    }
private void  resetApp()
{
    mAppUtil.clearCredentialsAndRestart();
}
}