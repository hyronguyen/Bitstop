package com.doan.bitstop.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.bitstop.Adapter.PopularGameAdapter;
import com.doan.bitstop.PopularGamesHome;
import com.doan.bitstop.R;
import com.doan.bitstop.SpaceItemDecoration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private PopularGameAdapter myAdapter;
    private List<PopularGamesHome> allGamesList = new ArrayList<>();
    private List<PopularGamesHome> filteredGamesList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization logic can go here
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        setupRecyclerView(view); // Setup RecyclerView with data

        SearchView searchView = view.findViewById(R.id.searchView);
        setupSearchView(searchView); // Set up SearchView functionality

        return view; // Return the inflated view
    }

    private void setupSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No action needed on query submission
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterGamesList(newText);
                return true;
            }
        });
    }

    private void filterGamesList(String query) {
        filteredGamesList.clear();
        if (TextUtils.isEmpty(query)) {
            // If query is empty, show all products
            filteredGamesList.addAll(allGamesList);
        } else {
            // Filter based on the title or description of the game
            for (PopularGamesHome game : allGamesList) {
                // Ensure title and description are not null before calling toLowerCase()
                String title = game.getProTitle() != null ? game.getProTitle().toLowerCase() : "";
                String description = game.getProDescription() != null ? game.getProDescription().toLowerCase() : "";

                // Check if the query matches title or description
                if (title.contains(query.toLowerCase()) || description.contains(query.toLowerCase())) {
                    filteredGamesList.add(game);
                }
            }
        }
        myAdapter.notifyDataSetChanged();  // Notify adapter about the change
    }

    public interface FirestoreCallback {
        void onCallback(List<PopularGamesHome> popularGamesList);
    }

    private void setupRecyclerView(View view) {
        ArrayListGameFromFirebase(new FirestoreCallback() {
            @Override
            public void onCallback(List<PopularGamesHome> popularGamesList) {
                allGamesList.addAll(popularGamesList);  // Store all data
                filteredGamesList.addAll(popularGamesList);  // Initially, show all data
                myAdapter = new PopularGameAdapter(filteredGamesList, requireContext());
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

        db.collection("PRODUCTS")
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

                                // Fetch the product ID (document ID) from Firestore
                                String productId = documentSnapshot.getId();

                                // Pass productId to the constructor
                                PopularGamesHome game = new PopularGamesHome(
                                        documentSnapshot.getString("pro_category"),
                                        documentSnapshot.getString("pro_img"),
                                        documentSnapshot.getString("pro_platform"),
                                        proPrice,

                                        documentSnapshot.getString("pro_title"),
                                        documentSnapshot.getString("pro_description"),
                                        productId  // Set the product ID here
                                );

                                Log.d("Firestore", "Product ID: " + productId);  // Log the product ID for debugging
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
