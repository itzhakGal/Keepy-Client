package com.example.keepy.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepy.R;
import com.example.keepy.app.domain.UserDetailsHelperClass;
import java.util.List;

public class UserDetailsAdapter extends RecyclerView.Adapter<UserDetailsAdapter.ViewHolder> {

    private List<UserDetailsHelperClass> mData;

    public UserDetailsAdapter(List<UserDetailsHelperClass> data) {
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserDetailsHelperClass data = mData.get(position);
        holder.fullNameTextView.setText(data.getFullName());
        holder.phoneNumberTextView.setText(data.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView;
        TextView phoneNumberTextView;

        ViewHolder(View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullName);
            phoneNumberTextView = itemView.findViewById(R.id.registerPhoneNumber);
        }
    }
}