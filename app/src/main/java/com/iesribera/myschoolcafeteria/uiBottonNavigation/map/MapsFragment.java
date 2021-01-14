package com.iesribera.myschoolcafeteria.uiBottonNavigation.map;

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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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

		supportMapFragment.getMapAsync(new OnMapReadyCallback() {
			@Override public void onMapReady(GoogleMap googleMap) {
				DatabaseReference locations = FirebaseDatabase.getInstance().getReference("/cafeterias");
				googleMapCreated = googleMap;
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

				googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
					@Override public void onMapClick(LatLng latLng) {
						MarkerOptions markerOptions = new MarkerOptions();
						markerOptions.position(latLng);
						markerOptions.title(latLng.latitude + " : " + latLng.longitude);
						googleMap.clear();
						googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
						googleMap.addMarker(markerOptions);
					}
				});

				googleMap.setOnMarkerClickListener(marker -> {
					UserEvent onClickMarkerEvent = new UserEvent();
					User user = User.getInstance();
					onClickMarkerEvent.uid = user.getUid();
					onClickMarkerEvent.description =
							String.format("User %s is getting its next order in %s",
										  user.getName(), marker.getTitle());
					onClickMarkerEvent.addToDatabase();
					Toast.makeText(getContext(), "Se ha seleccionado la ubicación correctamente",
								   Toast.LENGTH_LONG).show();
					return true;
				});
				Toast.makeText(getContext(), "El pedido se ha completado correctamente," +
									   "seleccione la cafetería de recogida",
							   Toast.LENGTH_LONG).show();
			}
		});

		if (googleMapCreated != null) {
			googleMapCreated.setOnMarkerClickListener(marker -> {
				UserEvent onClickMarkerEvent = new UserEvent();
				User user = User.getInstance();
				onClickMarkerEvent.uid = user.getUid();
				onClickMarkerEvent.description =
						String.format("User %s is getting its next order in %s",
									  user.getName(), marker.getTitle());
				onClickMarkerEvent.addToDatabase();
				Toast.makeText(getContext(), "Se ha seleccionado la ubicación correctamente",
							   Toast.LENGTH_LONG).show();
				return true;
			});
		}

//		return inflater.inflate(R.layout.fragment_maps, container, false);
		return view;
	}

	public void saveEventInDatabase(Marker marker) {

	}
	/*

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		SupportMapFragment mapFragment =
				(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
		if (mapFragment != null) {
			mapFragment.getMapAsync(callback);
		}
	}

	 */

}