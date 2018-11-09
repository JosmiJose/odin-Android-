package btm.odinandroidwallet.ui.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.HistoryAdapter;
import btm.odinandroidwallet.adapters.RecyclerViewAdapter;
import btm.odinandroidwallet.adapters.SubscriptionHistoryAdapter;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.GenericResponseNOKYC;
import btm.odinandroidwallet.networking.retrofit.pojo.HistoryValue;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionHistoryResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionListResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionValue;
import btm.odinandroidwallet.networking.retrofit.pojo.TransactionHistoryResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.auth.PinEntryActivity;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.home.MainActivity;
import btm.odinandroidwallet.ui.profile.ProfileWizardActivity;
import btm.odinandroidwallet.ui.views.RecyclerViewEmptySupport;
import btm.odinandroidwallet.ui.views.SwipeRefreshLayoutWithEmpty;
import btm.odinandroidwallet.util.PrefsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static btm.odinandroidwallet.ui.auth.PinEntryActivity.KEY_VALIDATING_PIN_FOR_RESULT;
import static btm.odinandroidwallet.ui.auth.PinEntryActivity.REQUEST_CODE_VALIDATE_PIN;

public class SubscribeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerViewEmptySupport recyclerView;
    List<SubscriptionValue> dataModels;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerViewAdapter RecyclerViewHorizontalAdapter;
    LinearLayoutManager HorizontalLayout;
    private TextView emptyView;
    private RecyclerViewAdapter mAdapter;
    View ChildView;
    int RecyclerViewItemPosition;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String APPROVEDSTATUS = "ACCEPTED";
    private String kycFlag = "";
    APIInterface apiInterface;
    private PrefsUtil mPrefsUtil;
    RelativeLayout layout;
    UserProfileResponse obj;
    TextView headerText;

    public static SubscribeFragment newInstance() {
        SubscribeFragment fragment = new SubscribeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subscribe, container, false);
        TextView points = rootView.findViewById(R.id.points);
        layout = rootView.findViewById(R.id.relativelayout1);
        emptyView = rootView.findViewById(R.id.empty_view);
        mPrefsUtil = new PrefsUtil(getActivity());
        headerText=rootView.findViewById(R.id.header_text);
        TextView header = rootView.findViewById(R.id.header);
        Gson gson = new Gson();
        String json = mPrefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        obj = gson.fromJson(json, UserProfileResponse.class);
        if (obj != null) {
            points.setText("" + obj.result.odinPoints);
            if (obj.result.isSubscribed) {
                header.setText(getResources().getString(R.string.you_are_memeber));
            } else {
                header.setText(getResources().getString(R.string.you_are_not_memeber));
            }

        }else
        {
            points.setText("0");
            header.setText(getResources().getString(R.string.you_are_not_memeber));
        }
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
        recyclerView = (RecyclerViewEmptySupport) rootView.findViewById(R.id.recyclerview1);
        recyclerView.setEmptyView(emptyView);
        RecyclerViewLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(RecyclerViewLayoutManager);


        HorizontalLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);


        // Adding on item click listener to RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting clicked value.
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);
                    kycFlag = mPrefsUtil.getValue(PrefsUtil.KYC_STATUS, "");
                    if (kycFlag.equals(APPROVEDSTATUS)) {
                        if(!obj.result.isSubscribed)
                        {
                            if(obj.result.odinPoints >dataModels.get(RecyclerViewItemPosition).points) {
                                Intent intent = new Intent(getActivity(), PinEntryActivity.class);
                                intent.putExtra(KEY_VALIDATING_PIN_FOR_RESULT, true);
                                startActivityForResult(intent, REQUEST_CODE_VALIDATE_PIN);
                            }
                            else
                            {
                                showToast(R.string.you_dont_have_enough_points, ToastCustom.TYPE_ERROR);
                            }
                        }
                        else
                        {
                            showToast(R.string.you_are_memeber, ToastCustom.TYPE_ERROR);
                        }

                    } else {
                        showToast(R.string.unverified_profile, ToastCustom.TYPE_ERROR);
                    }


                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                // mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                loadRecyclerViewData();
            }
        });
        return rootView;
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }

    private void loadRecyclerViewData() {
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);
        // Adding items to RecyclerView.
        getSubscriptionNetworkCall();

    }

    private void getSubscriptionNetworkCall() {
        Call<SubscriptionListResponse> call = apiInterface.getSubscriptionItems(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<SubscriptionListResponse>() {
            @Override
            public void onResponse(Call<SubscriptionListResponse> call, Response<SubscriptionListResponse> response) {

                Log.d("TAG", response.code() + "");
                SubscriptionListResponse resource = response.body();
                try {
                    if (resource.status) {
                        ContinueAnyWay(resource);
                    } else {
                        ContinueAnyWay(null);
                        showToast(R.string.get_subscription_list_error, ToastCustom.TYPE_ERROR);
                    }
                } catch (Exception ex) {
                    ContinueAnyWay(null);
                    showToast(R.string.get_subscription_list_error, ToastCustom.TYPE_ERROR);
                }
            }

            @Override
            public void onFailure(Call<SubscriptionListResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                }
                else {
                    showToast(R.string.get_subscription_list_error, ToastCustom.TYPE_ERROR);
                }
                ContinueAnyWay(null);

            }

        });
    }

    private void ContinueAnyWay(SubscriptionListResponse resource) {
       if (resource != null) {
            dataModels = resource.result.values;
            if (dataModels.size() > 0) {
                showList();
            } else {
                hideList();
            }

        } else {
            hideList();
        }
        mSwipeRefreshLayout.setRefreshing(false);


    }

    private void showList() {
        headerText.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        mAdapter = new RecyclerViewAdapter(dataModels, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private void hideList() {
        headerText.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setText(getResources().getString(R.string.no_data_available));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VALIDATE_PIN && resultCode == RESULT_OK) {
            subsribeNetworkCall(RecyclerViewItemPosition);
        }
    }

    private void subsribeNetworkCall(int positon) {
        SubscriptionValue value = dataModels.get(positon);
        Call<GenericResponseNOKYC> call = apiInterface.subscripeUser(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"), String.valueOf(value.subscribeId));
        call.enqueue(new Callback<GenericResponseNOKYC>() {
            @Override
            public void onResponse(Call<GenericResponseNOKYC> call, Response<GenericResponseNOKYC> response) {

                Log.d("TAG", response.code() + "");
                try {
                    GenericResponseNOKYC resource = response.body();
                    if (resource.status) {
                        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                                .setTitle(R.string.subscribe_success)
                                .setMessage(R.string.subscribe_success_msg)
                                .setPositiveButton(R.string.back_to_main, (dialog, which) -> goToMainActivity())
                                .show();
                    }
                } catch (Exception ex) {
                    showToast(R.string.subscribe_error, ToastCustom.TYPE_ERROR);
                }
            }

            @Override
            public void onFailure(Call<GenericResponseNOKYC> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                }
                else {
                    showToast(R.string.subscribe_error, ToastCustom.TYPE_ERROR);
                }

            }

        });

    }

    private void goToMainActivity() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onRefresh() {
        loadRecyclerViewData();
    }
}