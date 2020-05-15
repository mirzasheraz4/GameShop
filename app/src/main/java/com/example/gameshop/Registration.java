package com.example.gameshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }
    public void RegistrationPage(View view)
    {
        Intent intent=new Intent(this, RegistrationMain.class);
        startActivity(intent);

    }
    public void signupLater(View view)
    {
        Intent intent=new Intent(this, Home.class);
        Toast.makeText(this, "Guest Login", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
