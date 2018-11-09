package btm.odinandroidwallet.networking.retrofit;

import android.content.Context;
import android.os.Build;

import btm.odinandroidwallet.App;
import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.util.TLSSocketFactory;
import btm.odinandroidwallet.util.AppUtil;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;

   public static Retrofit getClient(Context context) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
       OkHttpClient client=null;
       CertificatePinner certificatePinner = new CertificatePinner.Builder()
               .add("www.odinwallet.io", AppUtil.pin)
               .add("www.odinwallet.io",AppUtil.pin2)
               .build();
       if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
           try {
               client = new OkHttpClient.Builder().certificatePinner(certificatePinner)
                       .sslSocketFactory(new TLSSocketFactory()).addInterceptor(interceptor).addInterceptor(new ConnectivityInterceptor(context)).build();
           } catch (Exception ex) {

           }
       }else
       {
           client = new OkHttpClient.Builder().certificatePinner(certificatePinner).addInterceptor(interceptor).addInterceptor(new ConnectivityInterceptor(context)).build();
       }

        retrofit = new Retrofit.Builder()
                .baseUrl(AppUtil.url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit;
    }

}