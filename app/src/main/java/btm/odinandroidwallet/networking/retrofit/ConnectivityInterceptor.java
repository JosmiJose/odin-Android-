package btm.odinandroidwallet.networking.retrofit;

import android.content.Context;

import java.io.IOException;

import btm.odinandroidwallet.networking.NoConnectivityException;
import btm.odinandroidwallet.util.AppUtil;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class ConnectivityInterceptor implements Interceptor {
 
    private Context mContext;
 
    public ConnectivityInterceptor(Context context) {
        mContext = context;
    }
 
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!AppUtil.isOnline(mContext)) {
            throw new NoConnectivityException();
        }
 
        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
 
}