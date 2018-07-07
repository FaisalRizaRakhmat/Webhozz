package wartaonline.chat.chatapp.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.util.List;

import wartaonline.chat.chatapp.R;

/**
 * Created by Asus on 8/31/2017.
 */

public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();
    private Context context;

    public  NotificationUtils(Context context){
        this.context = context;
    }

    public void showNotifMessage(String title, String message, String timestamp, Intent intent){
        showNotifMessage(title,message,timestamp,intent,null);
    }

    public void showNotifMessage(String title, String message, String timestamp, Intent intent, String imageUrl){
        //cek data
        if(TextUtils.isEmpty(message))
            return;

        //icon
        final int icon = R.mipmap.ic_launcher;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://"+context.getPackageName()
                +"/raw/notification");

        showNotification(builder,icon,title,message,timestamp,pendingIntent,alarmSound);

    }

    private void showNotification(NotificationCompat.Builder builder, int icon, String title, String message, String timestamp, PendingIntent pendingIntent, Uri alarmSound){
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);

        Notification notification;
        notification = builder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),icon))
                .setContentText(message)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PrefUtils.NOTIFICATION_ID,notification);
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

}
