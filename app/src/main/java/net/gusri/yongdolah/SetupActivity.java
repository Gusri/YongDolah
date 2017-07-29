package net.gusri.yongdolah;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

public class SetupActivity extends AppCompatActivity {
    private ImageButton mCaptureBtn;
    private EditText mLastName, mFirstName;
    private Button mSubmitProfBtn;
    private TextView mMailProf;
    private ImageView mProfImg;
    private Uri mCropImageUri;
    private Uri mCaptureUri = null;
    private ProgressDialog mProgress;

    private DatabaseReference mDBUsers;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        mDBUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");

        mProgress = new ProgressDialog(this);
        mLastName = (EditText) findViewById(R.id.et_LastName);
        mFirstName = (EditText) findViewById(R.id.et_FirstName);
        mMailProf = (TextView) findViewById(R.id.tv_mail);
        mSubmitProfBtn = (Button) findViewById(R.id.btn_SubmitProf);
        mProfImg = (ImageView) findViewById(R.id.iv_imgProf);
        mCaptureBtn = (ImageButton) findViewById(R.id.imb_Capture);

        mMailProf.setText(mAuth.getCurrentUser().getEmail());
        mSubmitProfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });

        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });
    }

    private void startSetupAccount() {
        final String firstName = mFirstName.getText().toString();
        final String lastName = mLastName.getText().toString();
        final String user_id = mAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && mCaptureUri != null) {
            mProgress.setMessage("Finishing Setup");
            mProgress.show();

            StorageReference filepath = mStorageImage.child(mCaptureUri.getLastPathSegment());

            filepath.putFile(mCaptureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadImage = taskSnapshot.getDownloadUrl();
                    String downloadUri = downloadImage.toString();
                    mDBUsers.child(user_id).child("firstname").setValue(firstName);
                    mDBUsers.child(user_id).child("lastname").setValue(lastName);
                    mDBUsers.child(user_id).child("imguser").setValue(downloadUri);
                    mDBUsers.child(user_id).child("account").setValue("usr");

                    mProgress.dismiss();

                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }
    }

    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                mCropImageUri = imageUri;
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
            } else {
                startCropImageActivity(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mCaptureUri = result.getUri();
                mProfImg.setImageURI(mCaptureUri);
                Toast.makeText(this, "Cropping successful", Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: ", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1, 1)
                .start(this);
    }
}
