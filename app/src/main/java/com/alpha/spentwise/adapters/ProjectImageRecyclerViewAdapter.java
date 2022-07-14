package com.alpha.spentwise.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.spentwise.R;

import java.util.List;

public class ProjectImageRecyclerViewAdapter extends RecyclerView.Adapter<ProjectImageRecyclerViewAdapter.ViewHolder> {

    private List<Integer> projectImages;
    private LayoutInflater layoutInflater;
    private OnNoteListener mOnNoteListener;
    private Activity activity;

    public ProjectImageRecyclerViewAdapter(Context ctx, List<Integer> projectImages, Activity activity, OnNoteListener onNoteListener){
        this.projectImages = projectImages;
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
    public void onBindViewHolder(@NonNull ProjectImageRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.projectImageView.setImageResource(projectImages.get(position));
        holder.projectImageView.setColorFilter(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
    }

    @Override
    public int getItemCount() {
        return projectImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView projectImageView;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            projectImageView = itemView.findViewById(R.id.rv_imageView);
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
