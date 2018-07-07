package wartaonline.chat.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.models.WeeklyPost;
import wartaonline.chat.chatapp.utils.PrefUtils;

public class AddWeeklyActivity extends AppCompatActivity {

    private EditText title,content;
    private Button btnsave;
    private ImageButton imgbtn;

    private static final int GALLERY_REQUEST = 1;

    private Uri imageUri = null;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private User userData;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weekly);


        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        btnsave = (Button) findViewById(R.id.btnsave);
        imgbtn = (ImageButton) findViewById(R.id.imgbtn);



        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("weekly");
        userData = PrefUtils.getCurrentUser(this);
        mStorage = FirebaseStorage.getInstance().getReference();

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

                if(imageUri != null && !title.toString().isEmpty() && !content.toString().isEmpty()){
                Toast.makeText(AddWeeklyActivity.this, "Status Saved", Toast.LENGTH_SHORT).show();

                StorageReference filepath = mStorage.child("weekly").child(imageUri.getLastPathSegment());
                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        final WeeklyPost weeklyPost = new WeeklyPost();
                        weeklyPost.title = title.getText().toString();
                        weeklyPost.content = content.getText().toString();
                        weeklyPost.userid = userData.uid;
                        weeklyPost.image = downloadUrl.toString();

                        String uid = databaseReference.push().getKey();
                        databaseReference.child(uid).setValue(weeklyPost);
                    }
                });
                finish();
                }else{
                    Toast.makeText(AddWeeklyActivity.this, "All field is required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
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
