package com.salama.authapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;

public class Profile extends AppCompatActivity {

    private static final int IMAGE_CODE =100 ;
    FirebaseUser user;
    ImageView imageView;
    EditText edprofile;
    Button savebtn;
    Uri imageuri;
    ProgressBar progressBar;
    String imageUrl;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageView=findViewById(R.id.imageview);
        edprofile=findViewById(R.id.profile_ed);
        savebtn=findViewById(R.id.savebtn);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressbar);

        loadUserInfo();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });

        loadUserInfo();
    }
    //////////////////////////////////////////////////////////////////////////////////////

    private void loadUserInfo() {

       user= mAuth.getCurrentUser();

      if(user !=null)
      {
          if(user.getPhotoUrl()!=null)
          {
              Glide.with(this).load(user.getPhotoUrl().toString()).into(imageView);
          }
          if(user.getDisplayName()!=null){
              edprofile.setText(user.getDisplayName());
          }
      }
    }

    private void showImageChooser() {
        Intent gallery=new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(gallery,IMAGE_CODE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_CODE && resultCode==RESULT_OK && data!=null)
        {
            // we receive the image un uri
            imageuri=data.getData();  // as the getdata() method return Uri type
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageuri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "an error has occurred", Toast.LENGTH_SHORT).show();
        }
     //   upLoadImageTofireBaseStorage();
//
    }

    private void saveUserInfo() {
        String profilename=edprofile.getText().toString();
        if(profilename.isEmpty()){
            edprofile.setError("Enter you name please");
            edprofile.requestFocus();
            return;
        }

        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null) {
            UserProfileChangeRequest profile=new UserProfileChangeRequest.Builder()
                    .setDisplayName(profilename)
                    .setPhotoUri(Uri.parse(imageUrl)).build();
            user.updateProfile(profile)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Profile.this, "profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }



    private void upLoadImageTofireBaseStorage() {
        StorageReference storagRef=
                FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        progressBar.setVisibility(View.VISIBLE);
        storagRef.putFile(imageuri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override

            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                imageUrl=taskSnapshot.getDownloadUrl().toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }




}
