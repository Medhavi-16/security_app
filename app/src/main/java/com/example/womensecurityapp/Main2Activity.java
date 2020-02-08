package com.example.womensecurityapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.womensecurityapp.User_login_info.Account_setup;
import com.example.womensecurityapp.User_login_info.Signup;
import com.example.womensecurityapp.model.User_residential_details;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main2Activity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    public static User_residential_details t=new User_residential_details();
    public static String status="true";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_history,
                R.id.nav_trusted, R.id.nav_contact, R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
         drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
             @Override
             public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

             }

             @Override
             public void onDrawerOpened(@NonNull View drawerView) {
               drawerView.bringToFront();
             }

             @Override
             public void onDrawerClosed(@NonNull View drawerView) {

             }

             @Override
             public void onDrawerStateChanged(int newState) {

             }
         });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                if(id==R.id.nav_home)
                {
                    navController.navigate(R.id.nav_home);
                }
                if(id==R.id.nav_profile)
                {
                    Toast.makeText(getApplicationContext(), "Profile clicked", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.nav_profile);
                }
                if(id==R.id.nav_history)
                {
                    Toast.makeText(getApplicationContext(), "History clicked", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.nav_history);
                }
                if(id==R.id.nav_contact)
                {
                    navController.navigate(R.id.nav_contact);
                }
                if(id==R.id.nav_trusted)
                {
                    navController.navigate(R.id.nav_trusted);
                }
                if(id==R.id.nav_share)
                {
                    navController.navigate(R.id.nav_share);
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;


            }
        });


       auth=FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    showDialog();
                }
                else
                {
                    final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    databaseReference.child("Personal_info").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            t=dataSnapshot.getValue(User_residential_details.class);
                            else
                            {
                                status="false";
                                Intent i=new Intent(Main2Activity.this, Account_setup.class);
                                startActivity(i);
                                Toast.makeText(getApplicationContext(),"Not set",Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        };

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.logout)
       auth.signOut();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        auth.addAuthStateListener(authStateListener);


        super.onStart();

    }
    public void showDialog()
    {
        Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_login);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT ;
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button login=dialog.findViewById(R.id.login);
        final Button Signup=dialog.findViewById(R.id.signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Main2Activity.this, Login.class);
                startActivity(i);

            }
        });
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Main2Activity.this, com.example.womensecurityapp.User_login_info.Signup.class);
                startActivity(i);

            }
        });
        dialog.show();
    }

}






