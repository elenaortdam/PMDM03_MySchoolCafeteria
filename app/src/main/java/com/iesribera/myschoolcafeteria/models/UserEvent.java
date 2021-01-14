package com.iesribera.myschoolcafeteria.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserEvent {
	public String uid;
	public String description;

	public UserEvent(String uid, String description) {
		this.uid = uid;
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

	public String getUid() {
		return uid;
	}

	public String getDescription() {
		return description;
	}
}
