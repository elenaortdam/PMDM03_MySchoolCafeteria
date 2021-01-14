package com.iesribera.myschoolcafeteria.uiBottonNavigation.fragments;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iesribera.myschoolcafeteria.R;
import com.iesribera.myschoolcafeteria.models.Order;
import com.iesribera.myschoolcafeteria.models.Product;
import com.iesribera.myschoolcafeteria.models.User;
import com.iesribera.myschoolcafeteria.models.UserEvent;
import com.iesribera.myschoolcafeteria.uiBottonNavigation.adapters.ProductAdapter;
import com.iesribera.myschoolcafeteria.uiBottonNavigation.map.MapsFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static android.content.ContentValues.TAG;

public class OrderFragment extends Fragment implements View.OnClickListener {

	private RecyclerView recyclerView;
	private ProductAdapter productAdapter;
	private FloatingActionButton orderButton;
	private final List<Product> products = new ArrayList<>();
	private final User user = User.getInstance();

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.order, container, false);

		if (user == null) {
			Toast.makeText(getContext(), "Esta zona está restringida para usuarios",
						   Toast.LENGTH_LONG).show();
			return view;
		}
		recyclerView = view.findViewById(R.id.productRecyclerView);
		TextView userName = view.findViewById(R.id.userName);
		orderButton = view.findViewById(R.id.orderButton);
		orderButton.setOnClickListener(v -> createOrder());
		userName.setText(User.getInstance().getName());
		DatabaseReference mDatabase = FirebaseDatabase.getInstance()
													  .getReference("/products");

		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mDatabase.addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {

				for (DataSnapshot productSnapshot : snapshot.getChildren()) {
					Product product = productSnapshot.getValue(Product.class);
					if (product != null) {
						downloadPhoto(product);
					}
					products.add(product);
				}
				productAdapter = new ProductAdapter(getContext(), products);
				recyclerView.setAdapter(productAdapter);
				productAdapter.notifyDataSetChanged();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			}

		});
		if (view.findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
			}

			// Create an instance of Fragment1
			MapsFragment firstFragment = new MapsFragment();

			// In case this activity was started with special instructions from an Intent,
			// pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getActivity().getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getChildFragmentManager().beginTransaction()
									 .add(R.id.fragment_container, firstFragment).commit();
		}
		return view;
	}

	@SuppressLint("DefaultLocale") public void createOrder() {
		List<Product> productsToOrder = products.stream()
												.filter(product -> product.getQuantity() > 0)
												.collect(Collectors.toList());
		Order order = new Order();
		if (productsToOrder.isEmpty()) {
			Toast.makeText(getContext(), "No se puede crear un pedido sin artículos", Toast.LENGTH_LONG).show();
			return;
		}
		order.setDetails(productsToOrder.stream()
										.map(Product::toMap)
										.collect(Collectors.toList()));
		double orderTotal = productsToOrder.stream()
										   .mapToDouble(Product::getPrice)
										   .sum();

		order.setOrderTotal((float) orderTotal);
		order.setUserName(user.getName());
		order.setUserId(user.getUid());
		order.setUserEmail(user.getEmail());
		if (order.getOrderTotal() < 0) {
			order.setOrderTotal(0f);
		}
		DatabaseReference orderReference =
				FirebaseDatabase.getInstance().getReference("/user-orders");
		orderReference.push().setValue(order);
		createOrderCreatedNotification(order);
		showMapActivity();
/*
		orderReference.runTransaction(new Transaction.Handler() {
			@NonNull
			@Override
			public Transaction.Result doTransaction(@NonNull MutableData currentData) {
				Integer currentValue = currentData.getValue(Integer.class);
				orderReference.push().setValue(order);

				if (currentValue == null) {
					currentData.setValue(1);
				} else {
					currentData.setValue(currentValue + 1);
				}
				return Transaction.success(currentData);
			}

			@Override
			public void onComplete(@Nullable DatabaseError error,
								   boolean committed, @Nullable DataSnapshot currentData) {
				if (error != null || !committed) {
					Toast.makeText(getContext(), "Ha ocurrido un error al crear el pedido",
								   Toast.LENGTH_LONG).show();
				} else {
					createOrderCreatedNotification(order);
					showMapActivity();
				}
			}
		});

 */
	}

	private void createOrderCreatedNotification(Order order) {
		User user = User.getInstance();
		UserEvent createOrder = new UserEvent();
		createOrder.uid = user.getUid();
		createOrder.description =
				String.format("Order sent! Total amount: %.2f €",
							  order.getOrderTotal());
		createOrder.addToDatabase();
	}

	private void showMapActivity() {
		/*
		Intent i = new Intent(getContext(), MapActivity.class);
		Toast.makeText(getActivity(),
					   "Escoge la cafetería de recogida", Toast.LENGTH_LONG).show();
		startActivity(i);

		 */

		Fragment fragment = new MapsFragment();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.map, fragment);
		transaction.commit();

//		FragmentManager fragmentManager=getActivity().getFragmentManager();
//		FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//		fragmentTransaction.replace(R.id.,fragment2,"tag");
//		fragmentTransaction.addToBackStack(null);
//		fragmentTransaction.commit();
	}

	@Override
	public void onClick(final View view) {
		int itemPosition = recyclerView.getChildLayoutPosition(view);
		Product item = products.get(itemPosition);
		Toast.makeText(getContext(), item.name, Toast.LENGTH_LONG).show();
	}

	private void downloadPhoto(Product p) {

		StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(p.image);
		try {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			final File localFile = File.createTempFile("PNG_" + timeStamp, ".png");
			mStorageReference.getFile(localFile)
							 .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
								 @Override
								 public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
									 //Insert the downloaded image in its right position at the ArrayList
									 String url = "gs://" + taskSnapshot.getStorage().getBucket() + "/" + taskSnapshot.getStorage().getName();
									 Log.d(TAG, "Loaded " + url);
									 for (Product p : products) {
										 if (p.image.equals(url)) {
											 p.photo = BitmapFactory.decodeFile(localFile.getAbsolutePath());
											 productAdapter.notifyDataSetChanged();
											 Log.d(TAG, "Loaded pic " + p.image + ";" + url + localFile.getAbsolutePath());

										 }
									 }
								 }

							 });
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}