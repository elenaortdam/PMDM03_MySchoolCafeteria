package com.iesribera.myschoolcafeteria;

import java.util.HashMap;
import java.util.Map;

public class Order {

    public String userName;
    public String userClass;
    public String userEmail;
    public String userId;
    public String pickupTime;
    public float orderTotal;

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", userId);
        result.put("name", userName);
        result.put("userEmail", userEmail);
        result.put("total", orderTotal);
        return result;
    }
}
