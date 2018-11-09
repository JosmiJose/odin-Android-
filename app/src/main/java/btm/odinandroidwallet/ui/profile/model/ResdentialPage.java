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

package btm.odinandroidwallet.ui.profile.model;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.ui.profile.ui.CustomerInfoFragment;
import btm.odinandroidwallet.ui.profile.ui.ResdentialAddressFragment;

public class ResdentialPage extends Page {
    public static final String ADDRESS_COUNTRY_KEY = "addresscountry";
    public static final String STATE_DATA_KEY = "state";
    public static final String CITY_DATA_KEY = "city";
    public static final String ZIPCODE_DATA_KEY = "zipcode";
    public static final String ADDRESS1_DATA_KEY = "address1";
    public static final String ADDRESS2_DATA_KEY = "address2";

    Context ctx;
    public ResdentialPage(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.ctx=context;
    }

    @Override
    public Fragment createFragment() {
        return ResdentialAddressFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(ctx.getString(R.string.your_country), mData.getString(ADDRESS_COUNTRY_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_state), mData.getString(STATE_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_city), mData.getString(CITY_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_zipcode), mData.getString(ZIPCODE_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_address1), mData.getString(ADDRESS1_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_address2), mData.getString(ADDRESS2_DATA_KEY), getKey(), -1,1));
    }

    @Override
    public boolean isCompleted() {
        if(TextUtils.isEmpty(mData.getString(ADDRESS_COUNTRY_KEY)))
        {
            return false;
        }
        //Request to make the state not mandatory field - request number OW-134
       /* else if(TextUtils.isEmpty(mData.getString(STATE_DATA_KEY)))
        {
            return false;
        }*/else if(TextUtils.isEmpty(mData.getString(CITY_DATA_KEY)))
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(ZIPCODE_DATA_KEY)))
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(ADDRESS1_DATA_KEY)))
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(ADDRESS2_DATA_KEY)))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
