package btm.odinandroidwallet.util;

import android.content.Context;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;

import java.util.Random;

public class StringUtils {

    private Context context;
    public StringUtils(Context context) {
        this.context = context;
    }
    /**
     Function/Module Name : getString
     Purpose : Method to get string by its ID
     Input: stringID
     Output :  string
     **/
    public String getString(@StringRes int stringId) {
        return context.getString(stringId);
    }

    public String getQuantityString(@PluralsRes int pluralId, int size) {
        return context.getResources().getQuantityString(pluralId, size, size);
    }

    public String getFormattedString(@StringRes int stringId, Object... formatArgs) {
        return context.getResources().getString(stringId, formatArgs);
    }
    public static String randomAlphanumericString(int count) {
        char[] chars = new char[count];
        Random random = new Random();
        final String POSSIBLE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < count; i++) {
            chars[i] = POSSIBLE_CHARS.charAt(random.nextInt(POSSIBLE_CHARS.length()));
        }
        return new String(chars);
    }
}
