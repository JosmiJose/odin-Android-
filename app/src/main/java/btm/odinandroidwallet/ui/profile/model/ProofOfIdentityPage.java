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
import btm.odinandroidwallet.ui.profile.ui.ProofOfIdentityFragment;

/**
 * A page asking for a name and an email.
 */
public class ProofOfIdentityPage extends Page {
    public static final String DOC_TYPE_DATA_KEY = "documenttype";
    public static final String NUMBER_KEY = "number";
    public static final String NATIONALITY_KEY = "nationality";
    public static final String EXPIRY_DATE_KEY = "expirydate";
    public static final String FRONT_IMAGE_KEY = "frontimage";
    public static final String BACK_IMAGE_KEY = "backimage";
    public static final String SELFIE_IMAGE_KEY = "selfieimage";

    Context ctx;

    public ProofOfIdentityPage(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.ctx=context;
    }

    @Override
    public Fragment createFragment() {
        return ProofOfIdentityFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(ctx.getString(R.string.your_nationality), mData.getString(NATIONALITY_KEY), getKey(),-1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_doc_type), mData.getString(DOC_TYPE_DATA_KEY), getKey(),-1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_doc_number), mData.getString(NUMBER_KEY), getKey(),-1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_doc_expiry_date), mData.getString(EXPIRY_DATE_KEY), getKey(),-1,1));
        dest.add(new ReviewItem(ctx.getString(R.string.your_front_image), mData.getString(FRONT_IMAGE_KEY), getKey(),-1,2));
        dest.add(new ReviewItem(ctx.getString(R.string.your_back_image), mData.getString(BACK_IMAGE_KEY), getKey(),-1,2));
        dest.add(new ReviewItem(ctx.getString(R.string.your_selfie_image), mData.getString(SELFIE_IMAGE_KEY), getKey(),-1,2));
    }

    @Override
    public boolean isCompleted() {
        if(TextUtils.isEmpty(mData.getString(DOC_TYPE_DATA_KEY)))
        {
            return false;
        }else if(TextUtils.isEmpty(mData.getString(NUMBER_KEY)))
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(EXPIRY_DATE_KEY)) && !(mData.getString(DOC_TYPE_DATA_KEY).equals("ID")))
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(FRONT_IMAGE_KEY)))
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(BACK_IMAGE_KEY)))
        {
            return false;
        }
        else if(TextUtils.isEmpty(mData.getString(SELFIE_IMAGE_KEY)))
        {
            return false;
        }else if(TextUtils.isEmpty(mData.getString(NATIONALITY_KEY)))
        {
            return false;
        }
        else
        {
            return true;
        }

    }
}
