package btm.odinandroidwallet.ui.exchange;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.ExchangeSpinnerAdapter;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.PortfolioValue;
import btm.odinandroidwallet.networking.retrofit.pojo.PreIcoCompanyListResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.PreIcoCompanyListValue;
import btm.odinandroidwallet.networking.retrofit.pojo.PurchaseTokenBody;
import btm.odinandroidwallet.networking.retrofit.pojo.SendTokenBody;
import btm.odinandroidwallet.networking.retrofit.pojo.SendTokenResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.auth.LoginActivity;
import btm.odinandroidwallet.ui.auth.PinEntryActivity;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.home.MainActivity;
import btm.odinandroidwallet.ui.ico.IcoDetailsActivity;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;
import btm.odinandroidwallet.util.TimerServiceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static btm.odinandroidwallet.ui.auth.PinEntryActivity.KEY_VALIDATING_PIN_FOR_RESULT;
import static btm.odinandroidwallet.ui.auth.PinEntryActivity.REQUEST_CODE_VALIDATE_PIN;

public class ExchangeFragment extends Fragment {
    TextView more;
    TextView availableTokens;
    private String APPROVEDSTATUS = "ACCEPTED";
    private String kycFlag = "";
    private PrefsUtil mPrefsUtil;
    APIInterface apiInterface;
    Button next;
    private List<PreIcoCompanyListValue> companies = new ArrayList<PreIcoCompanyListValue>();
    Spinner spinner;
    String address, amount,odinAmount;
    EditText amountTxt;
    EditText amountOdin;
    TextView rateValue;
    double rate;
    UserProfileResponse obj;
    LinearLayout mainContent;
    RelativeLayout altContent;
    TextView contentText;
    int curNoOfDecimals=0;

    public static ExchangeFragment newInstance() {
        ExchangeFragment fragment = new ExchangeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exchange_fragment, container, false);
        contentText = rootView.findViewById(R.id.content_text);
        mainContent = rootView.findViewById(R.id.main_content);
        altContent = rootView.findViewById(R.id.alt_content);
        mPrefsUtil = new PrefsUtil(getActivity());
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
        kycFlag = mPrefsUtil.getValue(PrefsUtil.KYC_STATUS, "");
        Gson gson = new Gson();
        String json = mPrefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        obj = gson.fromJson(json, UserProfileResponse.class);
        amountTxt = rootView.findViewById(R.id.amt_pre_ico);
        amountOdin = rootView.findViewById(R.id.amt_odin);

        spinner = (Spinner) rootView.findViewById(R.id.spinner_token);

        if (kycFlag.equals(APPROVEDSTATUS)) {
            getCompaniesNetworkCall();
        } else {
            mainContent.setVisibility(View.GONE);
            altContent.setVisibility(View.VISIBLE);
            contentText.setText(getResources().getString(R.string.profile_should_be_updated));
        }

