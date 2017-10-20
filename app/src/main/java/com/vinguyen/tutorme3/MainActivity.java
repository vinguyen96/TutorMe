package com.vinguyen.tutorme3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class MainActivity extends Fragment {

    private Button signOut, btnSaveEdit;

    private EditText hidden1, hidden2, hidden3, hidden4, hidden5;
    private TextView profileName, profileAge, profileDegree, profileContact, profileDesc, noOfStudentLikes, noOfTutorLikes;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseDatabase userDatabase;
    private DatabaseReference userReference, userDBReference;
    private String userID;
    private ImageView imgView, mondayView, tuesdayView, wednesdayView, thursdayView, fridayView, saturdayView, sundayView, btnEditProfile;
    private int PICK_IMAGE_REQUEST = 111;
    private Uri filePath;
    private ProgressDialog uploadingPD;
    private StorageReference myRef, defaultRef, storageRef;
    private DatabaseReference userDatabaseReference;
    private String mondayAvailability = "no";
    private String tuesdayAvailability = "no";
    private String wednesdayAvailability = "no";
    private String thursdayAvailability = "no";
    private String fridayAvailability = "no";
    private String saturdayAvailability = "no";
    private String sundayAvailability = "no";
    private Bitmap bImageMonday;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_main, container, false);


        activity = getActivity();

        rootView.setOnTouchListener(new OnSwipeTouchListener(activity) {
            public void onSwipeTop() {
                Toast.makeText(activity, "top", Toast.LENGTH_SHORT).show();
            }
        });

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }
        };

        signOut = (Button) rootView.findViewById(R.id.sign_out);

        noOfStudentLikes =(TextView) rootView.findViewById(R.id.noOfStudentLikes);
        noOfTutorLikes =(TextView) rootView.findViewById(R.id.noOfTutorLikes);

        profileName=(TextView) rootView.findViewById(R.id.profileName);
        profileAge=(TextView) rootView.findViewById(R.id.profileAge);
        profileDegree=(TextView) rootView.findViewById(R.id.profileDegree);
        profileContact=(TextView) rootView.findViewById(R.id.profileContact);
        profileDesc=(TextView) rootView.findViewById(R.id.profileDesc);

        mondayView = (ImageView) rootView.findViewById(R.id.monday);
        tuesdayView = (ImageView) rootView.findViewById(R.id.tuesday);
        wednesdayView = (ImageView) rootView.findViewById(R.id.wednesday);
        thursdayView = (ImageView) rootView.findViewById(R.id.thursday);
        fridayView = (ImageView) rootView.findViewById(R.id.friday);
        saturdayView = (ImageView) rootView.findViewById(R.id.saturday);
        sundayView = (ImageView) rootView.findViewById(R.id.sunday);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
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

        FirebaseDatabase userDatabaseStudentLikes = FirebaseDatabase.getInstance();
        DatabaseReference userDatabaseReferenceStudentLikes = userDatabaseStudentLikes.getReference();
        final Activity activity = getActivity();
        if (activity!=null && isAdded()) {
            if (userID != null) {
                userDatabaseReferenceStudentLikes.child("Users").child(userID).child("StudentLikes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        String studentsThatLikeMeString = activity.getResources().getString(R.string.studentsThatLikeMe);
                        noOfStudentLikes.setText(studentsThatLikeMeString + " " + Long.toString(count));
                        //getString(R.string.studentsThatLikeMe) +
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            if (userID != null) {
                FirebaseDatabase userDatabaseTutorLikes = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReferenceTutorLikes = userDatabaseTutorLikes.getReference();
                userDatabaseReferenceTutorLikes.child("Users").child(userID).child("TutorLikes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        String tutorsThatLikeMeString = activity.getResources().getString(R.string.tutorsThatLikeMe);
                        noOfTutorLikes.setText(tutorsThatLikeMeString + " " + Long.toString(count));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }

        getMondayAvailability();
        getTuesdayAvailability();
        getWednesdayAvailability();
        getThursdayAvailability();
        getFridayAvailability();
        getSaturdayAvailability();
        getSundayAvailability();

        mondayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReference = userDatabase.getReference("Users");
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                getMondayAvailability();
                if (mondayAvailability.equals("no")){
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("monday").setValue("yes");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.mondayfilled);
                    mondayView.setImageBitmap(bImage);
                }
                else {
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("monday").setValue("no");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.monday);
                    mondayView.setImageBitmap(bImage);
                }
            }
        });

        tuesdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReference = userDatabase.getReference("Users");
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                getTuesdayAvailability();
                if (tuesdayAvailability.equals("no")){
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("tuesday").setValue("yes");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.tuesdayfilled);
                    tuesdayView.setImageBitmap(bImage);
                }
                else {
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("tuesday").setValue("no");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.tuesday);
                    tuesdayView.setImageBitmap(bImage);
                }
            }
        });

        wednesdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReference = userDatabase.getReference("Users");
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                getWednesdayAvailability();
                if (wednesdayAvailability.equals("no")){
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("wednesday").setValue("yes");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.wednesdayfilled);
                    wednesdayView.setImageBitmap(bImage);
                }
                else {
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("wednesday").setValue("no");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.wednesday);
                    wednesdayView.setImageBitmap(bImage);
                }
            }
        });

        thursdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReference = userDatabase.getReference("Users");
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                getThursdayAvailability();
                if (thursdayAvailability.equals("no")){
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("thursday").setValue("yes");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.thursdayfilled);
                    thursdayView.setImageBitmap(bImage);
                }
                else {
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("thursday").setValue("no");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.thursday);
                    thursdayView.setImageBitmap(bImage);
                }
            }
        });

        fridayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReference = userDatabase.getReference("Users");
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
               getFridayAvailability();
                if (fridayAvailability.equals("no")){
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("friday").setValue("yes");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.fridayfilled);
                    fridayView.setImageBitmap(bImage);
                }
                else {
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("friday").setValue("no");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.friday);
                    fridayView.setImageBitmap(bImage);
                }
            }
        });

        saturdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReference = userDatabase.getReference("Users");
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                getSaturdayAvailability();
                if (saturdayAvailability.equals("no")){
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("saturday").setValue("yes");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.saturdayfilled);
                    saturdayView.setImageBitmap(bImage);
                }
                else {
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("saturday").setValue("no");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.saturday);
                    saturdayView.setImageBitmap(bImage);
                }
            }
        });

       sundayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReference = userDatabase.getReference("Users");
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                getSundayAvailability();
                if (sundayAvailability.equals("no")){
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("sunday").setValue("yes");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.sundayfilled);
                    sundayView.setImageBitmap(bImage);
                }
                else {
                    userDatabaseReference.child(currentFirebaseUser.getUid()).child("sunday").setValue("no");
                    Bitmap bImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.sunday);
                    sundayView.setImageBitmap(bImage);
                }
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://tutorme-61083.appspot.com/userProfileImages");
        myRef = storageRef.child(userID + ".jpg");
        defaultRef = storageRef.child("default.jpg");

        imgView = (ImageView)rootView.findViewById(R.id.imgView);

        uploadingPD = new ProgressDialog(getActivity());
        uploadingPD.setMessage("Uploading....");

        refreshImage();

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnEditProfile = (ImageView) rootView.findViewById(R.id.btnEditProfile);
        btnSaveEdit = (Button) rootView.findViewById(R.id.btnSaveProfile);
        btnSaveEdit.setVisibility(View.GONE);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            // @Override
            //public void onClick(View v) {
            //launch edit profile activity
            //change the current text fields into editable text


            public void onClick(View v) {
                if (btnSaveEdit.isShown() == true) {
                    btnSaveEdit.setVisibility(View.GONE);
                }
                else {
                    btnSaveEdit.setVisibility(View.VISIBLE);
                }

                ViewSwitcher switcher1 = (ViewSwitcher)getView().findViewById(R.id.my_switcher1);
                switcher1.showNext(); //or switcher.showPrevious();
                TextView editName = (TextView) switcher1.findViewById(R.id.hidden_edit_view1);
                String name = profileName.getText().toString();
                editName.setText(name);

                ViewSwitcher switcher2 = (ViewSwitcher)getView().findViewById(R.id.my_switcher2);
                switcher2.showNext(); //or switcher.showPrevious();
                TextView editAge = (TextView) switcher2.findViewById(R.id.hidden_edit_view2);
                String age = profileAge.getText().toString();
                editAge.setText(age);

                ViewSwitcher switcher3 = (ViewSwitcher)getView().findViewById(R.id.my_switcher3);
                switcher3.showNext(); //or switcher.showPrevious();
                TextView editDegree = (TextView) switcher3.findViewById(R.id.hidden_edit_view3);
                String degree = profileDegree.getText().toString();
                editDegree.setText(degree);

                ViewSwitcher switcher4 = (ViewSwitcher)getView().findViewById(R.id.my_switcher4);
                switcher4.showNext(); //or switcher.showPrevious();
                TextView editContact = (TextView) switcher4.findViewById(R.id.hidden_edit_view4);
                String contact = profileContact.getText().toString();
                editContact.setText(contact);

                ViewSwitcher switcher5 = (ViewSwitcher)getView().findViewById(R.id.my_switcher5);
                switcher5.showNext(); //or switcher.showPrevious();
                TextView editDescription = (TextView) switcher5.findViewById(R.id.hidden_edit_view5);
                String desc = profileDesc.getText().toString();
                editDescription.setText(desc);

            }
        });
        hidden1 = (EditText) rootView.findViewById(R.id.hidden_edit_view1);
        hidden2 = (EditText) rootView.findViewById(R.id.hidden_edit_view2);
        hidden3 = (EditText) rootView.findViewById(R.id.hidden_edit_view3);
        hidden4 = (EditText) rootView.findViewById(R.id.hidden_edit_view4);
        hidden5 = (EditText) rootView.findViewById(R.id.hidden_edit_view5);

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnSaveEdit.setVisibility(View.GONE);
                //to switch back views
                ViewSwitcher switcher1 = (ViewSwitcher) getView().findViewById(R.id.my_switcher1);
                ViewSwitcher switcher2 = (ViewSwitcher) getView().findViewById(R.id.my_switcher2);
                ViewSwitcher switcher3 = (ViewSwitcher) getView().findViewById(R.id.my_switcher3);
                ViewSwitcher switcher4 = (ViewSwitcher) getView().findViewById(R.id.my_switcher4);
                ViewSwitcher switcher5 = (ViewSwitcher) getView().findViewById(R.id.my_switcher5);

                String newName = hidden1.getText().toString();
                String newAge = hidden2.getText().toString();
                String newDegree = hidden3.getText().toString();
                String newContact = hidden4.getText().toString();
                String newDesc = hidden5.getText().toString();

                //savinng it to entity name
                FirebaseDatabase userDatabaseProfile = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReferenceProfile = userDatabaseProfile.getReference();
                userDatabaseReferenceProfile.child("Users").child(userID).child("name").setValue(newName);
                //age
                userDatabaseReferenceProfile.child("Users").child(userID).child("age").setValue(newAge);
                //degree
                userDatabaseReferenceProfile.child("Users").child(userID).child("degree").setValue(newDegree);
                //contact
                userDatabaseReferenceProfile.child("Users").child(userID).child("contact").setValue(newContact);
                //description
                //userDatabaseReferenceProfile.child("Users").child(userID).child("description").setValue(newDesc);

                profileName.setText(newName);
                profileAge.setText(newAge);
                profileContact.setText(newContact);
                profileDegree.setText(newDegree);
                profileDesc.setText(newDesc);

                switcher1.showPrevious(); //or switcher.showPrevious();
                switcher2.showPrevious();
                switcher3.showPrevious();
                switcher4.showPrevious();
                switcher5.showPrevious();
            }

        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
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

                profileName.setText(userEntity.getName());
                profileAge.setText(" " + userEntity.getAge());
                profileDegree.setText(userEntity.getDegree());
                profileContact.setText(" " + userEntity.getContact());
            }
        }

    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
                uploadImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    protected void uploadImage() {
        if(filePath != null) {
            uploadingPD.show();

            StorageReference childRef = storageRef.child(userID + ".jpg");

            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadingPD.dismiss();
                    refreshImage();
                    Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadingPD.dismiss();
                    refreshImage();
                    Toast.makeText(getActivity(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getActivity(), "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshImage() {
        if (getActivity() != null) {
            myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(activity).using(new FirebaseImageLoader()).load(myRef).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(imgView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Glide.with(activity).using(new FirebaseImageLoader()).load(defaultRef).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(imgView);
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


