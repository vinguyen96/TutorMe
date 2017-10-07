package com.vinguyen.tutorme3;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class StudentProfileViewFragment extends Fragment {
    private FirebaseDatabase userDatabaseCreate;
    private DatabaseReference userReference, userDatabaseReference, databaseRef;
    private TextView profileName, profileAge, profileDegree, profileContact,noOfStudentLikes,noOfTutorLikes;
    private ImageView imgView;
    private String userID;
    private Button acceptBtn, rejectBtn, likeBtn;
    private StorageReference myRef, defaultRef, storageRef;
    private FirebaseDatabase userDatabase;
    private String userIDCurrent;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_profile_view, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userID = bundle.getString("message");
        }
        userIDCurrent = FirebaseAuth.getInstance().getCurrentUser().getUid();

        noOfStudentLikes =(TextView) rootView.findViewById(R.id.noOfStudentLikes);
        noOfTutorLikes =(TextView) rootView.findViewById(R.id.noOfTutorLikes);

        profileName=(TextView) rootView.findViewById(R.id.profileName);
        profileAge=(TextView) rootView.findViewById(R.id.profileAge);
        profileDegree=(TextView) rootView.findViewById(R.id.profileDegree);
        profileContact=(TextView) rootView.findViewById(R.id.profileContact);
        profileContact.setVisibility(View.GONE);

        imgView=(ImageView) rootView.findViewById(R.id.imgView);
        likeBtn=(Button)rootView.findViewById(R.id.like);
        likeBtn.setVisibility(View.GONE);

        userDatabase = FirebaseDatabase.getInstance();
        userReference = userDatabase.getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null && userID != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storageRef = storage.getReferenceFromUrl("gs://tutorme-61083.appspot.com/userProfileImages");
            myRef = storageRef.child(userID + ".jpg");
            defaultRef = storageRef.child("default.jpg");
            refreshImage();
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        acceptBtn = (Button) rootView.findViewById(R.id.acceptBtn);
        rejectBtn = (Button) rootView.findViewById(R.id.rejectBtn);
        userDatabase=FirebaseDatabase.getInstance();
        userDatabaseReference=userDatabase.getReference();

        databaseRef = FirebaseDatabase.getInstance().getReference();
        if (userID != null) {
            ValueEventListener valueEventListener = databaseRef.child("INFS1609").child("Tutors").child(userIDCurrent).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot student : dataSnapshot.getChildren()) {;
                        if (student.getValue().equals("pending") == false) {
                            acceptBtn.setVisibility(View.GONE);
                            rejectBtn.setVisibility(View.GONE);
                        }
                        else {
                            acceptBtn.setVisibility(View.VISIBLE);
                            rejectBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        acceptBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Log.d("USERID", userID);
                    userDatabaseReference.child("INFS1609").child("Tutors").child(userIDCurrent).child(userID).setValue("accepted");
                    StudentPendingFragment fragment = new StudentPendingFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }
            }
        });
        rejectBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Log.d("USERID", userID);
                    userDatabaseReference.child("INFS1609").child("Tutors").child(userIDCurrent).child(userID).setValue("rejected");
                    StudentPendingFragment fragment = new StudentPendingFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }
            }
        });

        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userDatabaseReference = userDatabase.getReference();
        userDatabaseReference.child("Users").child(userID).child("TutorLikes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(userIDCurrent).exists()) {
                            likeBtn.setText("UnLike");
                        } else {
                            likeBtn.setText("Like");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReference = userDatabase.getReference();
                userDatabaseReference.child("Users").child(userID).child("TutorLikes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(userIDCurrent).exists()) {
                            FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference userDatabaseReference = userDatabase.getReference();
                            userDatabaseReference.child("Users").child(userID).child("TutorLikes").child(userIDCurrent).removeValue();
                        }
                        else {
                            FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference userDatabaseReference = userDatabase.getReference();
                            userDatabaseReference.child("Users").child(userID).child("TutorLikes").child(userIDCurrent).setValue("like");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        getTutorLikes();
        getStudentLikes();

        checkIfStudent();
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

                profileName.setText("Name: " + userEntity.getName());
                profileAge.setText("Age: " + userEntity.getAge());
                profileDegree.setText("Degree: " + userEntity.getDegree());
                profileContact.setText("Contact: " + userEntity.getContact());
            }
        }

    }

    public void getTutorLikes() {
        FirebaseDatabase userDatabaseTutorLikes = FirebaseDatabase.getInstance();
        DatabaseReference userDatabaseReferenceTutorLikes = userDatabaseTutorLikes.getReference();
        userDatabaseReferenceTutorLikes.child("Users").child(userID).child("TutorLikes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                noOfTutorLikes.setText(String.format("Tutors that like me: %s", Long.toString(count)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getStudentLikes() {
        FirebaseDatabase userDatabaseStudentLikes = FirebaseDatabase.getInstance();
        DatabaseReference userDatabaseReferenceStudentLikes = userDatabaseStudentLikes.getReference();
        userDatabaseReferenceStudentLikes.child("Users").child(userID).child("StudentLikes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                noOfStudentLikes.setText(String.format("Students that like me: %s", Long.toString(count)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void refreshImage() {
        if (getActivity() != null) {
            myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getActivity()).using(new FirebaseImageLoader()).load(myRef).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(imgView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Glide.with(getActivity()).using(new FirebaseImageLoader()).load(defaultRef).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(imgView);
                }
            });
        }
    }

    public void checkIfStudent () {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        userIDCurrent = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userID != null) {
            ValueEventListener valueEventListener = databaseRef.child("INFS1609").child("Tutors").child(userIDCurrent).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot tutor : dataSnapshot.getChildren()) {
                        Log.d("IDK", userIDCurrent + " " + userID + " " + tutor.getKey() + " " + tutor.getValue());
                        if (tutor.getValue().equals("pending") && tutor.getKey().equals(userID)) {
                            profileContact.setVisibility(View.GONE);
                        } else if (tutor.getValue().equals("rejected") && tutor.getKey().equals(userID)) {
                            profileAge.setVisibility(View.GONE);
                        } else if (tutor.getValue().equals("accepted") && tutor.getKey().equals(userID)) {
                            profileContact.setVisibility(View.VISIBLE);
                            likeBtn.setVisibility(View.VISIBLE);
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