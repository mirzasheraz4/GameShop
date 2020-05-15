package com.example.gameshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText emailET,passwordET;
    private Button logInbtn;
    private Button GloginBtn;
    int RC_SIGN_IN = 1234;

    private ProgressBar objectProgressBar;
    private FirebaseAuth objectFirebaseAuth;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connectXMLToJava();
        objectFirebaseAuth=FirebaseAuth.getInstance();

        GloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.GloginBtn :
                        signIn();
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void connectXMLToJava()
    {
        try
        {
            emailET=findViewById(R.id.emailET);
            passwordET=findViewById(R.id.passwordET);

            logInbtn=findViewById(R.id.logInbtn);
            objectProgressBar=findViewById(R.id.logInProgressBar);

            GloginBtn=findViewById(R.id.GloginBtn);
            logInbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signInUser();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "connectXMLToJava:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Intent intent=new Intent(Login.this, Home.class);
            startActivity(intent);
        } catch (ApiException e) {
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void signInUser()
    {
        try
        {
            if(!emailET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty())
            {
                if(objectFirebaseAuth!=null)
                {
                    if(objectFirebaseAuth.getCurrentUser()!=null)
                    {
                        objectFirebaseAuth.signOut();
                        Toast.makeText(this, "Sign Out Successfully", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        objectProgressBar.setVisibility(View.VISIBLE);
                        logInbtn.setEnabled(false);

                        objectFirebaseAuth.signInWithEmailAndPassword(emailET.getText().toString(),
                                passwordET.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(Login.this, "User sign in successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this,Home.class));

                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        objectProgressBar.setVisibility(View.INVISIBLE);
                                        logInbtn.setEnabled(true);

                                        Toast.makeText(Login.this, "Fails to sign in user:"+
                                                e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
            else if(emailET.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show();
                emailET.requestFocus();
            }
            else if(passwordET.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please enter the password", Toast.LENGTH_SHORT).show();
                passwordET.requestFocus();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "signInUser:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void back(View view)
    {
        Intent intent=new Intent(this, RegistrationMain.class);
        startActivity(intent);

    }
}
