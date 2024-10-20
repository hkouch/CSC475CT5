package com.example.ct5o1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> images_list;

    public GalleryAdapter(Context context, ArrayList<String> images_list) {
        this.context = context;
        this.images_list = images_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File image_file = new File(images_list.get(position));

        if (image_file.exists()) {
            Glide.with(context).load(image_file).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return images_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.gallery_item);
        }
    }
}
