package com.projects.assignment.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.projects.assignment.ui.TabFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public String[] title = {"General", "Business", "Technology","Science","Entertainment","Health","Sports"};

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TabFragment.getInstance(position,title[position]);
    }

    @Override
    public int getItemCount() {
        return title.length;
    }
}