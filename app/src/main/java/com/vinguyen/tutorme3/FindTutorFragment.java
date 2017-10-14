package com.vinguyen.tutorme3;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FindTutorFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Activity activtiy;
    ArrayList<String> tutors, availability;
    private int noOfTutors = 0;
    private static String LOG_TAG = "CardViewActivity";
    private ProgressBar spinner;
    private Button filter, sort;
    private String sortBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_card_view,container, false);

        activtiy = getActivity();
        getDataSet();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            availability = bundle.getStringArrayList("availability");
        }
        Bundle bundleSort = this.getArguments();
        if (bundleSort != null) {
            sortBy = bundleSort.getString("message");
        }

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activtiy);
        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        filter = rootView.findViewById(R.id.filter);
        sort = rootView.findViewById(R.id.sort);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (availability != null) {
                    final FilterDialog dialog = new FilterDialog(getActivity(), availability, new FilterDialog.ICustomDialogEventListener() {
                        @Override
                        public void customDialogEvent(ArrayList<String> array) {
                            availability = array;
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("availability", availability);
                            FindTutorFragment fragment = new FindTutorFragment();
                            fragment.setArguments(bundle);
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                    dialog.show();
                }
                else {
                    final FilterDialog dialog = new FilterDialog(getActivity(), new FilterDialog.ICustomDialogEventListener() {
                        @Override
                        public void customDialogEvent(ArrayList<String> array) {
                            availability = array;
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("availability", availability);
                            FindTutorFragment fragment = new FindTutorFragment();
                            fragment.setArguments(bundle);
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                    dialog.show();
                }

            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sortBy != null) {
                    final SortDialog dialog = new SortDialog(getActivity(), sortBy, new SortDialog.SCustomDialogEventListener() {
                        @Override
                        public void customDialogEvent(String sortByNew) {
                            sortBy = sortByNew;
                            Bundle bundleSort = new Bundle();
                            bundleSort.putString("message", sortBy);
                            FindTutorFragment fragment = new FindTutorFragment();
                            fragment.setArguments(bundleSort);
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                    dialog.show();
                }
                else {
                    final SortDialog dialog = new SortDialog(getActivity(), new SortDialog.SCustomDialogEventListener() {
                        @Override
                        public void customDialogEvent(String sortByNew) {
                            sortBy = sortByNew;
                            Bundle bundleSort = new Bundle();
                            bundleSort.putString("message", sortBy);
                            FindTutorFragment fragment = new FindTutorFragment();
                            fragment.setArguments(bundleSort);
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                    dialog.show();
                }
            }
        });


        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void getDataSet() {
        final ArrayList results = new ArrayList<DataObject>();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        ValueEventListener valueEventListener = databaseRef.child("Courses").child("INFS1609").child("Tutors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot tutor : dataSnapshot.getChildren()) {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    databaseRef.child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds.getKey().equals(tutor.getKey())) {
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
                                                    results.add(noOfTutors, obj);
                                                    noOfTutors++;
                                                }
                                            }

                                            noOfTutors = 0;
                                            if (sortBy != null) {
                                                if (sortBy.equals("Name")) {
                                                    Collections.sort(results, new Comparator<DataObject>() {
                                                        @Override
                                                        public int compare(DataObject s1, DataObject s2) {
                                                            return s1.getmText1().compareToIgnoreCase(s2.getmText1());
                                                        }
                                                    });
                                                }
                                            }
                                            mAdapter = new MyRecyclerViewAdapter(results);
                                            mRecyclerView.setLayoutManager(mLayoutManager);
                                            mRecyclerView.setAdapter(mAdapter);
                                            spinner.setVisibility(View.GONE);

                                            ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                                                    .MyClickListener() {
                                                @Override
                                                public void onItemClick(int position, View v) {
                                                    DataObject dObj = (DataObject) results.get(position);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("message", dObj.getUserID());
                                                    TutorProfileViewFragment fragment = new TutorProfileViewFragment();
                                                    fragment.setArguments(bundle);
                                                    getFragmentManager().beginTransaction()
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
