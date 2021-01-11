package com.iesribera.myschoolcafeteria.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iesribera.myschoolcafeteria.Product;
import com.iesribera.myschoolcafeteria.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements View.OnClickListener {

	LayoutInflater inflater;
	List<Product> mProductList = new ArrayList<>();
	private static View.OnClickListener clickListener;

	private View.OnClickListener listener;

	public ProductAdapter(Context context, List<Product> mProductList) {
		this.inflater = LayoutInflater.from(context);
		this.mProductList = mProductList;
	}

	@Override
	public void onClick(View v) {
		if (listener != null) {
			listener.onClick(v);
		}
	}

	public void setOnClickListener(View.OnClickListener listener) {
		this.listener = listener;

	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		TextView itemName, itemDescription, itemPrice;
		ImageView itemPhoto;
		ImageButton addButton, removeButton;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			itemName = itemView.findViewById(R.id.item_name);
			itemDescription = itemView.findViewById(R.id.item_description);
			itemPhoto = itemView.findViewById(R.id.item_photo);
			itemPrice = itemView.findViewById(R.id.item_price);
			addButton = itemView.findViewById(R.id.item_add);
		}
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.product_list, parent, false);
		view.setOnClickListener(listener);
		return new ViewHolder(view);
	}

	@Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		String name = mProductList.get(position).getName();
		String description = mProductList.get(position).getDescription();
		Double price = mProductList.get(position).getPrice();
		Bitmap image = mProductList.get(position).getPhoto();
		holder.itemName.setText(name);
		holder.itemDescription.setText(description);
		holder.itemPrice.setText(String.valueOf(price));
		holder.itemPhoto.setImageBitmap(image);
	}

	@Override public int getItemCount() {
		return mProductList.size();
	}

}
