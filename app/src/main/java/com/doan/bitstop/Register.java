package com.doan.bitstop;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextView backtologinText;
    EditText iuserEdit, imailEdit, ipassEdit, irepassEdit;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addControl();
        addEvent();
    }

    private void addEvent() {
        backtologinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String username = iuserEdit.getText().toString().trim();
                String email = imailEdit.getText().toString().trim();
                String password = ipassEdit.getText().toString();
                String confirmPassword = irepassEdit.getText().toString();

                // Check if any field is empty
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if email is valid
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check password length
                if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), "Mật khẩu quá ngắn", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check password strength
                if (!password.matches(".*[!@#$%^,.].*")) {
                    Toast.makeText(getApplicationContext(), "Mật khẩu quá yếu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if password and confirm password match
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Mật khẩu nhập lại không đúng", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if username already exists in Firestore
                CheckExistedName(username, exists -> {
                    if (exists) {
                        Toast.makeText(getApplicationContext(), "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        // Check if email already exists in Firestore
                        CheckExistedEmail(email, existsEmail -> {
                            if (existsEmail) {
                                Toast.makeText(getApplicationContext(), "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                            } else {
                                addUserToFirestore(username, password, email);
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private void addControl() {
        backtologinText = findViewById(R.id.button_backtologin);
        registerButton = findViewById(R.id.button_conregister);

        iuserEdit = findViewById(R.id.input_username);
        imailEdit = findViewById(R.id.input_email);
        ipassEdit = findViewById(R.id.input_regpassword);
        irepassEdit = findViewById(R.id.input_conpassword);
    }

    // Kiểm tra trùng tên
    private void CheckExistedName(String username, OnCheckNameCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("USERS")
                .whereEqualTo("user_name", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onResult(!task.getResult().isEmpty());
                    } else {
                        Toast.makeText(getApplicationContext(), "Error checking username", Toast.LENGTH_SHORT).show();
                        callback.onResult(false);
                    }
                });
    }

    // Kiểm tra trung mail
    private void CheckExistedEmail(String email, OnCheckEmailCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("USERS")
                .whereEqualTo("user_mail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onResult(!task.getResult().isEmpty());
                    } else {
                        Toast.makeText(getApplicationContext(), "Error checking email", Toast.LENGTH_SHORT).show();
                        callback.onResult(false);
                    }
                });
    }

    // Callback interface for checking username
    public interface OnCheckNameCallback {
        void onResult(boolean exists);
    }

    // Callback interface for checking email
    public interface OnCheckEmailCallback {
        void onResult(boolean exists);
    }

    private void addUserToFirestore(String username, String password, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("user_name", username);
        user.put("user_password", password); // Consider hashing passwords before storing
        user.put("user_mail", email);

        db.collection("USERS")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Lỗi khi đăng ký", Toast.LENGTH_SHORT).show();
                });
    }

}