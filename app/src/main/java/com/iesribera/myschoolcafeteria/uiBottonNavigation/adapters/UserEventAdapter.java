package com.iesribera.myschoolcafeteria.uiBottonNavigation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iesribera.myschoolcafeteria.R;
import com.iesribera.myschoolcafeteria.models.UserEvent;

import java.util.ArrayList;
import java.util.List;

public class UserEventAdapter extends RecyclerView.Adapter<UserEventAdapter.ViewHolder> {

	private final LayoutInflater inflater;
	private List<UserEvent> userEvents = new ArrayList<>();
	private OnItemClickListener mListener;

	public interface OnItemClickListener {
		void onItemClick(int position);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}

	public UserEventAdapter(Context context, List<UserEvent> userEvents) {
		this.inflater = LayoutInflater.from(context);
		this.userEvents = userEvents;
	}

	public void setOnClickListener(OnItemClickListener listener) {
		mListener = listener;

	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		TextView eventDescription;

		public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
			super(itemView);
			eventDescription = itemView.findViewById(R.id.userevent);
		}
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.event_list, parent, false);
		return new ViewHolder(view, mListener);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		String name = userEvents.get(position).getDescription();
		holder.eventDescription.setText(name);
	}

	@Override
	public int getItemCount() {
		return userEvents.size();
	}

}
