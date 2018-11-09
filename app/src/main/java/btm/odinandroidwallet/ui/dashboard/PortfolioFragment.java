package btm.odinandroidwallet.ui.dashboard;

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
import btm.odinandroidwallet.adapters.PortfolioAdapter;
import btm.odinandroidwallet.adapters.SubscriptionHistoryAdapter;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.PortfolioResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.PortfolioValue;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionHistoryResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.SubscriptionHistoryValue;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.views.RecyclerViewEmptySupport;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.PrefsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Waleed on 7/3/2018.
 */

public class PortfolioFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerViewEmptySupport recyclerView;
    private TextView emptyView;
    private PortfolioAdapter mAdapter;
    List<PortfolioValue> dataModels = new ArrayList<PortfolioValue>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    APIInterface apiInterface;
    private PrefsUtil mPrefsUtil;
    private String APPROVEDSTATUS="ACCEPTED";
    private String kycFlag = "";

    public static PortfolioFragment newInstance() {
        PortfolioFragment fragment = new PortfolioFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefsUtil = new PrefsUtil(getActivity());
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
        kycFlag = mPrefsUtil.getValue(PrefsUtil.KYC_STATUS, "");
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
        recyclerView.setEmptyView(emptyView);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                if(kycFlag.equals(APPROVEDSTATUS)) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    loadRecyclerViewData();
                }
                else
                {
                    hideList();
                }
            }
        });
        return view;
    }

    private void loadRecyclerViewData() {
        // Showing refresh animation before making http call
        if(kycFlag.equals("ACCEPTED")) {
            mSwipeRefreshLayout.setRefreshing(true);
            // dataModels=AppUtil.prepareHistoryData();

            getPortfolioNetworkCall();
        }

    }

    private void getPortfolioNetworkCall() {
        Call<PortfolioResponse> call = apiInterface.getPortfolioTokens(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<PortfolioResponse>() {
            @Override
            public void onResponse(Call<PortfolioResponse> call, Response<PortfolioResponse> response) {

                Log.d("TAG", response.code() + "");
                PortfolioResponse resource = response.body();
                try {
                    if (resource.status) {
                      mPrefsUtil.setValue(PrefsUtil.KYC_STATUS, resource.kycStatus);
                        ContinueAnyWay(resource);
                    } else {
                        if(resource.kycStatus.equals(APPROVEDSTATUS)) {
                            showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                        }
                        else
                        {
                            showToast(R.string.profile_should_be_updated, ToastCustom.TYPE_ERROR);
                        }
                        ContinueAnyWay(null);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                    ContinueAnyWay(null);
                }
            }

            @Override
            public void onFailure(Call<PortfolioResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                }
                else {
                    showToast(R.string.get_data_error, ToastCustom.TYPE_ERROR);
                }

                ContinueAnyWay(null);
            }

        });
    }

    private void ContinueAnyWay(PortfolioResponse resource) {

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
        mAdapter = new PortfolioAdapter(getActivity(), dataModels);
        recyclerView.setAdapter(mAdapter);
    }

    private void hideList() {
        try {
            mSwipeRefreshLayout.setRefreshing(false);
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyView.setText(getResources().getString(R.string.no_data_available));
        }
        catch (Exception Ex){}

    }

    @Override
    public void onRefresh() {
        if(kycFlag.equals(APPROVEDSTATUS)) {
            loadRecyclerViewData();
        }else
        {
            hideList();
        }
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        try {
            ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
        }catch (Exception Ex){}
    }
}
