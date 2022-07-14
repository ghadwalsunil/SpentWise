package com.alpha.spentwise.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.CategoryBudgetDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.dataManager.Constants;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;

public class CategoryBudgetProgressRecyclerViewAdapter extends RecyclerView.Adapter<CategoryBudgetProgressRecyclerViewAdapter.ViewHolder> {

    private List<CategoryBudgetDetails> categoryBudgetDetailsList;
    private LayoutInflater layoutInflater;
    private CurrencyDetails selectedCurrencyDetails;
    private NumberFormat myFormat = Constants.myFormat;
    private Currency cur;
    private Context ctx;
    private Activity activity;

    public CategoryBudgetProgressRecyclerViewAdapter(Context ctx, List<CategoryBudgetDetails> categoryBudgetDetailsList, CurrencyDetails selectedCurrencyDetails, Activity activity){
        this.categoryBudgetDetailsList = categoryBudgetDetailsList;
        this.layoutInflater = LayoutInflater.from(ctx);
        this.selectedCurrencyDetails = selectedCurrencyDetails;
        this.ctx = ctx;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryBudgetProgressRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.util_single_item_progressbar,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryBudgetProgressRecyclerViewAdapter.ViewHolder holder, int position) {

        CategoryBudgetDetails categoryBudgetDetails = categoryBudgetDetailsList.get(position);

        int percent = (int)(((float)categoryBudgetDetails.getCategoryExpenseThisMonth()/(float)categoryBudgetDetails.getCategoryBudget()) * 100);

        String categoryString = categoryBudgetDetails.getCategoryName();
        String amountString = cur.getSymbol() + " " + categoryBudgetDetails.getCategoryExpenseThisMonth() + " /";
        String budgetString = cur.getSymbol() + " " + categoryBudgetDetails.getCategoryBudget();

        if(percent >= 90){
            holder.progressBar.setProgressDrawable(ContextCompat.getDrawable(ctx,R.drawable.util_circle_progress_indicator_bad));
            holder.amountTextView.setTextColor(ctx.getResources().getColor(R.color.red, activity.getTheme()));
            holder.budgetTextView.setTextColor(ctx.getResources().getColor(R.color.red, activity.getTheme()));
        } else {
            holder.progressBar.setProgressDrawable(ContextCompat.getDrawable(ctx,R.drawable.util_circle_progress_indicator_good));
            holder.amountTextView.setTextColor(ctx.getResources().getColor(R.color.lime_green, activity.getTheme()));
            holder.budgetTextView.setTextColor(ctx.getResources().getColor(R.color.lime_green, activity.getTheme()));
        }
        if(percent <=100){
            holder.progressBar.setProgress(percent);
        } else {
            holder.progressBar.setProgress(100);
        }
        holder.titleTextView.setText(categoryString);
        holder.amountTextView.setText(amountString);
        holder.budgetTextView.setText(budgetString);
    }

    @Override
    public int getItemCount() {
        return categoryBudgetDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ProgressBar progressBar;
        TextView titleTextView, amountTextView, budgetTextView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.utilProgressBar_pBar);
            titleTextView = itemView.findViewById(R.id.utilProgressBar_tv_title);
            amountTextView = itemView.findViewById(R.id.utilProgressBar_tv_amount);
            budgetTextView = itemView.findViewById(R.id.utilProgressBar_tv_budget);
            cur = Currency.getInstance(selectedCurrencyDetails.getId());
        }
    }
}
