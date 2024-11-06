package com.doan.bitstop.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.doan.bitstop.Coupon;
import com.doan.bitstop.R;
import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private List<Coupon> couponList;

    public CouponAdapter(List<Coupon> couponList) {
        this.couponList = couponList;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);
        holder.textViewCouponId.setText("Coupon ID: " + coupon.getId());
        holder.textViewDiscount.setText("Discount: " + coupon.getDiscount());
        holder.textViewExpiration.setText("Expiration Date: " + coupon.getExpirationDate());
        holder.textViewStatus.setText("Status: " + coupon.getStatus());

        // Check the coupon status and set the background color for "available"
        if ("available".equals(coupon.getStatus())) {
            holder.textViewStatus.setBackgroundColor(Color.GREEN); // Green for available status
            holder.textViewStatus.setTextColor(Color.WHITE); // Optional: White text for better visibility
        } else {
            holder.textViewStatus.setBackgroundColor(Color.RED); // You can set another color for other statuses
            holder.textViewStatus.setTextColor(Color.WHITE); // Optional: White text for better visibility
        }
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCouponId, textViewDiscount, textViewExpiration, textViewStatus;

        public CouponViewHolder(View itemView) {
            super(itemView);
            textViewCouponId = itemView.findViewById(R.id.textViewCouponId);
            textViewDiscount = itemView.findViewById(R.id.textViewDiscount);
            textViewExpiration = itemView.findViewById(R.id.textViewExpiration);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
