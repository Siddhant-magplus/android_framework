package src.managers;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import src.data.Quote;

/*
 * A singleton for managing network communication with a REST service.
 *
 * It's using OkHttp client and AsyncTasks for executing the requests
 * on background threads.
 *
 * It's using Gson for parsing JSON results to Java POJOs
 *
 * Contains simple functions for logging.
 *
 *
 * DEFAULT VALUES:
 * Connection timeout - 30 seconds
 * Write timeout - 30 seconds
 * Read timeout - 30 seconds
 *
 *
 * USAGE:
 * DataManager.getInstance().init(context); (call only once during application's lifetime)
 * DataManager.getInstance().get_yoda_speak_sample(text);
 *
 *
 * NOTES:
 * It can be combined with an event bus (Otto) in order to post events when the
 * AsyncTask is finished.
 */
public class DataManager {

    public static final String TAG = DataManager.class.getSimpleName();

    public static final int CLIENT_CONNECT_TIMEOUT = 30; // in seconds
    public static final int CLIENT_WRITE_TIMEOUT = 30; // in seconds
    public static final int CLIENT_READ_TIMEOUT = 30; // in seconds

    // just a key used for consuming sample REST services listed in http://market.mashape.com
    private static final String MASHAPE_KEY = "3Q08UjbAn5msh4c3AIEx8rVFgmBzp1TXl7BjsnEQoHpiy4djsQ";

    // the base URL for a sample REST service
    private static final String BASE_URL_YODA_SPEAK = "https://yoda.p.mashape.com/yoda";

    // the base URL for a sample REST service
    private static final String BASE_URL_RANDOM_QUOTES = "https://andruxnet-random-famous-quotes.p.mashape.com/";

    private static final DataManager mInstance = new DataManager();

    private boolean initialized = false;
    private Context mContext;
    private Activity mBaseActivity;
    private OkHttpClient client;
    private Gson gson;



    private DataManager() {
        client = new OkHttpClient.Builder()
                .connectTimeout(CLIENT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(CLIENT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CLIENT_READ_TIMEOUT, TimeUnit.SECONDS)
                .build();


        gson = new Gson();
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



    /**
     * A sample GET request using OkHttp client
     * @param text
     */
    public void get_yoda_speak_sample(final String text) {
        Log.i(TAG, "Attempting to execute get_yoda_speak_sample");


        if(text == null || text.isEmpty()) {
            Log.i(TAG, "NULL or empty parameter");
            Log.i(TAG, "get_yoda_speak_sample aborted");
            return;
        }


        AsyncTask<Void, Void, Void> getYodaSpeak_task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Request request = new Request.Builder()
                            .url(BASE_URL_YODA_SPEAK + "?sentence=" + text)
                            .header("X-Mashape-Key", MASHAPE_KEY)
                            .header("Accept", "text/plain")
                            .build();

                    Log.i(TAG, "get_yoda_speak_sample request to be executed");
                    Response response = client.newCall(request).execute();

                    if(response.isSuccessful()) {
                        print_success_log("get_yoda_speak_sample", response.body().string());
                    } else {
                        print_error_log("get_yoda_speak_sample", response);
                    }
                } catch (IOException exp) {
                    Log.e(TAG, exp.getMessage());
                    exp.printStackTrace();
                }

                return null;
            }
        };


        runAsyncTask(getYodaSpeak_task);
    }


    /**
     * A simple POST request using OkHttp client
     * and sending parameters as JSON within the request.
     *
     * Uses Gson for parsing a JSON response to a Java POJO
     *
     * @param category
     */
    public void post_random_quotes(final String category) {
        Log.i(TAG, "Attempting to execute post_random_quotes");


        if(category == null || category.isEmpty()) {
            Log.i(TAG, "NULL or empty parameter");
            Log.i(TAG, "post_random_quotes aborted");
            return;
        }


        AsyncTask<Void, Void, Void> postRandomQuotes_task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONObject parametersJson = new JSONObject();
                    parametersJson.put("cat", category);


                    String parametersStr = parametersJson.toString();
                    MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(mediaType, parametersStr);


                    Request request = new Request.Builder()
                            .url(BASE_URL_RANDOM_QUOTES)
                            .header("ContentType", "application/json")
                            .header("X-Mashape-Key", MASHAPE_KEY)
                            .header("Accept", "application/json")
                            .post(body)
                            .build();


                    Log.i(TAG, "post_random_quotes request to be executed");
                    Response response = client.newCall(request).execute();


                    if(response.isSuccessful()) {
                        String result = response.body().string();
                        print_success_log("post_random_quotes", result);

                        Quote quote = gson.fromJson(result, Quote.class);
                    } else {
                        print_error_log("post_random_quotes", response);
                    }
                } catch (JSONException | IOException exp) {
                    Log.e(TAG, exp.getMessage());
                    exp.printStackTrace();
                }


                return null;
            }
        };


        runAsyncTask(postRandomQuotes_task);
    }



    /**
     * Print a simple success log
     * @param functionName The name of the function
     * @param object The result of the executed request
     */
    private void print_success_log(String functionName, Object object) {
        Log.i(TAG, functionName + " request executed successfully");
        Log.i(TAG, functionName + " request result: " + object.toString());
    }



    /**
     * Print a simple error log
     * @param functionName The name of the function
     * @param response The response of the executed request
     */
    private void print_error_log(String functionName, Response response) {
        Log.i(TAG, functionName + " request failed");
        Log.i(TAG, functionName + " request error code: " + response.code());
        Log.i(TAG, functionName + " request error message: " + response.message());
    }
}
