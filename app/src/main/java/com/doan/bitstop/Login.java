package com.doan.bitstop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {

    TextView registerText;
    EditText accountInput, passwordInput;
    Button loginButton;
    LinearLayout waitingLayout, loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AddControl();
        AddEvent();
    }

    private void AddEvent() {
        // chuyển đến đăng ký
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
        //Thực hiện đăng nhâp
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkpass = passwordInput.getText().toString();
                String checkuser = accountInput.getText().toString();
                LoginUser(checkuser,checkpass);
            }
        });

    }

    private void LoginUser(String checkuser, String checkpass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("USERS")
                .whereEqualTo("user_name", checkuser)
                .whereEqualTo("user_password", checkpass)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {

                            String userID = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Log.d("Login", userID);
                            waitingLayout.setVisibility(View.VISIBLE);
                            loginLayout.setVisibility(View.GONE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.putExtra("USER_ID", userID);
                                    startActivity(intent);
                                    waitingLayout.setVisibility(View.GONE);
                                    loginLayout.setVisibility(View.VISIBLE);

                                    finish();
                                }
                            }, 4000);

                        } else {
                            Log.d("Login", "Thất bại: Sai tài khoản hoặc mật khẩu");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Login", "Lỗi khi kiểm tra tài khoản: ", e);
                    }
                });
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