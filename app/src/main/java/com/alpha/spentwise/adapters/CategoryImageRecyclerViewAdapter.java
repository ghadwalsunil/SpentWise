package com.alpha.spentwise.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.spentwise.R;

import java.util.List;

public class CategoryImageRecyclerViewAdapter extends RecyclerView.Adapter<CategoryImageRecyclerViewAdapter.ViewHolder> {

    private List<Integer> images;
    private LayoutInflater layoutInflater;
    private OnNoteListener mOnNoteListener;
    private Activity activity;

    public CategoryImageRecyclerViewAdapter(Context ctx, List<Integer> images, Activity activity, OnNoteListener onNoteListener){
        this.images = images;
        this.layoutInflater = LayoutInflater.from(ctx);
        this.mOnNoteListener = onNoteListener;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.util_single_image,parent,false);
        return new ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryImageRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.categoryImageButton.setImageResource(images.get(position));
        holder.categoryImageButton.setBackgroundColor(Color.WHITE);
        holder.categoryImageButton.setColorFilter(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView categoryImageButton;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            categoryImageButton = itemView.findViewById(R.id.rv_imageView);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
