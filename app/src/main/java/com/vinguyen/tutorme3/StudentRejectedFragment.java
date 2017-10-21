package com.vinguyen.tutorme3;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentRejectedFragment extends Fragment {
    private RecyclerView mRecyclerViewR;
    private RecyclerView.Adapter mAdapterR;
    private RecyclerView.LayoutManager mLayoutManagerR;
    private ArrayList resultsR = new ArrayList<DataObject>();
    Activity activity;
    ArrayList<String> tutorsR, availabilityR;
    private static String LOG_TAG = "CardViewActivity";
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_student_rejected, container, false);
        activity = getActivity();

        tutorsR = new ArrayList<String>();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userID != null) {
            getDataSet();
        }

        mRecyclerViewR = (RecyclerView)rootView.findViewById(R.id.my_recycler_viewR);
        mRecyclerViewR.setHasFixedSize(true);
        mLayoutManagerR = new LinearLayoutManager(activity);

        return rootView;
    }

    public void getDataSet() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        ValueEventListener valueEventListener = databaseRef.child("INFS1609").child("Tutors").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot tutor : dataSnapshot.getChildren()) {
                    if (tutor.getValue().equals("rejected")) {
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                        databaseRef.child("Users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (ds.getKey().equals(tutor.getKey())) {
                                        resultsR.clear();
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
                                                        if (availabilityR != null) {
                                                            if (availabilityR.contains("monday") && userEntity.getMonday().equals("no")) {
                                                                return;
                                                            }
                                                            if (availabilityR.contains("tuesday") && userEntity.getTuesday().equals("no")) {
                                                                return;
                                                            }
                                                            if (availabilityR.contains("wednesday") && userEntity.getWednesday().equals("no")) {
                                                                return;
                                                            }
                                                            if (availabilityR.contains("thursday") && userEntity.getThursday().equals("no")) {
                                                                return;
                                                            }
                                                            if (availabilityR.contains("friday") && userEntity.getFriday().equals("no")) {
                                                                return;
                                                            }
                                                            if (availabilityR.contains("saturday") && userEntity.getSaturday().equals("no")) {
                                                                return;
                                                            }
                                                            if (availabilityR.contains("sunday") && userEntity.getSunday().equals("no")) {
                                                                return;
                                                            }
                                                        }
                                                        DataObject obj = new DataObject(userEntity.getName(), userEntity.getDegree(), key);
                                                        resultsR.add(obj);
                                                    }
                                                }

                                                mAdapterR = new MyRecyclerViewAdapter(resultsR);
                                                mRecyclerViewR.setLayoutManager(mLayoutManagerR);
                                                mRecyclerViewR.setAdapter(mAdapterR);

                                                ((MyRecyclerViewAdapter) mAdapterR).setOnItemClickListener(new MyRecyclerViewAdapter
                                                        .MyClickListener() {
                                                    @Override
                                                    public void onItemClick(int position, View v) {
                                                        DataObject dObj = (DataObject) resultsR.get(position);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("message", ((DataObject) resultsR.get(position)).getUserID());
                                                        StudentProfileViewFragment fragment = new StudentProfileViewFragment();
                                                        fragment.setArguments(bundle);
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