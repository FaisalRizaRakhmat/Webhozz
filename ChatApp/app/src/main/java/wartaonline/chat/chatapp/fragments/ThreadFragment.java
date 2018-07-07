package wartaonline.chat.chatapp.fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.ArrayList;
import java.util.List;

import wartaonline.chat.chatapp.AddPostActivity;
import wartaonline.chat.chatapp.AddThreadActivity;
import wartaonline.chat.chatapp.CommentThreadActivity;
import wartaonline.chat.chatapp.R;
import wartaonline.chat.chatapp.adapters.ThreadAdapter;
import wartaonline.chat.chatapp.models.CountThread;
import wartaonline.chat.chatapp.models.Post;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.utils.PrefUtils;

import static android.content.ContentValues.TAG;


public class ThreadFragment extends Fragment {
    private EditText title,content,txtsearch;
    private Button btnsave;
    private String strtitle, strcontent, strimage, stritemkey, callback2;
    String valp;



    private RecyclerView recyclerView;
    private ThreadAdapter threadAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private User userData;

    List<Post> list = new ArrayList<>();
    List<String> listkey = new ArrayList<>();

    List<Post> items = new ArrayList<>();
    List<String> itemkey = new ArrayList<>();

    private HashTagHelper mTextHashTagHelper;


    public ThreadFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        //sensorManager.registerListener(this, proximidad, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, brillo, SensorManager.SENSOR_DELAY_NORMAL);
        Log.e("Frontales","resume");


//        addNotification();

        SharedPreferences pref = getActivity().getSharedPreferences(PrefUtils.SHARED_PREF,0);
        //hasilToken.commit();
        String idhastag = pref.getString("hastagthread",null);
        Log.d(TAG, "session: "+idhastag);
        txtsearch.setText(idhastag);

        SharedPreferences.Editor hasilToken = pref.edit(); //.getString("hastag",null);
        hasilToken.putString("hastagthread","");
        hasilToken.commit();

//        if(idhastag.isEmpty() == false){
//
//        }

    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        //sensorManager.unregisterListener(this);
//        Log.e("Frontales","Pause");
//
//    }

//    private void addNotification() {
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(getActivity())
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("Notifications Warta Online")
//                        .setContentText("This is a test notification");
//
//        Intent notificationIntent = new Intent(getActivity(), AddPostActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);
//
//        // Add as notification
//        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thread, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        txtsearch = (EditText) view.findViewById(R.id.txtsearch);

        list.clear();
        listkey.clear();

        txtsearch.setText("");
        SharedPreferences pref = getActivity().getSharedPreferences(PrefUtils.SHARED_PREF,0);
        SharedPreferences.Editor hasilToken = pref.edit(); //.getString("hastag",null);
        hasilToken.putString("hastagthread","");
        hasilToken.commit();


        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorPrimary), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {
                Log.d(TAG, "onHashTagClicked: "+hashTag);
                //databaseReference = firebaseDatabase.getReference("threads").child()

                Intent intent = new Intent(getActivity(), CommentThreadActivity.class);
                intent.putExtra("title", strtitle);
                intent.putExtra("content", strcontent);
                intent.putExtra("image", strimage);
                intent.putExtra("key", stritemkey);
                getActivity().startActivity(intent);

            }
        });

        // pass a TextView or any descendant of it (incliding EditText) here.
        // Hash tags that are in the text will be hightlighed with a color passed to HasTagHelper
        mTextHashTagHelper.handle(txtsearch);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), AddThreadActivity.class);
                startActivity(intent);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("threads");
        userData = PrefUtils.getCurrentUser(getActivity());


        //createdata();

        threadAdapter = new ThreadAdapter(getActivity(),items,itemkey);
        recyclerView.setAdapter(threadAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list.clear();
        listkey.clear();
        //createdata();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<Post> list = new ArrayList<>();
//                List<String> listkey = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post p = snapshot.getValue(Post.class);
                    list.add(p);
                    listkey.add(String.valueOf(snapshot.getKey()));
                    Log.d(TAG, "key: "+String.valueOf(snapshot.getKey())+" , "+p.title);

                    //valp = snapshot.getKey();
                    //count(snapshot.getKey());
                    SharedPreferences pref = getActivity().getSharedPreferences(PrefUtils.SHARED_PREF,0);
                    String idhastag = pref.getString("countthread",null);
                    Log.d(TAG, "count session: "+idhastag);
                    //txtsearch.setText(idhastag);

                    Log.d(TAG, "callback: "+callback2);

                }

                threadAdapter.insertData(list,listkey);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(eventListener);
        threadAdapter.notifyDataSetChanged();

        txtsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //threadAdapter.filter((String) cs);
                //ThreadFragment.this.threadAdapter.filter((String) cs);

                if (cs.length() > 0){
                    final List<Post> filteredModelList = filter(list, cs.toString());
//                    if (filteredModelList.size() > 0) {
//                        //threadAdapter.insertData(filteredModelList,);
//                        Log.d(TAG, "onTextChanged3333: "+filteredModelList.get(0).title);
//                        //Log.d(TAG, "onTextChanged3333index: "+filteredModelList.get(0).);
//                        //return true;
//                    } else {
//                        Toast.makeText(getActivity(), "Not Found", Toast.LENGTH_SHORT).show();
//                        //return false;
//
//                    }
                }else{

                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> list2 = new ArrayList<>();
                List<String> listkey2 = new ArrayList<>();
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Post p = snapshot.getValue(Post.class);
                                list2.add(p);
                                listkey2.add(String.valueOf(snapshot.getKey()));
                                //Log.d(TAG, "key: "+String.valueOf(snapshot.getKey())+" , "+p.title);
                            }

                            threadAdapter.insertData(list2,listkey2);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    databaseReference.addValueEventListener(eventListener);
                    threadAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        return view;
    }

    public void callback(String cb) {
        callback2 = cb;
        //Log.d(TAG, "hi there, " + yourNameVariable);
    }

