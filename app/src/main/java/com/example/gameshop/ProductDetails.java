package com.example.gameshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetails extends AppCompatActivity {

    private ImageView productImage;
    private TextView productPrice, productDescription, productName;
    private String productID = "", state = "Normal";

    private Button add_to_cart_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        //initialize krenge

        productImage = (ImageView) findViewById(R.id.product_image_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);
        add_to_cart_button=findViewById(R.id.add_to_cart_button);




        getProductDetails(productID);

        add_to_cart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                addingToCartList();
            }
        });

    }

    private void getProductDetails(String productID)
    {
        //database reference bnaynge
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        //1 specific product ko search krrhay hai
        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //agar data exist krta hai
                if (dataSnapshot.exists())
                {
                    //product class ka 1 variable bnaynge or us variable me database ka data store krwa denge
                    Products products = dataSnapshot.getValue(Products.class);

                    //details set krdenge
                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addingToCartList()
    {
        String saveCurrentTime, saveCurrentDate;

        //time add krenge k kis waqt user product purchase krrha hai

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        //1 reference bnaynge jisme products add krenge cart me user select krega
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        //add krenge products hasmap ki madad se
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("discount", "");


        //cart list me 1 child node bnyange user k phone se usme uski cart ki products honge
        //1 user view hoga or same yehi kaam admin view me hoga agar to ye task successfull hojat hai to success ka toast show krwa de

        cartListRef
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            //same cart list me 1 admin view hoga usme current online user ka no aya hoga or usme saari products honge

                            cartListRef.child("Admin View")
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                //agar task successful hojaye to toast message show krwa de
                                                Toast.makeText(ProductDetails.this, "Product Added to cart Successfully", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(ProductDetails.this, Home.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void back(View view)
    {
        Intent intent=new Intent(this, Home.class);
        startActivity(intent);

    }
}
