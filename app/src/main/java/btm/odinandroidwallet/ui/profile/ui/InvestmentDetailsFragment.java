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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.profile.model.CustomerInfoPage;
import btm.odinandroidwallet.ui.profile.model.InvestmentDetailsPage;
import btm.odinandroidwallet.ui.profile.model.ProofOfIdentityPage;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;

public class InvestmentDetailsFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private InvestmentDetailsPage mPage;
    TextView taxId;
    InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source.length() > 0) {
                if (!Character.isLetterOrDigit(source.charAt(0))) {
                    showToast(R.string.does_not_accept_special_characters, ToastCustom.TYPE_ERROR);
                    return "";
                }else if(!AppUtil.isEnglishAlphabets(source.charAt(0))){
                    showToast(R.string.allow_only_english_alphabet, ToastCustom.TYPE_ERROR);
                    return "";
                }
            }
            return null;
        }
    };
    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }
    public static InvestmentDetailsFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        InvestmentDetailsFragment fragment = new InvestmentDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public InvestmentDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (InvestmentDetailsPage) mCallbacks.onGetPage(mKey);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_investment_details, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        Spinner spinner = (Spinner) rootView.findViewById(R.id.purpose);
        String[] purposes = getResources().getStringArray(R.array.purpose_options);
        PrepareSpinner(spinner, purposes,InvestmentDetailsPage.PURPOSE_KEY);

        Spinner spinner2 = (Spinner) rootView.findViewById(R.id.planned_range);
        String[] ranges = getResources().getStringArray(R.array.planned_options);
        PrepareSpinner(spinner2, ranges,InvestmentDetailsPage.PLANNED_KEY);

        Spinner spinner3 = (Spinner) rootView.findViewById(R.id.industry);
        String[] indOptions = getResources().getStringArray(R.array.Industry_options);
        PrepareSpinner(spinner3, indOptions,InvestmentDetailsPage.INDUSTRY_KEY);

        Spinner spinner4 = (Spinner) rootView.findViewById(R.id.work_type);
        String[] types = getResources().getStringArray(R.array.work_type_options);
        PrepareSpinner(spinner4, types,InvestmentDetailsPage.WORK_TYPE_KEY);

        taxId=rootView.findViewById(R.id.tax_id);
        taxId.setFilters(new InputFilter[] { filter ,new InputFilter.LengthFilter(15) });

    }

    private void PrepareSpinner(Spinner spinner, String[] options,String key) {
        ArrayAdapter<String> spinnerCountShoesArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,options);
        spinner.setAdapter(spinnerCountShoesArrayAdapter);
        int index=0;
        if(mPage.getData().getString(key)!=null) {
            try {
                index = Integer.valueOf(mPage.getData().getString(key));
            }
            catch (Exception Ex){}
        }
        spinner.setSelection(index);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0) {
                    String type = options[i];
                    setData(key, String.valueOf(i));
                }
                else
                {
                    setData(key, "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        taxId.setText(mPage.getData().getString(InvestmentDetailsPage.TAX_ID_KEY));
        taxId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setData(InvestmentDetailsPage.TAX_ID_KEY, (editable != null) ? editable.toString() : null);
            }
        });

    }

    private void ViewsChangeListener() {

    }

    private void setData(String key, String value) {
        mPage.getData().putString(key, value);
        mPage.notifyDataChanged();
    }


}
