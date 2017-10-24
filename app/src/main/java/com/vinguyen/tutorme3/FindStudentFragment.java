package com.vinguyen.tutorme3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindStudentFragment extends Fragment {
    private AutoCompleteTextView courseAC;
    private String userID, name;
    private FirebaseDatabase userDatabase;
    private DatabaseReference userReference;
    private Button findStudent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_student, container, false);

        courseAC = (AutoCompleteTextView)rootView.findViewById(R.id.courseAC);
        findStudent = (Button)rootView.findViewById(R.id.findTutor);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        databaseRef.child("Courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    databaseRef.child("Courses").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("KEYKEY", suggestionSnapshot.getKey());
                            if (dataSnapshot.child(suggestionSnapshot.getKey()).child("Tutors").hasChild(userID))  {
                                String suggestion = suggestionSnapshot.child("CourseName").getValue(String.class);
                                autoComplete.add(suggestion);
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
                Log.d("ERRORDB", databaseError.toString());
            }
        });
        if (autoComplete != null) {
            courseAC.setAdapter(autoComplete);
        }

        userDatabase = FirebaseDatabase.getInstance();
        userReference = userDatabase.getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showData(dataSnapshot);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        }

        findStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReference = userDatabase.getReference();
                String course = courseAC.getText().toString().trim();
                if (course.equals("")) {
                    String empty = getResources().getString(R.string.empty);
                    Toast.makeText(getActivity().getApplicationContext(), empty, Toast.LENGTH_SHORT).show();
                }
                else {
                Bundle bundle = new Bundle();
                bundle.putString("course", course);
                MyStudentTabFragment fragment = new MyStudentTabFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
                }
            }
        });

        return rootView;
    }

    private void showData(DataSnapshot dataSnapshot){
        for(DataSnapshot ds:dataSnapshot.getChildren()){
            UserEntity userEntity=new UserEntity();
            if (ds.hasChild(userID)){
                userEntity.setName(ds.child(userID).getValue(UserEntity.class).getName());
                userEntity.setAge(ds.child(userID).getValue(UserEntity.class).getAge());
                userEntity.setDegree(ds.child(userID).getValue(UserEntity.class).getDegree());
                userEntity.setContact(ds.child(userID).getValue(UserEntity.class).getContact());

                name = userEntity.getName();
            }
        }
    }
}
