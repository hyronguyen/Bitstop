package com.doan.bitstop.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import com.doan.bitstop.Adapter.CouponAdapter;
import com.doan.bitstop.Coupon;
import com.doan.bitstop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CouponFragment extends Fragment {
    private RecyclerView recyclerView;
    private CouponAdapter couponAdapter;
    private List<Coupon> couponList;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public CouponFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Log.d("CouponFragment", "onCreate: FirebaseAuth and Firestore initialized");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coupon, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        setupRecyclerView(view);
        Log.d("CouponFragment", "onCreateView: RecyclerView set up completed");
        return view;
    }

    public interface FirestoreCallback {
        void onCallback(List<Coupon> couponList);
    }

    private void setupRecyclerView(View view) {
        couponList = new ArrayList<>();
        couponAdapter = new CouponAdapter(couponList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(couponAdapter);
        Log.d("CouponFragment", "setupRecyclerView: RecyclerView initialized with empty coupon list");

        loadCoupons(new FirestoreCallback() {
            @Override
            public void onCallback(List<Coupon> coupons) {
                Log.d("CouponFragment", "onCallback: Coupons fetched: " + coupons.size());
                couponList.clear();
                couponList.addAll(coupons);
                couponAdapter.notifyDataSetChanged();
                Log.d("CouponFragment", "onCallback: Adapter notified of data change");
            }
        });

        // Swipe-to-delete setup
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // No need to handle move operation
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // When a coupon is swiped to the left
                int position = viewHolder.getAdapterPosition();
                Coupon coupon = couponList.get(position);

                // Show a confirmation dialog to delete the coupon
                new AlertDialog.Builder(getContext())
                        .setMessage("Do you want to delete this coupon?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteCoupon(coupon.getId(), position))
                        .setNegativeButton("Cancel", (dialog, which) -> couponAdapter.notifyItemChanged(position))
                        .show();
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void loadCoupons(FirestoreCallback firestoreCallback) {
        // Retrieve USER_ID from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginPrefs", getContext().MODE_PRIVATE);
        String savedUserId = sharedPreferences.getString("USER_ID", null);
        Log.d("CouponFragment", "loadCoupons: Saved USER_ID from SharedPreferences: " + savedUserId);

        List<Coupon> coupons = new ArrayList<>();

        db.collection("COUPON")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Log.d("CouponFragment", "onSuccess: Coupons found: " + queryDocumentSnapshots.size());
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                String userId = document.getString("cou_user");

                                if (userId != null && userId.equals(savedUserId)) {
                                    String couponId = document.getId();
                                    String discount = document.getString("cou_discount");

                                    // Handle the 'cou_exp' field as a Timestamp
                                    Timestamp expirationTimestamp = document.getTimestamp("cou_exp");
                                    String expiration = null;
                                    if (expirationTimestamp != null) {
                                        expiration = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                                .format(expirationTimestamp.toDate());
                                    }

                                    String status = document.getString("cou_status");

                                    Log.d("CouponFragment", "onSuccess: Coupon data: ID=" + couponId + ", Discount=" + discount
                                            + ", Expiration=" + expiration + ", Status=" + status);

                                    coupons.add(new Coupon(couponId, discount, expiration, status));
                                } else {
                                    Log.d("CouponFragment", "onSuccess: Coupon not for current user (userId=" + userId + ")");
                                }
                            }
                        } else {
                            Log.d("CouponFragment", "onSuccess: No coupons found");
                        }
                        firestoreCallback.onCallback(coupons);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("CouponFragment", "onFailure: Error fetching coupons", e);
                        firestoreCallback.onCallback(coupons);
                    }
                });
    }

    private void deleteCoupon(String couponId, int position) {
        // Delete coupon from Firestore
        db.collection("COUPON")
                .document(couponId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove the coupon from the list and notify the adapter
                    couponList.remove(position);
                    couponAdapter.notifyItemRemoved(position);
                    Log.d("CouponFragment", "Coupon deleted successfully");
                })
                .addOnFailureListener(e -> Log.e("CouponFragment", "Error deleting coupon", e));
    }
}
