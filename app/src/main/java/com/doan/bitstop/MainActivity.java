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
import com.doan.bitstop.Fragment.CouponFragment;
import com.doan.bitstop.Fragment.ProfileFragment;
import com.doan.bitstop.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigationView;
    TextView registerText;
    EditText accountInput, passwordInput;
    Button loginButton;
    LinearLayout waitingLayout, loginLayout;
    HomeFragment homefragment = new HomeFragment();
    CartFragment cartFragment= new CartFragment();
    CouponFragment couponFragment = new CouponFragment();
    SearchFragment searchFragment = new SearchFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Đặt layout trước khi tìm view

        // Khởi tạo BottomNavigationView
        navigationView = findViewById(R.id.bottom_navigation); // Đảm bảo rằng ID đúng

        // Kiểm tra trạng thái đăng nhập
        CheckLoginState();

        // Xử lý dữ liệu người dùng
        String userID = sharedPreferences.getString("USER_ID", null);
        Log.d("huy", "Logged in userID: " + userID);

        // Thiết lập các điều khiển khác
        AddControl();

        // Thiết lập BottomNavigationView
        setupBottomNavigation();

        LoadFragment(homefragment);
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
                    LoadFragment(couponFragment);
                    return true;
                }
                if(item.getItemId()==R.id.nav_profile){
                    LoadFragment(profileFragment);
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

    private void AddControl() {
        registerText = findViewById(R.id.button_register);
        accountInput = findViewById(R.id.input_account);
        passwordInput = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.button_login);
        waitingLayout = findViewById(R.id.layout_waiting);
        loginLayout = findViewById(R.id.layout_login);
    }

}
