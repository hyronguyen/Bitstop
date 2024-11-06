package com.doan.bitstop.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.doan.bitstop.Login;
import com.doan.bitstop.R;

public class ProfileFragment extends Fragment {

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userAddressTextView; // New TextView for Address
    private TextView userPhoneTextView;   // New TextView for Phone
    private TextView userCreditTextView;  // New TextView for Credit
    private TextView userRoleTextView;    // New TextView for Role
    private Button logoutButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize TextViews and Button
        userNameTextView = view.findViewById(R.id.account_info);
        userEmailTextView = view.findViewById(R.id.email_info);
        userAddressTextView = view.findViewById(R.id.address_info); // Initialize Address TextView
        userPhoneTextView = view.findViewById(R.id.phone_info);     // Initialize Phone TextView
        userCreditTextView = view.findViewById(R.id.credit_info);   // Initialize Credit TextView
        userRoleTextView = view.findViewById(R.id.role_info);       // Initialize Role TextView
        logoutButton = view.findViewById(R.id.button_logout);

        // Load user information from SharedPreferences
        loadUserInfo();

        // Set logout button click listener
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserInfo() {
        // Retrieve user information from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", getActivity().MODE_PRIVATE);
        String userName = sharedPreferences.getString("USER_NAME", "User"); // Updated key
        String userEmail = sharedPreferences.getString("USER_EMAIL", "user@example.com"); // Updated key
        String userAddress = sharedPreferences.getString("user_address", "N/A");
        String userPhone = sharedPreferences.getString("user_phone", "N/A");
        String userCredit = sharedPreferences.getString("user_credit", "N/A");
        String userRole = sharedPreferences.getString("user_role", "User");

        // Log retrieved values
        Log.d("ProfileFragment", "Loaded userName: " + userName);
        Log.d("ProfileFragment", "Loaded userEmail: " + userEmail);
        Log.d("ProfileFragment", "Loaded userAddress: " + userAddress);
        Log.d("ProfileFragment", "Loaded userPhone: " + userPhone);
        Log.d("ProfileFragment", "Loaded userCredit: " + userCredit);
        Log.d("ProfileFragment", "Loaded userRole: " + userRole);

        // Set the text views with user information
        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);
        userAddressTextView.setText(userAddress);
        userPhoneTextView.setText(userPhone);
        userCreditTextView.setText(userCredit);
        userRoleTextView.setText(userRole);
    }

    private void logout() {
        // Clear user session from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to the Login screen
        Intent intent = new Intent(getActivity(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish(); // Optional: Close the current activity
    }


}
