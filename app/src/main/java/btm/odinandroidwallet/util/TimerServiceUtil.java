package btm.odinandroidwallet.util;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class TimerServiceUtil extends Service
{
    private final static String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "btm.odinandroidwallet.util.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG, "Starting timer...");

        cdt = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                bi.putExtra("countdownFinished", true);
                sendBroadcast(bi);
                killService();
            }
        };

        cdt.start();

    }

    void killService(){
        cdt = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Killing service");
                stopSelf();
            }
        };

        cdt.start();
    }

    public void onDestroy()
    {
        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}
