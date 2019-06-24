package com.salama.authapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity {
    EditText edusername,edpassword;
    Button signupbtn;
    TextView logintext;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth=FirebaseAuth.getInstance();
        edusername=findViewById(R.id.signupuser);
        edpassword=findViewById(R.id.signuppass);
        signupbtn=findViewById(R.id.btnsignup);
        logintext=findViewById(R.id.txtsignup);
        progressBar=findViewById(R.id.progressbar);
        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
            }
        });
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, Profile.class));
        }
    }


    private void registerUser() {
        String username=edusername.getText().toString().trim();
        String password=edpassword.getText().toString().trim();
        if(username.isEmpty())
        {
            edusername.setError("please enter user name");
            edusername.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches())     // for verify email
        {
            edusername.setError("Please enter verify username");
            edusername.requestFocus();
            return;
        }
        if(password.isEmpty())
        {
            edpassword.setError("please enter password");
            edpassword.requestFocus();
            return;
        }
        // to test the password size shouldn't be less than 6 char
            if(edpassword.length()<6)
            {
                edpassword.setError("Minimum length of password is 6");
                edpassword.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        finish();
                        Intent intent=new Intent(SignUpActivity.this,Profile.class);
                        // TO CLEAR ALL PREVIOUS ACTIVITIES
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            Toast.makeText(SignUpActivity.this, "This username Has Been Registered Befor", Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    }
}
