package com.vinguyen.tutorme3;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class WithdrawFragment extends Fragment {


    public WithdrawFragment() {
        // Required empty public constructor
    }

    private Activity activity;
    private TextView withdrawText;
    private String course, userIDCurrent;
    private Button withdrawBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_withdraw, container, false);
        activity = getActivity();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            course = bundle.getString("course");
        }

        withdrawText = rootView.findViewById(R.id.withdrawText);
        String withdrawFrom = activity.getResources().getString(R.string.withdrawFrom);
        withdrawText.setText(withdrawFrom + " " + course);

        withdrawBtn = rootView.findViewById(R.id.withdrawBtn);
        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference userDatabaseReference = userDatabase.getReference();
                    userIDCurrent = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    userDatabaseReference.child("INFS1609").child("Tutors").child(userIDCurrent).removeValue();
                    userDatabaseReference.child("Courses").child(course).child("Tutors").child(userIDCurrent).removeValue();
                    FindStudentFragment fragment = new FindStudentFragment();
                    getParentFragment().getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        return rootView;
    }

}
