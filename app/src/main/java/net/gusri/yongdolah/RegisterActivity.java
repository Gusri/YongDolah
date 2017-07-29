package net.gusri.yongdolah;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText mMailRegister;
    private EditText mPassRegister;
    private Button mRegisterBtn;
    private ProgressDialog mProgress;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_Reg);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.register);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegInten = new Intent(RegisterActivity.this, LoginActivity.class);
                RegInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(RegInten);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        mMailRegister = (EditText) findViewById(R.id.et_EmailReg);
        mPassRegister = (EditText) findViewById(R.id.et_PassReg);
        mRegisterBtn = (Button) findViewById(R.id.btn_Register);

        mMailRegister.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        mPassRegister.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        mMailRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    mMailRegister.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    mMailRegister.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mail_outline, 0, 0, 0);
                }
            }
        });

        mPassRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    mPassRegister.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    mPassRegister.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_outline, 0, 0, 0);
                }
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        String email = mMailRegister.getText().toString().trim();
        String password = mPassRegister.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgress.setMessage("Sign Up ...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mProgress.dismiss();
                        Intent maintIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        maintIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(maintIntent);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

    }
}
