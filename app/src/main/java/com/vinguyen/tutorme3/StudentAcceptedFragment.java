package com.vinguyen.tutorme3;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentAcceptedFragment extends Fragment {
    private RecyclerView mRecyclerViewA;
    private RecyclerView.Adapter mAdapterA;
    private RecyclerView.LayoutManager mLayoutManagerA;
    private ArrayList resultsA = new ArrayList<DataObject>();
    Activity activity;
    ArrayList<String> tutorsA, availability;
    private int noOfTutors = 0;
    private static String LOG_TAG = "CardViewActivity";
    private String userID, course;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_student_accepted, container, false);
        activity = getActivity();
        tutorsA = new ArrayList<String>();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            course = bundle.getString("course");
        }

        getDataSet();

        mRecyclerViewA = (RecyclerView) rootView.findViewById(R.id.my_recycler_viewA);
        mRecyclerViewA.setHasFixedSize(true);
        mLayoutManagerA = new LinearLayoutManager(activity);

        return rootView;
    }

    public void getDataSet() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        if (userID != null && course != null) {
            ValueEventListener valueEventListener = databaseRef.child(course).child("Tutors").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (final DataSnapshot tutor : dataSnapshot.getChildren()) {
                        if (tutor.getValue().equals("accepted")) {
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                            databaseRef.child("Users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.getKey().equals(tutor.getKey())) {
                                            resultsA.clear();
                                            FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    UserEntity userEntity = new UserEntity();
                                                    String key = tutor.getKey();
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        if (ds.hasChild(key)) {
                                                            userEntity.setName(ds.child(key).getValue(UserEntity.class).getName());
                                                            userEntity.setDegree(ds.child(key).getValue(UserEntity.class).getDegree());
                                                            userEntity.setMonday(ds.child(key).getValue(UserEntity.class).getMonday());
                                                            userEntity.setTuesday(ds.child(key).getValue(UserEntity.class).getTuesday());
                                                            userEntity.setWednesday(ds.child(key).getValue(UserEntity.class).getWednesday());
                                                            userEntity.setThursday(ds.child(key).getValue(UserEntity.class).getThursday());
                                                            userEntity.setFriday(ds.child(key).getValue(UserEntity.class).getFriday());
                                                            userEntity.setSaturday(ds.child(key).getValue(UserEntity.class).getSaturday());
                                                            userEntity.setSunday(ds.child(key).getValue(UserEntity.class).getSunday());
                                                            if (availability != null) {
                                                                if (availability.contains("monday") && userEntity.getMonday().equals("no")) {
                                                                    return;
                                                                }
                                                                if (availability.contains("tuesday") && userEntity.getTuesday().equals("no")) {
                                                                    return;
                                                                }
                                                                if (availability.contains("wednesday") && userEntity.getWednesday().equals("no")) {
                                                                    return;
                                                                }
                                                                if (availability.contains("thursday") && userEntity.getThursday().equals("no")) {
                                                                    return;
                                                                }
                                                                if (availability.contains("friday") && userEntity.getFriday().equals("no")) {
                                                                    return;
                                                                }
                                                                if (availability.contains("saturday") && userEntity.getSaturday().equals("no")) {
                                                                    return;
                                                                }
                                                                if (availability.contains("sunday") && userEntity.getSunday().equals("no")) {
                                                                    return;
                                                                }
                                                            }
                                                            DataObject obj = new DataObject(userEntity.getName(), userEntity.getDegree(), key);
                                                            resultsA.add(obj);
                                                        }
                                                    }
                                                    mAdapterA = new MyRecyclerViewAdapter3(resultsA);
                                                    mRecyclerViewA.setLayoutManager(mLayoutManagerA);
                                                    mRecyclerViewA.setAdapter(mAdapterA);

                                                    ((MyRecyclerViewAdapter3) mAdapterA).setOnItemClickListener(new MyRecyclerViewAdapter3
                                                            .MyClickListener() {
                                                        @Override
                                                        public void onItemClick(int position, View v) {
                                                            DataObject dObj = (DataObject) resultsA.get(position);
                                                            Bundle bundle1 = new Bundle();
                                                            bundle1.putString("userID", ((DataObject) resultsA.get(position)).getUserID());
                                                            Bundle bundle2 = new Bundle();
                                                            bundle2.putString("course", course);
                                                            Bundle mainBundle = new Bundle();
                                                            mainBundle.putBundle("userID", bundle1);
                                                            mainBundle.putBundle("course", bundle2);
                                                            StudentProfileViewFragment fragment = new StudentProfileViewFragment();
                                                            fragment.setArguments(mainBundle);
                                                            getParentFragment().getFragmentManager().beginTransaction()
                                                                    .replace(R.id.fragment_container, fragment)
                                                                    .addToBackStack(null)
                                                                    .commit();
                                                            //Log.i(LOG_TAG, " Clicked on Item " + position);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}