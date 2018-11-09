package btm.odinandroidwallet.ui.send;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.SendSpinnerAdapter;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.PortfolioResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.PortfolioValue;
import btm.odinandroidwallet.networking.retrofit.pojo.SendTokenBody;
import btm.odinandroidwallet.networking.retrofit.pojo.SendTokenResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.auth.PinEntryActivity;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.home.MainActivity;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;
import btm.odinandroidwallet.util.TimerServiceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static btm.odinandroidwallet.ui.auth.PinEntryActivity.KEY_VALIDATING_PIN_FOR_RESULT;
import static btm.odinandroidwallet.ui.auth.PinEntryActivity.REQUEST_CODE_VALIDATE_PIN;

public class SendFragment extends Fragment {
    private List<PortfolioValue> itemList = new ArrayList<>();
    Button next;
    private PrefsUtil mPrefsUtil;
    private String APPROVEDSTATUS = "ACCEPTED";
    private String kycFlag = "";
    APIInterface apiInterface;
    TextInputEditText addressEdit;
    TextInputEditText amountEdit;
    String address, amount;
    Spinner spinner;
    String scanData;
    ScrollView mainContent;
    RelativeLayout altContent;
    private String odinScannedAddress;
    private String otherScannedAddress;
    TextView altText;
    double odinBalance = 0.0;
    double ethBalance = 0.0;

    public static SendFragment newInstance(String scanData) {
        SendFragment fragment = new SendFragment();
        fragment.scanData = scanData;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.send_fragment, container, false);
        altText = rootView.findViewById(R.id.alt_text);
        decodeScannedData();
        mainContent = rootView.findViewById(R.id.main_content);
        altContent = rootView.findViewById(R.id.alt_content);
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
        mPrefsUtil = new PrefsUtil(getActivity());
        kycFlag = mPrefsUtil.getValue(PrefsUtil.KYC_STATUS, "");
        addressEdit = rootView.findViewById(R.id.address);


        amountEdit = rootView.findViewById(R.id.amount);
        spinner = (Spinner) rootView.findViewById(R.id.spinner_token);
        if (kycFlag.equals(APPROVEDSTATUS)) {
            getPortfolioNetworkCall();
        } else {
            mainContent.setVisibility(View.GONE);
            altContent.setVisibility(View.VISIBLE);
        }

        Gson gson = new Gson();
        String json = mPrefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        UserProfileResponse obj = gson.fromJson(json, UserProfileResponse.class);

        if (obj != null) {
            if (obj.result.odinHolding > 0.0) {
                PortfolioValue value = getOdinValue(obj);
                itemList.add(value);
                odinBalance = obj.result.odinHolding;
            }

            if (obj.result.etherBalance > 0.0) {
                PortfolioValue value = getEtherValue(obj);
                itemList.add(value);
                ethBalance = obj.result.etherBalance;

            }
        }

        setData();
        next = rootView.findViewById(R.id.command_next);

