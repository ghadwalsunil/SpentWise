package com.alpha.spentwise.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.alpha.spentwise.R;
import com.alpha.spentwise.adapters.CategoryBudgetProgressRecyclerViewAdapter;
import com.alpha.spentwise.customDataObjects.CategoryBudgetDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.alpha.spentwise.dataManager.Constants;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static com.alpha.spentwise.utils.CustomFunctions.getDateIntCustom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BudgetTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetTabFragment extends Fragment {

    private ProgressBar overAllProjectProgressBar;
    private TextView overAllProjectProgressBarTextView, overAllProjectProgressBarCurrentTextView, overAllProjectProgressBarBudgetTextView;
    private TextView monthlyBudgetTitleTextView, categoryBudgetTitleTextView, noBudgetSetTextView;
    private ProjectDetails selectedProjectDetails;
    private CurrencyDetails selectedCurrencyDetails;
    private NumberFormat myFormat = NumberFormat.getInstance(Locale.ENGLISH);
    private Currency cur;
    private RecyclerView categoryBudgetRecyclerView;
    private CategoryBudgetProgressRecyclerViewAdapter budgetRecyclerViewAdapter;
    private GridLayoutManager budgetGridLayout;
    private DatabaseHandler dbHandler;
    private Context ctx;
    private List<CategoryBudgetDetails> categoryBudgetDetailsList;
    private ConstraintLayout constraintLayout_1, constraintLayout_2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BudgetTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BudgetTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BudgetTabFragment newInstance(String param1, String param2) {
        BudgetTabFragment fragment = new BudgetTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget_tab, container, false);
        overAllProjectProgressBar = view.findViewById(R.id.budgetFragment_pbar_projectProgress);
        overAllProjectProgressBarTextView = view.findViewById(R.id.budgetFragment_pbar_projectProgress_title);
        overAllProjectProgressBarCurrentTextView = view.findViewById(R.id.budgetFragment_pbar_projectProgress_current);
        overAllProjectProgressBarBudgetTextView = view.findViewById(R.id.budgetFragment_pbar_projectProgress_budget);
        categoryBudgetRecyclerView = view.findViewById(R.id.budgetFragment_recyclerView);
        monthlyBudgetTitleTextView = view.findViewById(R.id.budgetFragment_tv_title);
        categoryBudgetTitleTextView = view.findViewById(R.id.budgetFragment_tv_categoryTitle);
        noBudgetSetTextView = view.findViewById(R.id.budgetFragment_tv_budgetNotSet);
        constraintLayout_1 = view.findViewById(R.id.budgetFragment_constraintLayout_1);
        constraintLayout_2 = view.findViewById(R.id.budgetFragment_constraintLayout_2);
        ctx = getContext();
        dbHandler = new DatabaseHandler(ctx);

        //Get global variables
        selectedProjectDetails = ((UserProjectDataHolder)getActivity().getApplication()).getSelectedProject();
        selectedCurrencyDetails = ((UserProjectDataHolder)getActivity().getApplication()).getSelectedCurrencyDetails();
        cur = Currency.getInstance(selectedCurrencyDetails.getId());

        if(selectedProjectDetails.getProjectMonthlyBudget() == 0){
            constraintLayout_1.setVisibility(View.GONE);
        } else {
            setUpMonthlyBudgetProgressBar();
        }
        setUpCategoryLevelBudget();

        if(categoryBudgetDetailsList != null){
            if(selectedProjectDetails.getProjectMonthlyBudget() == 0 && categoryBudgetDetailsList.size() == 0){
                constraintLayout_2.setVisibility(View.GONE);
                noBudgetSetTextView.setVisibility(View.VISIBLE);
            }
        } else if(selectedProjectDetails.getProjectMonthlyBudget() == 0) {
            constraintLayout_2.setVisibility(View.GONE);
            noBudgetSetTextView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void setUpMonthlyBudgetProgressBar(){

        Calendar c = Calendar.getInstance();
        int startDate = getDateIntCustom(c.getTimeInMillis(),Constants.dateFormat);
        startDate = startDate - (startDate % 100) + 1;
        c.add(Calendar.MONTH,1);
        int endDate = getDateIntCustom(c.getTimeInMillis(),Constants.dateFormat);
        endDate = endDate - (endDate % 100) + 1;
        int debitEntryTypeID = 1;

        int sum = dbHandler.getSumOverIntervalAndType(startDate,endDate,debitEntryTypeID,selectedProjectDetails);

        int percent = (int)(((float)sum/(float)selectedProjectDetails.getProjectMonthlyBudget()) * 100);

        String amountString = cur.getSymbol() + " " + sum + " /";
        String budgetString = cur.getSymbol() + " " + selectedProjectDetails.getProjectMonthlyBudget();

        if(percent >= 90){
            overAllProjectProgressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(),R.drawable.util_circle_progress_indicator_bad));
            overAllProjectProgressBarCurrentTextView.setTextColor(getResources().getColor(R.color.red, getActivity().getTheme()));
            overAllProjectProgressBarBudgetTextView.setTextColor(getResources().getColor(R.color.red, getActivity().getTheme()));

        } else {
            overAllProjectProgressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(),R.drawable.util_circle_progress_indicator_good));
            overAllProjectProgressBarCurrentTextView.setTextColor(getResources().getColor(R.color.lime_green, getActivity().getTheme()));
            overAllProjectProgressBarBudgetTextView.setTextColor(getResources().getColor(R.color.lime_green, getActivity().getTheme()));
        }
        if(percent <=100){
            overAllProjectProgressBar.setProgress(percent);
        } else {
            overAllProjectProgressBar.setProgress(100);
        }
        overAllProjectProgressBarCurrentTextView.setText(amountString);
        overAllProjectProgressBarBudgetTextView.setText(budgetString);
    }

    private void setUpCategoryLevelBudget(){
        Calendar c = Calendar.getInstance();
        int startDate = getDateIntCustom(c.getTimeInMillis(),Constants.dateFormat);
        startDate = startDate - (startDate % 100) + 1;
        c.add(Calendar.MONTH,1);
        int endDate = getDateIntCustom(c.getTimeInMillis(),Constants.dateFormat);
        endDate = endDate - (endDate % 100) + 1;
        int debitEntryTypeID = 1;

        categoryBudgetDetailsList = dbHandler.getBudgetData(startDate,endDate,debitEntryTypeID,selectedProjectDetails);

        if(categoryBudgetDetailsList == null){
            categoryBudgetTitleTextView.setVisibility(View.GONE);
        }
        else if(categoryBudgetDetailsList.size() == 0){
            categoryBudgetTitleTextView.setVisibility(View.GONE);
        }
        else {
            budgetRecyclerViewAdapter = new CategoryBudgetProgressRecyclerViewAdapter(ctx,categoryBudgetDetailsList,selectedCurrencyDetails,getActivity());
            budgetGridLayout = new GridLayoutManager(ctx,2,GridLayoutManager.VERTICAL,false);
            categoryBudgetRecyclerView.setLayoutManager(budgetGridLayout);
            categoryBudgetRecyclerView.setAdapter(budgetRecyclerViewAdapter);
        }
    }
}