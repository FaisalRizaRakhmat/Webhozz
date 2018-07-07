package wartaonline.chat.chatapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import wartaonline.chat.chatapp.adapters.ChatGroupAdapter;
import wartaonline.chat.chatapp.models.ChatGroup;
import wartaonline.chat.chatapp.models.Message;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.services.MyFirebaseMessagingService;
import wartaonline.chat.chatapp.utils.PrefUtils;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class ChatGroupActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatGroupAdapter chatGroupAdapter;

    private Button btnsend;
    private EmojiconEditText etMessage;
    private Button btnJoin;

    private FirebaseAuth auth;
    private FirebaseUser fUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SimpleDateFormat sdf;
    private User userdata;
    private EmojIconActions emojIcon;
    private View root_view;
    private ImageView emojibtn;
    private TextView pending;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        root_view = findViewById(R.id.root_view);
        btnsend = (Button) findViewById(R.id.btnsend);
        etMessage = (EmojiconEditText) findViewById(R.id.etPesan);
        btnJoin = (Button) findViewById(R.id.btnJoin);
        emojibtn = (ImageView) findViewById(R.id.emojibtn);
        pending = (TextView)findViewById(R.id.pending);
        sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        userdata = PrefUtils.getCurrentUser(ChatGroupActivity.this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        List<Message> items = new ArrayList<>();
        chatGroupAdapter = new ChatGroupAdapter(this, items);
        recyclerView.setAdapter(chatGroupAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final String room = getIntent().getStringExtra("room");
        // set title room chat/group
        getSupportActionBar().setTitle(room);

        //instansiasi class2 dari firebase
        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("group").child(room);

        checkMembership();

        Intent intent = new Intent(this, NotifyService.class);
        this.stopService(intent);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pesan = etMessage.getText().toString();
                //addNotification();
                Date date = new Date();
                Message msg = new Message(fUser.getUid(), userdata.name, userdata.image, pesan, sdf.format(date));
                String uid = databaseReference.push().getKey();
                databaseReference.child(room).child(uid).setValue(msg);
                //new MyFirebaseMessagingService().sendMessageToFcm(new MyFirebaseMessagingService().getFCMNotificationMessage(userdata.name, pesan));

                //sendNotification(userdata.name,pesan);
                etMessage.setText("");
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("members").child(userdata.uid).setValue(false);
                btnJoin.setVisibility(View.GONE);
            }
        });

        //tarik data

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> items = new ArrayList<>();
                etMessage.setText("");
//                Log.i("GROUPFRAGMENT", "jml "+ dataSnapshot.l);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Log.i("ChatGroupActivity", snapshot.toString());
                    Log.i("ChatActivityMSG", "KEY : " + snapshot.getKey() + "" );
                    Message message = snapshot.getValue(Message.class);
                    Log.i("ChatGroupActivity", message.uid);
                    if (message.uid.equals(userdata.uid)){
                        Log.i("ChatGroupActivity", "masuk sini");
                        message.tipe = 1;
                    }else{
                        message.tipe = 0;
                    }
                    Log.i("Chat", "xxxx " + message.message);
                    items.add(message);

                }
                chatGroupAdapter.insertData(items);
                recyclerView.scrollToPosition(items.size()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ChatGroupActivity", "loadNote:onCancelled", databaseError.toException());
            }
        };
        databaseReference.child(room).addValueEventListener(valueEventListener);

        //setEmoji
        emojIcon = new EmojIconActions(this , root_view, etMessage, emojibtn);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.mipmap.kibot, R.mipmap.emoo);
    }

    private void sendNotification(String name, String content){
        Intent intent = new Intent(this, ChatGroupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(name)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

//    private void addNotification() {
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("Notifications Warta Online")
//                        .setContentText("This is a test notification");
//
//        Intent notificationIntent = new Intent(this, AddPostActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);
//
//        // Add as notification
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());
//    }


    private void checkMembership(){
        databaseReference.limitToLast(6).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatGroup chatGroup = dataSnapshot.getValue(ChatGroup.class);

                //cek jika ada member

                if(dataSnapshot.hasChild("members")){
                    //loop data member
                    Iterator it = chatGroup.members.entrySet().iterator();
                    User userLogin = PrefUtils.getCurrentUser(ChatGroupActivity.this);
                    int allowchat = 0;

                    while(it.hasNext()){
                        Map.Entry pair = (Map.Entry) it.next();
                        //cari uid yg sedang login
                        if (pair.getKey().toString().equals(userLogin.uid)){
                            if((Boolean)pair.getValue()==true){
                                allowchat = 1;
                            }else if((Boolean)pair.getValue()==false){
                                allowchat = 2;
                            }else{
                                allowchat = 0;
                            }
                        }
                    }
                    if(allowchat ==1){
                        etMessage.setVisibility(View.VISIBLE);
                        btnsend.setVisibility(View.VISIBLE);
                        btnJoin.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        emojibtn.setVisibility(View.VISIBLE);
                        pending.setVisibility(View.GONE);

                    }
                    else if (allowchat == 2){
                        btnJoin.setVisibility(View.GONE);
                        etMessage.setVisibility(View.GONE);
                        btnsend.setVisibility(View.GONE);
                        emojibtn.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        pending.setVisibility(View.VISIBLE);
                    }else if (allowchat == 0){
                        btnJoin.setVisibility(View.VISIBLE);
                        etMessage.setVisibility(View.GONE);
                        btnsend.setVisibility(View.GONE);
                        emojibtn.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        pending.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
