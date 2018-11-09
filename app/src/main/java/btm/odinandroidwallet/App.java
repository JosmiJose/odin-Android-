package btm.odinandroidwallet;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.security.KeyStore;
import java.util.List;

import devliving.online.securedpreferencestore.DefaultRecoveryHandler;
import devliving.online.securedpreferencestore.SecuredPreferenceStore;


public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
       // NetworkManager.getInstance(this);
        mContext = getApplicationContext();

        try {
            //not mandatory, can be null too
            String storeFileName = "securedStore";
            //not mandatory, can be null too
            String keyPrefix = "vss";
            //it's better to provide one, and you need to provide the same key each time after the first time
            byte[] seedKey = "seed".getBytes();
            SecuredPreferenceStore.init(getApplicationContext(), storeFileName, keyPrefix, seedKey, new DefaultRecoveryHandler());

            //SecuredPreferenceStore.init(getApplicationContext(), null);
            setupStore();
        } catch (Exception e) {
            // Handle error.
            e.printStackTrace();
        }
    }
    private void setupStore() {
        SecuredPreferenceStore.setRecoveryHandler(new DefaultRecoveryHandler(){
            @Override
            protected boolean recover(Exception e, KeyStore keyStore, List<String> keyAliases, SharedPreferences preferences) {
                Toast.makeText(mContext, "Encryption key got invalidated, will try to start over.", Toast.LENGTH_SHORT).show();
                return super.recover(e, keyStore, keyAliases, preferences);
            }
        });

        try {

        } catch (Exception e) {
        }
    }
}