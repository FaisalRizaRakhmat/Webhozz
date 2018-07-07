package wartaonline.chat.chatapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Calendar calendar = Calendar.getInstance();

    private static final String Tag = "ProfileActivity";
    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;

    private Uri mImageUri = null;

    private DatabaseReference databaseReference;
    private StorageReference mStorageImage;

    private EditText editTextName;
    private EditText editTextDOB;
    private EditText editTextAlamat;
    private Button buttonSave;
    private EditText editTextHP;

    private ImageButton Imagebutton;

    private static final int GALLERY_REQUEST = 1;

    private ProgressDialog progressDialog;

    private String Userid;

    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();



        user = firebaseAuth.getCurrentUser();
        Userid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        mStorageImage = FirebaseStorage.getInstance().getReference().child("profile_image");
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextDOB = (EditText) findViewById(R.id.editTextDOB);
        editTextAlamat = (EditText) findViewById(R.id.editTextAlamat);
        editTextHP = (EditText) findViewById(R.id.editTextHP);

        buttonSave = (Button) findViewById(R.id.buttonSave);


        Imagebutton = (ImageButton) findViewById(R.id.Imagebutton);

        progressDialog = new ProgressDialog(this);

        editTextDOB.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileActivity.this, listener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });





        Imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, GALLERY_REQUEST);
            }
        });


        //getting current use

        //initializing views
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //displaying logged in user name
        //textViewUserEmail.setText("Welcome "+user.getEmail());

        //adding listener to button
        buttonLogout.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Log.e("PROFILE", snapshot.toString());
                    // tampung data dari snapshot
                    User info = dataSnapshot.getValue(User.class);
                    Picasso.with(ProfileActivity.this).load(info.image).fit().into(Imagebutton);
                    editTextName.setText(info.name);
                    editTextDOB.setText(info.DOB);
                    editTextAlamat.setText(info.alamat);
                    editTextHP.setText(info.HP);
                    Log.e("PROFILE VAL ", info.image + "" );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child(Userid).addValueEventListener(valueEventListener);



    }


    //CropImage
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




    private void SaveUserInformation(){
        final String name = editTextName.getText().toString().trim();
        final String DOB = editTextDOB.getText().toString().trim();
        final String alamat = editTextAlamat.getText().toString().trim();
        final String user_id = firebaseAuth.getCurrentUser().getUid();
        final String HP = editTextHP.getText().toString().trim();

        //final UserInformation userInformation = new UserInformation(name,DOB);

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(DOB)){

            progressDialog.setMessage("saving information ...");
            progressDialog.show();

            if (mImageUri != null){

                StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());

                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String downloadUri = taskSnapshot.getDownloadUrl().toString();


                        databaseReference.child(user_id).child("name").setValue(name);
                        databaseReference.child(user_id).child("DOB").setValue(DOB);
                        databaseReference.child(user_id).child("image").setValue(downloadUri);
                        databaseReference.child(user_id).child("email").setValue(user.getEmail());
                        databaseReference.child(user_id).child("alamat").setValue(alamat);
                        databaseReference.child(user_id).child("HP").setValue(HP);


                        progressDialog.dismiss();
                        finish();
/*
                        Intent mainIntent = new Intent(ProfileActivity.this, TabActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);*/


                    }
                });
            }else{

                databaseReference.child(user_id).child("name").setValue(name);
                databaseReference.child(user_id).child("DOB").setValue(DOB);
                databaseReference.child(user_id).child("alamat").setValue(alamat);
                databaseReference.child(user_id).child("HP").setValue(HP);
                databaseReference.child(user_id).child("email").setValue(user.getEmail());

                progressDialog.dismiss();
                finish();
/*
                Intent mainIntent = new Intent(ProfileActivity.this, TabActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);*/
            }


        }



    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            editTextDOB.setText(""+dayOfMonth+"/"+(month+1)+"/"+ year);
        }
    };

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        if(view == buttonSave){
            SaveUserInformation();
        }

    }

}