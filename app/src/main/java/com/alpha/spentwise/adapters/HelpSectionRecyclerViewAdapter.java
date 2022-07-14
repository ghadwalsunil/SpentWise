package com.alpha.spentwise.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.spentwise.R;

import org.jetbrains.annotations.NotNull;

public class HelpSectionRecyclerViewAdapter extends RecyclerView.Adapter<HelpSectionRecyclerViewAdapter.ViewHolder> {

    private String[] faqQuestion;
    private String[] faqAnswer;
    private LayoutInflater layoutInflater;
    private OnNoteListener mOnNoteListener;
    private int selectedPosition;

    public HelpSectionRecyclerViewAdapter(Context ctx, String[] faqQuestion, String[] faqAnswer, OnNoteListener onNoteListener, int selectedPosition){
        this.faqQuestion = faqQuestion;
        this.faqAnswer = faqAnswer;
        this.layoutInflater = LayoutInflater.from(ctx);
        this.mOnNoteListener = onNoteListener;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.util_help_single_item,parent,false);
        return new ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HelpSectionRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.faqQuestionTextView.setText(faqQuestion[position]);
        holder.faqAnswerTextView.setText(faqAnswer[position]);
        if(position == selectedPosition){
            if(holder.faqAnswerTextView.getVisibility() == View.VISIBLE){
                holder.faqAnswerTextView.setVisibility(View.GONE);
                holder.faqQuestionTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_arrow_drop_down_24,0);
            } else {
                holder.faqAnswerTextView.setVisibility(View.VISIBLE);
                holder.faqQuestionTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_arrow_drop_up_24,0);
            }
        } else {
            holder.faqAnswerTextView.setVisibility(View.GONE);
            holder.faqQuestionTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_arrow_drop_down_24,0);
        }

    }

    @Override
    public int getItemCount() {
        return faqQuestion.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView faqQuestionTextView, faqAnswerTextView;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull @NotNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            faqQuestionTextView = itemView.findViewById(R.id.utilHelp_tv_title);
            faqAnswerTextView = itemView.findViewById(R.id.utilHelp_tv_answer);
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
