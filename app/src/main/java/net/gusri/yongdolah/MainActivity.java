package net.gusri.yongdolah;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import net.gusri.yongdolah.fragments.CerbungFragment;
import net.gusri.yongdolah.fragments.CerpenFragment;
import net.gusri.yongdolah.fragments.DongengFragment;
import net.gusri.yongdolah.fragments.EventFragment;
import net.gusri.yongdolah.fragments.HomeFragment;
import net.gusri.yongdolah.fragments.TabFragment;
import net.gusri.yongdolah.klass.ConnectionDetector;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mNavDrawer;
    private ActionBarDrawerToggle mBarToggle;
    private NavigationView mNavView;
    private Toolbar mToolbar;
    private FragmentManager mManager = getSupportFragmentManager();
    private TextView mLastName, mFirstName;
    private ImageView mProfileImg, mHeaderImg;
    private AdView mAdView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private Query mUserLog;

    ConnectionDetector mConnection;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, SplashActivity.class);
                    finish();
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                } else {
                    NavigationHeader();
                }
            }
        };

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        FirebaseMessaging.getInstance().subscribeToTopic("news");

        mUserLog = mDatabaseUsers.orderByChild("account").equalTo("adm");

        mConnection = new ConnectionDetector();
        if (!mConnection.isConnect(MainActivity.this)) buildDialog(MainActivity.this).show();
        else {
            setContentView(R.layout.activity_main);
        }
        CheckUser();
        printKeyHash();

        mToolbar = (Toolbar) findViewById(R.id.Bar);
        setSupportActionBar(mToolbar);

        mNavDrawer = (DrawerLayout) findViewById(R.id.activity_main);
        mBarToggle = new ActionBarDrawerToggle(this, mNavDrawer, R.string.open, R.string.close);
        mNavView = (NavigationView) findViewById(R.id.nav_View);
        View hView = mNavView.getHeaderView(0);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mLastName = (TextView) hView.findViewById(R.id.tv_Profile1);
        mFirstName = (TextView) hView.findViewById(R.id.tv_Profile2);
        mProfileImg = (ImageView) hView.findViewById(R.id.img_HeadUser);
        mHeaderImg = (ImageView) hView.findViewById(R.id.img_profile);

        mNavDrawer.addDrawerListener(mBarToggle);
        mBarToggle.syncState();
        assert getSupportActionBar() !=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavView.setNavigationItemSelectedListener(this);

        mManager.beginTransaction().replace(R.id.cont_Frame, new HomeFragment()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void NavigationHeader() {
        if (mAuth.getCurrentUser() != null) {
            mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String firstname = (String) dataSnapshot.child("firstname").getValue();
                    String lastname = (String) dataSnapshot.child("lastname").getValue();
                    String imageuser = (String) dataSnapshot.child("imguser").getValue();

                    final Context context = getApplicationContext();
                    mFirstName.setText(firstname);
                    mLastName.setText(lastname);

                    Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/yongdolah-da973.appspot.com/o/vintage.jpg?alt=media&token=a7b7bc97-73c3-49fb-aa8f-09b0480172a2")
                            .fitCenter().thumbnail(0.5f).crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(mHeaderImg);

                    Glide.with(context).load(imageuser).fitCenter().thumbnail(0.5f).crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(mProfileImg);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void CheckUser() {
        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        finish();
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int bar = item.getItemId();

        if (mBarToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (bar == R.id.action_setting) {

            aboutdDialog();
        } else if (bar == R.id.nav_posted) {
            if (mAuth.getCurrentUser() != null) {
                mUserLog.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String user = mAuth.getCurrentUser().getUid();
                        if(dataSnapshot.hasChild(user)){
                            startActivity(new Intent(MainActivity.this, PostedActivity.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }else{

                            adminDialog();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void aboutdDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    private void adminDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_admin, null);
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            mManager.beginTransaction().replace(R.id.cont_Frame, new HomeFragment()).commit();
            assert getSupportActionBar() !=null;
            getSupportActionBar().setTitle("Test aja");
        } else if (id == R.id.nav_cerbung) {
            mManager.beginTransaction().replace(R.id.cont_Frame, new CerbungFragment()).commit();
            assert getSupportActionBar() !=null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.cerbung);
        } else if (id == R.id.nav_cerpen) {
            mManager.beginTransaction().replace(R.id.cont_Frame, new CerpenFragment()).commit();
            getSupportActionBar().setTitle(R.string.cerpen);
        } else if (id == R.id.nav_dongeng) {
            mManager.beginTransaction().replace(R.id.cont_Frame, new DongengFragment()).commit();
            getSupportActionBar().setTitle(R.string.dongeng);
        } else if (id == R.id.nav_event) {
            mManager.beginTransaction().replace(R.id.cont_Frame, new EventFragment()).commit();
            getSupportActionBar().setTitle(R.string.event);
        } else if (id == R.id.nav_member){
            mManager.beginTransaction().replace(R.id.cont_Frame, new TabFragment()).commit();
            getSupportActionBar().setTitle("Group Member");
        } else if (id == R.id.nav_logout) {
            logout();
        }
        mNavDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Informasi! ...")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Apakah Anda ingin keluar dari Applikasi? ...")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(MainActivity.this);

                    }
                }).setNegativeButton("Tidak", null).show();
    }

    private void logout() {
        //setUserOffline();
        mAuth.signOut();
        LoginManager.getInstance().logOut();

    }

    private void setUserOffline() {
        if(mAuth.getCurrentUser()!=null){
            String userId= mAuth.getCurrentUser().getUid();


        }
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("net.gusri.yongdolah", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }

    public AlertDialog.Builder buildDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Offline Mode");
        builder.setMessage("Mohon Maaf, sebagian Content tidak bisa dibuka pada Mode Offline..");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}



