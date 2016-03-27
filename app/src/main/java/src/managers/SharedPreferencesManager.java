package src.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/*
 * A Singleton for managing SharedPreferences.
 *
 * Default mode is MODE_PRIVATE
 *
 *
 * IMPORTANT: The class is not thread safe. It should work fine in most
 * circumstances since the write and read operations are fast.
 *
 * Usage:
 *
 * String sampleString = SharedPreferencesManager.getInstance(context).getString(Key.SAMPLE_KEY_TEST);
 *
 * OR
 *
 * String sampleString = SharedPreferencesManager.getInstance(context).getString(Key.SAMPLE_KEY_TEST, "defaultValue");
 *
 * AND
 *
 * SharedPreferencesManager.getInstance(context).put(Key.SAMPLE_KEY_TEST, sampleString);
 *
 */

public class SharedPreferencesManager {

    public static final String TAG = SharedPreferencesManager.class.getSimpleName();

    private static final String SETTINGS_NAME = "default_settings";
    private static SharedPreferencesManager sSharedPreferencesManager;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;


    /**
     * Enum containing the keys used by the shared preferences
     */
    public enum Key {
        SAMPLE_KEY_TEST;
    }



    private SharedPreferencesManager(Context context) {
        mPreferences = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }



    public static SharedPreferencesManager getInstance(Context context) {
        if(sSharedPreferencesManager == null) {
            Log.i(TAG, "first initialization");
            sSharedPreferencesManager = new SharedPreferencesManager(context);
        }

        return sSharedPreferencesManager;
    }



    private void doEdit() {
        if(mEditor == null) {
            mEditor = mPreferences.edit();
        }
    }



    private void doCommit() {
        if(mEditor != null) {
            mEditor.commit();
            mEditor = null;
        }
    }



    // ======== PUT METHODS ============

    public void put(Key key, int val) {
        doEdit();
        mEditor.putInt(key.name(), val);
        doCommit();
        Log.i(TAG, "Key: " + key.name() + " Val: " + val + " included");
    }



    public void put(Key key, String val) {
        doEdit();
        mEditor.putString(key.name(), val);
        doCommit();
        Log.i(TAG, "Key: " + key.name() + " Val: " + val + " included");
    }



    public void put(Key key, boolean val) {
        doEdit();
        mEditor.putBoolean(key.name(), val);
        doCommit();
        Log.i(TAG, "Key: " + key.name() + " Val: " + val + " included");
    }



    public void put(Key key, float val) {
        doEdit();
        mEditor.putFloat(key.name(), val);
        doCommit();
        Log.i(TAG, "Key: " + key.name() + " Val: " + val + " included");
    }



    /**
     * Convenience method for storing doubles.
     * <p/>
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to be
     * cast to and from String.
     *
     * @param key The enum of the preference to store.
     * @param val The new value for the preference.
     */
    public void put(Key key, double val) {
        doEdit();
        mEditor.putString(key.name(), String.valueOf(val));
        doCommit();
        Log.i(TAG, "Key: " + key.name() + " Val: " + val + " included");
    }



    public void put(Key key, long val) {
        doEdit();
        mEditor.putString(key.name(), String.valueOf(val));
        doCommit();
        Log.i(TAG, "Key: " + key.name() + " Val: " + val + " included");
    }



    // ========= GET METHODS ===========

    public int getInt(Key key) {
        return mPreferences.getInt(key.name(), 0);
    }



    public int getInt(Key key, int defaultValue) {
        return mPreferences.getInt(key.name(), defaultValue);
    }



    public String getString(Key key) {
        return mPreferences.getString(key.name(), null);
    }



    public String getString(Key key, String defaultValue) {
        return mPreferences.getString(key.name(), defaultValue);
    }



    public boolean getBoolean(Key key) {
        return mPreferences.getBoolean(key.name(), false);
    }



    public boolean getBoolean(Key key, boolean defaultValue) {
        return mPreferences.getBoolean(key.name(), defaultValue);
    }



    public float getFloat(Key key) {
        return mPreferences.getFloat(key.name(), 0);
    }



    public float getFloat(Key key, float defaultValue) {
        return mPreferences.getFloat(key.name(), defaultValue);
    }



    /**
     * Convenience method for retrieving doubles.
     * <p/>
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to be
     * cast to and from String.
     *
     * @param key The enum of the preference to fetch.
     */
    public double getDouble(Key key) {
        return getDouble(key, 0);
    }




    /**
     * Convenience method for retrieving doubles.
     * <p/>
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to be
     * cast to and from String.
     *
     * @param key The enum of the preference to fetch.
     */
    public double getDouble(Key key, double defaultValue) {
        try {
            return Double.valueOf(mPreferences.getString(key.name(), String.valueOf(defaultValue)));
        } catch (NumberFormatException nfe) {
            Log.e(TAG, nfe.getMessage());
            nfe.printStackTrace();
            return defaultValue;
        }
    }




    /**
     * Remove records from SharedPreferences.
     *
     * @param keys The enum of the key(s) / record(s) to be removed.
     */
    public void remove(Key... keys) {
        doEdit();
        for (Key key : keys) {
            mEditor.remove(key.name());
            Log.i(TAG, "Key: " + key.name() + " removed");
        }
        doCommit();
    }



    /**
     * Remove all keys from SharedPreferences.
     */
    public void clear() {
        doEdit();
        mEditor.commit();
        doCommit();
        Log.i(TAG, "All records cleared");
    }



    public void edit() {
        mEditor = mPreferences.edit();
    }



    public void commit() {
        mEditor.commit();
        mEditor = null;
    }
}
