package com.doan.bitstop.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.bitstop.Adapter.PopularGameAdapter;
import com.doan.bitstop.PopularGamesHome;
import com.doan.bitstop.R;
import com.doan.bitstop.SpaceItemDecoration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;

    // Thay đổi quyền truy cập của onCreate thành public hoặc bỏ qua nó nếu không cần thiết
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Chỉ dùng để khởi tạo các logic cần thiết, không khởi tạo view
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        setupRecyclerView(view); // Sử dụng view để gọi findViewById



        return view; // Trả về view đã inflate
    }

    public interface FirestoreCallback {
        void onCallback(List<PopularGamesHome> popularGamesList);
    }

    private void setupRecyclerView(View view) {
        ArrayListGameFromFirebase(new FirestoreCallback() {
            @Override
            public void onCallback(List<PopularGamesHome> popularGamesList) {
                RecyclerView recyclerView = view.findViewById(R.id.recycler_view); // Dùng view để findViewById

                // Set the adapter
                PopularGameAdapter myAdapter = new PopularGameAdapter(popularGamesList, requireContext());
                recyclerView.setAdapter(myAdapter);

                // Set LayoutManager with 2 columns
                int numberOfColumns = 2; // Set the number of columns to 2
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
                recyclerView.setLayoutManager(gridLayoutManager);

                // Add item decoration for spacing between items
                int space = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
                recyclerView.addItemDecoration(new SpaceItemDecoration(space));
            }
        });
    }


    private void ArrayListGameFromFirebase(FirestoreCallback firestoreCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<PopularGamesHome> popularGamesList = new ArrayList<>();

        // Set the limit for the number of products to fetch
        int limit = 4; // Change this to the number of products you want to display

        db.collection("PRODUCTS")
                .limit(limit) // Add the limit here
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                Number proPrice = documentSnapshot.getDouble("pro_price");
                                if (proPrice == null) {
                                    proPrice = 0;
                                }
                                String productId = documentSnapshot.getId();
                                // Pass productId to the constructor
                                PopularGamesHome game = new PopularGamesHome(
                                        documentSnapshot.getString("pro_category"),
                                        documentSnapshot.getString("pro_img"),
                                        documentSnapshot.getString("pro_platform"),
                                        proPrice,
                                        documentSnapshot.getString("pro_title"),
                                        documentSnapshot.getString("pro_description"),  // Description was missing
                                        productId  // Set the product ID here
                                );
                                Log.d("abc", documentSnapshot.getString("pro_img"));
                                popularGamesList.add(game);
                            }
                        } else {
                            Log.d("Firestore", "No data found!");
                        }
                        firestoreCallback.onCallback(popularGamesList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error fetching data", e);
                        firestoreCallback.onCallback(popularGamesList);
                    }
                });
    }




}
