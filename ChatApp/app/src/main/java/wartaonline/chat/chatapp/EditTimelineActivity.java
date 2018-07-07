package wartaonline.chat.chatapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.users.LoginActivity;

/**
 * Created by Alfian on 3/24/2018.
 */

public class EditTimelineActivity extends AppCompatActivity {

    /*String title,content,image,place,hour,date,key;
    ImageButton imgbtn;
    Button btnsave
    EditText title1,content1,place1,hour1,date1;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private User userData;
    private StorageReference mStorage;
            */

    private ImageButton Imagebutton;
    private static final int GALLERY_REQUEST = 1;
    private EditText ibadah, pendeta, tempat, tgl, jam;
    String image;
    Bundle extras;
    private ProgressDialog progressDialog;
    private Uri mImageUri = null;
    private StorageReference mStorageImage;
    private DatabaseReference databaseReference;
    private Button btnsave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timeline);
        getSupportActionBar().setTitle("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Imagebutton = (ImageButton) findViewById(R.id.imgbtn);
        progressDialog = new ProgressDialog(this);
        mStorageImage = FirebaseStorage.getInstance().getReference().child("post");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");

        ibadah = (EditText) findViewById(R.id.ibadah);
        pendeta = (EditText) findViewById(R.id.pendeta);
        tempat = (EditText) findViewById(R.id.tempat);
        tgl = (EditText) findViewById(R.id.tgl);
        jam = (EditText) findViewById(R.id.jam);
        btnsave = (Button) findViewById(R.id.btnsave);

        extras = getIntent().getExtras();

        ibadah.setText(extras.getString("title"));
        pendeta.setText(extras.getString("content"));
        tempat.setText(extras.getString("place"));
        tgl.setText(extras.getString("date"));
        jam.setText(extras.getString("hour"));
        //jam.setText(extras.getString("key"));
        image= extras.getString("image");

        Picasso.with(getApplicationContext()).load(image).into(Imagebutton);
        Imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, GALLERY_REQUEST);
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTimeline();
                Log.d("TAG", "onClick: XXXXXXX");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

                Imagebutton.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void SaveTimeline(){

        final String stribadah = ibadah.getText().toString().trim();
        final String strpendeta = pendeta.getText().toString().trim();
        final String strtempat = tempat.getText().toString().trim();
        final String timeline_id = extras.getString("key");
        final String strjam = jam.getText().toString().trim();
        final String strtgl = tgl.getText().toString().trim();
        //final String strimage = extras.getString("image");

        //final UserInformation userInformation = new UserInformation(name,DOB);

        if(!TextUtils.isEmpty(stribadah) && !TextUtils.isEmpty(strpendeta) && !TextUtils.isEmpty(strtempat) && !TextUtils.isEmpty(strjam)){

            progressDialog.setMessage("saving information ...");
            progressDialog.show();

            if (mImageUri != null){

                StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        databaseReference.child(timeline_id).child("title").setValue(stribadah);
                        databaseReference.child(timeline_id).child("content").setValue(strpendeta);
                        databaseReference.child(timeline_id).child("place").setValue(strtempat);
                        databaseReference.child(timeline_id).child("hour").setValue(strjam);
                        databaseReference.child(timeline_id).child("date").setValue(strtgl);
                        databaseReference.child(timeline_id).child("image").setValue(downloadUri);

                        progressDialog.dismiss();
                        finish();
                    }
                });
            }else {

                databaseReference.child(timeline_id).child("title").setValue(stribadah);
                databaseReference.child(timeline_id).child("content").setValue(strpendeta);
                databaseReference.child(timeline_id).child("place").setValue(strtempat);
                databaseReference.child(timeline_id).child("hour").setValue(strjam);
                databaseReference.child(timeline_id).child("date").setValue(strtgl);

                progressDialog.dismiss();
                finish();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

//            SharedPreferences pref = getApplicationContext().getSharedPreferences(PrefUtils.SHARED_PREF,0);
//            SharedPreferences.Editor hasilToken = pref.edit(); //.getString("hastag",null);
//            hasilToken.putString("hastag",hastagid);
//            hasilToken.commit();
            Log.d("tag", "onOptionsItemSelected: click");
            this.finish();
            //return false;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onClick(View view) {
//        //if logout is pressed
////        if(view == buttonLogout){
////            //logging out the user
////            firebaseAuth.signOut();
////            //closing activity
////            finish();
////            //starting login activity
////            startActivity(new Intent(this, LoginActivity.class));
////        }
////
////        if(view == buttonSave){
////            SaveUserInformation();
////        }
//
//    }

}
