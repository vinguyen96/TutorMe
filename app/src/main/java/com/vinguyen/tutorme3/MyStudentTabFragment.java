package com.vinguyen.tutorme3;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyStudentTabFragment extends Fragment{

    //Mandatory Constructor
    public MyStudentTabFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_student_tab,container, false);

        ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.pager);
        setupViewPager(viewPager);
        TabLayout tabs = (TabLayout)rootView.findViewById(R.id.fixture_tabs);
        tabs.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        Activity activity = getActivity();
        String pending = activity.getResources().getString(R.string.pending);
        String rejected = activity.getResources().getString(R.string.rejected);
        String accepted = activity.getResources().getString(R.string.accepted);
        adapter.addFragment(new StudentPendingFragment(), pending);
        adapter.addFragment(new StudentAcceptedFragment(), accepted);
        adapter.addFragment(new StudentRejectedFragment(), rejected);
        viewPager.setAdapter(adapter);

    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
