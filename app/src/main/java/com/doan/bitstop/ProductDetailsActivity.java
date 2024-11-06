package com.doan.bitstop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton; // Import the ImageButton class
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ProductDetailsActivity extends AppCompatActivity {
    private int quantity = 1; // Initialize quantity
    private TextView quantityTextView; // TextView to display the quantity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details); // Your layout XML

        // Retrieve intent extras
        Intent intent = getIntent();
        String id = intent.getStringExtra("PRODUCT_ID");
        String name = intent.getStringExtra("PRODUCT_NAME");
        String price = intent.getStringExtra("PRODUCT_PRICE");
        String category = intent.getStringExtra("PRODUCT_CATEGORY");
        String platform = intent.getStringExtra("PRODUCT_PLATFORM");
        String description = intent.getStringExtra("PRODUCT_DESCRIPTION");
        String getProImg = intent.getStringExtra("PRODUCT_IMAGE_URL"); // Now handle URLs, not resource IDs

        // Find views by ID
        ImageView productImage = findViewById(R.id.photo);
        TextView productName = findViewById(R.id.product_name);
        TextView productPrice = findViewById(R.id.thePriceOfProduct);
        TextView productCategory = findViewById(R.id.product_category);
        TextView productPlatform = findViewById(R.id.product_platform);
        TextView productDescription = findViewById(R.id.product_description);
        quantityTextView = findViewById(R.id.quantity); // TextView for quantity display

        // Find buttons for increasing and decreasing quantity
        Button increaseButton = findViewById(R.id.increaseQuantityButton);
        Button decreaseButton = findViewById(R.id.decreaseQuantityButton);
        Button addToCartButton = findViewById(R.id.addToCartButton);
        // Back button implementation


// Inside your onCreate method:
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity and return to the previous one
            }
        });


        // Set data to views
        productName.setText(name);
        productPrice.setText(price);
        productCategory.setText(category);
        productPlatform.setText(platform);
        productDescription.setText(description);

        // Load image using Glide
        Glide.with(this)
                .load(getProImg)  // Use the URL passed from intent
                .placeholder(R.drawable.background)  // A placeholder image while loading
                .error(R.drawable.button_square)  // An error image if loading fails
                .into(productImage);  // Target ImageView

        // Initialize quantity display
        updateQuantityDisplay();

        // Set click listeners for increase and decrease buttons
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++; // Increase quantity
                updateQuantityDisplay(); // Update the display
            }
        });

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) { // Prevent quantity from going below 1
                    quantity--; // Decrease quantity
                }
                updateQuantityDisplay(); // Update the display
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Convert price to double
                double priceDouble = Double.parseDouble(price);
                Log.d("ProductDetails", "Product ID: " + id);
                // Tạo đối tượng CartItem
                CartItem cartItem = new CartItem(id,name, priceDouble, quantity, getProImg);

                // Lấy dữ liệu giỏ hàng từ SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sharedPreferences.getString("cartItems", null);
                Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
                ArrayList<CartItem> cartItems = gson.fromJson(json, type);

                if (cartItems == null) {
                    cartItems = new ArrayList<>();
                }

                // Thêm sản phẩm mới vào giỏ hàng
                cartItems.add(cartItem);

                // Lưu lại giỏ hàng vào SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String updatedJson = gson.toJson(cartItems);
                editor.putString("cartItems", updatedJson);
                editor.apply();

                // Thông báo thành công
                Toast.makeText(ProductDetailsActivity.this, "Item added to cart successfully!", Toast.LENGTH_SHORT).show();
            }
        });

    }

        // Method to update quantity display
    private void updateQuantityDisplay() {
        quantityTextView.setText("Quantity: " + quantity); // Update the TextView with the current quantity
    }
}
