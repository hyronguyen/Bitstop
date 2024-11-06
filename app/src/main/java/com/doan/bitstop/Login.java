package com.doan.bitstop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.mindrot.jbcrypt.BCrypt;

public class Login extends AppCompatActivity {

    TextView registerText, userInfoText;
    EditText accountInput, passwordInput;
    Button loginButton, logoutButton;
    LinearLayout waitingLayout, loginLayout, userInfoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // If logged in, go to home activity
        if (isLoggedIn) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();  // Close Login activity
            return; // Exit onCreate
        } else {
            setContentView(R.layout.activity_login);  // Show login layout
            AddControl();
            AddEvent();
        }
    }

    private void AddEvent() {
        // Transition to registration
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
        // Perform login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkpass = passwordInput.getText().toString();
                String checkuser = accountInput.getText().toString();
                waitingLayout.setVisibility(View.VISIBLE);
                loginLayout.setVisibility(View.GONE);
                LoginUser(checkuser, checkpass);
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

                            // Check password
                            if (BCrypt.checkpw(checkpass, storedHashedPassword)) {
                                String userID = userDoc.getId();
                                String userName = userDoc.getString("user_name");
                                String email = userDoc.getString("user_email");
                                String userAddress = userDoc.getString("user_address"); // New field
                                String userPhone = userDoc.getString("user_phone");     // New field
                                String userCredit = userDoc.getString("user_credit");   // New field
                                String userRole = userDoc.getString("user_role");       // New field

                                // Save user information
                                SaveLoginState(userID, userName, email, userAddress, userPhone, userCredit, userRole);

                                // Switch to home activity
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Close Login activity
                            } else {
                                // Incorrect password
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

    private void SaveLoginState(String userID, String userName, String email, String userAddress, String userPhone, String userCredit, String userRole) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("USER_ID", userID);
        editor.putString("USER_NAME", userName);
        editor.putString("USER_EMAIL", email);
        editor.putString("user_address", userAddress);   // Save address
        editor.putString("user_phone", userPhone);       // Save phone
        editor.putString("user_credit", userCredit);     // Save credit
        editor.putString("user_role", userRole);         // Save role
        editor.apply();
    }
}
