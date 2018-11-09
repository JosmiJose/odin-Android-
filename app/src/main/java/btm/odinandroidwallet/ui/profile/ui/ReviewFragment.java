package btm.odinandroidwallet.ui.profile.ui;


import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.ui.profile.model.AbstractWizardModel;
import btm.odinandroidwallet.ui.profile.model.ModelCallbacks;
import btm.odinandroidwallet.ui.profile.model.Page;
import btm.odinandroidwallet.ui.profile.model.ReviewItem;
import btm.odinandroidwallet.ui.views.ProgressImageView;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.MediaUtils;

public class ReviewFragment extends ListFragment implements ModelCallbacks {
    private Callbacks mCallbacks;
    private AbstractWizardModel mWizardModel;
    private List<ReviewItem> mCurrentReviewItems;

    private ReviewAdapter mReviewAdapter;
    private static final int THUMBNAIL_SIZE = 80;

    public ReviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReviewAdapter = new ReviewAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        TextView titleView = (TextView) rootView.findViewById(android.R.id.title);
        titleView.setText(R.string.review);
        titleView.setTextColor(getResources().getColor(R.color.primary_navy_dark));
        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(mReviewAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks");
        }

        mCallbacks = (Callbacks) activity;

        mWizardModel = mCallbacks.onGetModel();
        mWizardModel.registerListener(this);
        onPageTreeChanged();
    }

    @Override
    public void onPageTreeChanged() {
        onPageDataChanged(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;

        mWizardModel.unregisterListener(this);
    }

    @Override
    public void onPageDataChanged(Page changedPage) {
        mCurrentReviewItems = AppUtil.getReviewItems(mWizardModel);
        if (mReviewAdapter != null) {
            mReviewAdapter.notifyDataSetInvalidated();
        }
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallbacks.onEditScreenAfterReview(mCurrentReviewItems.get(position).getPageKey());
    }

    public interface Callbacks {
        AbstractWizardModel onGetModel();
        void onEditScreenAfterReview(String pageKey);
    }

    private class ReviewAdapter extends BaseAdapter {
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            ReviewItem reviewItem = mCurrentReviewItems.get(position);
            if (reviewItem.getType() == 1 || reviewItem.getType()==3) {
                return 0;
            } else {
                return 1;
            }
        }
        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public Object getItem(int position) {
            return mCurrentReviewItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mCurrentReviewItems.get(position).hashCode();
        }

        @Override
        public View getView(int position, View view, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            int type = getItemViewType(position);
            View rootView ;
            if(type==0)
            {rootView= inflater.inflate(R.layout.list_item_review, container, false);}
            else {
                rootView= inflater.inflate(R.layout.review_list_item_image, container, false);
            }

            ReviewItem reviewItem = mCurrentReviewItems.get(position);
            String value = reviewItem.getDisplayValue();
            if (TextUtils.isEmpty(value)) {
                value = "(None)";
            }

            ((TextView) rootView.findViewById(android.R.id.text1)).setText(reviewItem.getTitle());
            if(reviewItem.getType()==1) {
                ((TextView) rootView.findViewById(android.R.id.text2)).setText(value);
            }
            else if(reviewItem.getType()==3)
            {
                String intValue = "";
                if(reviewItem.getTitle().equals(getString(R.string.your_work_type)))
                {
                    String[] types = getResources().getStringArray(R.array.work_type_options);
                    int index=Integer.valueOf(reviewItem.getDisplayValue());
                    intValue=getValue(index,types);

                }else if(reviewItem.getTitle().equals(getString(R.string.your_industry)))
                {
                    String[] types = getResources().getStringArray(R.array.Industry_options);
                    int index=Integer.valueOf(reviewItem.getDisplayValue());
                    intValue=getValue(index,types);

                }else if(reviewItem.getTitle().equals(getString(R.string.your_planned_investment_range)))
                {
                    String[] types = getResources().getStringArray(R.array.planned_options);
                    int index=Integer.valueOf(reviewItem.getDisplayValue());
                    intValue=getValue(index,types);

                }else if(reviewItem.getTitle().equals(getString(R.string.your_purpose_of_action)))
                {
                    String[] types = getResources().getStringArray(R.array.purpose_options);
                    int index=Integer.valueOf(reviewItem.getDisplayValue());
                    intValue=getValue(index,types);

                }

                ((TextView) rootView.findViewById(android.R.id.text2)).setText(intValue);
            }
            else if(reviewItem.getType()==2) {
                if(!TextUtils.isEmpty(reviewItem.getDisplayValue())) {
                    Uri uri = Uri.parse(reviewItem.getDisplayValue());
                    ImageView image = rootView.findViewById(R.id.img);
                    prepareImage(image, uri);
                }
            }
            return rootView;
        }

        @Override
        public int getCount() {
            return mCurrentReviewItems.size();
        }
    }
    private String getValue(int index, String[] options )
    {
        return options[index];
    }
    private void prepareImage(ImageView image, Uri uri) {
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        File file = new File(uri.getPath());
        Picasso.with(getContext())
                .load(file)
                .resize(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                .into(image);

    }
}