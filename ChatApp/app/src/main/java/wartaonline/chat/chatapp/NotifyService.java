package wartaonline.chat.chatapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wartaonline.chat.chatapp.models.ChatGroup;
import wartaonline.chat.chatapp.models.Message;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.utils.PrefUtils;

import static com.android.volley.VolleyLog.TAG;

public class NotifyService extends Service {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private User userdata;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("group");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChatGroup> items = new ArrayList<>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ChatGroup room = snapshot.getValue(ChatGroup.class);
                    Log.i("list group", "sip sip"+snapshot.getKey().toString());
                    checkmeberstatus(snapshot.getKey().toString());
                    checkgroup(snapshot.getKey().toString());
                    items.add(room);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("GROUPFRAGMENT", "loadNote:onCancelled", databaseError.toException());
            }
        };

        databaseReference.orderByKey().addValueEventListener(valueEventListener);



//        databaseReference = firebaseDatabase.getReference("group").child(room);
//        //addNotification();
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<Message> items = new ArrayList<>();
//
//                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
//                    Message message = snapshot.getValue(Message.class);
//                    if (message.uid.equals(userdata.uid)){
//                        Log.i("ChatGroupActivity", "masuk sini");
//                        message.tipe = 1;
//                    }else{
//                        message.tipe = 0;
//                    }
//                    items.add(message);
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w("ChatGroupActivity", "loadNote:onCancelled", databaseError.toException());
//            }
//        };
//        databaseReference.child(room).addValueEventListener(valueEventListener);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    //List<String> itemsxxx = new ArrayList<>();
    //List<HashMap<String,Object>> itemsxxx = new ArrayList<HashMap<String, Object>>();
    HashMap<String,Object> itemsxxx = new HashMap<String, Object>();

    public void checkmeberstatus(final String namegroup){
        //String varxxx = checkmeberstatus(namegroup);
        //checkmeberstatus(namegroup);
        itemsxxx.clear();
        userdata = PrefUtils.getCurrentUser(this);
        databaseReference = firebaseDatabase.getReference("group").child(namegroup);
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //List<StatusMember> items = new ArrayList<>();
                //itemsxxx.clear();
                itemsxxx.put(namegroup,dataSnapshot.child("members").getValue());
                //itemsxxx[namegroup].add(dataSnapshot.child("members").getValue().toString());
//                if(dataSnapshot.child("members").getValue().toString().contains("true")){
//                    Log.d(TAG, "ChatGroupActivity1xxxx true: "+ dataSnapshot.child("members").getValue().toString());
//                    //Log.i("ChatGroupActivity1xxxx true", dataSnapshot.child("members").getValue().toString());
//                }else{
//                    Log.d(TAG, "ChatGroupActivity1xxxx false: "+ dataSnapshot.child("members").getValue().toString());
//
//                }
//                Log.d(TAG, "ChatGroupActivity1wkwkwkk: "+ items.toString());

                //return itemsxxx;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ChatGroupActivity", "loadNote:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(valueEventListener);
        //return itemsxxx;
    }

    private void checkgroup(final String namegroup){
        //checkmeberstatus(namegroup);
        //String vvv = String.valueOf(checkmeberstatus(namegroup));
        //Log.d(TAG, "onDataChange utktktk: "+checkmeberstatus(namegroup));
        userdata = PrefUtils.getCurrentUser(this);
        databaseReference = firebaseDatabase.getReference("group").child(namegroup);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> items = new ArrayList<>();
                items.clear();
                //Log.i("ChatGroupActivity1", dataSnapshot.child("members").getValue().toString());
                //Log.i("ChatGroupActivity1", String.valueOf(this.checkmeberstatus(namegroup)));

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);

                    //Log.i("ChatGroupActivity1", dataSnapshot.child("members").child(userdata.uid).getValue().toString());
                    //Log.d("sswkwkwkw", String.valueOf(message.tipe));
                    Log.d("rrrrxxxx",message.uid);
                    Log.d("rrrrxxx",String.valueOf(itemsxxx.toString()));
                    //Log.d("rrrr",String.valueOf(itemsxxx.contains(userdata.uid+"=true")));
                    Log.d("rrrr user id",userdata.uid);
                    //itemsxxx.get(namegroup).equals(userdata.uid);
                    //List uid = itemsxxx.get(namegroup);
                    try{
                        JSONObject uid = new JSONObject(itemsxxx.get(namegroup).toString());
                        Log.d("rrrr user id wkwkw",String.valueOf(uid.get(userdata.uid))); //itemsxxx[namegroup][userdata.uid]
                        if (message.uid.equals(userdata.uid)){
                            Log.i("ChatGroupActivity", message.uid);
                            message.tipe = 1;
                            //addNotification(message.name,message.message);
                        }else{
                            Log.i("ChatGroupActivity xxxx", message.uid);
                            message.tipe = 0;
                            //addNotification(message.name,message.message);
                            //itemsxxx.stream().filter( x -> !itemsxxx.contains(userdata.uid) ).forEach(x -> System.out.println(x));
                            if(uid.get(userdata.uid).equals(true)){
                                addNotification(message.name, message.message, namegroup);
                            }
                        }
                        Log.i("Chat", "xxxx " + message.message);
                        items.add(0,message);
                    }catch (Exception e){

                    }
                    //JSONObject uid = new JSONObject(itemsxxx.get(namegroup).toString());

                    //itemsxxx.clear();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ChatGroupActivity", "loadNote:onCancelled", databaseError.toException());
            }
        };
        databaseReference.child(namegroup).addValueEventListener(valueEventListener);
    }

    private void addNotification(String judul, String content,String namegroup) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(judul)
                        .setContentText(content);

        Intent notificationIntent = new Intent(this, ChatGroupActivity.class);
        notificationIntent.putExtra("room", namegroup);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
//        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());


        Notification notification0 = new NotificationCompat.Builder(this)
                //.setGroup(KEY_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        android.R.drawable.ic_dialog_email))
                .setContentTitle(judul)
                .setContentText(content)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setSummaryText("This is my inbox style summary."))
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setLights(ContextCompat.getColor(
                        getApplicationContext(), R.color.red_50), 1000, 1000)
                .setVibrate(new long[]{800, 800, 800, 800})
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();
        //notification0.setLatestEventInfo(context, title, message, intent);
        //notification0.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(0, notification0);
        //manager.set
    }
}