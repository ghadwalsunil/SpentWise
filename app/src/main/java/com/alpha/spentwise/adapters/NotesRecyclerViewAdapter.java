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
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.CategoryBudgetDetails;

import org.jetbrains.annotations.NotNull;

import java.util.Currency;
import java.util.List;

import static com.alpha.spentwise.dataManager.Constants.myFormat;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private List<CategoryBudgetDetails> categoryBudgetDetailsList;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private int currentDate, daysInMonth;
    private Currency cur;

    public NotesRecyclerViewAdapter(Context ctx, Activity activity, List<CategoryBudgetDetails> categoryBudgetDetailsList, int currentDate, int daysInMonth, Currency cur){
        this.layoutInflater = LayoutInflater.from(ctx);
        this.categoryBudgetDetailsList = categoryBudgetDetailsList;
        this.activity = activity;
        this.currentDate = currentDate;
        this.daysInMonth = daysInMonth;
        this.cur = cur;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.util_notes_single_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NotesRecyclerViewAdapter.ViewHolder holder, int position) {

        CategoryBudgetDetails categoryBudgetDetails = categoryBudgetDetailsList.get(position);

        int imageID = activity.getResources().getIdentifier(categoryBudgetDetails.getCategoryImageName(),"drawable", activity.getPackageName());
        holder.categoryIconImageView.setImageResource(imageID);
        holder.categoryIconImageView.setColorFilter(activity.getResources().getColor(R.color.app_primary_color,activity.getTheme()));
        holder.categoryNameTextView.setText(categoryBudgetDetails.getCategoryName());

        int estimatedSpending = Math.round(((float)categoryBudgetDetails.getCategoryExpenseThisMonth() / (float)(currentDate % 100)) * daysInMonth);
        int dayOfExceedingSpending = Math.round(((float)categoryBudgetDetails.getCategoryBudget()) / ((float)categoryBudgetDetails.getCategoryExpenseThisMonth() / (float)(currentDate % 100)));

        String projectStatus = "";
        if(categoryBudgetDetails.getCategoryExpenseThisMonth() >= categoryBudgetDetails.getCategoryBudget()){
            projectStatus = "You have already exhausted your monthly budget";
            holder.categoryEstimatedTextView.setTextColor(Color.RED);
            holder.categoryStatusTextView.setTextColor(Color.RED);
            holder.categorySpentTextView.setTextColor(Color.RED);
        } else if(estimatedSpending > categoryBudgetDetails.getCategoryBudget()){
            projectStatus = "Slow Down! At the current rate, you'll exhaust your budget by day " + dayOfExceedingSpending + " of this month";
            holder.categoryEstimatedTextView.setTextColor(activity.getResources().getColor(R.color.bright_orange,activity.getTheme()));
            holder.categoryStatusTextView.setTextColor(activity.getResources().getColor(R.color.bright_orange,activity.getTheme()));
            holder.categorySpentTextView.setTextColor(activity.getResources().getColor(R.color.bright_orange,activity.getTheme()));
        } else {
            projectStatus = "Well done! Your spending is on track with your budget this month";
            holder.categoryEstimatedTextView.setTextColor(activity.getResources().getColor(R.color.lime_green,activity.getTheme()));
            holder.categoryStatusTextView.setTextColor(activity.getResources().getColor(R.color.lime_green,activity.getTheme()));
            holder.categorySpentTextView.setTextColor(activity.getResources().getColor(R.color.lime_green,activity.getTheme()));
        }
        holder.categoryStatusTextView.setText(projectStatus);
        holder.categoryBudgetTextView.setTextColor(activity.getResources().getColor(R.color.lime_green,activity.getTheme()));

        String estimatedString = "", budgetString = "", spentString = "";
        if(cur != null){
            estimatedString = cur.getSymbol() + " " + myFormat.format(estimatedSpending);
            budgetString = cur.getSymbol() + " " + myFormat.format(categoryBudgetDetails.getCategoryBudget());
            spentString = cur.getSymbol() + " " + myFormat.format(categoryBudgetDetails.getCategoryExpenseThisMonth());
        }
        holder.categoryEstimatedTextView.setText(estimatedString);
        holder.categoryBudgetTextView.setText(budgetString);
        holder.categorySpentTextView.setText(spentString);
    }

    @Override
    public int getItemCount() {
        return categoryBudgetDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView categoryIconImageView;
        private TextView categoryNameTextView, categoryStatusTextView, categoryEstimatedTextView, categoryBudgetTextView, categorySpentTextView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            categoryIconImageView = itemView.findViewById(R.id.utilNotes_iv_categoryIcon);
            categoryNameTextView = itemView.findViewById(R.id.utilNotes_tv_categoryName);
            categoryStatusTextView = itemView.findViewById(R.id.utilNotes_tv_statusDesc);
            categoryEstimatedTextView = itemView.findViewById(R.id.utilNotes_tv_estimatedValue);
            categoryBudgetTextView = itemView.findViewById(R.id.utilNotes_tv_budgetValue);
            categorySpentTextView = itemView.findViewById(R.id.utilNotes_tv_spentValue);

        }
    }

}
