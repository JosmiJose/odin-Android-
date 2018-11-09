package btm.odinandroidwallet.util;

public interface PersistentPrefs {
    String KEY_PIN_IDENTIFIER = "pin_kookup_key";
    String KEY_GUID = "guid";
    String KEY_PIN_FAILS = "pin_fails";
    String LOGGED_OUT = "logged_out";
    String USER_TOKEN = "user_token";
    String KYC_STATUS = "kyc_status";
    String USER_PROFILE = "user_profile";
    String getValue(String name, String value);

    void setValue(String name, String value);

    int getValue(String name, int value);

    void setValue(String name, int value);

    void setValue(String name, long value);

    long getValue(String name, long value);

    boolean getValue(String name, boolean value);

    void setValue(String name, boolean value);

    boolean has(String name);

    void removeValue(String name);

    void clear();

    void logOut();

    void logIn();
}
