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


public class TutorRejectedFragment extends Fragment {
    private String userID, userIDCurrent;
    private Activity activity;
    DatabaseReference databaseRef;
    ArrayList<String> tutors, availability;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_tutor_pending, container, false);
        activity = getActivity();

        tutors = new ArrayList<String>();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        if (userID != null) {
            ValueEventListener valueEventListener = databaseRef.child("INFS1609").child("Tutors").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot student : dataSnapshot.getChildren()) {
                        checkIfTutor(student.getKey().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        return rootView;
    }

    public void compareUsers(final String tutorID) {
        databaseRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(tutorID)) {
                        showData(tutorID);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void showData(final String tutorID) {
        final ArrayList results = new ArrayList<DataObject>();
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserEntity userEntity = new UserEntity();
                String key = tutorID;
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
                        results.add(obj);
                    }
                }

                mAdapter = new MyRecyclerViewAdapter(results);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

                ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                        .MyClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        DataObject dObj = (DataObject) results.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("message", ((DataObject) results.get(position)).getUserID());
                        TutorProfileViewFragment fragment = new TutorProfileViewFragment();
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

    public void checkIfTutor (final String tutorID) {
        if (userID != null) {
            userIDCurrent = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ValueEventListener valueEventListener = databaseRef.child("INFS1609").child("Tutors").child(tutorID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot student : dataSnapshot.getChildren()) {
                        if (student.getValue().equals("rejected") && student.getKey().equals(userIDCurrent)) {
                            compareUsers(tutorID);
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
