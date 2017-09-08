package com.vinguyen.tutorme3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindTutorFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter arrayAdapter;
    DatabaseReference databaseRef;
    ArrayList<String> tutors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_tutor, container, false);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        listView = (ListView)rootView.findViewById(R.id.listView);

        tutors = new ArrayList<String>();
        ValueEventListener valueEventListener = databaseRef.child("Courses").child("INFS1609").child("Tutors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tutor : dataSnapshot.getChildren()) {
                    compareUsers(tutor);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    public void compareUsers(final DataSnapshot tutor) {
        databaseRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(tutor.getKey())) {
                        showData(tutor);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void showData(final DataSnapshot tutor) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserEntity userEntity = new UserEntity();
                String key = tutor.getKey();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.hasChild(key)) {
                        userEntity.setName(ds.child(key).getValue(UserEntity.class).getName());
                        userEntity.setAge(ds.child(key).getValue(UserEntity.class).getAge());
                        tutors.add(userEntity.getName() + " " + userEntity.getAge());
                    }
                }

                arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                        tutors);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}