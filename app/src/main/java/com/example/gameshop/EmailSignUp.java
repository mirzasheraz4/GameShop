package com.example.gameshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EmailSignUp extends AppCompatActivity {

    private EditText emailET,passwordET,fNameET,lNameET;
    private Button SignUpbtn;

    private static final String COLLECTION_NAME="Users";
    private FirebaseFirestore objectFirebaseFirestore;
    private Dialog objectDialog;

    private ProgressBar objectProgressBar;
    private FirebaseAuth objectFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);

        objectFirebaseFirestore=FirebaseFirestore.getInstance();
        objectDialog=new Dialog(this);

        objectFirebaseAuth=FirebaseAuth.getInstance();
        connectXMLObjects();
    }

    private void connectXMLObjects()
    {
        try
        {
            emailET=findViewById(R.id.emailET);
            passwordET=findViewById(R.id.passwordET);
            fNameET=findViewById(R.id.fNameET);
            lNameET=findViewById(R.id.lNameET);

            SignUpbtn=findViewById(R.id.SignUpbtn);
            objectProgressBar=findViewById(R.id.signUpProgressBar);

            SignUpbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkIfUserExists();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "connectXMLObjects:" +e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfUserExists()
    {
        try
        {
            if(!emailET.getText().toString().isEmpty())
            {
                if(objectFirebaseAuth!=null)
                {
                    objectProgressBar.setVisibility(View.VISIBLE);
                    SignUpbtn.setEnabled(false);

                    objectFirebaseAuth.fetchSignInMethodsForEmail(emailET.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean check=task.getResult().getSignInMethods().isEmpty();
                                    if(!check)
                                    {
                                        SignUpbtn.setEnabled(true);
                                        objectProgressBar.setVisibility(View.INVISIBLE);

                                        Toast.makeText(EmailSignUp.this, "User already exists", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(check)
                                    {

                                        signUpUser();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    SignUpbtn.setEnabled(true);
                                    objectProgressBar.setVisibility(View.INVISIBLE);

                                    Toast.makeText(EmailSignUp.this, "Fails to check if user exists:"
                                            +e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else
            {
                emailET.requestFocus();
                Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            SignUpbtn.setEnabled(true);
            objectProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "checkIfUserExists:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void signUpUser()
    {
        try
        {
            if(!emailET.getText().toString().isEmpty()
                    && !passwordET.getText().toString().isEmpty())
            {
                if(objectFirebaseAuth!=null)
                {
                    objectProgressBar.setVisibility(View.VISIBLE);
                    SignUpbtn.setEnabled(false);

                    objectDialog.show();
                    final Map<String, Object> objectMap = new HashMap<>();
                    objectMap.put("Email", emailET.getText().toString());
                    objectMap.put("Password", passwordET.getText().toString());
                    objectMap.put("First Name", fNameET.getText().toString());
                    objectMap.put("Last Name", lNameET.getText().toString());

                    objectFirebaseFirestore.collection(COLLECTION_NAME)
                            .document(emailET.getText().toString())
                            .set(objectMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EmailSignUp.this, "User created Successfully", Toast.LENGTH_SHORT).show();
                                    objectDialog.dismiss();
                                    objectProgressBar.setVisibility(View.INVISIBLE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    objectDialog.dismiss();
                                }
                            });
                    objectFirebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString(),
                            passwordET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    if(authResult.getUser()!=null)
                                    {
                                        objectFirebaseAuth.signOut();
                                        emailET.setText("");

                                        passwordET.setText("");

                                        fNameET.setText("");

                                        lNameET.setText("");
                                        emailET.requestFocus();

                                        SignUpbtn.setEnabled(true);

                                    }
                                }
                            } )
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    SignUpbtn.setEnabled(true);
                                    emailET.requestFocus();

                                    objectProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(EmailSignUp.this, "Fails to create user:"
                                            +e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else if(emailET.getText().toString().isEmpty())
            {
                SignUpbtn.setEnabled(true);
                objectProgressBar.setVisibility(View.INVISIBLE);

                emailET.requestFocus();
                Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show();
            }
            else if(passwordET.getText().toString().isEmpty())
            {
                SignUpbtn.setEnabled(true);
                objectProgressBar.setVisibility(View.INVISIBLE);

                passwordET.requestFocus();
                Toast.makeText(this, "Please enter the password", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            SignUpbtn.setEnabled(true);
            emailET.requestFocus();

            objectProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "signUpUser:"+
                    e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(objectFirebaseAuth.getCurrentUser()!=null)
        {
            objectFirebaseAuth.signOut();
        }
    }
    public void back(View view)
    {
        Intent intent=new Intent(this, RegistrationMain.class);
        startActivity(intent);

    }

}
