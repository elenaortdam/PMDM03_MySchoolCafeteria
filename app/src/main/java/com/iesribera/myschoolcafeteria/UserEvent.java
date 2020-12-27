package com.iesribera.myschoolcafeteria;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserEvent {
	public String Uid;
	public String description;

	public UserEvent(String uid, String description) {
		Uid = uid;
		this.description = description;
	}

	public UserEvent() {

	}

	public void addToDatabase() {
		DatabaseReference database = FirebaseDatabase.getInstance().getReference();
		String key = database.child("events").push().getKey();
		Map<String, Object> childUpdates = new HashMap<>();
		database.child("events/" + key).setValue(this);
	}
}
