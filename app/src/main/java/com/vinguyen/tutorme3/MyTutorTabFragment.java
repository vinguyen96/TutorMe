package com.vinguyen.tutorme3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyTutorTabFragment extends Fragment {

    private FragmentTabHost mTabHost;

    //Mandatory Constructor
    public MyTutorTabFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_tutor_tab,container, false);


        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("Pending").setIndicator("Pending"),
                TutorPendingFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Accepted").setIndicator("Accepted"),
                TutorAcceptedFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Rejected").setIndicator("Rejected"),
                TutorRejectedFragment.class, null);


        return rootView;
    }
}
