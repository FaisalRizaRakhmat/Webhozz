package wartaonline.chat.chatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import wartaonline.chat.chatapp.MainApplication;
import wartaonline.chat.chatapp.models.User;


public class PrefUtils {

    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String SHARED_PREF ="chat_firebase";
    public static final String PUSH_NOTIFICATION = "push_notification";
    public static final int NOTIFICATION_ID = 100;

    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
        return settings.getBoolean(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static int getInt(String key, int defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
        return settings.getInt(key, defValue);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static long getLong(String key, long defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
        return settings.getLong(key, defValue);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static String getString(String key, String defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
        return settings.getString(key, defValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
        editor.putString(key, value);
//        editor.apply();
        editor.commit();
    }

    public static void remove(String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
        editor.remove(key);
        editor.commit();
    }


    //
    public static void setCurrentUser(User currentUser, Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.putObject("current_user_value", currentUser);
        complexPreferences.commit();
    }

    public static User getCurrentUser(Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        User currentUser = complexPreferences.getObject("current_user_value", User.class);
        return currentUser;
    }

    public static void clearCurrentUser( Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

    public static SharedPreferences pref(){
        return PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
    }

}