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

/**
 * A page asking for a name and an email.
 */
public class CustomerInfoPage extends Page {
    public static final String DOB_DATA_KEY = "dob";
    public static final String COUNTRY_DATA_KEY = "country";
    public static final String HOME_COUNTRY_DATA_KEY = "homecountry";
    public static final String PHONE_DATA_KEY = "phone";
    public static final String PHONECODE_DATA_KEY = "phonecode";
    public static final String FNAME_DATA_KEY = "fname";
    public static final String MNAME_DATA_KEY = "mname";
    public static final String LNAME_DATA_KEY = "lname";
    public static final String GENDER_DATA_KEY = "gender";
    Context ctx;
    public CustomerInfoPage(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.ctx=context;
    }

    @Override
    public Fragment createFragment() {
        return CustomerInfoFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        //dest.add(new ReviewItem(ctx.getString(R.string.your_name), mData.getString(FNAME_DATA_KEY)+" "+mData.getString(MNAME_DATA_KEY)+" "+mData.getString(LNAME_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_f_name), mData.getString(FNAME_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_m_name), mData.getString(MNAME_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_l_name), mData.getString(LNAME_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_dob), mData.getString(DOB_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_gender), mData.getString(GENDER_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_phone_country), mData.getString(COUNTRY_DATA_KEY), getKey(), -1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_phone), mData.getString(PHONE_DATA_KEY), getKey(), -1,1));
    }

    @Override
    public boolean isCompleted() {
        if(TextUtils.isEmpty(mData.getString(DOB_DATA_KEY)))
        {
            return false;
        }else if(TextUtils.isEmpty(mData.getString(PHONE_DATA_KEY)))
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(FNAME_DATA_KEY))||mData.getString(FNAME_DATA_KEY).length()>50)
        {
            return false;
        }
        /*else if(TextUtils.isEmpty(mData.getString(MNAME_DATA_KEY))||mData.getString(FNAME_DATA_KEY).length()>50)
        {
            return false;
        }*/
        else if(TextUtils.isEmpty(mData.getString(LNAME_DATA_KEY))||mData.getString(FNAME_DATA_KEY).length()>50)
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(GENDER_DATA_KEY)))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
