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
import wartaonline.chat.chatapp.adapters.RoomAdapter;
import wartaonline.chat.chatapp.models.ChatGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;




    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;



    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("group");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        List<ChatGroup> items = new ArrayList<>();
        roomAdapter = new RoomAdapter(getActivity(), items);
        recyclerView.setAdapter(roomAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChatGroup> items = new ArrayList<>();

//                Log.i("GROUPFRAGMENT", "jml "+ dataSnapshot.l);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ChatGroup room = snapshot.getValue(ChatGroup.class);
                    Log.i("GROUPFRAGMENT", snapshot.toString());
                    items.add(room);

                }
                roomAdapter.insertData(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("GROUPFRAGMENT", "loadNote:onCancelled", databaseError.toException());
            }
        };

        databaseReference.orderByKey().addValueEventListener(valueEventListener);

        return view;
    }

}