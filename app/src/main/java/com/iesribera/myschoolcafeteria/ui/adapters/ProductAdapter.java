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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

	private final LayoutInflater inflater;
	private List<Product> mProductList = new ArrayList<>();
	private OnItemClickListener mListener;

	public interface OnItemClickListener {
		void onItemClick(int position);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}

	public ProductAdapter(Context context, List<Product> mProductList) {
		this.inflater = LayoutInflater.from(context);
		this.mProductList = mProductList;
	}

	public void setOnClickListener(OnItemClickListener listener) {
		mListener = listener;

	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		TextView itemName, itemDescription, itemPrice, quantity;
		ImageView itemPhoto;
		ImageButton addButton, removeButton;

		public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
			super(itemView);
			itemName = itemView.findViewById(R.id.item_name);
			itemDescription = itemView.findViewById(R.id.item_description);
			itemPhoto = itemView.findViewById(R.id.item_photo);
			itemPrice = itemView.findViewById(R.id.item_price);
			quantity = itemView.findViewById(R.id.item_quantity);
			addButton = itemView.findViewById(R.id.item_add);
			removeButton = itemView.findViewById(R.id.item_remove);
		}
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.product_list, parent, false);
		return new ViewHolder(view, mListener);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		String name = mProductList.get(position).getName();
		String description = mProductList.get(position).getDescription();
		Double price = mProductList.get(position).getPrice();
		Bitmap image = mProductList.get(position).getPhoto();
		holder.itemName.setText(name);
		holder.itemDescription.setText(description);
		holder.itemPrice.setText(String.valueOf(price));
		holder.itemPhoto.setImageBitmap(image);
		holder.addButton.setOnClickListener(v -> editQuantity(holder, position, true));
		holder.removeButton.setOnClickListener(v -> editQuantity(holder, position, false));
	}

	//TODO: elena controlar que no se puedan poner cantidades negativas
	private void editQuantity(@NonNull ViewHolder holder, int position, boolean increaseQuantity) {
		int quantityUpdated = mProductList.get(position).getQuantity();
		if (increaseQuantity) {
			quantityUpdated += 1;
		} else {
			quantityUpdated -= 1;
		}
		mProductList.get(position).setQuantity(quantityUpdated);
		holder.quantity.setText(String.valueOf(quantityUpdated));
	}

	@Override public int getItemCount() {
		return mProductList.size();
	}

}
