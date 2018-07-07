package wartaonline.chat.chatapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by Asus on 6/8/2017.
 */

public class MainApplication extends Application {
    private static Context mContext;
    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mContext = getApplicationContext();

    }
}
