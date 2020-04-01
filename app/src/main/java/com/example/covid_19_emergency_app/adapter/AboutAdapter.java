package com.example.covid_19_emergency_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.covid_19_emergency_app.R;
import com.example.covid_19_emergency_app.model.AboutModel;

import java.util.List;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.AboutHolder> {

    private List<AboutModel> aboutModels;
    private Context context;


    public AboutAdapter(Context context, List<AboutModel> aboutModelList) {
        aboutModels = aboutModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public AboutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.about_item,parent,false);
        return new AboutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutHolder holder, int position) {
        AboutModel currentItem = aboutModels.get(position);

        Glide.with(context).load(currentItem.getImageUrl()).into(holder.imgPost);
        holder.txtTitle.setText(currentItem.getTitle());
        holder.txtDesc.setText(currentItem.getDesc());
    }

    @Override
    public int getItemCount() {
        return aboutModels.size();
    }

    public class AboutHolder extends RecyclerView.ViewHolder {
        private ImageView imgPost;
        private TextView txtTitle;
        private TextView txtDesc;

        public AboutHolder(@NonNull View itemView) {
            super(itemView);
            imgPost = itemView.findViewById(R.id.image_post);
            txtTitle = itemView.findViewById(R.id.title_post);
            txtDesc = itemView.findViewById(R.id.des_post);
        }
    }
}
