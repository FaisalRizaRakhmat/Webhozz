package wartaonline.chat.chatapp.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import wartaonline.chat.chatapp.utils.PrefUtils;

/**
 * Created by Asus on 8/31/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    //buat nama log
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        String hasilToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, hasilToken);
        //simpan token diprefrence
        storeRegIdInPref(hasilToken);

        //send UI registration token complete
        Intent regComplete = new Intent(PrefUtils.REGISTRATION_COMPLETE);
        regComplete.putExtra("token", hasilToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(regComplete);


    }
        private void storeRegIdInPref(String token){
            //PrefUtils.putString("regId", token);
            SharedPreferences pref = getApplicationContext().getSharedPreferences(PrefUtils.SHARED_PREF,0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("regId",token);
            editor.commit();

        }

}
