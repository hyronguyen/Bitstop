package com.doan.bitstop;

public class PopularGamesHome {
    private String pro_category;
    private String pro_img;
    private String pro_platform;
    private Number pro_price;
    private String pro_title;
    private String pro_description;
    private String productId; // Add productId field

    // Updated constructor to include productId
    public PopularGamesHome(String pro_category, String pro_img, String pro_platform, Number pro_price, String pro_title, String pro_description, String productId) {
        this.pro_category = pro_category;
        this.pro_img = pro_img;
        this.pro_platform = pro_platform;
        this.pro_price = pro_price;
        this.pro_title = pro_title;
        this.pro_description = pro_description;
        this.productId = productId; // Initialize productId
    }

    // Getters for each field, including the productId
    public String getProTitle() {
        return pro_title;
    }

    // Modify the getProImg method to return only the first URL
    public String getProImg() {
        if (pro_img != null && !pro_img.isEmpty()) {
            // Split the URLs by spaces and return the first URL
            String[] urls = pro_img.split(" ");
            return urls[0]; // Return the first URL
        }
        return null; // Return null if pro_img is null or empty
    }


    public String getProPriceString() {
        return String.valueOf(pro_price);
    }

    public String getProCategory() {
        return pro_category;
    }

    public String getProPlatform() {
        return pro_platform;
    }

    public String getProDescription() {
        return pro_description;
    }

    public String getProductId() {
        return productId;
    }
}
