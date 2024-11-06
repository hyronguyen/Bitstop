package com.doan.bitstop.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.bitstop.Adapter.CartAdapter;
import com.doan.bitstop.CartItem;
import com.doan.bitstop.OrderDetailsActivity;
import com.doan.bitstop.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartFragment extends Fragment implements CartAdapter.OnQuantityChangeListener {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private ArrayList<CartItem> cartItems;
    private TextView totalPriceTextView;
    private Button checkoutButton, emptyCartButton;

    private static final String PREFS_NAME = "CartPrefs";
    private static final String CART_ITEMS_KEY = "cartItems";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCart);
        totalPriceTextView = view.findViewById(R.id.totalPrice);
        checkoutButton = view.findViewById(R.id.checkoutButton);
        emptyCartButton = view.findViewById(R.id.emptyCartButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        cartItems = loadCartItems(); // Load cart items from SharedPreferences

        cartAdapter = new CartAdapter(requireContext(), cartItems, this);
        recyclerView.setAdapter(cartAdapter);

        updateTotalPrice();

        checkoutButton.setOnClickListener(v -> proceedToCheckout());
        emptyCartButton.setOnClickListener(v -> emptyCart());

        return view;
    }

    private ArrayList<CartItem> loadCartItems() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(CART_ITEMS_KEY, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CartItem>>(){}.getType();
        ArrayList<CartItem> items = gson.fromJson(json, type);

        // Merge duplicate items (same product) by increasing quantity
        if (items != null) {
            Map<String, CartItem> cartMap = new HashMap<>();
            for (CartItem item : items) {
                if (cartMap.containsKey(item.getId())) {
                    CartItem existingItem = cartMap.get(item.getId());
                    existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity()); // Increase quantity
                } else {
                    cartMap.put(item.getId(), item);
                }
            }
            items = new ArrayList<>(cartMap.values()); // Convert map back to list
        } else {
            items = new ArrayList<>();
        }

        // Log each cart item and its ID
        for (CartItem item : items) {
            Log.d("CartItem", "Product ID: " + item.getId() + ", Name: " + item.getName() + ", Quantity: " + item.getQuantity());
        }

        return items;
    }
    public void saveCartItems() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartItems);
        editor.putString(CART_ITEMS_KEY, json);
        editor.apply();
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));
    }

    private void emptyCart() {
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
        saveCartItems();
    }

    private void proceedToCheckout() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String or_cid = sharedPreferences.getString("USER_ID", null);

        if (or_cid == null) {
            showToast("User ID not found. Please log in again.");
            return;
        }

        // Log the cart items before proceeding
        for (CartItem item : cartItems) {
            Log.d("CheckoutCartItem", "Product ID: " + item.getId() + ", Name: " + item.getName() + ", Quantity: " + item.getQuantity());
        }

        // Convert cartItems to JSON to pass to OrderDetailsActivity
        Gson gson = new Gson();
        String cartItemsJson = gson.toJson(cartItems);

        // Calculate the total price
        double totalPrice = calculateSubtotal();

        // Start OrderDetailsActivity and pass the cart items, user ID, and total price
        Intent intent = new Intent(requireActivity(), OrderDetailsActivity.class); // Use requireActivity()
        intent.putParcelableArrayListExtra("cartItems", cartItems);
        intent.putExtra("CART_ITEMS", cartItemsJson);
        intent.putExtra("USER_ID", or_cid);
        intent.putExtra("TOTAL_PRICE", totalPrice);

        startActivity(intent);
    }


    private double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQuantityChange() {
        updateTotalPrice();
        saveCartItems(); // Ensure cart is saved after quantity change
    }
}