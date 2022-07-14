package com.alpha.spentwise.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.spentwise.customDataObjects.CategoryDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.EntryDetails;
import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.TransactionModeDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import static com.alpha.spentwise.dataManager.Constants.dateFormat;
import static com.alpha.spentwise.dataManager.Constants.myFormat;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SearchEntryRecyclerViewAdapter extends RecyclerView.Adapter<SearchEntryRecyclerViewAdapter.ViewHolder> {

    private List<EntryDetails> activeEntryList;
    private LayoutInflater layoutInflater;
    private List<CategoryDetails> allCategories;
    private List<TransactionModeDetails> allTransactionModes;
    private CurrencyDetails selectedCurrencyDetails;
    private Currency cur;
    private OnNoteListener mOnNoteListener;
    private Activity activity;


    public SearchEntryRecyclerViewAdapter(Context ctx, List<EntryDetails> activeEntryList, List<CategoryDetails> allCategories, List<TransactionModeDetails> allTransactionModes, OnNoteListener onNoteListener, CurrencyDetails currencyDetails, Activity activity){
        this.activeEntryList = activeEntryList;
        this.allCategories = allCategories;
        this.allTransactionModes = allTransactionModes;
        this.layoutInflater = LayoutInflater.from(ctx);
        this.mOnNoteListener = onNoteListener;
        this.selectedCurrencyDetails = currencyDetails;
        this.cur = Currency.getInstance(selectedCurrencyDetails.getId());
        this.activity = activity;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.util_single_search_entry, parent,false);

        return new ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchEntryRecyclerViewAdapter.ViewHolder holder, int position) {

        EntryDetails entryDetails = activeEntryList.get(position);

        //Set entry category
        holder.entryCategory.setText(getCategoryName(entryDetails.getEntryCategoryID(), allCategories));
        //If the category of that transaction is now deleted, the category name will be in gray
        if(getCategoryStatus(entryDetails.getEntryCategoryID(),allCategories) == 0)
            holder.entryCategory.setTextColor(Color.LTGRAY);
        else
            holder.entryCategory.setTextColor(activity.getResources().getColor(R.color.app_primary_color, activity.getTheme()));

        //Set Entry Comment
        holder.entryComment.setText(entryDetails.getEntryComment());

        //Set Entry Date
        try {
            holder.entryDate.setText(getDateString(entryDetails.getEntryDate(), dateFormat));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Set entry amount
        holder.entryAmount.setText(cur.getSymbol() + " " + myFormat.format(entryDetails.getEntryAmount()));
        //If the amount is credited, it will be shown in green, else red.
        if(entryDetails.getEntryType() == 1)
            holder.entryAmount.setTextColor(Color.RED);
        else
            holder.entryAmount.setTextColor(Color.GREEN);

        //Set transaction mode
        holder.entryMode.setText(getTransactionModeName(entryDetails.getEntryMode(),allTransactionModes));
        //If the transaction mode is deleted, it will be shown in gray
        if(getTransactionModeStatus(entryDetails.getEntryMode(),allTransactionModes) == 1)
            holder.entryMode.setTextColor(activity.getResources().getColor(R.color.app_primary_color, activity.getTheme()));
        else
            holder.entryMode.setTextColor(Color.LTGRAY);
    }

    @Override
    public int getItemCount() {
        return activeEntryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView entryCategory, entryComment, entryDate, entryAmount, entryMode;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull @NotNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            entryCategory = itemView.findViewById(R.id.singleEntry_tv_category);
            entryComment = itemView.findViewById(R.id.singleEntry_tv_Comment);
            entryDate = itemView.findViewById(R.id.singleEntry_tv_Date);
            entryAmount = itemView.findViewById(R.id.singleEntry_tv_Amount);
            entryMode = itemView.findViewById(R.id.singleEntry_tv_Mode);
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

    public static String getCategoryName(int categoryID, List<CategoryDetails> categoryList){
        List<CategoryDetails> result = categoryList.stream()
                .filter(item -> item.getCategoryID() == categoryID)
                .collect(Collectors.toList());

        return result.get(0).getCategoryName();
    }

    public static int getCategoryStatus(int categoryID, List<CategoryDetails> categoryList){
        List<CategoryDetails> result = categoryList.stream()
                .filter(item -> item.getCategoryID() == categoryID)
                .collect(Collectors.toList());

        return result.get(0).getCategoryStatus();
    }

    public static String getTransactionModeName(int transactionModeID, List<TransactionModeDetails> transactionModeList){
        List<TransactionModeDetails> result = transactionModeList.stream()
                .filter(item -> item.getTransactionModeID() == transactionModeID)
                .collect(Collectors.toList());

        return result.get(0).getTransactionModeName();
    }

    public static int getTransactionModeStatus(int transactionModeID, List<TransactionModeDetails> transactionModeList){
        List<TransactionModeDetails> result = transactionModeList.stream()
                .filter(item -> item.getTransactionModeID() == transactionModeID)
                .collect(Collectors.toList());

        return result.get(0).getTransactionModeStatus();
    }

    public static String getDateString(int dateInt, SimpleDateFormat dateFormat) throws ParseException {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateFormat.parse(String.valueOf(dateInt)));
    }

}
