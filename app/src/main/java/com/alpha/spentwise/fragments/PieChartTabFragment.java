package com.alpha.spentwise.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.DateStringIntegerFormat;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.alpha.spentwise.utils.CustomFunctions.getDateIntCustom;
import static com.alpha.spentwise.utils.CustomFunctions.getDateLongCustom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PieChartTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PieChartTabFragment extends Fragment {

    private PieChart pieChart;
    private DatabaseHandler dbHandler;
    private Button debitGraphButton, creditGraphButton;
    private int debitEntryType = 1, creditEntryType = 2, entryType = 1;
    private Context ctx;
    private ProjectDetails selectedProjectDetails;
    private AutoCompleteTextView monthAutoCompleteTextView;
    private ArrayAdapter monthArrayAdapter;
    private ArrayList<DateStringIntegerFormat> monthList;
    private DateStringIntegerFormat selectedMonth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PieChartTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PieChartTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PieChartTabFragment newInstance(String param1, String param2) {
        PieChartTabFragment fragment = new PieChartTabFragment();
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
        View view = inflater.inflate(R.layout.fragment_pie_chart_tab, container, false);

        pieChart = view.findViewById(R.id.pieChartFragment_pieChart);
        debitGraphButton = view.findViewById(R.id.pieChartFragment_btn_debit);
        creditGraphButton = view.findViewById(R.id.pieChartFragment_btn_credit);
        ctx = getContext();
        dbHandler = new DatabaseHandler(ctx);
        monthAutoCompleteTextView = view.findViewById(R.id.pieChartFragment_atv_month);
        //Get global variables
        selectedProjectDetails = ((UserProjectDataHolder)getActivity().getApplication()).getSelectedProject();

        setupPieChart();
        try {
            setupAutoCompleteTextView();
            loadPieChartData(entryType,selectedMonth.getDateCustomInt());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        monthAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = (DateStringIntegerFormat) monthList.get(position);
                try {
                    loadPieChartData(entryType,selectedMonth.getDateCustomInt());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        debitGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    entryType = debitEntryType;
                    loadPieChartData(entryType,selectedMonth.getDateCustomInt());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        creditGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    entryType = creditEntryType;
                    loadPieChartData(entryType,selectedMonth.getDateCustomInt());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterTextSize(14);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    private void loadPieChartData(int type, int startDate) throws ParseException {

        if(type == 1){
            pieChart.setCenterText("Expense by Category");
            pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        } else {
            pieChart.setCenterText("Income by Category");
        }

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(getDateLongCustom(startDate,Constants.dateFormat)));
        c.add(Calendar.MONTH,1);
        int endDate = getDateIntCustom(c.getTimeInMillis(),Constants.dateFormat);

        int sum = dbHandler.getSumOverIntervalAndType(startDate,endDate,type,selectedProjectDetails);
        List<Pair<String,Integer>> nameValueList= dbHandler.getDataForPieChart(startDate,endDate,type,selectedProjectDetails);

        ArrayList<PieEntry> entries = new ArrayList<>();

        for(int i = 0; i < nameValueList.size(); i++){
            entries.add(new PieEntry(((float)nameValueList.get(i).second/(float)sum), nameValueList.get(i).first));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void setupAutoCompleteTextView() throws ParseException {

        monthList = dbHandler.getMonthList(selectedProjectDetails);
        monthArrayAdapter = new ArrayAdapter(getContext(), R.layout.util_exposed_dropdown_item, monthList);
        monthAutoCompleteTextView.setAdapter(monthArrayAdapter);
        //Set default option
        selectedMonth = monthList.get(monthList.size() - 1);
        monthAutoCompleteTextView.setText(selectedMonth.toString(),false);
    }

}