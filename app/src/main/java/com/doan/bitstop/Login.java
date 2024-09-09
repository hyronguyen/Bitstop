package com.doan.bitstop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.mindrot.jbcrypt.BCrypt;

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
                waitingLayout.setVisibility(View.VISIBLE);
                loginLayout.setVisibility(View.GONE);
                LoginUser(checkuser,checkpass);
            }
        });

    }


    private void LoginUser(String checkuser, String checkpass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("USERS")
                .whereEqualTo("user_name", checkuser)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                            String storedHashedPassword = userDoc.getString("user_password");

                            // Use BCrypt to check the provided password against the stored hash
                            if (BCrypt.checkpw(checkpass, storedHashedPassword)) {
                                String userID = userDoc.getId();
                                SaveLoginState(userID);
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                waitingLayout.setVisibility(View.GONE);
                                loginLayout.setVisibility(View.VISIBLE);
                                finish();
                            } else {
                                // Wrong password
                                waitingLayout.setVisibility(View.GONE);
                                loginLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Invalid credentials, please try again!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // User not found
                            waitingLayout.setVisibility(View.GONE);
                            loginLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "User not found, please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show();
                        waitingLayout.setVisibility(View.GONE);
                        loginLayout.setVisibility(View.VISIBLE);
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

    private void SaveLoginState(String userID) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("USER_ID", userID);
        editor.apply();
    }

}