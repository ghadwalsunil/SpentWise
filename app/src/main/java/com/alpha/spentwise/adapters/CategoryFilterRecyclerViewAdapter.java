package com.alpha.spentwise.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.spentwise.customDataObjects.FilterDetails;
import com.alpha.spentwise.R;

import org.jetbrains.annotations.NotNull;

public class CategoryFilterRecyclerViewAdapter extends RecyclerView.Adapter<CategoryFilterRecyclerViewAdapter.ViewHolder> {

    private FilterDetails filterDetails;
    private LayoutInflater layoutInflater;
    private int[] itemPositionStatus;
    private OnNoteListenerCategory mOnNoteListener;

    public CategoryFilterRecyclerViewAdapter(Context ctx, FilterDetails filterDetails, int[] itemPositionStatus, OnNoteListenerCategory onNoteListener){
        this.filterDetails = filterDetails;
        this.layoutInflater = LayoutInflater.from(ctx);
        this.itemPositionStatus = itemPositionStatus;
        this.mOnNoteListener = onNoteListener;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.util_filter_entry_single_button,parent,false);
        return new ViewHolder(view,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryFilterRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.attributeTextView.setText(filterDetails.getCategoryDetails().get(position).getCategoryName());

        if(itemPositionStatus[position] == 1) {
            holder.attributeTextView.setBackgroundResource(R.drawable.util_textview_selected_background);
            holder.attributeTextView.setTextColor(Color.WHITE);
        } else {
            holder.attributeTextView.setBackgroundResource(R.drawable.util_textview_unselected_background);
            holder.attributeTextView.setTextColor(Color.BLACK);
        }

    }

    @Override
    public int getItemCount() {
        return filterDetails.getCategoryDetails().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView attributeTextView;
        OnNoteListenerCategory onNoteListener;

        public ViewHolder(@NonNull @NotNull View itemView, OnNoteListenerCategory onNoteListener) {
            super(itemView);
            attributeTextView = itemView.findViewById(R.id.filterButton_tv);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
            int clickedItemPosition = getAdapterPosition();
            if(clickedItemPosition == 0){
                itemPositionStatus = resetFilter(itemPositionStatus);
            }
            else{
                if(itemPositionStatus[clickedItemPosition] == 0){
                    itemPositionStatus[clickedItemPosition] = 1;
                    if(itemPositionStatus[0] == 1)
                        itemPositionStatus[0] = 0;
                } else {
                    if(atLeastTwoItemSelected(itemPositionStatus))
                        itemPositionStatus[clickedItemPosition] = 0;
                }
            }
            notifyDataSetChanged();
        }
    }

    public interface OnNoteListenerCategory{
        void onNoteClick(int position);
    }

    private boolean atLeastTwoItemSelected(int[] itemSelectedPositionList){
        int sum = 0;
        for(int i = 0; i < itemSelectedPositionList.length; i++){
            sum = sum + itemSelectedPositionList[i];
            if(sum > 1)
                return true;
        }
        return false;
    }

    private static int[] resetFilter(int[] itemPositionStatus){

        itemPositionStatus[0] = 1;
        for(int i=1; i < itemPositionStatus.length; i++){
            itemPositionStatus[i] = 0;
        }

        return itemPositionStatus;

    }
}
