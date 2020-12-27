package com.iesribera.myschoolcafeteria;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class Product {
    public String name;
    public String description;
    public String image;
    public Bitmap photo;
    public double price;
    public int quantity;

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("description", description);
        result.put("price", price);
        result.put("image", image);
        result.put("quantity",quantity);
        return result;
    }

    public Product(){

    }
    public void removeQuantity() {
        quantity--;
        if(quantity<0) quantity=0; //can't get below 0
    }

    public void addQuantity() {
        quantity++;
    }



    public Product(String name, String description, float price, int quantity) {
        this.name = name;
        this.description = description;

        this.price = price;
        this.quantity = quantity;
    }


}
