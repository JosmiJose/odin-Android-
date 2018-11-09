package btm.odinandroidwallet.adapters.helper;
/**
 * Method that remove Trailing Newlines From Text
 *
 * @param text the text to be modified
 * @return The result of modified text
 */
public class TextHelper {

    public static CharSequence removeTrailingNewlinesFromText(CharSequence text) {
        if (text == null || text.length() < 1) {
            return "";
        }

        if (text.charAt(text.length() - 1) == "\n".charAt(0)) {
            return removeTrailingNewlinesFromText(text.subSequence(0, text.length() - 1));
        }

        return text;
    }
}
