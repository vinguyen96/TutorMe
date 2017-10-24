package com.vinguyen.tutorme3;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private String sortBy, filterSuburb, filterCourse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_card_view,container, false);

        activtiy = getActivity();

        Bundle bundle = getArguments();
        if (bundle != null) {
            Bundle bundle1 = bundle.getBundle("availability");
            Bundle bundle2 = bundle.getBundle("filterSuburb");
            Bundle bundle3 = bundle.getBundle("filterCourse");
            if (bundle1 != null) {
                availability = bundle1.getStringArrayList("availabilityR");
            }
            if (bundle2 != null) {
                filterSuburb = bundle2.getString("filterSuburb");
            }
            if (bundle3 != null) {
                filterCourse = bundle3.getString("filterCourse");
            }
        }

        Bundle bundleCourse = getArguments();
        if (bundleCourse != null) {
            filterCourse = bundleCourse.getString("course");
        }

        Bundle bundleSort = getArguments();
        if (bundleSort != null) {
            Bundle bundle1 = bundle.getBundle("sortByBundle");
            Bundle bundle2 = bundle.getBundle("filterCourse");
            if (bundle1 != null) {
                sortBy = bundle1.getString("sortBy");
            }
            if (bundle2 != null) {
                filterCourse = bundle2.getString("filterCourse");
            }
        }
        getDataSet();

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
                if (availability != null && filterSuburb != null) {
                    final FilterDialog dialog = new FilterDialog(getActivity(), availability, filterSuburb, new FilterDialog.ICustomDialogEventListener() {
                        @Override
                        public void customDialogEvent(ArrayList<String> array, String suburb) {
                            availability = array;
                            filterSuburb = suburb;
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("availabilityR", availability);
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("filterSuburb", filterSuburb);
                            Bundle bundle3 = new Bundle();
                            bundle3.putString("filterCourse", filterCourse);
                            FindTutorFragment fragment = new FindTutorFragment();
                            Bundle mainBundle = new Bundle();
                            mainBundle.putBundle("availability", bundle);
                            mainBundle.putBundle("filterSuburb", bundle2);
                            mainBundle.putBundle("filterCourse", bundle3);
                            fragment.setArguments(mainBundle);
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
                        public void customDialogEvent(ArrayList<String> array, String suburb) {
                            availability = array;
                            filterSuburb = suburb;
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("availabilityR", availability);
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("filterSuburb", filterSuburb);
                            FindTutorFragment fragment = new FindTutorFragment();
                            Bundle bundle3 = new Bundle();
                            bundle3.putString("filterCourse", filterCourse);
                            Bundle mainBundle = new Bundle();
                            mainBundle.putBundle("availability", bundle);
                            mainBundle.putBundle("filterSuburb", bundle2);
                            mainBundle.putBundle("filterCourse", bundle3);
                            fragment.setArguments(mainBundle);
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
                            bundleSort.putString("sortBy", sortBy);
                            Bundle bundleCourse = new Bundle();
                            bundleCourse.putString("filterCourse", filterCourse);
                            Bundle mainBundle = new Bundle();
                            mainBundle.putBundle("sortByBundle", bundleSort);
                            mainBundle.putBundle("filterCourse", bundleCourse);
                            FindTutorFragment fragment = new FindTutorFragment();
                            fragment.setArguments(mainBundle);
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
                            bundleSort.putString("sortBy", sortBy);
                            Bundle bundleCourse = new Bundle();
                            bundleCourse.putString("filterCourse", filterCourse);
                            Bundle mainBundle = new Bundle();
                            mainBundle.putBundle("sortByBundle", bundleSort);
                            mainBundle.putBundle("filterCourse", bundleCourse);
                            FindTutorFragment fragment = new FindTutorFragment();
                            fragment.setArguments(mainBundle);
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
        if (filterCourse != null) {
            ValueEventListener valueEventListener = databaseRef.child("Courses").child(filterCourse).child("Tutors").addValueEventListener(new ValueEventListener() {
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
                                                        userEntity.setSuburb(ds.child(key).getValue(UserEntity.class).getSuburb());
                                                        int tutorLikes;
                                                        if (ds.child(key).child("TutorLikes").getValue() != null) {
                                                            tutorLikes = (int) ds.child(key).child("TutorLikes").getChildrenCount();
                                                            Log.d("TUTORLIKES", userEntity.getName() + " " + Integer.toString(tutorLikes));
                                                        } else {
                                                            tutorLikes = 0;
                                                        }
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
                                                        if (filterSuburb != null) {
                                                            Log.d("WHATISFILTER", filterSuburb);
                                                            if (filterSuburb.equals(" ") == false) {
                                                                if (userEntity.getSuburb().startsWith(filterSuburb) == false) {
                                                                    return;
                                                                }
                                                            }
                                                        }
                                                        DataObject obj = new DataObject(userEntity.getName(), userEntity.getDegree(), tutorLikes, key);
                                                        results.add(noOfTutors, obj);
                                                        noOfTutors++;
                                                    }
                                                }

                                                noOfTutors = 0;
                                                if (sortBy != null) {
                                                    Log.d("SORTBY", sortBy);
                                                    if (sortBy.equals("Full Name")) {
                                                        Collections.sort(results, new Comparator<DataObject>() {
                                                            @Override
                                                            public int compare(DataObject s1, DataObject s2) {
                                                                return s1.getmText1().compareToIgnoreCase(s2.getmText1());
                                                            }
                                                        });
                                                    } else if (sortBy.equals("Like")) {
                                                        Collections.sort(results, new Comparator<DataObject>() {
                                                            @Override
                                                            public int compare(DataObject s1, DataObject s2) {
                                                                return s2.getTutorLikes() - s1.getTutorLikes();
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
                                                        Bundle bundle1 = new Bundle();
                                                        bundle1.putString("userID", dObj.getUserID());
                                                        Bundle bundle2 = new Bundle();
                                                        bundle2.putString("course", filterCourse);
                                                        Bundle mainBundle = new Bundle();
                                                        mainBundle.putBundle("userID", bundle1);
                                                        mainBundle.putBundle("course", bundle2);
                                                        TutorProfileViewFragment fragment = new TutorProfileViewFragment();
                                                        fragment.setArguments(mainBundle);
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
}
