package com.vinguyen.tutorme3;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class TutorRejectedFragment extends Fragment {
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter arrayAdapterID;
    private String userID, userIDCurrent;
    private Activity activity;
    DatabaseReference databaseRef;
    ArrayList<String> tutors;
    ArrayList<String> tutorsID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_tutor_pending, container, false);
        activity = getActivity();

        databaseRef = FirebaseDatabase.getInstance().getReference();
        listView = (ListView)rootView.findViewById(R.id.listView);

        tutors = new ArrayList<String>();
        tutorsID = new ArrayList<String>();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("message", tutorsID.get(position));
                TutorProfileViewFragment fragment = new TutorProfileViewFragment();
                fragment.setArguments(bundle);
                getParentFragment().getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                //Toast.makeText(getActivity(), tutorsID.get(position), Toast.LENGTH_SHORT).show();
            }
        });


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
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserEntity userEntity = new UserEntity();
                String key = tutorID;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.hasChild(key)) {
                        userEntity.setName(ds.child(key).getValue(UserEntity.class).getName());
                        userEntity.setAge(ds.child(key).getValue(UserEntity.class).getAge());
                        tutors.add(userEntity.getName() + " " + userEntity.getAge());
                        tutorsID.add(key);
                    }
                }

                arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1,
                        tutors);
                listView.setAdapter(arrayAdapter);
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
