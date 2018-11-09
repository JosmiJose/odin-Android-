package btm.odinandroidwallet.ui.createwallet;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputEditText;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.RegisterResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.User;
import btm.odinandroidwallet.ui.auth.PinEntryActivity;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.ViewUtils;
import info.blockchain.wallet.util.FormatsUtil;
import info.blockchain.wallet.util.PasswordUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 *
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CreateWalletActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    ConstraintSet set = new ConstraintSet();
    ConstraintLayout mainConstraintLayout;
    int passwordStrength = 0;
    ProgressBar bar;
    TextInputEditText pass;
    TextInputEditText pass_confirm;
    TextInputEditText emailInput;
    APIInterface apiInterface;
    AppUtil appUtil;
    int[] strengthVerdicts = new int[]{
            R.string.strength_weak,
            R.string.strength_medium,
            R.string.strength_normal,
            R.string.strength_strong
    };
    int[] strengthColors = new int[]{
            R.drawable.progress_red,
            R.drawable.progress_orange,
            R.drawable.progress_blue,
            R.drawable.progress_green
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtil=new AppUtil(this);
        apiInterface = APIClient.getClient(this).create(APIInterface.class);
        setContentView(R.layout.activity_create_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        TextView title = findViewById(R.id.title);
        title.setText(getResources().getString(R.string.create_a_wallet));
        mainConstraintLayout = findViewById(R.id.mainConstraintLayout);
        set.clone(mainConstraintLayout);
        TextView tos = findViewById(R.id.tos);
        tos.setMovementMethod(LinkMovementMethod.getInstance());
        Button commandNext = findViewById(R.id.command_next);
        commandNext.setClickable(false);
        bar = findViewById(R.id.pass_strength_bar);
        bar.setMax(100 * 10);
        pass = findViewById(R.id.wallet_pass);
        pass_confirm = findViewById(R.id.wallet_pass_confirm);
        pass.setOnFocusChangeListener(this);
        emailInput = findViewById(R.id.email_address);
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                showEntropyContainer();
                calculateEntropy(editable.toString());
                hideShowCreateButton(editable.toString().length(), pass_confirm.getText().toString().length());

            }
        });

        pass_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                hideShowCreateButton(pass.getText().toString().length(), editable.toString().length());

            }
        });

        commandNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextClicked();
            }
        });

        String text = getString(R.string.agree_terms_of_service) + " ";
        String text2 = getString(R.string.blockchain_tos);

        SpannableString spannable = new SpannableString(text + text2);

        spannable.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary_blue_accent)),
                text.length(),
                text.length() + text2.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        tos.setText(spannable, TextView.BufferType.SPANNABLE);
        tos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url=AppUtil.url+"api/tnc/";
                AppUtil.showWebPage(CreateWalletActivity.this,url,R.string.options_tos);
            }
        });
        hideEntropyContainer();

    }

    private void onNextClicked() {
        String email = emailInput.getText().toString().trim();
        String password1 = pass.getText().toString();
        String password2 = pass_confirm.getText().toString();

        validateCredentials(email, password1, password2);
    }

    private void validateCredentials(String email, String password1, String password2) {
        if (!FormatsUtil.isValidEmailAddress(email)) {
            showToast(R.string.invalid_email, ToastCustom.TYPE_ERROR);
        } else if (password1.length() < 4) {
            showToast(R.string.invalid_password_too_short, ToastCustom.TYPE_ERROR);
        } else if (password1.length() > 255) {
            showToast(R.string.invalid_password, ToastCustom.TYPE_ERROR);
        } else if (!password1.equals(password2)) {
            showToast(R.string.password_mismatch_error, ToastCustom.TYPE_ERROR);
        } else if (passwordStrength < 50) {
            new AlertDialog.Builder(CreateWalletActivity.this, R.style.AlertDialogStyle)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.weak_password)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialog1, which) -> {
                        pass_confirm.setText("");
                        pass_confirm.requestFocus();
                        pass.setText("");
                        pass.requestFocus();
                    })
                    .setNegativeButton(R.string.polite_no, (dialog1, which) -> {
                        createWallet(email, password1);
                    })
                    .show();
        }else {
            createWallet(email, password1);
        }
    }

    private void showToast(int message, String toastType) {

        ToastCustom.makeText(CreateWalletActivity.this, getText(message), ToastCustom.LENGTH_LONG, toastType);
    }

    private void calculateEntropy(String password) {
        passwordStrength = (int) Math.round(PasswordUtil.getStrength(password));
        setEntropyStrength(passwordStrength);
        if (between(passwordStrength, 0, 25)) {
            setEntropyLevel(0);
        } else if (between(passwordStrength, 26, 50)) {
            setEntropyLevel(1);
        } else if (between(passwordStrength, 51, 75)) {
            setEntropyLevel(2);
        } else if (between(passwordStrength, 76, 100)) {
            setEntropyLevel(3);
        }


    }

    private void setEntropyLevel(int level) {
        TextView passStrengthText = findViewById(R.id.pass_strength_verdict);
        passStrengthText.setText(strengthVerdicts[level]);
        bar.setProgressDrawable(ContextCompat.getDrawable(this, strengthColors[level]));
        // entropy_container.pass_strength_verdict.setText(strengthVerdicts[level])
    }

    private void showEntropyContainer() {
        TransitionManager.beginDelayedTransition(mainConstraintLayout);
        set.setVisibility(R.id.entropy_container, ConstraintSet.VISIBLE);
        set.applyTo(mainConstraintLayout);
    }

    private void setEntropyStrength(int score) {
        ObjectAnimator animator1 = ObjectAnimator.ofInt(
                bar,
                "progress",
                bar.getProgress(),
                score * 10
        );
        animator1.setDuration(300);
        animator1.setInterpolator(new DecelerateInterpolator());
        animator1.start();
    }

    public static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        if (i >= minValueInclusive && i <= maxValueInclusive)
            return true;
        else
            return false;
    }

    private void hideShowCreateButton(int password1Length, int password2Length) {
        if (password1Length > 0 && password1Length == password2Length) {
            showCreateWalletButton();
        } else {
            hideCreateWalletButton();
        }
    }

    private void hideEntropyContainer() {
        TransitionManager.beginDelayedTransition(mainConstraintLayout);
        set.setVisibility(R.id.entropy_container, ConstraintSet.INVISIBLE);
        set.applyTo(mainConstraintLayout);
    }

    private void showCreateWalletButton() {
        TransitionManager.beginDelayedTransition(mainConstraintLayout);
        set.setVisibility(R.id.command_next, ConstraintSet.VISIBLE);
        set.applyTo(mainConstraintLayout);
    }

    private void hideCreateWalletButton() {
        TransitionManager.beginDelayedTransition(mainConstraintLayout);
        set.setVisibility(R.id.command_next, ConstraintSet.GONE);
        set.applyTo(mainConstraintLayout);
    }

    private void createWallet(String email, String password) {
        User user = new User(email, password);
        registerNetworkCall(user);
    }

    private void registerNetworkCall(User user) {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(this);
        mProgressDialog.show();
        Call<RegisterResponse> call = apiInterface.createUser(user);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.d("TAG", response.code() + "");

                try {
                    RegisterResponse resource = response.body();
                    if(resource.status) {
                        backToMain();
                    }
                    else
                    {
                        if(TextUtils.isEmpty(resource.result.message)) {
                            showToast(R.string.registeration_error, ToastCustom.TYPE_ERROR);
                        }
                        else
                        {
                            showStringToast(resource.result.message, ToastCustom.TYPE_ERROR);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showToast(R.string.registeration_error, ToastCustom.TYPE_ERROR);
                }

                AppUtil.hideProgressDialog(mProgressDialog, CreateWalletActivity.this);

            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                }
                else {
                    showToast(R.string.registeration_error, ToastCustom.TYPE_ERROR);
                }
                call.cancel();
                AppUtil.hideProgressDialog(mProgressDialog, CreateWalletActivity.this);

            }
        });
    }
    public void showStringToast(String message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(CreateWalletActivity.this, message, ToastCustom.LENGTH_SHORT, toastType);
    }
    private void backToMain() {
        ViewUtils.hideKeyboard(this);
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.success_register_title)
                .setMessage(R.string.success_register)
                .setPositiveButton(R.string.back_to_landing, (dialog, which) -> appUtil.restartApp())
                .show();

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            showEntropyContainer();
        } else {
            hideEntropyContainer();
        }
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
