package com.example.dailyselfi;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private ArrayList<PhotoModel> photoList;
    private Context context;
    private OnPhotoClickListener photoClickListener;

    public ArrayList<PhotoModel> getPhotoList() {
        return photoList;
    }

    public OnDeletePhotosListener getDeletePhotosListener() {
        return deletePhotosListener;
    }

    public OnPhotoClickListener getPhotoClickListener() {
        return photoClickListener;
    }

    public Context getContext() {
        return context;
    }

    public OnDeletePhotosListener deletePhotosListener;

    public PhotoAdapter(ArrayList<PhotoModel> photoList, Context context,
                        OnPhotoClickListener photoClickListener, OnDeletePhotosListener deletePhotosListener) {
        this.photoList = photoList;
        this.context = context;
        this.photoClickListener = photoClickListener;
        this.deletePhotosListener = deletePhotosListener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        PhotoModel photo = photoList.get(position);

        Glide.with(context).load(photo.getPath()).into(holder.imageView);
        holder.textViewDateTime.setText(photo.getDateTime());

        // Xử lý khi nhấn vào item
        holder.itemView.setOnClickListener(v -> photoClickListener.onPhotoClick(photo));

        // Xử lý checkbox
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> photo.setSelected(isChecked));

        // Xử lý khi nhấn nút "Xem chi tiết"
        holder.btnViewDetails.setOnClickListener(v -> photoClickListener.onPhotoClick(photo));
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(PhotoModel photo);
    }

    public interface OnDeletePhotosListener {
        void onDeletePhotos(ArrayList<PhotoModel> selectedPhotos);
    }

    public ArrayList<PhotoModel> getAllSelected() {
        return (ArrayList<PhotoModel>) this.photoList.stream().filter(pt -> pt.isSelected()).collect(Collectors.toList());
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewDateTime;
        CheckBox checkBox;
        Button btnViewDetails;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            checkBox = itemView.findViewById(R.id.checkBox);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}