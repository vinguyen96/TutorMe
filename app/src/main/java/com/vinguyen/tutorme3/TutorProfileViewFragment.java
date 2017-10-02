package com.vinguyen.tutorme3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.app.Activity.RESULT_OK;


public class TutorProfileViewFragment extends Fragment {

    private FirebaseDatabase userDatabaseCreate;
    private DatabaseReference userReference;
    private TextView profileName, profileAge, profileDegree, profileContact;
    private String userID, currentUserID;
    private StorageReference myRef, defaultRef, storageRef;
    private Button becomeStudent;
    private FirebaseDatabase userDatabase;
    private DatabaseReference userDatabaseReference;
    private String userIDCurrent;
    private ImageView imgView, mondayView, tuesdayView, wednesdayView, thursdayView, fridayView, saturdayView, sundayView;
    private String mondayAvailability = "no";
    private String tuesdayAvailability = "no";
    private String wednesdayAvailability = "no";
    private String thursdayAvailability = "no";
    private String fridayAvailability = "no";
    private String saturdayAvailability = "no";
    private String sundayAvailability = "no";
    private Bitmap bImageMonday;
    private Activity activity;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_view, container, false);
        activity = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userID = bundle.getString("message");
        }
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        profileName=(TextView) rootView.findViewById(R.id.profileName);
        profileAge=(TextView) rootView.findViewById(R.id.profileAge);
        profileDegree=(TextView) rootView.findViewById(R.id.profileDegree);
        profileContact=(TextView) rootView.findViewById(R.id.profileContact);

        imgView=(ImageView) rootView.findViewById(R.id.imgView);

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
                    Log.d("CHECKPOINT", userID);
                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        becomeStudent=(Button)rootView.findViewById(R.id.becomeStudent);
        if (currentUserID.equals(userID)) {
            becomeStudent.setVisibility(View.GONE);
        }

        userDatabase=FirebaseDatabase.getInstance();
        userDatabaseReference=userDatabase.getReference("Courses");
            becomeStudent.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View view) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        userIDCurrent = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d("USERID", userID);
                        userDatabaseReference.child("INFS1609").child("Tutors").child(userID).child(userIDCurrent).setValue("pending");
                        /*FindTutorFragment fragment = new FindTutorFragment();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .commit();*/
                    }
                }
                });
        mondayView = (ImageView) rootView.findViewById(R.id.monday);
        tuesdayView = (ImageView) rootView.findViewById(R.id.tuesday);
        wednesdayView = (ImageView) rootView.findViewById(R.id.wednesday);
        thursdayView = (ImageView) rootView.findViewById(R.id.thursday);
        fridayView = (ImageView) rootView.findViewById(R.id.friday);
        saturdayView = (ImageView) rootView.findViewById(R.id.saturday);
        sundayView = (ImageView) rootView.findViewById(R.id.sunday);

        getMondayAvailability();
        Log.d("TEST", mondayAvailability);
        getTuesdayAvailability();
        Log.d("TEST", tuesdayAvailability);
        getWednesdayAvailability();
        Log.d("TEST", wednesdayAvailability);
        getThursdayAvailability();
        Log.d("TEST", thursdayAvailability);
        getFridayAvailability();
        Log.d("TEST", fridayAvailability);
        getSaturdayAvailability();
        Log.d("TEST", saturdayAvailability);
        getSundayAvailability();
        Log.d("TEST", sundayAvailability);

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

    public void getMondayAvailability() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
            DatabaseReference userDatabaseReference = userDatabase.getReference();
            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        UserEntity availability = new UserEntity();
                        if (ds.hasChild(userID)) {
                            availability.setMonday(ds.child(userID).getValue(UserEntity.class).getMonday());
                            if (availability.getMonday().equals("yes")) {
                                mondayAvailability = "yes";
                                bImageMonday = BitmapFactory.decodeResource(activity.getResources(), R.drawable.mondayfilled);
                            }
                            if (availability.getMonday().equals("no")) {
                                mondayAvailability = "no";
                                bImageMonday = BitmapFactory.decodeResource(activity.getResources(), R.drawable.monday);
                            }
                            mondayView.setImageBitmap(bImageMonday);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void getTuesdayAvailability() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
            userDatabaseReference = userDatabase.getReference();

            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        UserEntity availability = new UserEntity();
                        if (ds.hasChild(userID)) {
                            availability.setTuesday(ds.child(userID).getValue(UserEntity.class).getTuesday());
                            if (availability.getTuesday().equals("yes")) {
                                tuesdayAvailability = "yes";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.tuesdayfilled);
                                tuesdayView.setImageBitmap(bImage);
                            }
                            if (availability.getTuesday().equals("no")) {
                                tuesdayAvailability = "no";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.tuesday);
                                tuesdayView.setImageBitmap(bImage);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void getWednesdayAvailability() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
            userDatabaseReference = userDatabase.getReference();

            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        UserEntity availability = new UserEntity();
                        if (ds.hasChild(userID)) {
                            availability.setWednesday(ds.child(userID).getValue(UserEntity.class).getWednesday());
                            if (availability.getWednesday().equals("yes")) {
                                wednesdayAvailability = "yes";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.wednesdayfilled);
                                wednesdayView.setImageBitmap(bImage);
                            }
                            if (availability.getWednesday().equals("no")) {
                                wednesdayAvailability = "no";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.wednesday);
                                wednesdayView.setImageBitmap(bImage);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void getThursdayAvailability() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
            userDatabaseReference = userDatabase.getReference();

            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        UserEntity availability = new UserEntity();
                        if (ds.hasChild(userID)) {
                            availability.setThursday(ds.child(userID).getValue(UserEntity.class).getThursday());
                            if (availability.getThursday().equals("yes")) {
                                thursdayAvailability = "yes";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.thursdayfilled);
                                thursdayView.setImageBitmap(bImage);
                            }
                            if (availability.getThursday().equals("no")) {
                                thursdayAvailability = "no";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.thursday);
                                thursdayView.setImageBitmap(bImage);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void getFridayAvailability() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
            userDatabaseReference = userDatabase.getReference();

            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        UserEntity availability = new UserEntity();
                        if (ds.hasChild(userID)) {
                            availability.setFriday(ds.child(userID).getValue(UserEntity.class).getFriday());
                            if (availability.getFriday().equals("yes")) {
                                fridayAvailability = "yes";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.fridayfilled);
                                fridayView.setImageBitmap(bImage);
                            }
                            if (availability.getFriday().equals("no")) {
                                fridayAvailability = "no";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.friday);
                                fridayView.setImageBitmap(bImage);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void getSaturdayAvailability() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
            userDatabaseReference = userDatabase.getReference();

            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        UserEntity availability = new UserEntity();
                        if (ds.hasChild(userID)) {
                            availability.setSaturday(ds.child(userID).getValue(UserEntity.class).getSaturday());
                            if (availability.getSaturday().equals("yes")) {
                                saturdayAvailability = "yes";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.saturdayfilled);
                                saturdayView.setImageBitmap(bImage);
                            }
                            if (availability.getSaturday().equals("no")) {
                                saturdayAvailability = "no";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.saturday);
                                saturdayView.setImageBitmap(bImage);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void getSundayAvailability() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
            userDatabaseReference = userDatabase.getReference();

            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        UserEntity availability = new UserEntity();
                        if (ds.hasChild(userID)) {
                            availability.setSunday(ds.child(userID).getValue(UserEntity.class).getSunday());
                            if (availability.getSunday().equals("yes")) {
                                sundayAvailability = "yes";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.sundayfilled);
                                sundayView.setImageBitmap(bImage);
                            }
                            if (availability.getSunday().equals("no")) {
                                sundayAvailability = "no";
                                Bitmap bImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.sunday);
                                sundayView.setImageBitmap(bImage);
                            }
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

