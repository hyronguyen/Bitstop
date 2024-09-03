package com.doan.bitstop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.doan.bitstop.Fragment.CartFragment;
import com.doan.bitstop.Fragment.HomeFragment;
import com.doan.bitstop.Fragment.MessageFragment;
import com.doan.bitstop.Fragment.ProfileFragment;
import com.doan.bitstop.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigationView;

    HomeFragment homefragment = new HomeFragment();
    CartFragment cartFragment= new CartFragment();
    MessageFragment messageFragment = new MessageFragment();
    SearchFragment searchFragment = new SearchFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckLoginState();
        setContentView(R.layout.activity_main);
        String userID = sharedPreferences.getString("USER_ID", null);
        Log.d("huy", "Logged in userID: " + userID);
        AddControl();
        setupBottomNavigation();
    }

    private void AddControl() {
        navigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.nav_home){
                    LoadFragment(homefragment);
                    return true;
                }
                if (item.getItemId() == R.id.nav_search) {
                    LoadFragment(searchFragment);
                    return true;
                }

                if(item.getItemId()==R.id.nav_cart){
                    LoadFragment(cartFragment);
                    return true;
                }

                if(item.getItemId()== R.id.nav_message){
                    LoadFragment(messageFragment);
                    return true;
                }
                if(item.getItemId()==R.id.nav_profile){
                    Logout();
                    return true;
                }
                return true;
            }
        });
    }

    public void LoadFragment(Fragment fmNew){
        FragmentTransaction fmTrans = getSupportFragmentManager().beginTransaction();
        fmTrans.replace(R.id.main_frame, fmNew);
        fmTrans.addToBackStack(null);
        fmTrans.commit();
    }

    private void CheckLoginState(){
         sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            return;
        }
    }

    private void Logout() {
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

}
