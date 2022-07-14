package com.alpha.spentwise.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.alpha.spentwise.fragments.BudgetTabFragment;
import com.alpha.spentwise.fragments.PieChartTabFragment;
import com.alpha.spentwise.fragments.StatsTabFragment;

import org.jetbrains.annotations.NotNull;

public class AnalyticsPageAdapter extends FragmentStateAdapter {


    public AnalyticsPageAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0: return new BudgetTabFragment();
            case 1: return new PieChartTabFragment();
            case 2: return new StatsTabFragment();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
