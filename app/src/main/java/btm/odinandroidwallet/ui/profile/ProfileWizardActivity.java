package btm.odinandroidwallet.ui.profile;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hbb20.CountryCodePicker;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.models.Country;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.GenericResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.Profile;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.home.MainActivity;
import btm.odinandroidwallet.ui.profile.model.AbstractWizardModel;
import btm.odinandroidwallet.ui.profile.model.CustomerInfoPage;
import btm.odinandroidwallet.ui.profile.model.InvestmentDetailsPage;
import btm.odinandroidwallet.ui.profile.model.ModelCallbacks;
import btm.odinandroidwallet.ui.profile.model.Page;
import btm.odinandroidwallet.ui.profile.model.ProofOfIdentityPage;
import btm.odinandroidwallet.ui.profile.model.ResdentialPage;
import btm.odinandroidwallet.ui.profile.model.ReviewItem;
import btm.odinandroidwallet.ui.profile.ui.PageFragmentCallbacks;
import btm.odinandroidwallet.ui.profile.ui.ReviewFragment;
import btm.odinandroidwallet.ui.profile.ui.StepPagerStrip;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileWizardActivity extends AppCompatActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private AbstractWizardModel mWizardModel;

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private List<ReviewItem> mCurrentReviewItems;
    APIInterface apiInterface;
    String firstName = "";
    String middleName = "";
    String lastName = "";
    String dateOfBirth = "";
    String homeCountry = "";
    String nationality = "";
    String countryOfResidence = "";
    String contactNumbrer = "";
    String gender = "";
    double odinHolding = 100;
    String profession = "Mobile Developer";
    String reason = "I dont have a solid reason ";
    String address1 = "";
    String address2 = "";
    String address3 = "This is address 3";
    String city = "";
    String state = "";
    String zipcode = "";
    String documentType = "";
    String documentNumber = "";
    String publicKey = "This is public key";
    String workAddress = "This is work address";
    private PrefsUtil mPrefsUtil;
    private String KYCNOTSTARTED = "NOT_STARTED";
    String kycFlag = KYCNOTSTARTED;
    String phoneCountryCode = "";
    public String contactNumberCode = "";
    public String documentExpriy = "";
    public String taxId = "";
    public int industry = 0;
    public int workType = 0;
    public int purposeOfAction = 0;
    public int plannedInvestment = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_wizard_main);
        Bundle extras = getIntent().getExtras();
        try {
            kycFlag = extras.getString("kycStatus");
        }
        catch (Exception ex){}
        mPrefsUtil = new PrefsUtil(ProfileWizardActivity.this);
        apiInterface = APIClient.getClient(this).create(APIInterface.class);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        TextView title = findViewById(R.id.title);
        title.setText(getResources().getString(R.string.update_profile));
        mWizardModel = new SandwichWizardModel(this);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);
        initUi();
        // initData();


    }

    private void initUi() {
        try {

            if (!kycFlag.equals(KYCNOTSTARTED)) {
                findViewById(R.id.not_started_layout).setVisibility(View.GONE);
                findViewById(R.id.pending_layout).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.pending_layout).setVisibility(View.GONE);
                findViewById(R.id.not_started_layout).setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {

        }


        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    mCurrentReviewItems = AppUtil.getReviewItems(mWizardModel);
                    Uri uri = null;
                    ArrayList<File> imageFiles = new ArrayList<File>();
                    for (ReviewItem item : mCurrentReviewItems) {
                        if (item.getType() == 1 || item.getType() == 3) {
                            getProfileDetails(item);
                        } else if (item.getType() == 2) {
                           // uri = Uri.parse(item.getDisplayValue());
                            File file = new File(item.getDisplayValue());
                            imageFiles.add(file);
                        }

                    }

                    Profile profile = new Profile(firstName, middleName, lastName, dateOfBirth, nationality, countryOfResidence, contactNumbrer, odinHolding, profession, reason, address1, address2, address3, city, state, zipcode, homeCountry, documentType, documentNumber, publicKey, workAddress, gender, kycFlag, phoneCountryCode, contactNumberCode, documentExpriy,taxId,purposeOfAction, industry, workType, plannedInvestment);
                    createProfileNetworkCall(profile, imageFiles);
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);

                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
    }


    private void getProfileDetails(ReviewItem item) {
        if (item.getTitle().equals(getString(R.string.your_f_name))) {
            firstName = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_m_name))) {
            middleName = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_l_name))) {
            lastName = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_dob))) {
            dateOfBirth = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_nationality))) {
            nationality = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_country))) {
            countryOfResidence = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_phone))) {
            contactNumbrer = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_gender))) {
            gender = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_address1))) {
            address1 = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_address2))) {
            address2 = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_state))) {
            state = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_city))) {
            city = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_zipcode))) {
            zipcode = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_doc_type))) {
            documentType = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_doc_number))) {
            documentNumber = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_doc_number))) {
            zipcode = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_phone_country))) {
            phoneCountryCode = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_home_country))) {
            homeCountry = item.getDisplayValue();
        } else if (item.getTitle().equals(getString(R.string.your_industry))) {
            industry = Integer.valueOf(item.getDisplayValue());
        } else if (item.getTitle().equals(getString(R.string.your_purpose_of_action))) {
            purposeOfAction = Integer.valueOf(item.getDisplayValue());
        } else if (item.getTitle().equals(getString(R.string.your_work_type))) {
            workType = Integer.valueOf(item.getDisplayValue());
        } else if (item.getTitle().equals(getString(R.string.your_planned_investment_range))) {
            plannedInvestment = Integer.valueOf(item.getDisplayValue());
        } else if (item.getTitle().equals(getString(R.string.your_doc_expiry_date))) {
            documentExpriy = item.getDisplayValue();
        }else if (item.getTitle().equals(getString(R.string.your_tax_id))) {
            taxId = item.getDisplayValue();
        }


    }

    private void createProfileNetworkCall(Profile profile, ArrayList<File> files) {
        HashMap<String, RequestBody> map = profileToParts(profile);
        MultipartBody.Part frontFilePart = MultipartBody.Part.createFormData("img_front", files.get(0).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(0)));
        MultipartBody.Part backFilePart = MultipartBody.Part.createFormData("img_back", files.get(1).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(1)));
        MultipartBody.Part selfieFilePart = MultipartBody.Part.createFormData("img_selfie", files.get(2).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(2)));
        ProgressDialog mProgressDialog = AppUtil.getProgressDialog(this);
        mProgressDialog.show();

        Call<GenericResponse> call = apiInterface.createProfile(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"), frontFilePart, backFilePart, selfieFilePart, map, intValuesToParts(profile));
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Log.d("TAG", response.code() + "");

                try {
                    GenericResponse resource = response.body();
                    if (resource.status) {
                        mPrefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                        new AlertDialog.Builder(ProfileWizardActivity.this, R.style.AlertDialogStyle)
                                .setTitle(R.string.update_profile_success)
                                .setMessage(R.string.update_profile_success_msg)
                                .setPositiveButton(R.string.back_to_main, (dialog, which) -> goToMainActivity())
                                .show();
                    } else {
                        showToast(R.string.create_profile_error, ToastCustom.TYPE_ERROR);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showToast(R.string.create_profile_error, ToastCustom.TYPE_ERROR);
                }

                AppUtil.hideProgressDialog(mProgressDialog, ProfileWizardActivity.this);

            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                } else {
                    showToast(R.string.create_profile_error, ToastCustom.TYPE_ERROR);
                }
                call.cancel();
                AppUtil.hideProgressDialog(mProgressDialog, ProfileWizardActivity.this);

            }
        });
    }

    private HashMap<String, RequestBody> profileToParts(Profile profile) {
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("first_name", getBodyFromString(profile.firstName));
        map.put("middle_name", getBodyFromString(profile.middleName));
        map.put("last_name", getBodyFromString(profile.lastName));
        map.put("date_of_birth", getBodyFromString(profile.dateOfBirth));
        map.put("nationality", getBodyFromString(profile.nationality));
        map.put("country_of_residence", getBodyFromString(profile.countryOfResidence));
        map.put("contact_number", getBodyFromString(profile.contactNumber));
        map.put("odin_holding", getBodyFromString(String.valueOf(profile.odinHolding)));
        map.put("profession", getBodyFromString(profile.profession));
        map.put("reason", getBodyFromString(profile.reason));
        map.put("address_line_1", getBodyFromString(profile.addressLine1));
        map.put("address_line_2", getBodyFromString(profile.addressLine2));
        map.put("city", getBodyFromString(profile.city));
        map.put("state", getBodyFromString(profile.state));
        map.put("postal_code", getBodyFromString(profile.postalCode));
        map.put("country_home", getBodyFromString(profile.countryHome));
        map.put("document_type", getBodyFromString(profile.documentType));
        map.put("document_number", getBodyFromString(profile.documentNumber));
        map.put("work_address", getBodyFromString(profile.workAddress));
        map.put("gender", getBodyFromString(profile.gender));
        map.put("phone_country_code", getBodyFromString(profile.countryCode));
        if(!profile.documentType.equals("ID")){
            map.put("document_expiry", getBodyFromString(profile.documentExpriy));
        }
        map.put("contact_number_code", getBodyFromString(profile.contactNumberCode));
        map.put("tax_id", getBodyFromString(profile.taxId));

        return map;
    }

    private HashMap<String, Integer> intValuesToParts(Profile profile) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("industry", profile.industry - 1);
        map.put("work_type", profile.workType - 1);
        map.put("purpose_of_action", profile.purposeOfAction - 1);
        map.put("planned_investment", profile.plannedInvestment - 1);
        printMap(map);
        return map;
    }

    private void printMap(HashMap<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
        }
    }

    private RequestBody getBodyFromString(String txt) {
        RequestBody body = AppUtil.toRequestBody(txt);
        return body;
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(this, getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }

    private void goToMainActivity() {
        Intent i = new Intent(ProfileWizardActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(mEditingAfterReview
                    ? R.string.review
                    : R.string.next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (mCurrentPageSequence == null) {
                return 0;
            }
            return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
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

    private void initData() {
        Gson gson = new Gson();
        String json = mPrefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        UserProfileResponse obj = gson.fromJson(json, UserProfileResponse.class);
        if (obj != null) {
            for (Page page : mCurrentPageSequence) {
                if (page.getTitle().equals("Personal Info")) {
                    injectPersonalInfo(page, obj);
                } else if (page.getTitle().equals("Residential Address")) {
                    injectResidentialInfo(page, obj);
                } else if (page.getTitle().equals("Investment Details")) {
                    injectInvestmentDetails(page, obj);
                } else if (page.getTitle().equals("Proof Of Identity")) {
                    injectProofOfIdentityInfo(page, obj);
                }
            }
            mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mPagerAdapter);
        }

    }

    private void injectPersonalInfo(Page page, UserProfileResponse response) {
        setData(page, CustomerInfoPage.FNAME_DATA_KEY, response.result.firstName);
        setData(page, CustomerInfoPage.COUNTRY_DATA_KEY, response.result.phoneCountryCode);
        setData(page, CustomerInfoPage.PHONE_DATA_KEY, response.result.contactNumber);
        setData(page, CustomerInfoPage.FNAME_DATA_KEY, response.result.firstName);
        setData(page, CustomerInfoPage.MNAME_DATA_KEY, response.result.middleName);
        setData(page, CustomerInfoPage.LNAME_DATA_KEY, response.result.lastName);
        setData(page, CustomerInfoPage.DOB_DATA_KEY, response.result.dateOfBirth);
        setData(page, CustomerInfoPage.PHONE_DATA_KEY, response.result.contactNumber);
        setData(page, CustomerInfoPage.HOME_COUNTRY_DATA_KEY, response.result.countryHome);
    }

    private void injectResidentialInfo(Page page, UserProfileResponse response) {
        setData(page, ResdentialPage.ADDRESS_COUNTRY_KEY, response.result.countryOfResidence);
        setData(page, ResdentialPage.STATE_DATA_KEY, response.result.state);
        setData(page, ResdentialPage.CITY_DATA_KEY, response.result.city);
        setData(page, ResdentialPage.ZIPCODE_DATA_KEY, response.result.postalCode);
        setData(page, ResdentialPage.ADDRESS1_DATA_KEY, response.result.addressLine1);
        setData(page, ResdentialPage.ADDRESS2_DATA_KEY, response.result.addressLine2);
    }

    private void injectProofOfIdentityInfo(Page page, UserProfileResponse response) {
        setData(page, ProofOfIdentityPage.DOC_TYPE_DATA_KEY, response.result.documentType);
        setData(page, ProofOfIdentityPage.NUMBER_KEY, response.result.documentNumber);
        setData(page, ProofOfIdentityPage.NATIONALITY_KEY, response.result.nationality);
        setData(page, ProofOfIdentityPage.EXPIRY_DATE_KEY, response.result.documentExpiry);
        // setData(page,ProofOfIdentityPage.FRONT_IMAGE_KEY, response.result.firstName);
        //setData(page,ProofOfIdentityPage.BACK_IMAGE_KEY, response.result.middleName);
        // setData(page,ProofOfIdentityPage.SELFIE_IMAGE_KEY, response.result.lastName);
    }

    private void injectInvestmentDetails(Page page, UserProfileResponse response) {
        setData(page, InvestmentDetailsPage.WORK_TYPE_KEY, getWorkTypeIndex(response.result.workType));
        setData(page, InvestmentDetailsPage.INDUSTRY_KEY, getIndustryIndex(response.result.industry));
        setData(page, InvestmentDetailsPage.PLANNED_KEY, getPlannedIndex(response.result.plannedInvestment));
        setData(page, InvestmentDetailsPage.PURPOSE_KEY, getPurposeIndex(response.result.purposeOfAction));
    }

    private void setData(Page mPage, String key, String value) {
        mPage.getData().putString(key, value);
        mPage.notifyDataChanged();
    }

    private String getWorkTypeIndex(String value) {
        int index = 0;
        String[] types = getResources().getStringArray(R.array.work_type_options);
        for (int i = 0; i < types.length; i++) {

            if (types[i].equals(value)) {
                index = i;
            }
        }
        return String.valueOf(index);
    }

    private String getIndustryIndex(String value) {
        int index = 0;
        String[] types = getResources().getStringArray(R.array.Industry_options);
        for (int i = 0; i < types.length; i++) {

            if (types[i].equals(value)) {
                index = i;
            }
        }
        return String.valueOf(index);
    }

    private String getPlannedIndex(String value) {
        int index = 0;
        String[] types = getResources().getStringArray(R.array.planned_options);
        for (int i = 0; i < types.length; i++) {

            if (types[i].equals(value)) {
                index = i;
            }
        }
        return String.valueOf(index);
    }

    private String getPurposeIndex(String value) {
        int index = 0;
        String[] types = getResources().getStringArray(R.array.purpose_options);
        for (int i = 0; i < types.length; i++) {

            if (types[i].equals(value)) {
                index = i;
            }
        }
        return String.valueOf(index);
    }
}