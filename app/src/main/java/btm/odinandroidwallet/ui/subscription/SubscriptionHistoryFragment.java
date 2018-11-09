package btm.odinandroidwallet.ui.subscription;

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

import java.util.ArrayList;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.PointsHistoryAdapter;
import btm.odinandroidwallet.adapters.PortfolioAdapter;
import btm.odinandroidwallet.adapters.SubscriptionHistoryAdapter;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.PointsHistoryResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.PointsHistoryValue;
import btm.odinandroidwallet.networking.retrofit.pojo.PortfolioResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionHistoryResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionHistoryValue;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionListResponse;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.views.RecyclerViewEmptySupport;
import btm.odinandroidwallet.ui.views.SwipeRefreshLayoutWithEmpty;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Waleed on 7/3/2018.
 */

public class SubscriptionHistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerViewEmptySupport recyclerView;
    private TextView emptyView;
    private SubscriptionHistoryAdapter mAdapter;
    List<SubscriptionHistoryValue> dataModels = new ArrayList<SubscriptionHistoryValue>();
    SwipeRefreshLayoutWithEmpty mSwipeRefreshLayout;
    APIInterface apiInterface;
    private PrefsUtil mPrefsUtil;


    public static SubscriptionHistoryFragment newInstance() {
        SubscriptionHistoryFragment fragment = new SubscriptionHistoryFragment();
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
        recyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.recycler_view);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(emptyView);

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayoutWithEmpty) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadRecyclerViewData();
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
        Call<SubscriptionHistoryResponse> call = apiInterface.getSubscriptionHistory(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<SubscriptionHistoryResponse>() {
            @Override
            public void onResponse(Call<SubscriptionHistoryResponse> call, Response<SubscriptionHistoryResponse> response) {

                Log.d("TAG", response.code() + "");
                SubscriptionHistoryResponse resource = response.body();
                try {
                    if (resource.status) {
                      //  mPrefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                        ContinueAnyWay(resource);
                    } else {
                        showToast(R.string.get_history_error, ToastCustom.TYPE_ERROR);
                    }
                }
                catch (Exception ex)
                {
                    showToast(R.string.get_history_error, ToastCustom.TYPE_ERROR);
                    ContinueAnyWay(null);
                }
            }

            @Override
            public void onFailure(Call<SubscriptionHistoryResponse> call, Throwable t) {
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

    private void ContinueAnyWay(SubscriptionHistoryResponse resource) {

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
        mAdapter = new SubscriptionHistoryAdapter(getActivity(), dataModels);
        recyclerView.setAdapter(mAdapter);
    }

    private void hideList() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setText(getResources().getString(R.string.no_data_available));
    }

    @Override
    public void onRefresh() {
        loadRecyclerViewData();
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }
}
