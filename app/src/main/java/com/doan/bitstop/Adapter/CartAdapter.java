package com.doan.bitstop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doan.bitstop.CartItem;
import com.doan.bitstop.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private ArrayList<CartItem> cartItems;
    private OnQuantityChangeListener quantityChangeListener;

    public interface OnQuantityChangeListener {
        void onQuantityChange();
    }

    public CartAdapter(Context context, ArrayList<CartItem> cartItems, OnQuantityChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.quantityChangeListener = listener; // Initialize listener
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_items, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Set data to views
        holder.productName.setText(item.getName());
        holder.productPrice.setText("Price: $" + item.getPrice());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));

        // Load image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.background)
                .error(R.drawable.button_square)
                .into(holder.productImage);

        // Set click listeners for increasing and decreasing quantity
        holder.increaseButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);  // Increase quantity
            holder.quantityTextView.setText(String.valueOf(item.getQuantity()));  // Update display
            quantityChangeListener.onQuantityChange(); // Notify change
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);  // Decrease quantity
                holder.quantityTextView.setText(String.valueOf(item.getQuantity()));  // Update display
                quantityChangeListener.onQuantityChange(); // Notify change
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, quantityTextView;
        ImageView productImage;
        Button increaseButton, decreaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityTextView = itemView.findViewById(R.id.quantity);
            productImage = itemView.findViewById(R.id.productImage);
            increaseButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseButton = itemView.findViewById(R.id.decreaseQuantityButton);
        }
    }


}