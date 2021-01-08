package com.iesribera.myschoolcafeteria.ui.home;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iesribera.myschoolcafeteria.Product;
import com.iesribera.myschoolcafeteria.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class OrderFragment extends Fragment {

	private HomeViewModel homeViewModel;
	private final List<Product> mProductList = new ArrayList<>();
	private RecyclerView recyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager layoutManager;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		homeViewModel =
				new ViewModelProvider(this).get(HomeViewModel.class);
		View root = inflater.inflate(R.layout.order, container, false);
		final TextView textView = root.findViewById(R.id.item_name);
		homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
			@Override
			public void onChanged(@Nullable String s) {
				textView.setText(s);
			}
		});
		return root;
	}

	private void loadProductList() {
		DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

		mDatabase.child("products").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {

				for (DataSnapshot productSnapshot : snapshot.getChildren()) {
					Product product = productSnapshot.getValue(Product.class);
					downloadPhoto(product);
					mProductList.add(product);
				}

				//TODO: Update the IU
				//for example
				//notifyOnDataSetChanged() inside a RecyclerView

			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			}
		});
	}

	private void downloadPhoto(Product p) {

		StorageReference mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(p.image);
		try {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			final File localFile = File.createTempFile("PNG_" + timeStamp, ".png");
			mStorageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
					//Insert the downloaded image in its right position at the ArrayList
					String url = "gs://" + taskSnapshot.getStorage().getBucket() + "/" + taskSnapshot.getStorage().getName();
					Log.d(TAG, "Loaded " + url);
					for (Product p : mProductList) {
						if (p.image.equals(url)) {
							p.photo = BitmapFactory.decodeFile(localFile.getAbsolutePath());
							//TODO: Update the IU: For example: notifyDataSetChanged(); on a RecyclerView
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