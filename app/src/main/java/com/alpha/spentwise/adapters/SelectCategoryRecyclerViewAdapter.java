package com.alpha.spentwise.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.spentwise.customDataObjects.CategoryDetails;
import com.alpha.spentwise.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SelectCategoryRecyclerViewAdapter extends RecyclerView.Adapter<SelectCategoryRecyclerViewAdapter.categoryViewHolder> {


    private List<CategoryDetails> categoryList;
    private Activity activity;
    private OnNoteListener mOnNoteListener;
    private int selectedPosition;

    public SelectCategoryRecyclerViewAdapter(List<CategoryDetails> categoryList, Activity activity, OnNoteListener onNoteListener, int selectedPosition) {
        this.categoryList = categoryList;
        this.activity = activity;
        this.mOnNoteListener = onNoteListener;
        this.selectedPosition = selectedPosition;
    }

    @NotNull
    @Override
    public categoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.util_single_image_with_title_small, parent, false);
        categoryViewHolder viewHolder = new categoryViewHolder(view, mOnNoteListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull categoryViewHolder holder, int position) {

        CategoryDetails category = categoryList.get(position);

        int imageID = activity.getResources().getIdentifier(category.getCategoryImageName(),"drawable", activity.getPackageName());

        holder.imageView.setImageResource(imageID);
        holder.textView.setText(category.getCategoryName());

        if(position == selectedPosition){
            holder.imageView.setBackgroundColor(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
            holder.textView.setBackgroundColor(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
            holder.imageView.setColorFilter(Color.WHITE);
            holder.textView.setTextColor(Color.WHITE);
        } else {
            holder.imageView.setBackgroundColor(Color.WHITE);
            holder.textView.setBackgroundColor(Color.WHITE);
            holder.imageView.setColorFilter(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
            holder.textView.setTextColor(activity.getResources().getColor(R.color.sea_green, activity.getTheme()));
        }

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class categoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView;
        OnNoteListener onNoteListener;

        public categoryViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.selectCategoryRV_iv_categoryImage);
            textView = itemView.findViewById(R.id.selectCategoryRV_tv_categoryName);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {

            onNoteListener.onNoteClick(getAdapterPosition());
            selectedPosition = getAdapterPosition();
            notifyDataSetChanged();

        }
    }


    public interface OnNoteListener{
        void onNoteClick(int position);
    }


}
