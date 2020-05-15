package com.example.gameshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationMain extends AppCompatActivity {
    private Button GmailsignUpbtn;

    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_main);



        GmailsignUpbtn=findViewById(R.id.GmailsignUpbtn);
    }
    public void Cancel_Registration(View view)
    {
        Intent intent=new Intent(this, Registration.class);
        startActivity(intent);

    }
    public void SignUpUsingEmail(View view)
    {
        Intent intent=new Intent(this, EmailSignUp.class);
        startActivity(intent);
    }
    public void loginUser(View view)
    {
        Intent intent=new Intent(this, Login.class);
        startActivity(intent);
    }
    protected void onPause() {
        super.onPause();
        RegistrationMain.this.finish();
    }
}
