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


public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button loginbtn;
    TextView signuptxt;
    EditText edusername,edpassword;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        loginbtn=findViewById(R.id.btnlogin);
        signuptxt=findViewById(R.id.txtsignup);
        edusername=findViewById(R.id.signupuser);
        edpassword=findViewById(R.id.signuppass);
        progressBar=findViewById(R.id.progressbar);
        signuptxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

    }

    // important to not to enter the login activity when i open the app
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, Profile.class));
        }
    }

    private void userLogin() {
        String username=edusername.getText().toString().trim();
        String password=edpassword.getText().toString().trim();
        if(username.isEmpty())
        {
            edusername.setError("please enter user name");
            edusername.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches())     // for verify email structure
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
        // it's a sign method so we her check the password is the same in firebase
        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    finish(); // as when press back don't back to login activity so it finish the
                    // current activity then go to the next code which will pass to profile act
                    // important step ... we will ad flag mean that close all previous activities as when the user press back don't sign in again
                    Intent intent=new Intent(MainActivity.this,Profile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }else {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}

