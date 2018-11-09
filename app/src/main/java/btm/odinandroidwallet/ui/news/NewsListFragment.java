package btm.odinandroidwallet.ui.news;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.NewsListAdapter;
import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.networking.retrofit.APIClient;
import btm.odinandroidwallet.networking.retrofit.APIInterface;
import btm.odinandroidwallet.networking.retrofit.pojo.NewsListResponse;
import btm.odinandroidwallet.networking.retrofit.pojo.NewsListValue;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.decoration.DividerItemDecoration;
import btm.odinandroidwallet.ui.listener.ClickListener;
import btm.odinandroidwallet.ui.views.RecyclerViewEmptySupport;
import btm.odinandroidwallet.util.PrefsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsListFragment extends Fragment implements ClickListener {
    private RecyclerViewEmptySupport mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mRootView;
    APIInterface apiInterface;
    private PrefsUtil mPrefsUtil;
    List<NewsListValue> dataModels = new ArrayList<NewsListValue>();
    TextView emptyView;
    NewsListAdapter mAdapter;
    UserProfileResponse obj;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_story_list, container, false);

        emptyView=rootView.findViewById(R.id.empty_view);
        mPrefsUtil = new PrefsUtil(getActivity());
        apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
        Gson gson = new Gson();
        String json = mPrefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        obj = gson.fromJson(json, UserProfileResponse.class);
        mRootView = rootView;

        setupRecyclerView(rootView);
        setupRefreshLayout(rootView);
        mRecyclerView.setEmptyView(emptyView);

        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onClick(View v, int position, boolean isLongClick) {
        openStoryAtPosition(position);
    }



    private void setupRecyclerView(View rootView) {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mRootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = (RecyclerViewEmptySupport) rootView.findViewById(R.id.stories);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRootView.getContext()));
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.Adapter mAdapter = new NewsListAdapter(Collections.EMPTY_LIST, this);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void setupRefreshLayout(View rootView) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadStories();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadStories();
            }
        });
    }


    private void loadStories() {
        Call<NewsListResponse> call = apiInterface.getNews(getString(R.string.auth_prefix) + " " + mPrefsUtil.getValue(PrefsUtil.USER_TOKEN, "token"));
        call.enqueue(new Callback<NewsListResponse>() {
            @Override
            public void onResponse(Call<NewsListResponse> call, Response<NewsListResponse> response) {

                Log.d("TAG", response.code() + "");
                try {
                    NewsListResponse resource = response.body();
                    if (resource.status) {
                        ContinueAnyWay(resource);
                    }
                }catch (Exception Ex)
                {
                    ContinueAnyWay(null);
                    showToast(R.string.get_news_failed, ToastCustom.TYPE_ERROR);
                }
            }

            @Override
            public void onFailure(Call<NewsListResponse> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // No internet connection
                    showToast(R.string.no_network_error, ToastCustom.TYPE_ERROR);
                }
                else {
                    showToast(R.string.get_news_failed, ToastCustom.TYPE_ERROR);
                }
                ContinueAnyWay(null);

            }

        });
    }

    private void ContinueAnyWay(NewsListResponse resource) {

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
        mRecyclerView.setVisibility(View.VISIBLE);
        if(obj != null)
        {
            if(!obj.result.isSubscribed)
            {
                dataModels=filterPrivate(dataModels);
            }
        }
        mAdapter = new NewsListAdapter(dataModels,this);
        mRecyclerView.setAdapter(mAdapter);
    }
    private List<NewsListValue> filterPrivate(List<NewsListValue>items)
    {
     List<NewsListValue> values =   new ArrayList<>();
     for(NewsListValue value : items)
     {
         if(value.newFlag.equals("PUBLIC"))
         {
             values.add(value);
         }
     }
     return values;
    }
    private void hideList() {
        emptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        emptyView.setText(getResources().getString(R.string.no_data_available));
    }
    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }
    private void openStoryAtPosition(int position) {
        Intent i = new Intent(getActivity(), NewsDetailsActivity.class);
        i.putExtra("newsItem", dataModels.get(position));
        startActivity(i);
    }


}
