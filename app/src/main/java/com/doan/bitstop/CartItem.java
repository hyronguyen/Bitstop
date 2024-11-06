package com.doan.bitstop;



import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private String imageUrl;

    // Constructor
    public CartItem(String id, String name, double price, int quantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // Modify the getImageUrl method to return only the first URL
    public String getImageUrl() {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Split the URLs by spaces and return the first URL
            String[] urls = imageUrl.split(" ");
            if (urls.length > 0) {
                return urls[0]; // Return the first URL
            }
        }
        // Logging for debugging
        System.out.println("Image URL is null or empty: " + imageUrl);
        return null; // Return null if imageUrl is null or empty
    }

    // Setter for quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Parcelable implementation
    protected CartItem(Parcel in) {
        id = in.readString();  // Read the id field
        name = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
        imageUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);  // Write the id field
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeString(imageUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };
}