//    public ArrayList<CountThread> arrayList= new ArrayList<>();
//
//    public void count(String codigodareserva){
//        // Read from the database
//        //Log.d(TAG, "comment key "+codigodareserva);
//        //valp = codigodareserva;
//        final String test;
//        final int[] jml = {0};
//        databaseReference = firebaseDatabase.getReference("comment_thread/"+codigodareserva);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //String value = dataSnapshot.getValue(String.class);
//                for (DataSnapshot emailSnapshot: dataSnapshot.getChildren()) {
//                    Log.d(TAG, "onDataChange: couunt"+emailSnapshot);
//                    CountThread location =emailSnapshot.getValue(CountThread.class);
//                    arrayList.add(location);
//                    //jml[0] = jml[0] + 1;
////                    String emailFromDatabase = emailSnapshot.getValue(String.class);
////                    if (emailFromDatabase.equals(emailFromUser)) {
////                        System.out.println("Found a matching email");
////                    }
//                }
//                //dataSnapshot.getValue(CountThread.class);
//                //Log.d(TAG, "onDataChange: couunt"+dataSnapshot.getKey()+","+dataSnapshot);
//                //textview1.setText("Status da reserva "+ value);
//                //valp = String.valueOf(dataSnapshot.getChildrenCount());
////                return valp;
//
//                callback(String.valueOf(dataSnapshot.getChildrenCount()));
//
//                callback2 = String.valueOf(dataSnapshot.getChildrenCount());
//                //new CountThread(dataSnapshot.getKey(),String.valueOf(dataSnapshot.getChildrenCount()));
//
//
////                SharedPreferences pref = getActivity().getSharedPreferences(PrefUtils.SHARED_PREF,0);
////                SharedPreferences.Editor hasilcount = pref.edit(); //.getString("hastag",null);
////                hasilcount.putString("countthread",String.valueOf(dataSnapshot.getChildrenCount()));
////                hasilcount.commit();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                //textview1.setText("Status com falha");
//            }
//        });
//        //Log.d(TAG, "comment key "+arrayList["username"]+" , ");
//       //return valp;
//    }

    private List<Post> filter(List<Post> models, String query) {
        query = query.toLowerCase();

        final List<Post> filteredModelList = new ArrayList<>();
        final List<String> filterlistkey = new ArrayList<>();
        for (Post model : models) {
            final String text = model.title.toLowerCase();
            Log.d(TAG, "filter1: "+query);
            if (text.contains(query)) {
                Log.d(TAG, "filter2: "+text);

                strtitle = model.title;
                strcontent = model.content;
                strimage = model.image;
                stritemkey = listkey.get(models.indexOf(model));

                filteredModelList.add(model);
                filterlistkey.add(listkey.get(models.indexOf(model)));
                //Log.d(TAG, "filter: "+models.indexOf(model)+", "+listkey.get(models.indexOf(model)));
                //listkey.get()
                //threadAdapter.insertData(filteredModelList,filterlistkey);
            }
        }
        threadAdapter.insertData(filteredModelList,filterlistkey);
        //createdata();
        //threadAdapter = new ThreadAdapter(getActivity(),items,itemkey);
        //recyclerView.setAdapter(threadAdapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        threadAdapter.notifyDataSetChanged();
        return filteredModelList;
    }

    public void createdata() {
          ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post p = snapshot.getValue(Post.class);
                    list.add(p);
                    listkey.add(String.valueOf(snapshot.getKey()));
                }

                threadAdapter.insertData(list,listkey);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(eventListener);
        threadAdapter.notifyDataSetChanged();
    }


//    void filter(String text){
//        databaseReference.child("Users").orderByChild("content").equalTo(text).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Post user = dataSnapshot.getChildren().iterator().next().getValue(Post.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//
////        List<Post> temp = new ArrayList();
////        for(Post d: list){
////            //or use .equal(text) with you want equal match
////            //use .toLowerCase() for better matches
////            if(d.key.toLowerCase().contains(text)){
////                temp.add(d);
////            }
////        }
////        //update recyclerview
////        threadAdapter.updateList(temp);
////
////        ValueEventListener eventListener = new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                List<Post> list = new ArrayList<>();
////                List<String> listkey = new ArrayList<>();
////                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
////                    Post p = snapshot.getValue(Post.class);
////                    list.add(p);
////                    listkey.add(String.valueOf(snapshot.getKey()));
////                }
////
////                threadAdapter.insertData(list,listkey);
////
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        };
////
////        databaseReference.addValueEventListener(eventListener);
////        threadAdapter.notifyDataSetChanged();
//
//    }

}
