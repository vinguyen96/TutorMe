package com.vinguyen.tutorme3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName, inputAge, inputDegree, inputContact;
    private Button btnSignIn, btnSignUp, btnNext;
    private String email, password, name, age, degree, contact;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnNext = (Button) findViewById(R.id.next_button);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputName = (EditText) findViewById(R.id.name);
        inputAge = (EditText) findViewById(R.id.age);
        inputDegree = (EditText) findViewById(R.id.degree);
        inputContact = (EditText) findViewById(R.id.contact);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignUp.setVisibility(View.GONE);
        inputName.setVisibility(View.GONE);
        inputAge.setVisibility(View.GONE);
        inputDegree.setVisibility(View.GONE);
        inputContact.setVisibility(View.GONE);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    btnNext.setVisibility(View.GONE);
                                    inputEmail.setVisibility(View.GONE);
                                    inputPassword.setVisibility(View.GONE);
                                    btnSignUp.setVisibility(View.VISIBLE);
                                    inputName.setVisibility(View.VISIBLE);
                                    inputAge.setVisibility(View.VISIBLE);
                                    inputDegree.setVisibility(View.VISIBLE);
                                    inputContact.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = inputName.getText().toString().trim();
                age = inputAge.getText().toString().trim();
                degree = inputDegree.getText().toString().trim();
                contact = inputContact.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(degree) || TextUtils.isEmpty(contact)) {
                    Toast.makeText(getApplicationContext(), "All fields must be filled in!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userDatabaseReference = userDatabase.getReference("Users");
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                UserEntity userEntity = new UserEntity(name, age, degree, contact);
                userDatabaseReference.child(currentFirebaseUser.getUid()).setValue(userEntity);

                startActivity(new Intent(SignupActivity.this, MainActivity.class));

                //progressBar.setVisibility(View.VISIBLE);
                //create user

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}