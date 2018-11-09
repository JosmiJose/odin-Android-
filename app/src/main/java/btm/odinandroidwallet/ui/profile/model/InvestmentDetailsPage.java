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
import btm.odinandroidwallet.ui.profile.ui.InvestmentDetailsFragment;
import btm.odinandroidwallet.ui.profile.ui.ResdentialAddressFragment;

public class InvestmentDetailsPage extends Page {
    public static final String PURPOSE_KEY = "purpose";
    public static final String PLANNED_KEY = "planned";
    public static final String INDUSTRY_KEY = "industry";
    public static final String WORK_TYPE_KEY = "worktype";
    public static final String TAX_ID_KEY = "taxId";
    Context ctx;
    public InvestmentDetailsPage(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.ctx=context;
    }

    @Override
    public Fragment createFragment() {
        return InvestmentDetailsFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(ctx.getString(R.string.your_purpose_of_action), mData.getString(PURPOSE_KEY), getKey(), -1,3));
        dest.add(new ReviewItem(ctx.getString(R.string.your_planned_investment_range), mData.getString(PLANNED_KEY), getKey(), -1,3));
        dest.add(new ReviewItem(ctx.getString(R.string.your_industry), mData.getString(INDUSTRY_KEY), getKey(), -1,3));
        dest.add(new ReviewItem(ctx.getString(R.string.your_work_type), mData.getString(WORK_TYPE_KEY), getKey(), -1,3));
        dest.add(new ReviewItem(ctx.getString(R.string.your_tax_id), mData.getString(TAX_ID_KEY), getKey(), -1,1));
    }

    @Override
    public boolean isCompleted() {
        if(TextUtils.isEmpty(mData.getString(PURPOSE_KEY)))
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(PLANNED_KEY)))
        {
            return false;
        }else if(TextUtils.isEmpty(mData.getString(INDUSTRY_KEY)))
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(WORK_TYPE_KEY)))
        {
            return false;
        } else if(TextUtils.isEmpty(mData.getString(TAX_ID_KEY)))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
