package com.example.womensecurityapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class Main2Activity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
               /** Fragment fragment = null;
                Class fragmentClass= HomeFragment.class;
                if(id==R.id.nav_profile)
                {
                   fragmentClass=ProfileFragment.class;

                }




                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_layout,fragment).commit();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                //;*/


            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


       /** navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);

                drawer.closeDrawers();

                int id = menuItem.getItemId();
                if(id==R.id.nav_profile)
                    navController.navigate(R.id.nav_profile);
                else if(id==R.id.nav_history)
                    navController.navigate(R.id.nav_history);

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
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

}






