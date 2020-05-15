package com.example.gameshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private EditText accNameET, accLastNameET, accEmailET, accAddET, accPhoneET;
    private ImageView profileIV;
    private Button UpdateBTN;
    private FirebaseAuth objectFirebaseAuth;
    private FirebaseFirestore objectFirebaseFireStore;
    private ProgressBar objectProgressBar;
    private DocumentReference objectDocumentReference;
    private static final int REQUEST_CODE = 1234;
    private boolean isImageSelected = false;
    private Uri objectUri;
    private StorageReference objectStorageReference;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        accNameET = findViewById(R.id.accNameET);
        accLastNameET = findViewById(R.id.accLastNameET);
        accEmailET = findViewById(R.id.accEmailET);
        accAddET = findViewById(R.id.accAddET);
        accPhoneET = findViewById(R.id.accPhoneET);
        profileIV = findViewById(R.id.profileIV);
        objectProgressBar = findViewById(R.id.UpdateProgressBar);
        UpdateBTN = findViewById(R.id.UpdateBTN);
        objectFirebaseAuth = FirebaseAuth.getInstance();
        objectFirebaseFireStore = FirebaseFirestore.getInstance();
        objectStorageReference = FirebaseStorage.getInstance().getReference("MyImages");


        profileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromGallery();
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();

            accEmailET.setText(personName);
            accEmailET.setText(personEmail);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }

        try {
            FirebaseUser user = objectFirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String email = user.getEmail();
                accEmailET.setText(email);
            }
            objectFirebaseFireStore.collection("Users")
                    .document(accEmailET.getText().toString())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                String url = documentSnapshot.getString("URL");
                                String email = documentSnapshot.getString("Email");
                                String name = documentSnapshot.getString("First Name");
                                String Lastname = documentSnapshot.getString("Last Name");
                                String address = documentSnapshot.getString("Address");
                                String Phone = documentSnapshot.getString("Phone");
                                accEmailET.setText(email);
                                accNameET.setText(name);
                                accLastNameET.setText(Lastname);
                                accAddET.setText(address);
                                accPhoneET.setText(Phone);
                                downloadImage();

                                //  Toast.makeText(getActivity (), "Image Downloaded", Toast.LENGTH_SHORT).show();

                            } else {
                                //  Toast.makeText(getActivity (), "No Such File Exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    // Toast.makeText(getActivity (), "Failed To Retrieve Image" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception ex) {

            //Toast.makeText(getActivity (), "DownloadError: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToCloudStorage() {
        if (isImageSelected) {
            objectProgressBar.setVisibility(View.VISIBLE);
            String imagePath = accNameET.getText().toString() + "." + getExtension(objectUri);
            final StorageReference imageRef = objectStorageReference.child(imagePath);

            UploadTask objectUploadTask = imageRef.putFile(objectUri);
            objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        objectProgressBar.setVisibility(View.INVISIBLE);
                        throw task.getException();
                    }

                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> objectMap = new HashMap<>();
                        objectMap.put("url", task.getResult().toString());

                        objectFirebaseFireStore.collection("Links")
                                .document(accNameET.getText().toString())
                                .set(objectMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        objectProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        objectProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(Profile.this, "Fails to upload image:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    objectProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Profile.this, "Fails to upload image:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
            ;

        }
    }

    public void updateValue(View view) {
        try {
            if (!accNameET.getText().toString().isEmpty() && !accAddET.getText().toString().isEmpty()
                    && !accPhoneET.getText().toString().isEmpty()) {
                objectProgressBar.setVisibility(View.VISIBLE);
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("First Name", accNameET.getText().toString());
                objectMap.put("Last Name", accLastNameET.getText().toString());
                objectMap.put("Address", accAddET.getText().toString());
                objectMap.put("Phone", accPhoneET.getText().toString());
                objectDocumentReference = objectFirebaseFireStore.collection("Users")
                        .document(accEmailET.getText().toString());

                uploadImageToCloudStorage();
                objectDocumentReference.update(objectMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                objectProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(Profile.this, "Data updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                objectProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(Profile.this, "Fails to update data:"
                                        + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            objectProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "updateValue:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri objectUri) {
        try {
            ContentResolver objectContentResolver = getContentResolver();
            MimeTypeMap objectMimeTypeMap = MimeTypeMap.getSingleton();

            String extension = objectMimeTypeMap.getExtensionFromMimeType(objectContentResolver.getType(objectUri));
            return extension;
        } catch (Exception e) {
            Toast.makeText(this, "getExtension:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    private void selectImageFromGallery() {
        try {
            Intent objectIntent = new Intent();
            objectIntent.setType("image/*");

            objectIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(objectIntent, REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "selectImageFromGallery:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            objectUri = data.getData();
            if (objectUri != null) {
                try {
                    Bitmap objectBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), objectUri);
                    profileIV.setImageBitmap(objectBitmap);

                    isImageSelected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Data is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No Image is selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadImage() {
        if (!accNameET.getText().toString().isEmpty()) {
            objectFirebaseFireStore.collection("Links").document(accNameET.getText().toString())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        objectProgressBar.setVisibility(View.INVISIBLE);
                        String url = documentSnapshot.getString("url");
                        Glide.with(Profile.this)
                                .load(url).into(profileIV);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    objectProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Profile.this, "Fail to download data:" +
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            objectProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Please enter the iamge name to download", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);

    }
}
