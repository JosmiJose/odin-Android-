package btm.odinandroidwallet.ui.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.HistoryAdapter;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.HistoryValue;
import btm.odinandroidwallet.networking.retrofit.pojo.TransactionHistoryResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.views.SwipeRefreshLayoutWithEmpty;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Waleed on 7/3/2018.
 */

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private HistoryAdapter mAdapter;
    List<HistoryValue> dataModels = new ArrayList<HistoryValue>();
    SwipeRefreshLayoutWithEmpty mSwipeRefreshLayout;
    APIInterface apiInterface;
    private PrefsUtil mPrefsUtil;

    private AppUtil.Mode mode;
    private String APPROVEDSTATUS="ACCEPTED";
    String kycFlag="NOTSTARTED";
    public static HistoryFragment newInstance(AppUtil.Mode mode) {
        HistoryFragment fragment = new HistoryFragment();
        fragment.mode = mode;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefsUtil = new PrefsUtil(getActivity());
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        kycFlag=mPrefsUtil.getValue(PrefsUtil.KYC_STATUS,"");
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayoutWithEmpty) view.findViewById(R.id.swipe_container);
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



                // Fetching data from server
                if(kycFlag.equals("ACCEPTED")) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    loadRecyclerViewData();
                }else
                {
                    hideList();
                }
            }
        });
        return view;
    }

    private void loadRecyclerViewData() {
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);
        // dataModels=AppUtil.prepareHistoryData();

        getHistoryNetworkCall();

    }

    private void getHistoryNetworkCall() {
        Call<TransactionHistoryResponse> call = apiInterface.getHistoryItems(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<TransactionHistoryResponse>() {
            @Override
            public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {

                Log.d("TAG", response.code() + "");
                TransactionHistoryResponse resource = response.body();
                if(resource != null) {
                    if(resource.status) {
                       mPrefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                        ContinueAnyWay(resource);
                    }
                    else
                    {
                        if(resource.kycStatus.equals(APPROVEDSTATUS)) {
                            showToast(R.string.get_history_error, ToastCustom.TYPE_ERROR);
                        }
                        else
                        {
                            showToast(R.string.profile_should_be_updated, ToastCustom.TYPE_ERROR);
                        }
                        ContinueAnyWay(null);
                    }
                }
                else
                {
                    ContinueAnyWay(null);
                    showToast(R.string.get_history_error, ToastCustom.TYPE_ERROR);
                }
            }

            @Override
            public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                }
                else {
                    showToast(R.string.get_history_error, ToastCustom.TYPE_ERROR);
                }
                ContinueAnyWay(null);

            }

        });
    }

    private void ContinueAnyWay(TransactionHistoryResponse resource) {

        if(resource != null) {
            dataModels = resource.result.values;
            if(dataModels.size()>0) {
                showList();
            }
            else
            {
                hideList();
            }

        }else
        {
            hideList();
        }
        mSwipeRefreshLayout.setRefreshing(false);


    }

    private void showList() {
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        dataModels=filterModels(mode);
        mAdapter = new HistoryAdapter(getActivity(), dataModels);
        recyclerView.setAdapter(mAdapter);
    }

    private void hideList() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setText(getResources().getString(R.string.no_data_available));
    }

    private List<HistoryValue> filterModels(AppUtil.Mode mode) {
        Gson gson = new Gson();
        String json = mPrefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        UserProfileResponse obj = gson.fromJson(json, UserProfileResponse.class);
        List<HistoryValue> values = new ArrayList<HistoryValue>();
        if (mode == AppUtil.Mode.SENT) {
            for (HistoryValue value : dataModels) {
                if (obj != null) {
                    if (value.fromAddress.equals(obj.result.walletId)) {
                        values.add(value);
                    }
                }
            }
        } else if (mode == AppUtil.Mode.RECEIVED) {
            for (HistoryValue value : dataModels) {
                if (obj != null) {
                    if (value.toAddress.equals(obj.result.walletId)) {
                        values.add(value);
                    }
                }
            }
        }
        return values;
    }

    @Override
    public void onRefresh() {
        if(kycFlag.equals("ACCEPTED")) {
            loadRecyclerViewData();
        }
        else {
            hideList();
        }
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }
}
