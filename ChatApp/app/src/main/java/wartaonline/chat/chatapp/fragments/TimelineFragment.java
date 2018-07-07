package wartaonline.chat.chatapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import wartaonline.chat.chatapp.AddPostActivity;
import wartaonline.chat.chatapp.R;
import wartaonline.chat.chatapp.adapters.TimelineAdapter;
import wartaonline.chat.chatapp.models.Post;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.utils.PrefUtils;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {
    private EditText title,content;
    private Button btnsave;

    private RecyclerView recyclerView;
    private TimelineAdapter timelineAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private User userData;


    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
            }
        });



        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("posts");
        userData = PrefUtils.getCurrentUser(getActivity());


        final List<Post> items = new ArrayList<>();
        final List<String> itemkey = new ArrayList<>();
        timelineAdapter = new TimelineAdapter(getActivity(),items,itemkey);
//        timelineAdapter = new TimelineAdapter(getActivity(), items, new TimelineAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                Log.d(TAG, "clicked position:" + position);
//
//            }
//        });

        recyclerView.setAdapter(timelineAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //tarik data
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> list = new ArrayList<>();
                List<String> listkey = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange xxxx: "+snapshot);
                    Post p = snapshot.getValue(Post.class); //.getValue(Post.class);
                    //PostKey pk = snapshot.getValue(PostKey.class);
                    //new PostKey().key;
                    list.add(p);
                    //listkey.add(pk);
                    listkey.add(String.valueOf(snapshot.getKey()));
                }
//                Comparator compare = Collections.reverseOrder();
//                Collections.sort(list, compare);
//                Collections.sort(listkey, compare);
                //Log.d(TAG, "onDataChange: datalist"+listkey.);
                timelineAdapter.insertData(list,listkey);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(eventListener);
        timelineAdapter.notifyDataSetChanged();

        return view;
    }

}
