package com.arapa.app.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.arapa.app.fragments.AboutFragment;
import com.arapa.app.fragments.ContactFragment;
import com.arapa.app.fragments.CoursesFragment;
import com.arapa.app.util.School;

public class PagerAdapter extends FragmentStateAdapter {

    private School school;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, School school) {
        super(fragmentActivity);
        this.school = school;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
            default:
                return new AboutFragment(school);
            case 1:
                return new CoursesFragment(school);
            case 2:
                return new ContactFragment(school);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
