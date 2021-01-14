package com.iesribera.myschoolcafeteria.uiBottonNavigation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iesribera.myschoolcafeteria.R;
import com.iesribera.myschoolcafeteria.models.User;
import com.iesribera.myschoolcafeteria.models.UserEvent;
import com.iesribera.myschoolcafeteria.uiBottonNavigation.adapters.UserEventAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

	private final User user = User.getInstance();
	private RecyclerView recyclerView;
	private UserEventAdapter eventAdapter;
	private final List<UserEvent> userEvents = new ArrayList<>();

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_notifications, container, false);
		if (user == null) {
			Toast.makeText(getContext(), "Esta zona está restringida para usuarios",
						   Toast.LENGTH_LONG).show();
			return view;
		}

		recyclerView = view.findViewById(R.id.eventRecyclerView);
		DatabaseReference mDatabase = FirebaseDatabase.getInstance()
													  .getReference("/events");

		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		mDatabase.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {

				for (DataSnapshot productSnapshot : snapshot.getChildren()) {
					UserEvent event = productSnapshot.getValue(UserEvent.class);
					userEvents.add(event);
				}
				eventAdapter = new UserEventAdapter(getContext(), userEvents);
				recyclerView.setAdapter(eventAdapter);
				eventAdapter.notifyDataSetChanged();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			}

		});

		return view;
	}
}