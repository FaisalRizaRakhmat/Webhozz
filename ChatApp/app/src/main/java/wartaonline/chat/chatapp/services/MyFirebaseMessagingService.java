package wartaonline.chat.chatapp.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import wartaonline.chat.chatapp.MainActivity;
import wartaonline.chat.chatapp.utils.NotificationUtils;
import wartaonline.chat.chatapp.utils.PrefUtils;

/**
 * Created by Asus on 8/31/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();


    private NotificationUtils notificationUtils;
    //penerima messaing push notif
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.e(TAG, "FROM " + remoteMessage.getFrom());

        if(remoteMessage == null)
            return;

        //cek message berisi notif payload
        if(remoteMessage.getNotification() != null){
            Log.e (TAG, "Notif body" + remoteMessage.getNotification().getBody());

            //handle notif
            handleNotification(remoteMessage.getNotification().getBody());

        }

        //cek jika message berisi data payload
        if(remoteMessage.getData().size() > 0){
            Log.e(TAG, "Data Payload" + remoteMessage.getData().toString());
            try{
                JSONObject jsonObject = new JSONObject(remoteMessage.getData().toString());

            }catch(Exception ex){

            }

        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(PrefUtils.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            //notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    public void sendMessageToFcm(String jsonMessage) {
        final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        try {
//            Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
//                    .addHeader("Content-Type", "application/json; UTF-8")
//                    .addHeader("Authorization", "Bearer " + FirebaseInstanceId.getInstance().getToken())
//                    .post(RequestBody.create(mediaType, jsonMessage)).build();

            Log.d(TAG, "sendMessageToFcm: "+FirebaseInstanceId.getInstance().getToken());

            //Response response = httpClient.newCall(request).execute();
            //Log.d(TAG, "sendMessageToFcm: "+response);
//            if (response.isSuccessful()) {
//                //log.info("Message has been sent to FCM server " + response.body().string());
//            }

            //public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
//            final MediaType JSON
//                    = MediaType.parse("application/json; charset=utf-8");
//
//            RequestBody body = RequestBody.create(JSON, jsonMessage);
//            Request request = new Request.Builder()
//                    .url("https://fcm.googleapis.com/fcm/send")
//                    .post(body)
//                    .addHeader("Authorization", "key=" + "AAAAGF_5SiA:APA91bH0lkq7IHgKySHVt9uRVVuEEvO4Fzwh_D0QTjM-h8S9Yuk-Jio_XYQlNLcMc9InfiSBydh4vYn2PPktE6p43WXKhv31g1nSjSgqoKRp_Rxf-8vVgKeScfbzDctooglAn9SA6f7G")
//                    .build();
//            Response response = httpClient.newCall(request).execute(); //mClient.newCall(request).execute();
//            //return response.body().string();
//            if (response.isSuccessful()) {
//                //log.info("Message has been sent to FCM server " + response.body().string());
//            }

            //OkHttpClient client = new OkHttpClient();
            try {

            JSONObject json=new JSONObject();
            JSONObject dataJson=new JSONObject();
            dataJson.put("body","Hi this is sent from device to device");
            dataJson.put("title","dummy title");

            json.put("notification",dataJson);
            //json.put("to",regToken);
            RequestBody body = RequestBody.create(mediaType, json.toString());
            Request request = new Request.Builder()
                    .header("Authorization","key=AIzaSyAw88b3tKQ0rRp7mIZlacRqO2A3JFYr2SA")
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            String finalResponse = response.body().string();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            //log.info("Error in sending message to FCM server " + e);
        }

    }

    public String getFCMNotificationMessage(String title, String msg) {
        JsonObject jsonObj = new JsonObject();
        // client registration key is sent as token in the message to FCM server
        jsonObj.addProperty("token", FirebaseInstanceId.getInstance().getToken());

        JsonObject notification = new JsonObject();
        notification.addProperty("body", msg);
        notification.addProperty("title", title);
        jsonObj.add("notification", notification);

        JsonObject message = new JsonObject();
        message.add("message", jsonObj);

        //log.info("notification message " + message.toString());

        return message.toString();
    }

//    private void handleDataMessage(JSONObject json) {
//        Log.e(TAG, "push json: " + json.toString());
//
//        try {
//            JSONObject data = json.getJSONObject("data");
//
//            String title = data.getString("title");
//            String message = data.getString("message");
//            boolean isBackground = data.getBoolean("is_background");
//            String imageUrl = data.getString("image");
//            String timestamp = data.getString("timestamp");
//            JSONObject payload = data.getJSONObject("payload");
//
//            Log.e(TAG, "title: " + title);
//            Log.e(TAG, "message: " + message);
//            Log.e(TAG, "isBackground: " + isBackground);
//            Log.e(TAG, "payload: " + payload.toString());
//            Log.e(TAG, "imageUrl: " + imageUrl);
//            Log.e(TAG, "timestamp: " + timestamp);
//
//
//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(PrefUtils.PUSH_NOTIFICATION);
//                pushNotification.putExtra("message", message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                //notificationUtils.playNotificationSound();
//            } else {
//                // app is in background, show the notification in notification tray
//                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//                resultIntent.putExtra("message", message);
//
//                // check for image attachment
//                if (TextUtils.isEmpty(imageUrl)) {
//                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
//                } else {
//                    // image is present, show notification with image
//                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
//                }
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Json Exception: " + e.getMessage());
//        } catch (Exception e) {
//            Log.e(TAG, "Exception: " + e.getMessage());
//        }
//    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotifMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotifMessage(title, message, timeStamp, intent, imageUrl);
    }
}