        //disable next button for 30s after every transactions
        if (AppUtil.isMyServiceRunning(getContext(), TimerServiceUtil.class)) {
            if (next != null)
                next.setText(getResources().getString(R.string.please_wait));
            next.setEnabled(false);
            next.setBackgroundColor(getResources().getColor(R.color.light_grey));
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address = addressEdit.getText().toString();
                amount = amountEdit.getText().toString();
                if (!address.startsWith("0x") || address.length() != 42) {
                    showErrorToast(R.string.invalid_address);
                } else if (address == null || address.isEmpty()) {
                    showErrorToast(R.string.empty_address);
                } else if (amount == null || amount.isEmpty() || Double.valueOf(amount) <= 0) {
                    showErrorToast(R.string.empty_amount);
                } else if (Double.valueOf(amount) > itemList.get(spinner.getSelectedItemPosition()).holdings) {
                    showErrorToast(R.string.not_enough_balance);
                } else if (AppUtil.minEthBalance > ethBalance) {
                    showErrorToast(R.string.not_enough_transaction_fee);
                } else {

                    if (kycFlag.equals(APPROVEDSTATUS)) {
                        Intent intent = new Intent(getActivity(), PinEntryActivity.class);
                        intent.putExtra(KEY_VALIDATING_PIN_FOR_RESULT, true);
                        startActivityForResult(intent, REQUEST_CODE_VALIDATE_PIN);

                    } else {
                        showToast(R.string.unverified_profile, ToastCustom.TYPE_ERROR);
                    }
                }


            }
        });

        TextView maximum = rootView.findViewById(R.id.maximum);
        maximum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemList.get(spinner.getSelectedItemPosition()).tokenName.equals("Eth")) {
                    //Format the ETH to show up to 4 decimal places only
                    amountEdit.setText(AppUtil.formatDoubleToGivenDecimals(itemList.get(spinner.getSelectedItemPosition()).holdings, 2, 4));
                } else if (itemList.get(spinner.getSelectedItemPosition()).noOfDecimals > 0) {
                    amountEdit.setText(AppUtil.formatDoubleToGivenDecimals(itemList.get(spinner.getSelectedItemPosition()).holdings, 2, 4));
                } else if (itemList.get(spinner.getSelectedItemPosition()).noOfDecimals == 0) {
                    amountEdit.setText(AppUtil.formatDoubleToGivenDecimals(itemList.get(spinner.getSelectedItemPosition()).holdings, 0, 0));
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PortfolioValue item = itemList.get(i);
                amountEdit.setText("");
                if (item.noOfDecimals > 0) {
                    amountEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                } else {
                    amountEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                amountEdit.setFilters(new InputFilter[]{
                        new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {

                            @Override
                            public CharSequence filter(CharSequence source, int start, int end,
                                                       Spanned dest, int dstart, int dend) {
                                StringBuilder builder = new StringBuilder(dest);
                                builder.insert(dstart, source);
                                String temp = builder.toString();
                                if (temp.contains(".")) {
                                    temp = temp.substring(temp.indexOf(".") + 1);
                                    //Request to allow only 4 decimal places for all decimal tokens
                                    if (!(item.noOfDecimals > 0) || (temp.length() > 4 || temp.length() > item.noOfDecimals)) {
                                        showErrorToast(R.string.too_many_decimal_points);
                                        return "";
                                    }
                                }
                                return super.filter(source, start, end, dest, dstart, dend);
                            }
                        }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return rootView;
    }

    private void decodeScannedData() {
        if (isJSONValid(scanData)) {
            try {
                JSONObject object = new JSONObject(scanData);
                odinScannedAddress = object.getString("to");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            otherScannedAddress = scanData;
        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private void getPortfolioNetworkCall() {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(getActivity());
        mProgressDialog.show();
        Call<PortfolioResponse> call = apiInterface.getPortfolioTokens(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<PortfolioResponse>() {
            @Override
            public void onResponse(Call<PortfolioResponse> call, Response<PortfolioResponse> response) {

                Log.d("TAG", response.code() + "");
                PortfolioResponse resource = response.body();
                try {
                    if (resource.status) {
                        mPrefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
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
                    ex.printStackTrace();
                    showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                    ContinueAnyWay(null, mProgressDialog);
                }
            }

            @Override
            public void onFailure(Call<PortfolioResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                } else {
                    showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                }

                ContinueAnyWay(null, mProgressDialog);
            }

        });
    }

    private void ContinueAnyWay(PortfolioResponse resource, ProgressDialog dialog) {
        if (resource != null) {
            itemList.addAll(resource.result.values);
            itemList = filterItems();
            setData();
        }

        AppUtil.hideProgressDialog(dialog, getActivity());
    }

    private List<PortfolioValue> filterItems() {
        List<PortfolioValue> items = new ArrayList<>();
        for (PortfolioValue value : itemList) {
            if (!value.isDbToken) {
                items.add(value);
            }
        }
        return items;
    }

    private void setData() {
        try {
            if (itemList.size() > 0) {
                spinner.setAdapter(new SendSpinnerAdapter(getActivity(), R.layout.close_spinner_layout, itemList));
                if (!TextUtils.isEmpty(odinScannedAddress)) {
                    addressEdit.setText(odinScannedAddress);
                } else if (!TextUtils.isEmpty(otherScannedAddress)) {
                    addressEdit.setText(otherScannedAddress);
                    spinner.setSelection(1);
                }
            } else {
                mainContent.setVisibility(View.GONE);
                altContent.setVisibility(View.VISIBLE);
                altText.setText(R.string.no_current_holdings);

            }
        } catch (Exception Ex) {
        }
    }

    @NonNull
    private PortfolioValue getOdinValue(UserProfileResponse obj) {
        PortfolioValue value = new PortfolioValue();
        value.tokenName = "ODIN";
        value.odinValue = 1.0;
        value.tokenLongName = "ODIN";
        value.holdings = obj.result.odinHolding;
        value.tokenImage = "";
        value.isDbToken = false;
        value.noOfDecimals = 0;
        return value;
    }

    @NonNull
    private PortfolioValue getEtherValue(UserProfileResponse obj) {
        PortfolioValue value = new PortfolioValue();
        value.tokenName = "Eth";
        value.odinValue = 0.0;
        value.tokenLongName = "Eth";
        value.holdings = obj.result.etherBalance;
        value.tokenImage = "";
        value.isDbToken = false;
        value.noOfDecimals = 18;
        return value;
    }

    private void showErrorToast(@StringRes int message) {
        showToast(message, ToastCustom.TYPE_ERROR);
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }

    public void showStringToast(String message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), message, ToastCustom.LENGTH_SHORT, toastType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VALIDATE_PIN && resultCode == RESULT_OK) {
            //Contact send token api
            int position = spinner.getSelectedItemPosition();
            PortfolioValue value = itemList.get(position);

            sendToken(value);
        }
    }

    private void sendToken(PortfolioValue value) {
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(getActivity());
        mProgressDialog.show();
        SendTokenBody body = new SendTokenBody(value.tokenLongName.toLowerCase(), Double.valueOf(amount), address, Double.valueOf(value.odinValue.toString()));
        Call<SendTokenResponse> call = apiInterface.sendToken(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"), body);

        call.enqueue(new Callback<SendTokenResponse>() {
            @Override
            public void onResponse(Call<SendTokenResponse> call, Response<SendTokenResponse> response) {

                Log.d("TAG", response.code() + "");
                //start timer to disable button of submit for 30s
                Intent serviceInten = new Intent(getActivity(), TimerServiceUtil.class);
                getActivity().startService(serviceInten);

                try {
                    SendTokenResponse resource = response.body();
                    if (resource.status) {
                        mPrefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                                .setTitle(R.string.send_token_success)
                                .setMessage(resource.result.message)
                                .setPositiveButton(R.string.back_to_main, (dialog, which) -> goToMainActivity())
                                .show();
                    } else {
                        showStringToast(resource.result.message, ToastCustom.TYPE_ERROR);
                    }
                } catch (Exception exception) {
                    showToast(R.string.send_token_error, ToastCustom.TYPE_ERROR);
                }

                AppUtil.hideProgressDialog(mProgressDialog, getActivity());
            }

            @Override
            public void onFailure(Call<SendTokenResponse> call, Throwable t) {
                showToast(R.string.send_token_error, ToastCustom.TYPE_ERROR);
                AppUtil.hideProgressDialog(mProgressDialog, getActivity());
            }

        });


    }

    private void goToMainActivity() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Notification that timer is done and we can enable the button back
            boolean isTimerFinished = intent.getBooleanExtra("countdownFinished", false);
            if (isTimerFinished) {
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