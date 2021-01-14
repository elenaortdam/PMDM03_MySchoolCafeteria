package com.iesribera.myschoolcafeteria.uiBottonNavigation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iesribera.myschoolcafeteria.R;
import com.iesribera.myschoolcafeteria.models.Cafeteria;
import com.iesribera.myschoolcafeteria.models.User;
import com.iesribera.myschoolcafeteria.models.UserEvent;

public class MapsFragment extends Fragment {

	GoogleMap googleMapCreated;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_maps, container, false);
		SupportMapFragment supportMapFragment = (SupportMapFragment)
				getChildFragmentManager().findFragmentById(R.id.map);
		User user = User.getInstance();
		if (user == null) {
			Toast.makeText(getContext(), "Acceso restringido solo para usuarios",
						   Toast.LENGTH_LONG).show();

		} else {
			loadMap(supportMapFragment);

		}

		return view;
	}

	private void loadMap(SupportMapFragment supportMapFragment) {
		supportMapFragment.getMapAsync(googleMap -> {
			DatabaseReference locations = FirebaseDatabase.getInstance().getReference("/cafeterias");
			googleMapCreated = googleMap;
			addDefaultLocations(googleMap, locations);

			googleMap.setOnMarkerClickListener(marker -> {
				UserEvent onClickMarkerEvent = new UserEvent();
				User user1 = User.getInstance();
				onClickMarkerEvent.uid = user1.getUid();
				onClickMarkerEvent.description =
						String.format("User %s is getting its next order in %s",
									  user1.getName(), marker.getTitle());
				onClickMarkerEvent.addToDatabase();
				Toast.makeText(getContext(), "Se ha seleccionado la ubicación correctamente",
							   Toast.LENGTH_LONG).show();
				return true;
			});

			Toast.makeText(getContext(), "El pedido se ha completado correctamente," +
								   "seleccione la cafetería de recogida",
						   Toast.LENGTH_LONG).show();
		});
	}

	private void addDefaultLocations(GoogleMap googleMap, DatabaseReference locations) {
		locations.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override public void onDataChange(@NonNull DataSnapshot snapshot) {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				PolylineOptions poly = new PolylineOptions();
				for (DataSnapshot d : snapshot.getChildren()) {
					Cafeteria cafeteria = d.getValue(Cafeteria.class);
					LatLng l = new LatLng(cafeteria.latitude, cafeteria.longitude);
					googleMap.addMarker(new MarkerOptions().position(l).title(cafeteria.name)
														   .snippet("Ocupación máxima:" +
																			cafeteria.occupation + "%"))
							 .setIcon(BitmapDescriptorFactory.defaultMarker());
					builder.include(l);
					poly.add(l);

				}
				googleMap.addPolyline(poly);

				LatLngBounds bounds = builder.build();
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
				googleMap.animateCamera(cu);

			}

			@Override public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

}