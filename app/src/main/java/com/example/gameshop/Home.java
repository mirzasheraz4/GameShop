package com.example.gameshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity {

    private FirebaseAuth objectFirebaseAuth;
    private NavigationView objectNavigationView;
    private DrawerLayout objectDrawerLayout;

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private Toolbar objectToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        objectFirebaseAuth=FirebaseAuth.getInstance();

        objectNavigationView=findViewById(R.id.navigationView);
        objectDrawerLayout=findViewById(R.id.drawerLayout);
        objectFirebaseAuth= FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        objectToolbar=findViewById(R.id.toolBar);
        objectNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.HomeItem)
                {
                    Toast.makeText(Home.this, "Home is clicked", Toast.LENGTH_SHORT).show();
                    closeMyDrawer();
                    return true;
                }
                else if(item.getItemId()==R.id.ProfileItem)
                {
                    closeMyDrawer();
                    return true;
                }
                else if(item.getItemId()==R.id.AboutItem)
                {
                    startActivity(new Intent(Home.this, Home.class));
                    finish();
                    closeMyDrawer();
                    return true;
                }
                else if(item.getItemId()==R.id.signoutItem)
                {
                    objectFirebaseAuth.signOut();
                    signout();
                    Toast.makeText(Home.this, "Sign Out Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Home.this, Login.class));
                    finish();
                    closeMyDrawer();
                    return true;
                }
                return false;
            }
        });

        setUpHamBurgerIcon();

    }
    @Override
    protected void onStart() {
        super.onStart();

        //query set krnege k products retrieve krey database me se iskay lioye 1 product ki model class
        //bnaynge jaha se sari info get krnege

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef, Products.class)
                        .build();



        //bridge between ui and data source

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                        //display krnege sara data holder object ko use krtay hoye
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Rs = " + model.getPrice() + "");
                        Picasso.get().load(model.getImage()).into(holder.imageView);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {



                                Intent intent = new Intent(Home.this, ProductDetails.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        //adapter set krenge
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private void closeMyDrawer()
    {
        objectDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setUpHamBurgerIcon()
    {
        try
        {
            ActionBarDrawerToggle objectActionBarDrawerToggle=
                    new ActionBarDrawerToggle(
                            this,
                            objectDrawerLayout,
                            objectToolbar,
                            R.string.open,
                            R.string.close
                    );

            objectActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));

            objectActionBarDrawerToggle.syncState();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "setUpHamBurgerIcon:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void signout() {
        objectFirebaseAuth.signOut();
        try {
            GoogleSignIn.getClient(getApplicationContext(),
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).signOut();
        } catch (Exception ex) {
            Toast.makeText(this, "Logging Out Error", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Sign Out Successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
