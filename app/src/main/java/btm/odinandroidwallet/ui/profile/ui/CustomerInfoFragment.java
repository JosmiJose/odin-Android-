/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package btm.odinandroidwallet.ui.profile.ui;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.profile.ProfileWizardActivity;
import btm.odinandroidwallet.ui.profile.model.CustomerInfoPage;
import btm.odinandroidwallet.ui.profile.model.ResdentialPage;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerInfoFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private CustomerInfoPage mPage;
    EditText dob;
    CountryCodePicker countryCodePicker;
    CountryCodePicker homeCountryCodePicker;
    EditText etPhone, fname, mname, lname;
    private RadioGroup genderRadioGroup;
    private String KYCNOTSTARTED = "NOT_STARTED";
    String kycFlag = KYCNOTSTARTED;
    private PrefsUtil mPrefsUtil;
    DatePickerDialog dpd;
    int currentSelectedYear = 0;
    int currentSelectedMonth = 0;
    int currentSelectedDay = 0;
    String[] genders = {
            "Male",
            "Female"
    };

    public static CustomerInfoFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        CustomerInfoFragment fragment = new CustomerInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CustomerInfoFragment() {
    }

    InputFilter phoneFilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.length() > 0) {
                if (!Character.isLetterOrDigit(source.charAt(0))) {
                    showToast(R.string.does_not_accept_special_characters, ToastCustom.TYPE_ERROR);
                    return "";
                }
            }
            return null;
        }
    };
    InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source.length() > 0) {
                if (Character.isLetterOrDigit(source.charAt(0))) {
                    if (Character.isDigit(source.charAt(0))) {
                        showToast(R.string.does_not_accept_numbers, ToastCustom.TYPE_ERROR);
                        return "";
                    }else if(!AppUtil.isEnglishAlphabets(source.charAt(0))){
                        showToast(R.string.allow_only_english_alphabet, ToastCustom.TYPE_ERROR);
                        return "";
                    }
                } else {
                    showToast(R.string.does_not_accept_special_characters, ToastCustom.TYPE_ERROR);
                    return "";
                }
            }
            return null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (CustomerInfoPage) mCallbacks.onGetPage(mKey);
        mPrefsUtil = new PrefsUtil(getActivity());
        kycFlag = mPrefsUtil.getValue(PrefsUtil.KYC_STATUS, KYCNOTSTARTED);
        Gson gson = new Gson();
        String json = mPrefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        UserProfileResponse obj = gson.fromJson(json, UserProfileResponse.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_customer_info, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        dob = rootView.findViewById(R.id.dob);
        dob.setText(mPage.getData().getString(CustomerInfoPage.DOB_DATA_KEY));
        fname = (EditText) rootView.findViewById(R.id.fname);
        fname.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(AppUtil.inputLength)});
        fname.setText(mPage.getData().getString(CustomerInfoPage.FNAME_DATA_KEY));
        mname = (EditText) rootView.findViewById(R.id.mname);
        mname.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(AppUtil.inputLength)});
        mname.setText(mPage.getData().getString(CustomerInfoPage.MNAME_DATA_KEY));
        lname = (EditText) rootView.findViewById(R.id.lname);
        lname = (EditText) rootView.findViewById(R.id.lname);
        lname.setText(mPage.getData().getString(CustomerInfoPage.LNAME_DATA_KEY));
        lname.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(AppUtil.inputLength)});
        etPhone = (EditText) rootView.findViewById(R.id.et_phone);
        etPhone.setFilters(new InputFilter[]{phoneFilter, new InputFilter.LengthFilter(AppUtil.inputLength)});
        etPhone.setText(mPage.getData().getString(CustomerInfoPage.PHONE_DATA_KEY));
        countryCodePicker = (CountryCodePicker) rootView.findViewById(R.id.ccp);

        setData(CustomerInfoPage.HOME_COUNTRY_DATA_KEY, "");
        /* homeCountryCodePicker= (CountryCodePicker) rootView.findViewById(R.id.select_country);
        if (!TextUtils.isEmpty(mPage.getData().getString(CustomerInfoPage.HOME_COUNTRY_DATA_KEY))) {
            homeCountryCodePicker.setCountryForNameCode(mPage.getData().getString(CustomerInfoPage.HOME_COUNTRY_DATA_KEY));
        } else {
            setData(CustomerInfoPage.HOME_COUNTRY_DATA_KEY, homeCountryCodePicker.getDefaultCountryNameCode());
        }

        homeCountryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                System.out.println(homeCountryCodePicker.getSelectedCountryName());
                setData(CustomerInfoPage.HOME_COUNTRY_DATA_KEY, homeCountryCodePicker.getSelectedCountryNameCode());
            }
        });*/
        countryCodePicker.registerCarrierNumberEditText(etPhone);
        if (!TextUtils.isEmpty(mPage.getData().getString(CustomerInfoPage.COUNTRY_DATA_KEY))) {
            countryCodePicker.setCountryForNameCode(mPage.getData().getString(CustomerInfoPage.COUNTRY_DATA_KEY));
            etPhone.setText(mPage.getData().getString(CustomerInfoPage.PHONE_DATA_KEY));
        } else {
            setData(CustomerInfoPage.COUNTRY_DATA_KEY, countryCodePicker.getDefaultCountryNameCode());
            setData(CustomerInfoPage.PHONECODE_DATA_KEY, countryCodePicker.getDefaultCountryCode());
        }

        genderRadioGroup = (RadioGroup) rootView.findViewById(R.id.gender_radio_group);
        setData(CustomerInfoPage.GENDER_DATA_KEY, genders[0]);
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                String gender;
                if (selectedId == R.id.female_radio_btn)
                    gender = genders[1];
                else
                    gender = genders[0];

                setData(CustomerInfoPage.GENDER_DATA_KEY, gender);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewsChangeListener();
    }

    private void ViewsChangeListener() {
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });
        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showDate();
                }
            }
        });
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                setData(CustomerInfoPage.COUNTRY_DATA_KEY, countryCodePicker.getSelectedCountryNameCode());
                setData(CustomerInfoPage.PHONECODE_DATA_KEY, countryCodePicker.getSelectedCountryCode());
            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((editable.length() >= 4) && (editable.length() <= 20)) {
                    setData(CustomerInfoPage.PHONE_DATA_KEY, (editable != null) ? editable.toString() : null);
                } else {
                    setData(CustomerInfoPage.PHONE_DATA_KEY, "");
                }
            }
        });
        fname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 50) {
                    showToast(R.string.too_large_name, ToastCustom.TYPE_ERROR);
                }

                setData(CustomerInfoPage.FNAME_DATA_KEY, (editable != null) ? editable.toString() : null);
            }
        });
        mname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 50) {
                    showToast(R.string.too_large_name, ToastCustom.TYPE_ERROR);
                }

                setData(CustomerInfoPage.MNAME_DATA_KEY, (editable != null) ? editable.toString() : null);
            }
        });
        lname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 50) {
                    showToast(R.string.too_large_name, ToastCustom.TYPE_ERROR);
                }

                setData(CustomerInfoPage.LNAME_DATA_KEY, (editable != null) ? editable.toString() : null);

            }
        });
    }

    private void showDate() {
        Calendar now = Calendar.getInstance();
        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    CustomerInfoFragment.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            if (currentSelectedYear != 0) {
                dpd.initialize(
                        CustomerInfoFragment.this,
                        currentSelectedYear,
                        currentSelectedMonth,
                        currentSelectedDay
                );
            } else {
                dpd.initialize(
                        CustomerInfoFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
            }
        }
        dpd.setMaxDate(Calendar.getInstance());
        Dialog dialogFrg = dpd.getDialog();
        if (dialogFrg != null && dialogFrg.isShowing()) {
            // no need to call dialog.show(ft, "DatePicker");
        } else {
            dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
        }

    }

    private void setData(String key, String value) {
        mPage.getData().putString(key, value);
        mPage.notifyDataChanged();
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        currentSelectedYear = year;
        currentSelectedMonth = monthOfYear;
        currentSelectedDay = dayOfMonth;
        //Formate date to DD/MM/YYYY
        //backend accept
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat disaplayFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd");
        String displayDate = disaplayFormat.format(calendar.getTime());
        String date = apiFormat.format(calendar.getTime());

        if (validAge(year, monthOfYear, dayOfMonth)) {
            dob.setText(displayDate);
            setData(CustomerInfoPage.DOB_DATA_KEY, date);
        } else {
            showToast(R.string.invalid_age, ToastCustom.TYPE_ERROR);
            dob.setText("");
            setData(CustomerInfoPage.DOB_DATA_KEY, "");
        }
    }

    //to check the validity of the date
    private boolean validAge(int year, int monthOfYear, int dayOfMonth) {
        Calendar calBefore = Calendar.getInstance();
        // Age must be more than 15 years
        calBefore.add(Calendar.YEAR, -AppUtil.minimumAge);
        //add 3 minutes for comparing same date as birthday
        calBefore.add(Calendar.MINUTE, +3);
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, monthOfYear, dayOfMonth);
        if (calBefore.compareTo(selectedDate) < 0) {
            //Minimum Age is before selected age which is invalid
            return false;
        } else {
            return true;
        }
    }


}
