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


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentPendingFragment extends Fragment {

    private RecyclerView mRecyclerViewP;
    private RecyclerView.Adapter mAdapterP;
    private RecyclerView.LayoutManager mLayoutManagerP;
    private ArrayList resultsP = new ArrayList<DataObject>();
    Activity activity;
    ArrayList<String> tutorsP, availabilityR;
    private static String LOG_TAG = "PendingFragment";
    private String userID, course;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_student_pending, container, false);
        activity = getActivity();
        tutorsP = new ArrayList<String>();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            course = bundle.getString("course");
        }

        getDataSet();

        mRecyclerViewP = (RecyclerView) rootView.findViewById(R.id.my_recycler_viewP);
        mRecyclerViewP.setHasFixedSize(true);
        mLayoutManagerP = new LinearLayoutManager(activity);

        return rootView;
    }

    public void getDataSet() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        if (userID != null && course !=null) {
            ValueEventListener valueEventListener = databaseRef.child(course).child("Tutors").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (final DataSnapshot tutor : dataSnapshot.getChildren()) {
                        if (tutor.getValue().equals("pending")) {
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                            databaseRef.child("Users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.getKey().equals(tutor.getKey())) {
                                            resultsP.clear();
                                            FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    UserEntity userEntity = new UserEntity();
                                                    String key = tutor.getKey();
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        if (ds.hasChild(key)) {
                                                            userEntity.setName(ds.child(key).getValue(UserEntity.class).getName());
                                                            userEntity.setDegree(ds.child(key).getValue(UserEntity.class).getDegree());
                                                            DataObject obj = new DataObject(userEntity.getName(), userEntity.getDegree(), key);
                                                            resultsP.add(obj);
                                                        }
                                                    }

                                                    mAdapterP = new MyRecyclerViewAdapter2(resultsP);
                                                    mRecyclerViewP.setLayoutManager(mLayoutManagerP);
                                                    mRecyclerViewP.setAdapter(mAdapterP);

                                                    ((MyRecyclerViewAdapter2) mAdapterP).setOnItemClickListener(new MyRecyclerViewAdapter2
                                                            .MyClickListener() {
                                                        @Override
                                                        public void onItemClick(int position, View v) {
                                                            DataObject dObj = (DataObject) resultsP.get(position);
                                                            Bundle bundle1 = new Bundle();
                                                            bundle1.putString("userID", ((DataObject) resultsP.get(position)).getUserID());
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