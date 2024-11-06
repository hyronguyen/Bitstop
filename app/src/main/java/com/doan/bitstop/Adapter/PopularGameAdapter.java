package com.doan.bitstop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doan.bitstop.PopularGamesHome;
import com.doan.bitstop.ProductDetailsActivity;
import com.doan.bitstop.R;

import java.util.List;

public class PopularGameAdapter extends RecyclerView.Adapter<PopularGameAdapter.MyViewHolder> {
    private List<PopularGamesHome> gamesList;
    private Context context;


    public PopularGameAdapter(List<PopularGamesHome> gamesList, Context context) {
        this.gamesList = gamesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.populargames_home, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PopularGamesHome game = gamesList.get(position);

        // Set game details
        holder.titleTextView.setText(game.getProTitle());
        holder.priceTextView.setText(game.getProPriceString());

        Glide.with(context)
                .load(game.getProImg())
                .into(holder.imageView);

        // Set click listener to navigate to ProductDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("PRODUCT_ID", game.getProductId()); // Pass the product ID
            intent.putExtra("PRODUCT_NAME", game.getProTitle());
            intent.putExtra("PRODUCT_PRICE", game.getProPriceString());
            intent.putExtra("PRODUCT_CATEGORY", game.getProCategory());
            intent.putExtra("PRODUCT_PLATFORM", game.getProPlatform());
            intent.putExtra("PRODUCT_DESCRIPTION", game.getProDescription());
            intent.putExtra("PRODUCT_IMAGE_URL", game.getProImg()); // Pass the image URL

            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView priceTextView; // Make sure this is declared
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.game_title);
            imageView = itemView.findViewById(R.id.game_cover);
            priceTextView = itemView.findViewById(R.id.game_price); // Correct initialization
        }
    }

    }


