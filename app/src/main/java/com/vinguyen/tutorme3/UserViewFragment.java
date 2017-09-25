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


public class UserViewFragment extends Fragment {

    private FirebaseDatabase userDatabaseCreate;
    private DatabaseReference userReference;
    private TextView profileName, profileAge, profileDegree, profileContact;
    private ImageView imgView;
    String userID;
    private StorageReference myRef, defaultRef, storageRef;
    private Button becomeStudent;
    private FirebaseDatabase userDatabase;
    private DatabaseReference userDatabaseReference;
    private String userIDCurrent;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_view, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userID = bundle.getString("message");
        }

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
}
