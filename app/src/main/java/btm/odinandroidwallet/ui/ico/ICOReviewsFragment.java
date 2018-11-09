package btm.odinandroidwallet.ui.ico;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.ICOReviewsAdapter;
import btm.odinandroidwallet.networking.retrofit.pojo.PreIcoCompanyListValue;
import btm.odinandroidwallet.objects.Review;

public class ICOReviewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    ArrayList<Review> reviews;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    ICOReviewsAdapter RecyclerViewHorizontalAdapter;
    LinearLayoutManager HorizontalLayout;
    View ChildView;
    int RecyclerViewItemPosition;
    SwipeRefreshLayout mSwipeRefreshLayout;
    PreIcoCompanyListValue value;
    public static ICOReviewsFragment newInstance(PreIcoCompanyListValue value) {
        ICOReviewsFragment fragment = new ICOReviewsFragment();
        fragment.value=value;
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.ico_reviews_fragment,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview1);

        RecyclerViewLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
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
                loadRecyclerViewData();
            }
        });
        return rootView;
    }

    private void loadRecyclerViewData() {
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);
        // Adding items to RecyclerView.
        AddItemsToRecyclerViewArrayList();
        RecyclerViewHorizontalAdapter = new ICOReviewsAdapter(reviews);
        recyclerView.setAdapter(RecyclerViewHorizontalAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    public void AddItemsToRecyclerViewArrayList() {

        reviews = new ArrayList<>();
        reviews.add(new Review("Team Strength",value.companyReview,value.companyRating));

    }
    @Override
    public void onRefresh() {
        loadRecyclerViewData();
    }
}