package btm.odinandroidwallet.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import btm.odinandroidwallet.R;

public class PrefixRttv extends RelativeTimeTextView {
    public PrefixRttv(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CharSequence getRelativeTimeDisplayString(long referenceTime, long now) {
        final String relativeTime = super.getRelativeTimeDisplayString(referenceTime, now).toString();
        return getResources().getString(R.string.format_relative_time_with_prefix, relativeTime);
    }    
}