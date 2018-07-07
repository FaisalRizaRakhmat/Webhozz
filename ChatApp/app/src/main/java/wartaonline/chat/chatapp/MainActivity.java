package wartaonline.chat.chatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wartaonline.chat.chatapp.fragments.GroupFragment;
import wartaonline.chat.chatapp.fragments.ThreadFragment;
import wartaonline.chat.chatapp.fragments.TimelineFragment;
import wartaonline.chat.chatapp.fragments.WeeklyFragment;
import wartaonline.chat.chatapp.models.ChatGroup;
import wartaonline.chat.chatapp.users.LoginActivity;
import wartaonline.chat.chatapp.utils.PrefUtils;

public class MainActivity extends AppCompatActivity {
    //private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    private BroadcastReceiver broadcastReceiver;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private int[]tabIcons = {R.mipmap.bumim,R.mipmap.chat, R.mipmap.grup,R.mipmap.kertas};

    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, NotifyService.class);
        this.startService(intent);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // tab icon

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

        //cek tangkapan broadcast receiver
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //chat tipe intent yang dibroadcast
                if(intent.getAction().equals(PrefUtils.REGISTRATION_COMPLETE)){
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(PrefUtils.SHARED_PREF,0);
                    String hasilToken = pref.getString("regId",null);
                    Log.e("MainActvity","firebase registration id = " + hasilToken);
                }
            }
        };
        //cek token sudah disimpan/belum
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PrefUtils.SHARED_PREF,0);
        String hasilToken = pref.getString("regId",null);
        Log.e("MainActvity","firebase registration id = " + hasilToken);
        setUSerToken(hasilToken);
    }

    private void setUSerToken(String token){
        firebaseDatabase.getReference("users").child(user.getUid()).child("token").setValue(token);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //register receiver registration
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(PrefUtils.REGISTRATION_COMPLETE));

        //register new push messaging receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(PrefUtils.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    private void setupViewPager(ViewPager viewPager){
        adapter  = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TimelineFragment(),"World");
        adapter.addFragment(new GroupFragment(),"Group");
        adapter.addFragment(new WeeklyFragment(),"Weekly");

        adapter.addFragment(new ThreadFragment(),"Thread");


        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        getSupportActionBar().setTitle("World");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Group");
                        break;
                    case 2:
                        getSupportActionBar().setTitle("Weekly");
                        break;
                    case 3:
                        getSupportActionBar().setTitle("Thread");
                        break;


                    default:
                        getSupportActionBar().setTitle("");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment>mFragmentList = new ArrayList<>();
        private final List<String>mFragmentTittle = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTittle.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTittle.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                auth.signOut();

                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            case R.id.profile:
               startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
           /* case R.id.Items:
                startActivity(new Intent(MainActivity.this, ItemActivity.class));
                return true;*/
            case R.id.service:
                startActivity(new Intent(MainActivity.this, ServiceActivity.class));
                return true;
            case R.id.addgroup:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter name : ");

                //final EditText input_field = new EditText(this);

                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.activity_add_group, null);
                final EditText txtnamegroup = (EditText) dialogView.findViewById(R.id.txtGroupName);
                final EditText txtaddress = (EditText) dialogView.findViewById(R.id.txtAddress);

                builder.setView(dialogView);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = txtnamegroup.getText().toString();
                        String address = txtaddress.getText().toString();

                        // get reference ke chat
                        databaseReference = firebaseDatabase.getReference("group");

                        HashMap<String , Boolean> members = new HashMap<>();
                        members.put(user.getUid(), true);


                        ChatGroup group = new ChatGroup(user.getUid(), databaseReference.push()
                                .getKey(), name, PrefUtils.getCurrentUser(MainActivity.this).name, "", members);
 //                        databaseReference.setValue(group);

                        databaseReference.child(name).setValue(group);

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

                builder.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
