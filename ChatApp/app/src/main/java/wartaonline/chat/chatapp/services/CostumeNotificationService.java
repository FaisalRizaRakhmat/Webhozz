/*
/*
package abbalove.chat.chatapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import abbalove.chat.chatapp.MainActivity;
import abbalove.chat.chatapp.models.Message;
import abbalove.chat.chatapp.utils.PrefUtils;

import static android.R.id.message;




public class CostumeNotificationService extends Service {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot snapshot: dataSnapshot.getChildren()){


                Message message = snapshot.getValue(Message.class);
                Log.i("BackgroundMessage", message.uid);
               if (message.uid.equals( PrefUtils.getCurrentUser(MainActivity.context).uid)){

                }else{
                    Log.i("BackgroundChat", "zzzz " + message.message);
                }



            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("ChatGroupActivity", "loadNote:onCancelled", databaseError.toException());
        }
    };
    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        //
        String room =intent.getExtras().get("room").toString();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("group").child(room);
        databaseReference.child(room).addValueEventListener(valueEventListener);
        Log.d("BackgroundStart","Listen to room = "+room);
        return START_NOT_STICKY;
    }
}
*/