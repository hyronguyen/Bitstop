package com.doan.bitstop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

import com.doan.bitstop.Fragment.CartFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {
    private EditText nameEditText, addressEditText, phoneEditText, countryEditText, couponEditText;
    private Button confirmOrderButton, useDefaultAddressButton, applyCouponButton; // New button for applying coupon
    private String userId;
    private ArrayList<CartItem> cartItems;
    private double totalPrice; // Make totalPrice a member variable
    private static final String PREFS_NAME = "LoginPrefs";
    private TextView totalTextView;
    public static final String CART_ITEMS_KEY = "CART_ITEMS_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        nameEditText = findViewById(R.id.editTextName);
        addressEditText = findViewById(R.id.editTextAddress);
        phoneEditText = findViewById(R.id.editTextPhone);
        countryEditText = findViewById(R.id.editCountry);
        couponEditText = findViewById(R.id.editTextCoupon); // Initialize couponEditText
        confirmOrderButton = findViewById(R.id.buttonConfirmOrder);
        useDefaultAddressButton = findViewById(R.id.buttonUseDefault);
        applyCouponButton = findViewById(R.id.buttonApplyCoupon); // Initialize applyCouponButton
        totalTextView = findViewById(R.id.textViewTotal); // Use the member variable for totalTextView

        // Set default country
        countryEditText.setText("Vietnam");

        // Retrieve user ID, cart items, and total price from the intent
        userId = getIntent().getStringExtra("USER_ID");
        String cartItemsJson = getIntent().getStringExtra("CART_ITEMS");
        totalPrice = getIntent().getDoubleExtra("TOTAL_PRICE", 0.00); // Retrieve total price

        // Deserialize cartItemsJson into a list of CartItem objects
        Gson gson = new Gson();
        Type cartItemListType = new TypeToken<List<CartItem>>(){}.getType();
        cartItems = gson.fromJson(cartItemsJson, cartItemListType);

        // Ensure cartItems is not null
        if (cartItems == null) {
            cartItems = new ArrayList<>(); // Initialize with an empty list if null
        }

        // Display the total price in the TextView
        totalTextView.setText(String.format("Total: $%.2f", totalPrice));

        // Set up the button click listener for Confirm Order
        confirmOrderButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String phone = phoneEditText.getText().toString();

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            placeOrder(name, address, phone);
        });

        // Set up the button click listener for Use Default Address
        useDefaultAddressButton.setOnClickListener(v -> fillDefaultAddress());

        // Set up the button click listener for Apply Coupon
        applyCouponButton.setOnClickListener(v -> applyCoupon());
    }



    private void fillDefaultAddress() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = sharedPreferences.getString("USER_NAME", "");
        String address = sharedPreferences.getString("user_address", "");
        String phone = sharedPreferences.getString("user_phone", "");

        // Fill the EditText fields with user's default information
        nameEditText.setText(name);
        addressEditText.setText(address);
        phoneEditText.setText(phone);
        countryEditText.setText("Vietnam"); // Default to Vietnam
    }

    private void placeOrder(String name, String address, String phone) {
        Map<String, Object> orderData = new HashMap<>();

        // User and order details
        orderData.put("or_cid", userId); // Customer ID
        orderData.put("or_name", name); // Name
        orderData.put("or_address", address); // Address
        orderData.put("or_phone", phone); // Phone number
        orderData.put("or_payment", "Ship COD"); // Default payment method
        orderData.put("or_status", "Processing"); // Default order status

        // Calculate the subtotal (total price before coupon)
        long subtotal = calculateSubtotal();
        orderData.put("or_subtotal", subtotal);

        // Set order date as the current timestamp
        orderData.put("or_date", FieldValue.serverTimestamp());

        // Set invoice URL (this is just an example; replace with actual invoice URL generation logic)
        String invoiceUrl = "https://example.com/invoice.pdf";
        orderData.put("or_invo", invoiceUrl);

        // Create the items list (from the cart)
        ArrayList<Map<String, Object>> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("id", item.getId());  // Use the product ID here
            itemData.put("title", item.getName());
            itemData.put("quantity", item.getQuantity());
            itemData.put("total", item.getPrice() * item.getQuantity());

            // Log the product ID and other details
            Log.d("OrderDetails", "Adding to order - Product ID: " + item.getId() + ", Title: " + item.getName() + ", Quantity: " + item.getQuantity() + ", Total: " + item.getPrice() * item.getQuantity());

            orderItems.add(itemData);
        }
        orderData.put("or_items", orderItems);

        // Save the order data to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ORDERS")
                .add(orderData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();

                    // Clear the cart after order is placed (implement your clear cart logic here)

                    // Now create and save the SM record
                    String orderId = documentReference.getId(); // Get the order ID
                    Map<String, Object> smData = new HashMap<>();

                    smData.put("sm_date", FieldValue.serverTimestamp()); // Current timestamp
                    smData.put("sm_des", "Output for order:"+orderId); // Description with order ID
                    smData.put("sm_items",orderItems); // Items from the order
                    smData.put("sm_res", "Stock Staff"); // Default responsible person
                    smData.put("sm_status", "Processing"); // Default status
                    smData.put("sm_type", "Output"); // Output type

                    db.collection("SM")
                            .add(smData)
                            .addOnSuccessListener(smDocumentReference -> {
                                Log.d("SMDetails", "SM record added for order: " + orderId);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("SMDetails", "Failed to add SM record", e);
                            });

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show());
    }




    private long calculateSubtotal() {
        long subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    private void applyCoupon() {
        String couponCode = couponEditText.getText().toString().trim();

        if (couponCode.isEmpty()) {
            Toast.makeText(this, "Please enter a coupon code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch coupon data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("COUPON").document(couponCode).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch the status field to check if it's already used
                        String couponStatus = documentSnapshot.getString("cou_status");

                        if ("used".equals(couponStatus)) {
                            // If the coupon is already used, inform the user
                            Toast.makeText(this, "This coupon has already been used.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Fetch the discount field
                        String discountStr = documentSnapshot.getString("cou_discount");

                        if (discountStr != null) {
                            try {
                                // Convert the string discount to double
                                double discount = Double.parseDouble(discountStr);

                                // Ensure the discount is between 0.1 (10%) and 0.99 (99%)
                                if (discount < 0.1 || discount > 0.99) {
                                    Toast.makeText(this, "Invalid coupon discount. The discount must be between 10% and 99%.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Calculate the new total
                                double newTotal = totalPrice - (totalPrice * discount);
                                totalTextView.setText(String.format("Total: $%.2f", newTotal));

                                // Mark the coupon as used
                                updateCouponStatusToUsed(couponCode);

                                Toast.makeText(this, "Coupon applied successfully!", Toast.LENGTH_SHORT).show();
                            } catch (NumberFormatException e) {
                                Toast.makeText(this, "Invalid coupon discount value", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Coupon discount is missing", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Coupon code does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to apply coupon. Please try again.", Toast.LENGTH_SHORT).show());
    }

    private void updateCouponStatusToUsed(String couponCode) {
        // Fetch Firestore instance and update the coupon's status to "used"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("COUPON").document(couponCode)
                .update("cou_status", "used")
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated the coupon status
                    Log.d("OrderDetails", "Coupon status updated to 'used'");
                })
                .addOnFailureListener(e -> {
                    // Handle failure to update coupon status
                    Log.e("OrderDetails", "Failed to update coupon status", e);
                });
    }




}
