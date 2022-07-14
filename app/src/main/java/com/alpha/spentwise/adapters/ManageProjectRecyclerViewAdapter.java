package com.alpha.spentwise.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.ProjectDetails;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ManageProjectRecyclerViewAdapter extends RecyclerView.Adapter<ManageProjectRecyclerViewAdapter.ViewHolder> {

    private List<ProjectDetails> projectList = new ArrayList<>();
    private LayoutInflater inflater;
    private Activity activity;
    private OnNoteListener mOnNoteListener;
    private int selectedProjectPosition;

    public ManageProjectRecyclerViewAdapter(Context ctx, List<ProjectDetails> projectList, Activity activity, OnNoteListener onNoteListener, int selectedProjectPosition) {
        this.projectList = projectList;
        this.inflater = LayoutInflater.from(ctx);
        this.activity = activity;
        this.mOnNoteListener = onNoteListener;
        this.selectedProjectPosition = selectedProjectPosition;
    }

    @NonNull
    @NotNull
    @Override
    public ManageProjectRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.util_single_image_with_title_large,parent,false);
        return new ViewHolder(view,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ManageProjectRecyclerViewAdapter.ViewHolder holder, int position) {

        ProjectDetails projectDetails = projectList.get(position);
        int imageID = activity.getResources().getIdentifier(projectDetails.getProjectImageName(),"drawable", activity.getPackageName());
        holder.projectImageView.setImageResource(imageID);
        holder.projectNameTextView.setText(projectDetails.getProjectName());

        if(position == (projectList.size() - 1)){
            //holder.projectCardView.setBackgroundColor(Color.WHITE);
            holder.projectImageView.setBackgroundColor(Color.WHITE);
            holder.projectNameTextView.setBackgroundColor(Color.WHITE);
            holder.projectImageView.setColorFilter(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
            holder.projectNameTextView.setTextColor(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
        } else if(position == selectedProjectPosition){
            //holder.projectCardView.setBackgroundColor(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
            holder.projectImageView.setBackgroundColor(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
            holder.projectNameTextView.setBackgroundColor(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
            holder.projectImageView.setColorFilter(Color.WHITE);
            holder.projectNameTextView.setTextColor(Color.WHITE);
        } else {
            //holder.projectCardView.setBackgroundColor(Color.WHITE);
            holder.projectImageView.setBackgroundColor(Color.WHITE);
            holder.projectNameTextView.setBackgroundColor(Color.WHITE);
            holder.projectImageView.setColorFilter(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
            holder.projectNameTextView.setTextColor(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView projectImageView;
        TextView projectNameTextView;
        OnNoteListener onNoteListener;
        CardView projectCardView;

        public ViewHolder(@NonNull @NotNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            projectImageView = itemView.findViewById(R.id.rv_manageProject_projectImage);
            projectNameTextView = itemView.findViewById(R.id.rv_manageProject_projectName);
            projectCardView = itemView.findViewById(R.id.rv_manageProject_cardView);
            itemView.setOnClickListener(this);
            this.onNoteListener = onNoteListener;

        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
            selectedProjectPosition = getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }


}
