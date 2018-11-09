package btm.odinandroidwallet.ui.ico;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.TeamAdapter;
import btm.odinandroidwallet.networking.retrofit.pojo.PreIcoCompanyListValue;
import btm.odinandroidwallet.networking.retrofit.pojo.TeamDetail;
import btm.odinandroidwallet.objects.TeamMember;
import btm.odinandroidwallet.ui.customviews.GridRecyclerView;

public class ICOTeamFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    GridRecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TeamAdapter adapter;
    List<TeamDetail> members=new ArrayList<>();
    PreIcoCompanyListValue value;
    FrameLayout emptyLayout;
    public static ICOTeamFragment newInstance(PreIcoCompanyListValue value) {
        ICOTeamFragment fragment = new ICOTeamFragment();
        fragment.value=value;
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_team,container,false);
        emptyLayout=rootView.findViewById(R.id.empty_layout);
        recyclerView = (GridRecyclerView) rootView.findViewById(R.id.recycler_view_team);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_container);
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

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                try {
                    if (value.teamDetails.size() > 0) {
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        loadRecyclerViewData();
                    } else {
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                }catch (Exception Ex){}
            }
        });
        return rootView;
    }
    private void loadRecyclerViewData() {
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);
        // Adding items to RecyclerView.
        members=value.teamDetails;
        adapter = new TeamAdapter(members,getActivity());
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        loadRecyclerViewData();
    }
}