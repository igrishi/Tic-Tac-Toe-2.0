package com.rishi.tic_tac_toe20;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class Signin extends AppCompatActivity {

    private SignInButton signin_button;
    private int RC_SIGN_IN=123;
    private String TAG="Signin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signin_button=findViewById(R.id.signin);
        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google_sigin();
            }
        });
    }

    private void google_sigin(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();

        GoogleSignInClient mGoogleSignInClient=GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

        //intent
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            //User already logged in
            String name=account.getDisplayName();
            String photo= Objects.requireNonNull(account.getPhotoUrl()).toString();
            User.setName(name);
            User.setImage(photo);
            Intent intent=new Intent(Signin.this,HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            if (account != null) {
                String email=account.getEmail();
                String name=account.getDisplayName();
                String photo= Objects.requireNonNull(account.getPhotoUrl()).toString();
                String unique_id=account.getId();
                Log.d(TAG, "handleSignInResult: "+email+" "+name);
                Log.d(TAG, "handleSignInResult: "+photo+" "+account.getId());
                User.setName(name);
                User.setImage(photo);
                database_write(name,photo,unique_id);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void database_write(String name, String photo, String unique_id) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("name",name);
        map.put("image",photo);
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("users").document(unique_id).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //everything is fine
                            Toast.makeText(Signin.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Signin.this,HomePage.class);
                            startActivity(intent);
                            finish();
                        }else{
                            //handle failures
                            Toast.makeText(Signin.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
