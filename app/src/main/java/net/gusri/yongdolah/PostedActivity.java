package net.gusri.yongdolah;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PostedActivity extends AppCompatActivity {

    private EditText mTitle, mDesc;
    private Spinner mCategory;
    private Toolbar mToolbar;
    private Uri mImageUri = null;
    private ProgressDialog mProgress;
    private ImageView mPostImage;
    private FloatingActionButton mTakePicturetn;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.keepSynced(true);

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mPostImage = (ImageView) findViewById(R.id.img_ImagePost);
        mTakePicturetn = (FloatingActionButton) findViewById(R.id.fab_TakePicture);
        mCategory = (Spinner) findViewById(R.id.spn_Category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PostedActivity.this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(adapter);

        mTitle = (EditText) findViewById(R.id.et_Title);
        mDesc = (EditText) findViewById(R.id.et_Isi);
        mProgress = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_posted);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainInten = new Intent(PostedActivity.this, MainActivity.class);
                mainInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainInten);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        mTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        mDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });


        mTakePicturetn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });

    }

    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void StartPosted() {
        mProgress.setTitle("Posting ...");

        final String title_val = mTitle.getText().toString().trim();
        final String desc_val = mDesc.getText().toString().trim();
        final String category_val = mCategory.getSelectedItem().toString().trim();
        final String dateUpl = DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NUMERIC_DATE);

        if (!TextUtils.isEmpty(category_val) && !TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null) {
            mProgress.show();
            StorageReference filepath = mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests") Uri downloadImage = taskSnapshot.getDownloadUrl();
                    final String downloadUrl = downloadImage.toString();
                    final DatabaseReference newPost = mDatabase.push();


                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("dateupload").setValue(dateUpl);
                            newPost.child("category").setValue(category_val);
                            newPost.child("title").setValue(title_val);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("image").setValue(downloadUrl);
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("imguser").setValue(dataSnapshot.child("imguser").getValue());
                            newPost.child("firstname").setValue(dataSnapshot.child("firstname").getValue());
                            newPost.child("lastname").setValue(dataSnapshot.child("lastname").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PostedActivity.this, "Upload File Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(PostedActivity.this, MainActivity.class));
                                        finish();
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mProgress.dismiss();
                }
            });

            filepath.putFile(mImageUri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgress.dismiss();
                    Toast.makeText(PostedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {

                mImageUri = imageUri;
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
            } else {
                startCropImageActivity(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mPostImage.setImageURI(mImageUri);
                Toast.makeText(this, "Cropping successful", Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: ", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCropImageActivity(mImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.posted_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int bar = item.getItemId();

        if (bar == R.id.nav_upl) {
            StartPosted();
        }else if (bar == R.id.action_setting) {
            aboutdDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void aboutdDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PostedActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.about_dialog, null);
        Button mThxbtn = (Button) mView.findViewById(R.id.btn_thx);
        builder.setView(mView);
        final AlertDialog dialog= builder.create();
        dialog.show();
        mThxbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
