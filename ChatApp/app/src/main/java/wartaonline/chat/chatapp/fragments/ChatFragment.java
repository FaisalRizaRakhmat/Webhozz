package wartaonline.chat.chatapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import wartaonline.chat.chatapp.R;
import wartaonline.chat.chatapp.adapters.HistoryAdapter;
import wartaonline.chat.chatapp.models.Message;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.utils.PrefUtils;

import static com.android.volley.VolleyLog.TAG;

/**
 * A simple {@link Fragment} subclass.
 */

// chat fragment history

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference userRef;

    private User userData;
    private List<Message> list;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("chats");
        userRef = firebaseDatabase.getReference("users");
        userData = PrefUtils.getCurrentUser(getActivity());


        final List<Message> items = new ArrayList<>();
        historyAdapter = new HistoryAdapter(getActivity(),items);
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();

        //narik data dari firebase
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //untuk mengambil chat terakhir
                String lastMassage = "";
                String lastperson = "";
                String lastdate = "";
                String name = "";
                String[] parts;
                String name2 = "";

                //lop data
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.toString().contains(userData.uid)){

                        final String chatUid = snapshot.getKey().toString().substring(snapshot.getKey().toString().lastIndexOf("_") +1);
                        parts = snapshot.getKey().toString().split("_");
                        Log.d("wkwkwk",parts.toString());
                        parts = snapshot.getKey().toString().split("_");
                        for (String fruit : parts) {
                            if(fruit.equals(userData.uid) == false){
                                name2 = fruit;
                                Log.e(TAG, "uid tidak sama: "+fruit+" , "+userData.uid);
                            }
                        }

                        for(DataSnapshot msg: snapshot.getChildren()){
                            if(msg.hasChildren()){
                                Message message = msg.getValue(Message.class);
                                lastMassage = message.message;
                                lastperson = message.name;
                                lastdate =  message.tanggal;
                                //name = message.name;

//                                if (message.uid.equals(userData.uid) == true){
//                                    name = name2;
//                                }else{
//                                    name = chatUid;
//                                }

                            }
                        }

                        Log.e(TAG, "uid tidak sama: "+name2+" , "+userData.uid);

                        final Message history = new Message();
//                        history.name = getUsernameChat(chatUid);
                        history.message = lastMassage;
                        history.uid = name2;
                        history.originalname = lastperson;
                        history.tanggal = lastdate;
                        //history.name = chatUid;
//                        getUsernameChat(chatUid, history);
                        /*

                        Log.e("USER", "historiname : " + history.name);*/

                        list.add(history);


                    }
                }

                Log.e("CHAT", "count "+ list.size());
                final List<Message> list2 = new ArrayList<>();
                for (final Message msg: list){
                    Log.e("CHAT", "msg "+ msg.uid);
                    userRef.child(msg.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                User user = dataSnapshot.getValue(User.class);
                                list.clear();
                                Log.e("USER", "user : " + user.name);
                                if (user.uid.equals(msg.uid)) {
                                    msg.name = user.name;
                                    //msg.tanggal =
                                    //msg.name = snapshot.getValue().toString();
                                    list2.add(msg);
                                    break;
                                }
                                else{
                                    list2.add(msg);
                                    break;
                                }
                            }
                            historyAdapter.insertData(list2);
                            list.clear();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.orderByKey().addValueEventListener(eventListener);
        return view;
    }



}