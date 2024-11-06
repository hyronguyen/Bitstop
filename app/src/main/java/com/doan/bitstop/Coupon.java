package com.doan.bitstop;

public class Coupon {
    private String id;
    private String discount;
    private String expirationDate;
    private String status;

    public Coupon(String id, String discount, String expirationDate, String status) {
        this.id = id;
        this.discount = discount;
        this.expirationDate = expirationDate;
        this.status = status;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDiscount() {
        return discount;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getStatus() {
        return status;
    }
}
