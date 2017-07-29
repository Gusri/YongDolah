package net.gusri.yongdolah;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SingleActivity extends AppCompatActivity {
    private String mPost_key = null;
    private ImageView mSingImg;
    private TextView mDescSing;
    private ImageButton mHomeBtn, mLikeBtn, mSmileBtn;

    private FloatingActionButton mMenufab, mDeletedfab, mPostedfab;
    private Animation mShowLayout, mHideLayout;
    private Toolbar mToolbar;

    private boolean mProsesLike = false;
    private boolean mProsesSmile = false;

    private Query mUserLog;
    private FirebaseAuth mAuth;
    private CollapsingToolbarLayout mCollapsing = null;
    private DatabaseReference mDatabase, mDBLikes, mDBSmile, mDBUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        mAuth = FirebaseAuth.getInstance();

        mDBUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDBUsers.keepSynced(true);

        mUserLog = mDBUsers.orderByChild("account").equalTo("adm");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.keepSynced(true);

        mDBLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDBLikes.keepSynced(true);

        mDBSmile = FirebaseDatabase.getInstance().getReference().child("Smiles");
        mDBSmile.keepSynced(true);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainInten = new Intent(SingleActivity.this, MainActivity.class);
                mainInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainInten);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        mShowLayout = AnimationUtils.loadAnimation(SingleActivity.this, R.anim.show_layout);
        mHideLayout = AnimationUtils.loadAnimation(SingleActivity.this, R.anim.hide_layout);

        mCollapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsing.setTitle(getTitle());
        mCollapsing.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        mCollapsing.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        mLikeBtn = (ImageButton) findViewById(R.id.btn_Like);
        mSmileBtn = (ImageButton) findViewById(R.id.btn_smile);
        mHomeBtn = (ImageButton) findViewById(R.id.btn_Home);
        mPost_key = getIntent().getExtras().getString("blog_id");
        mSingImg = (ImageView) findViewById(R.id.img_SingImage);
        mDescSing = (TextView) findViewById(R.id.tv_SingDesc);
        mDeletedfab = (FloatingActionButton) findViewById(R.id.fab_deleted);
        mPostedfab = (FloatingActionButton) findViewById(R.id.fab_Posted);

        mHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainInten = new Intent(SingleActivity.this, MainActivity.class);
                mainInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainInten);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        mHomeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mHomeBtn.setImageResource(R.drawable.ic_home_dark);
                if (event.getAction() == MotionEvent.ACTION_UP)
                    mHomeBtn.setImageResource(R.drawable.ic_home_light);
                return false;
            }
        });

        mMenufab = (FloatingActionButton) findViewById(R.id.fab_menu);
        mMenufab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String uid = (String) dataSnapshot.child("uid").getValue();

                        if (mPostedfab.getVisibility() == View.VISIBLE) {
                            mPostedfab.setVisibility(View.GONE);
                            mPostedfab.startAnimation(mHideLayout);
                            fabClosed();
                        } else {
                            mPostedfab.setVisibility(View.VISIBLE);
                            mPostedfab.startAnimation(mShowLayout);
                            fabOpened();
                        }

                        if (mAuth.getCurrentUser().getUid().equals(uid)) {
                            if (mDeletedfab.getVisibility() == View.VISIBLE) {
                                mDeletedfab.setVisibility(View.GONE);
                                mDeletedfab.startAnimation(mHideLayout);
                                fabClosed();
                            } else {
                                mDeletedfab.setVisibility(View.VISIBLE);
                                mDeletedfab.startAnimation(mShowLayout);
                                fabOpened();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mDeletedfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeletedfab.setVisibility(View.GONE);
                mPostedfab.setVisibility(View.GONE);
                mDeletedfab.startAnimation(mHideLayout);
                mPostedfab.startAnimation(mHideLayout);
                fabClosed();

                new AlertDialog.Builder(SingleActivity.this)
                        .setTitle("Informasi")
                        .setMessage("Apakah anda yakin delete Content ini?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child(mPost_key).removeValue();
                                Intent mainIntent = new Intent(SingleActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        });

        mPostedfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeletedfab.setVisibility(View.GONE);
                mPostedfab.setVisibility(View.GONE);
                mDeletedfab.startAnimation(mHideLayout);
                mPostedfab.startAnimation(mHideLayout);
                fabOpened();
                Intent postedIntent = new Intent(SingleActivity.this, PostedActivity.class);
                postedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(postedIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        });

        mDBLikes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid())) {
                    mLikeBtn.setImageResource(R.drawable.ic_like_hand_color);

                } else {
                    mLikeBtn.setImageResource(R.drawable.ic_like_hand_gray);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProsesLike = true;
                mDBLikes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mProsesLike) {
                            if (dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                mDBLikes.child(mPost_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                mProsesLike = false;

                            } else {
                                mDBLikes.child(mPost_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                mProsesLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mSmileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProsesSmile = true;

                mDBSmile.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mProsesSmile) {
                            if (dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                mDBSmile.child(mPost_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                mProsesSmile = false;
                            } else {
                                mDBSmile.child(mPost_key).child(mAuth.getCurrentUser().getUid()).setValue("Smile: ");
                                mProsesSmile = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mDBSmile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid())) {
                    mSmileBtn.setImageResource(R.drawable.ic_happy_color);
                } else {
                    mSmileBtn.setImageResource(R.drawable.ic_happy_gray);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_image = (String) dataSnapshot.child("image").getValue();
                String pos_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();

                Glide.with(SingleActivity.this).load(post_image).placeholder(R.drawable.no_img).centerCrop().crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(mSingImg);

                mDescSing.setText(post_desc);
                mCollapsing.setTitle(pos_title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserLog.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_id = mAuth.getCurrentUser().getUid();
                if (dataSnapshot.hasChild(user_id)) {
                    mMenufab.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void fabOpened() {
        ViewCompat.animate(mMenufab)
                .rotation(45.0F)
                .withLayer()
                .setDuration(500L)
                .setInterpolator(new OvershootInterpolator(7.0F))
                .start();

    }
    private void fabClosed() {
        ViewCompat.animate(mMenufab)
                .rotation(0.0F)
                .withLayer()
                .setDuration(500L)
                .setInterpolator(new OvershootInterpolator(7.0F))
                .start();
    }
    @Override
    public void onBackPressed() {

    }

}
