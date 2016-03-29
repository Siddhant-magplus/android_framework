package src.managers;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class DataManager {

    public static final String TAG = DataManager.class.getSimpleName();

    public static final int CLIENT_CONNECT_TIMEOUT = 30; // in seconds
    public static final int CLIENT_WRITE_TIMEOUT = 30; // in seconds
    public static final int CLIENT_READ_TIMEOUT = 30; // in seconds

    private static final DataManager mInstance = new DataManager();

    private boolean initialized = false;
    private Context mContext;
    private Activity mBaseActivity;
    private OkHttpClient client;



    private DataManager() {
        client = new OkHttpClient.Builder()
                .connectTimeout(CLIENT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(CLIENT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CLIENT_READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }



    public static DataManager getInstance() { return mInstance; }



    public void init(Activity activity) {
        if(activity == null) {
            Log.e(TAG, "Context is null");
            return;
        }


        if(initialized) {
            return;
        }


        mContext = activity;
        mBaseActivity = activity;

        initialized = true;
    }



    public static AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
}
