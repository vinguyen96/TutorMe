package com.vinguyen.tutorme3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName, inputAge, inputDegree, inputContact, inputSuburb, captchaText, inputDescription;
    private Button btnSignIn, btnSignUp;
    private String email, password, name, age, degree, contact, suburb, description;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    protected Bitmap image;
    protected String answer = "";
    private int width;
    protected int height;
    protected int x = 0;
    protected int y = 0;
    protected static List usedColors;
    private Mathcap mathCaptcha;
    private EditText Captchatext;
    private ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputName = (EditText) findViewById(R.id.name);
        inputAge = (EditText) findViewById(R.id.age);
        inputDegree = (EditText) findViewById(R.id.degree);
        inputContact = (EditText) findViewById(R.id.contact);
        inputSuburb = (EditText) findViewById(R.id.suburb);
        inputDescription = (EditText) findViewById(R.id.description);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        /*btnSignUp.setVisibility(View.GONE);
        inputName.setVisibility(View.GONE);
        inputAge.setVisibility(View.GONE);
        inputDegree.setVisibility(View.GONE);
        inputContact.setVisibility(View.GONE);
        inputSuburb.setVisibility(View.GONE);
        inputDescription.setVisibility(View.GONE);*/

        imageView1 = (ImageView)findViewById(R.id.imageView);
        captchaText = (EditText) findViewById(R.id.captchaText);
        mathCaptcha = new Mathcap(600, 150, Mathcap.MathOptions.PLUS_MINUS);
        imageView1.setImageBitmap(mathCaptcha.getImage());
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
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
                if (checkString(password)==false){
                    Toast.makeText(getApplicationContext(), "Password must have at least 1 numerical digit, an uppercase and a lowercase character!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mathCaptcha.checkAnswer(captchaText.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Captcha does not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                name = inputName.getText().toString().trim();
                age = inputAge.getText().toString().trim();
                degree = inputDegree.getText().toString().trim();
                contact = inputContact.getText().toString().trim();
                suburb = inputSuburb.getText().toString().trim();
                description = inputDescription.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(degree) || TextUtils.isEmpty(contact) || TextUtils.isEmpty(suburb)|| TextUtils.isEmpty(description)) {
                    String empty = getResources().getString(R.string.empty);
                    Toast.makeText(getApplicationContext(), empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, " " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference userDatabaseReference = userDatabase.getReference("Users");
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                UserEntity userEntity = new UserEntity(name, age, degree, contact, suburb, description);
                                userDatabaseReference.child(currentFirebaseUser.getUid()).setValue(userEntity);

                                startActivity(new Intent(SignupActivity.this, NavigationDrawerActivity.class));
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    /*btnNext.setVisibility(View.GONE);
                                    inputEmail.setVisibility(View.GONE);
                                    inputPassword.setVisibility(View.GONE);
                                    btnSignUp.setVisibility(View.VISIBLE);
                                    inputName.setVisibility(View.VISIBLE);
                                    inputAge.setVisibility(View.VISIBLE);
                                    inputDegree.setVisibility(View.VISIBLE);
                                    inputContact.setVisibility(View.VISIBLE);
                                    inputSuburb.setVisibility(View.VISIBLE);
                                    inputDescription.setVisibility(View.VISIBLE);*/
                                }
                            }
                        });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private static boolean checkString(String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for(int i=0;i < str.length();i++) {
            ch = str.charAt(i);
            if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            }
            else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if(numberFlag && capitalFlag && lowerCaseFlag){
                return true;
            }
        }
        return false;
    }
}


