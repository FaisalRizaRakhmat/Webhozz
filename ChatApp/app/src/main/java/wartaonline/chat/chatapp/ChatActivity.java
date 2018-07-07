package wartaonline.chat.chatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import wartaonline.chat.chatapp.adapters.ChatAdapter;
import wartaonline.chat.chatapp.models.Message;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.utils.PrefUtils;
import wartaonline.chat.chatapp.utils.VolleySingleton;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity {

    //recycle view
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> items;

    //send Chat
    private Button btnSend;
    private EmojiconEditText etMessage;
    private String uidToChat;
    private String nameToChat;
    private User userData;

    //firebase
    private FirebaseAuth auth;
    private FirebaseUser fUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //emojicon
    private EmojIconActions emojIcon;
    private ImageView emojibtn;
    private View root_view;
    private SimpleDateFormat sdf;

    //token
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_one_to_one);
        //connect id component
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        etMessage = (EmojiconEditText) findViewById(R.id.etPesan);
        btnSend = (Button) findViewById(R.id.btnsend);
        root_view = findViewById(R.id.root_view);
        sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        emojibtn = (ImageView) findViewById(R.id.emojibtn);

        //get userdata from preference
        userData = PrefUtils.getCurrentUser(this);

        // get data dari kiriman activity sebelumnya
        uidToChat = getIntent().getStringExtra("uid");
        nameToChat = getIntent().getStringExtra("name");

        // set title
        getSupportActionBar().setTitle(nameToChat);

        // firebase
        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        // get node chats ( jika belum ada , maka terbuat automatic )
        databaseReference = firebaseDatabase.getReference().child("chats");

        //get token
        firebaseDatabase.getReference("users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                userToken= user.token;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //recycle view dan adapter
        items = new ArrayList<>();
        chatAdapter = new ChatAdapter(items, ChatActivity.this);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final String chat_type_1 = userData.uid + "_" + uidToChat;
        final String chat_type_2 = uidToChat + "_" + userData.uid;

        //panggil method getdata
        getData(chat_type_1,chat_type_2);

        // end message to firebase

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Date date = new Date();
                final String waktu = String.valueOf(Calendar.getInstance().getTimeInMillis());
                sendPushNotif();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //check type1 / type2
                        if(dataSnapshot.hasChild(chat_type_1)){
                            Log.i("CHATACTIVITY","send chat to"+ chat_type_1);
                            Message message = new Message(userData.uid , userData.name , userData.email, etMessage.getText().toString(),sdf.format(date));
                            //send to firebase node = chats and timeilis
                            databaseReference.child(chat_type_1).child(waktu).setValue(message);

                            //clear edittext
                            etMessage.setText("");
                            getData(chat_type_1,chat_type_2);

                        }else if(dataSnapshot.hasChild(chat_type_2)){
                            Log.i("CHATACTIVITY","send chat to"+ chat_type_2);
                            Message message = new Message(userData.uid , userData.name , userData.email, etMessage.getText().toString(),sdf.format(date));
                            //send to firebase node = chats and timeilis
                            databaseReference.child(chat_type_2).child(waktu).setValue(message);

                            //clear edittext
                            etMessage.setText("");
                            getData(chat_type_1,chat_type_2);
                        }else{
                            Log.i("CHATACTIVITY","send chat sucess");
                            Message message = new Message(userData.uid , userData.name , userData.email, etMessage.getText().toString(),sdf.format(date));
                            //send to firebase node = chats and timeilis
                            databaseReference.child(chat_type_1).child(waktu).setValue(message);

                            //clear edittext
                            etMessage.setText("");
                            getData(chat_type_1,chat_type_2);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        //setEmoji
        emojIcon = new EmojIconActions(this , root_view, etMessage, emojibtn);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.mipmap.ic_launcher, R.mipmap.ic_launcher);
    }
    private void getData(final String type1 , final String type2){
        databaseReference.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //nampilin chat

                if(dataSnapshot.hasChild(type1)){


                    databaseReference.getRef().child(type1).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator it = dataSnapshot.getChildren().iterator();
                            items.clear();
                            while(it.hasNext()){
                                DataSnapshot ds =(DataSnapshot) it.next();
                                Message msg = ds.getValue(Message.class);
                                items.add(msg);

                            }
                            chatAdapter.insertData(items);
                            //chat palig bwah
                            recyclerView.scrollToPosition(items.size()-1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else if(dataSnapshot.hasChild(type2)){

                    databaseReference.getRef().child(type2).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator it = dataSnapshot.getChildren().iterator();
                            items.clear();
                            while(it.hasNext()){
                                DataSnapshot ds =(DataSnapshot) it.next();
                                Message msg = ds.getValue(Message.class);
                                items.add(msg);

                            }
                            chatAdapter.insertData(items);
                            recyclerView.scrollToPosition(items.size()-1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }else{
                    Log.i("CHATACTIVITY" , "No ROOM AVAILABLE");
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void sendPushNotif(){
        String URL = "https://fcm.googleapis.com/fcm/send";
        JSONObject obj = new JSONObject();
        try{
            obj.put("to", userToken);
            JSONObject objData = new JSONObject();
            objData.put("message", etMessage.getText().toString());
            obj.put("data", objData);

            JSONObject objMessage = new JSONObject();
            objMessage.put("title", userData.name);
            objMessage.put("body", etMessage.getText().toString());

            obj.put("notification", objMessage);
        }catch(Exception ex){

        }

        // volley post dengan param json object
        JsonObjectRequest jsRequest = new JsonObjectRequest(Request.Method.POST, URL, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("CHATRESPONSE", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("CHATERROR", error.getMessage());
                    }
                }
        )
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "key=AAAAIQqbvGU:APA91bF5QgRHzx0aWMmukGoiwG2tkrfbY96nd75ayUrhgiXci2rM_sC0AvehSvSF3vV8T9cOUT538coN-Vziot69P9aUhAtuIOWPNAi0hcrP4hzFO5b-zH72DqzM602l0kwB8PlPE8KI");
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(this).addToRequestQueue(jsRequest);

    }

}
