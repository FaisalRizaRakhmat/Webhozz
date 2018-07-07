package wartaonline.chat.chatapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import wartaonline.chat.chatapp.models.Post;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.utils.PrefUtils;


public class AddPostActivity extends AppCompatActivity  {
    private EditText title,content,place,date,hour;
    private Button btnsave;
    private ImageButton imgbtn;
    Calendar calendar = Calendar.getInstance();

    private static final int GALLERY_REQUEST = 1;

    private Uri imageUri = null;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private User userData;
    private StorageReference mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        btnsave = (Button) findViewById(R.id.btnsave);
        place = (EditText) findViewById(R.id.place);
        date = (EditText) findViewById(R.id.date);
        hour = (EditText) findViewById(R.id.hour);
        imgbtn = (ImageButton) findViewById(R.id.imgbtn);



        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("posts");
        userData = PrefUtils.getCurrentUser(this);
        mStorage = FirebaseStorage.getInstance().getReference();

        date.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddPostActivity.this, listener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        imgbtn.setOnClickListener(new View.OnClickListener() {
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
                Log.d("yy", "onClick: URI"+imageUri);
                title = (EditText) findViewById(R.id.title);
                content = (EditText) findViewById(R.id.content);
                btnsave = (Button) findViewById(R.id.btnsave);
                place = (EditText) findViewById(R.id.place);
                date = (EditText) findViewById(R.id.date);
                hour = (EditText) findViewById(R.id.hour);

                if(imageUri != null && !title.toString().isEmpty() && !content.toString().isEmpty() && !place.toString().isEmpty() && !date.toString().isEmpty() && !hour.toString().isEmpty()){
//                    Log.d("yy", "onClick: ada");
                    Toast.makeText(AddPostActivity.this, "Status Saved", Toast.LENGTH_SHORT).show();
                StorageReference filepath = mStorage.child("post").child(imageUri.getLastPathSegment());
                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        final Post post = new Post();
                        post.title = title.getText().toString();
                        post.content = content.getText().toString();
                        post.place = place.getText().toString();
                        post.date = date.getText().toString();
                        post.hour = hour.getText().toString();
                        post.userid = userData.uid;
                        post.image = downloadUrl.toString();

                        String uid = databaseReference.push().getKey();
                        databaseReference.child(uid).setValue(post);
                    }
                });
                finish();

                }else{
                    Toast.makeText(AddPostActivity.this, "All field is required", Toast.LENGTH_SHORT).show();
//                    Log.d("yy", "onClick: kosong");
                }
            }
        });


    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            date.setText(""+dayOfMonth+"/"+(month+1)+"/"+ year);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                imgbtn.setImageURI(imageUri);


            }
        }
    }
}