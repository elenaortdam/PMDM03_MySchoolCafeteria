package com.iesribera.myschoolcafeteria.ui.order;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.iesribera.myschoolcafeteria.User;
import com.iesribera.myschoolcafeteria.ui.adapters.ProductAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class OrderFragment extends Fragment implements View.OnClickListener {

	private RecyclerView recyclerView;

	private ProductAdapter productAdapter;
	ImageButton addButton;
	ImageButton removeButton;
	private final List<Product> products = new ArrayList<>();

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.order, container, false);
		recyclerView = view.findViewById(R.id.recyclerview);
		View productView = inflater.inflate(R.layout.product_list, container, false);
		addButton = productView.findViewById(R.id.item_add);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				v.getTag();
			}
		});

		TextView userName = view.findViewById(R.id.userName);
		userName.setText(User.getInstance().getName());
		DatabaseReference mDatabase = FirebaseDatabase.getInstance()
													  .getReference("/products");
//		recyclerView.setHasFixedSize(true);
		recyclerView.setNestedScrollingEnabled(false);

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
		TextView quantity = view.findViewById(R.id.item_quantity);
//		productAdapter.setOnItemClickListener(this::changeItem);

		return view;
	}

	public void changeItem(int position) {
		products.get(position).setQuantity(position);
		productAdapter.notifyItemChanged(position);
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