        more = rootView.findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), IcoDetailsActivity.class);
                int position = spinner.getSelectedItemPosition();
                i.putExtra("ico", companies.get(position));
                startActivity(i);
            }
        });
        availableTokens = rootView.findViewById(R.id.available_tokens);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PreIcoCompanyListValue item = companies.get(i);
                if (obj != null) {
                    if (obj.result.isSubscribed) {
                        double supply = item.remainSupply + item.remainPremiumSupply;
                        availableTokens.setText(getResources().getText(R.string.available_tokens) + " " + supply + " " + item.preIcoTicker);
                    } else {
                        availableTokens.setText(getResources().getText(R.string.available_tokens) + " " + item.remainSupply + " " + item.preIcoTicker);
                    }

                } else {
                    availableTokens.setText(getResources().getText(R.string.available_tokens) + " " + item.remainSupply + " " + item.preIcoTicker);

                }
                curNoOfDecimals=item.noOfDecimals;
                if (item.noOfDecimals > 0) {
                    amountTxt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    amountTxt.setFilters(new InputFilter[]{
                            new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {

                                @Override
                                public CharSequence filter(CharSequence source, int start, int end,
                                                           Spanned dest, int dstart, int dend) {
                                    StringBuilder builder = new StringBuilder(dest);
                                    builder.insert(dstart, source);
                                    String temp = builder.toString();
                                    if (temp.contains(".")) {
                                        temp = temp.substring(temp.indexOf(".") + 1);
                                        if (temp.length() > item.noOfDecimals) {
                                            showErrorToast(R.string.too_many_decimal_points);
                                            return "";
                                        }
                                    }
                                    return super.filter(source, start, end, dest, dstart, dend);
                                }
                            }
                    });
                } else {
                    amountTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                more.setText(getResources().getText(R.string.read_more) + " " + item.preIcoTicker);

                rateValue.setText("1 ODIN = " + "" + rate + " " + item.preIcoTicker);
                amountOdin.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        next = rootView.findViewById(R.id.command_next);


        //disable next button for 30s after every transactions
        if(AppUtil.isMyServiceRunning(getContext(),TimerServiceUtil.class)){
            if(next!=null)
                next.setText(getResources().getString(R.string.please_wait));
            next.setEnabled(false);
            next.setBackgroundColor(getResources().getColor(R.color.light_grey));
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = amountTxt.getText().toString();
                odinAmount=amountOdin.getText().toString();
                double odinAmountValue = 0.0;
                double amtDbl = 0.0;
                try {
                    amtDbl=Double.valueOf(amount);
                    odinAmountValue=Double.valueOf(odinAmount);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (kycFlag.equals(APPROVEDSTATUS) && obj != null) {
                    if(odinAmountValue > obj.result.odinHolding)
                    {
                        showErrorToast(R.string.no_enough_odin_balance);
                    }
                    else if (amount == null || amount.isEmpty() || amtDbl == 0) {
                        showErrorToast(R.string.empty_amount);
                    } else if (Double.valueOf(amount) > companies.get(spinner.getSelectedItemPosition()).totalSupply) {
                        showErrorToast(R.string.not_enough_token_supply);
                    } else if (AppUtil.minEthBalance > obj.result.etherBalance) {
                        showErrorToast(R.string.not_enough_transaction_fee);
                    } else {
                        Intent intent = new Intent(getActivity(), PinEntryActivity.class);
                        intent.putExtra(KEY_VALIDATING_PIN_FOR_RESULT, true);
                        startActivityForResult(intent, REQUEST_CODE_VALIDATE_PIN);
                    }
                } else {
                    showToast(R.string.unverified_profile, ToastCustom.TYPE_ERROR);
                }
            }
        });
        rateValue = rootView.findViewById(R.id.rate_value);
        amountOdin.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(s.toString())) {
                    try {
                        double value = Double.parseDouble(s.toString());
                        double result=Double.valueOf(value*rate).doubleValue();
                        NumberFormat nf = NumberFormat.getNumberInstance();
                        nf.setMaximumFractionDigits(curNoOfDecimals);
                        nf.setMinimumFractionDigits(1);
                        amountTxt.setText(nf.format(result));
                    } catch (Exception Ex) {
                    }
                } else {
                    amountTxt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        return rootView;
    }

    private void showErrorToast(@StringRes int message) {
        showToast(message, ToastCustom.TYPE_ERROR);
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VALIDATE_PIN && resultCode == RESULT_OK) {
            //Contact send token api
            int position = spinner.getSelectedItemPosition();
            PreIcoCompanyListValue value = companies.get(position);

            sendToken(value);
        }

    }

    private void sendToken(PreIcoCompanyListValue value) {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(getActivity());
        mProgressDialog.show();
        PurchaseTokenBody body = new PurchaseTokenBody(value.preIcoTicker, Double.valueOf(amount), Double.valueOf(odinAmount));
        Call<SendTokenResponse> call = apiInterface.purchaseToken(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"), body);
        call.enqueue(new Callback<SendTokenResponse>() {
            @Override
            public void onResponse(Call<SendTokenResponse> call, Response<SendTokenResponse> response) {

                Log.d("TAG", response.code() + "");
                //start timer to disable button of submit for 30s
                getActivity().startService(new Intent(getActivity(), TimerServiceUtil.class));
                try {
                    SendTokenResponse resource = response.body();
                    if (resource.status) {
                        mPrefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                                .setTitle(R.string.exchange_token_success)
                                .setMessage(resource.result.message)
                                .setPositiveButton(R.string.dialog_continue, null)
                                .show();
                    } else {
                        showStringToast(resource.result.message, ToastCustom.TYPE_ERROR);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    showToast(R.string.exchange_pre_ico_failed, ToastCustom.TYPE_ERROR);
                }

                AppUtil.hideProgressDialog(mProgressDialog, getActivity());
            }

            @Override
            public void onFailure(Call<SendTokenResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                } else {
                    showToast(R.string.exchange_pre_ico_failed, ToastCustom.TYPE_ERROR);
                }

                AppUtil.hideProgressDialog(mProgressDialog, getActivity());
            }

        });


    }

    public void showStringToast(String message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), message, ToastCustom.LENGTH_SHORT, toastType);
    }

    private void goToMainActivity() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void getCompaniesNetworkCall() {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(getActivity());
        mProgressDialog.show();
        Call<PreIcoCompanyListResponse> call = apiInterface.getCompanyList(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<PreIcoCompanyListResponse>() {
            @Override
            public void onResponse(Call<PreIcoCompanyListResponse> call, Response<PreIcoCompanyListResponse> response) {

                Log.d("TAG", response.code() + "");
                PreIcoCompanyListResponse resource = response.body();
                try {
                    if (resource.status) {
                        //   mPrefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                        ContinueAnyWay(resource, mProgressDialog);
                    } else {
                        if (resource.kycStatus.equals(APPROVEDSTATUS)) {
                            showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                        } else {
                            showToast(R.string.profile_should_be_updated, ToastCustom.TYPE_ERROR);
                        }
                        ContinueAnyWay(null, mProgressDialog);
                    }
                } catch (Exception ex) {
                    showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                    ContinueAnyWay(null, mProgressDialog);
                }
            }

            @Override
            public void onFailure(Call<PreIcoCompanyListResponse> call, Throwable t) {
                showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                ContinueAnyWay(null, mProgressDialog);
            }

        });
    }

    private void ContinueAnyWay(PreIcoCompanyListResponse resource, ProgressDialog dialog) {
        if (resource != null) {
            companies = resource.result.values;
            companies = filterItems();
            if (companies.size() > 0) {
                spinner.setAdapter(new ExchangeSpinnerAdapter(getActivity(), R.layout.spinner_layout, companies));
            } else {
                mainContent.setVisibility(View.GONE);
                altContent.setVisibility(View.VISIBLE);
                contentText.setText(getResources().getString(R.string.no_current_icos));
            }

        }else
        {
            mainContent.setVisibility(View.GONE);
            altContent.setVisibility(View.VISIBLE);
            contentText.setText(getResources().getString(R.string.no_current_icos));
        }

        AppUtil.hideProgressDialog(dialog, getActivity());
    }

    private List<PreIcoCompanyListValue> filterItems() {
        List<PreIcoCompanyListValue> items = new ArrayList<>();
        for (PreIcoCompanyListValue value : companies) {
            if (TextUtils.isEmpty(value.contractAddress)) {
                items.add(value);
            }
        }
        return items;
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Notification that timer is done and we can enable the button back
            boolean isTimerFinished = intent.getBooleanExtra("countdownFinished", false);
            if(isTimerFinished){
                next.setText(getResources().getString(R.string.dialog_continue));
                next.setEnabled(true);
                next.setBackgroundColor(getResources().getColor(R.color.product_green_medium));
            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(br, new IntentFilter(TimerServiceUtil.COUNTDOWN_BR));
    }
    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(br);
    }

    @Override
    public void onStop() {
        try {
            getActivity().unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }
}