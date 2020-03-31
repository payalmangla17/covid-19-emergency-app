package com.example.covid_19_emergency_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_19_emergency_app.R;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.AboutHolder> {
    @NonNull
    @Override
    public AboutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.about_item,parent,false);
        return new AboutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class AboutHolder extends RecyclerView.ViewHolder {
        public AboutHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
