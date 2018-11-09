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
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.profile.model.CustomerInfoPage;
import btm.odinandroidwallet.ui.profile.model.ResdentialPage;
import btm.odinandroidwallet.util.AppUtil;

public class ResdentialAddressFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ResdentialPage mPage;

    CountryCodePicker countryCodePicker;
    EditText state, city, zipcode, address1,address2;
    InputFilter cityStateFilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source.length() > 0) {
                //allow only - and space
                if(Character.toString(source.charAt(0)).equals("-")||Character.toString(source.charAt(0)).equals(" "))
                {
                } else if (!Character.isLetterOrDigit(source.charAt(0))) {
                    showToast(R.string.does_not_accept_special_characters, ToastCustom.TYPE_ERROR);
                    return "";
                }else if (!AppUtil.isEnglishAlphabets(source.charAt(0))){
                    showToast(R.string.allow_only_english_alphabet, ToastCustom.TYPE_ERROR);
                    return "";
                }
            }
            return null;
        }
    };
    InputFilter addressFilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source.length() > 0) {
                if (isAllowedAddressChar(Character.toString(source.charAt(0)))) {

                } else if (!Character.isLetterOrDigit(source.charAt(0))) {
                    showToast(R.string.does_not_accept_special_characters, ToastCustom.TYPE_ERROR);
                    return "";
                } else if (!AppUtil.isEnglishAlphabets(source.charAt(0))) {
                    showToast(R.string.allow_only_english_alphabet, ToastCustom.TYPE_ERROR);
                    return "";
                }
            }
            return null;
        }
    };
    private boolean isAllowedAddressChar(String ch)
    {
        for (String letter : allowedAddressChars)
        {
            if(letter.equals(ch))
            {
                return true;
            }
        }

        return false;
    }
    String [] allowedAddressChars=new String[]{",",".","\\","/","-"," ","#"};
    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }
    public static ResdentialAddressFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ResdentialAddressFragment fragment = new ResdentialAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ResdentialAddressFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ResdentialPage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_resedential_address, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());
        countryCodePicker = (CountryCodePicker) rootView.findViewById(R.id.select_country);
        if (!TextUtils.isEmpty(mPage.getData().getString(ResdentialPage.ADDRESS_COUNTRY_KEY))) {
            countryCodePicker.setCountryForNameCode(mPage.getData().getString(ResdentialPage.ADDRESS_COUNTRY_KEY));
        } else {
            setData(ResdentialPage.ADDRESS_COUNTRY_KEY, countryCodePicker.getDefaultCountryNameCode());
        }
        state = rootView.findViewById(R.id.state);
        state.setText(mPage.getData().getString(ResdentialPage.STATE_DATA_KEY));
        state.setFilters(new InputFilter[] { cityStateFilter,new InputFilter.LengthFilter(AppUtil.inputLength) });
        city = rootView.findViewById(R.id.city);
        city.setText(mPage.getData().getString(ResdentialPage.CITY_DATA_KEY));
        city.setFilters(new InputFilter[] {cityStateFilter, new InputFilter.LengthFilter(AppUtil.inputLength) });
        zipcode = rootView.findViewById(R.id.zip_code);
        zipcode.setText(mPage.getData().getString(ResdentialPage.ZIPCODE_DATA_KEY));
        zipcode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(AppUtil.inputLength) });
        address1 = rootView.findViewById(R.id.address1);
        address1.setText(mPage.getData().getString(ResdentialPage.ADDRESS1_DATA_KEY));
        address1.setFilters(new InputFilter[] { addressFilter,new InputFilter.LengthFilter(AppUtil.inputLength) });
        address2 = rootView.findViewById(R.id.address2);
        address2.setText(mPage.getData().getString(ResdentialPage.ADDRESS2_DATA_KEY));
        address2.setFilters(new InputFilter[] {addressFilter, new InputFilter.LengthFilter(AppUtil.inputLength) });
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                setData(ResdentialPage.ADDRESS_COUNTRY_KEY, countryCodePicker.getSelectedCountryNameCode());
            }
        });
        return rootView;
    }
    private void setData(String key, String value) {
        mPage.getData().putString(key, value);
        mPage.notifyDataChanged();
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
        state.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setData(ResdentialPage.STATE_DATA_KEY, (editable != null) ? editable.toString() : null);
            }
        });
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setData(ResdentialPage.CITY_DATA_KEY, (editable != null) ? editable.toString() : null);
            }
        });
        zipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setData(ResdentialPage.ZIPCODE_DATA_KEY, (editable != null) ? editable.toString() : null);
            }
        });
        address1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setData(ResdentialPage.ADDRESS1_DATA_KEY, (editable != null) ? editable.toString() : null);
            }
        });
        address2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setData(ResdentialPage.ADDRESS2_DATA_KEY, (editable != null) ? editable.toString() : null);
            }
        });
    }


}
