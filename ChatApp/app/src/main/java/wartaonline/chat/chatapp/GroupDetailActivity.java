package wartaonline.chat.chatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wartaonline.chat.chatapp.adapters.MemberAdapter;
import wartaonline.chat.chatapp.models.ChatGroup;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.utils.PrefUtils;

public class GroupDetailActivity extends AppCompatActivity {

    private String groupname;
    private RecyclerView recyclerView;
    private MemberAdapter memberAdapter;
    private TextView tvroomname;

    private DatabaseReference mref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        groupname = getIntent().getStringExtra("room");

        Log.i("GROUPDETAILACTIVITY", "groupname = " + groupname);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        tvroomname = (TextView) findViewById(R.id.tvroomname);

        mref = FirebaseDatabase.getInstance().getReference();

        List<User> member = new ArrayList<>();
        memberAdapter = new MemberAdapter(member,GroupDetailActivity.this, mref.child("group")
                .child(groupname).child("members"), false);
        recyclerView.setAdapter(memberAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupDetailActivity.this));



        //isi data recycler view dari firebase
        mref.child("group").child(groupname).limitToLast(6).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatGroup group = dataSnapshot.getValue(ChatGroup.class);
                tvroomname.setText(group.roomname);
                //cek members child
                if(dataSnapshot.hasChild("members")){
                    Iterator it = group.members.entrySet().iterator();
                    HashMap<String , Boolean> user = new HashMap<>();
                    while (it.hasNext()){
                        Map.Entry pair = (Map.Entry) it.next();
                        user.put(pair.getKey().toString(), (Boolean) pair.getValue());
                        Log.i("GROUPDETAILACTIVITY", "pair" + pair.getKey()+ " " + pair.getValue());
                        it.remove();
                    }

                    User userLogin = PrefUtils.getCurrentUser(GroupDetailActivity.this);
                    Boolean isCreator = group.createByUserId.equals(userLogin.uid);
                    memberAdapter.isCreator = isCreator;
                    getUsers(user);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private  void getUsers(final HashMap<String , Boolean> members){
        mref.child("users").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                List<String> liststatus = new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    Log.i("GROUPDETAILACTIVITY", user.name);
                    if(members.containsKey(user.uid)){
                        users.add(user);
                        liststatus.add(String.valueOf(members.get(user.uid).booleanValue()));

                    }
                }
                memberAdapter.insertData(users,liststatus);
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
