package com.example.abhiraj.cardviewwithrecyclerview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import org.joda.time.LocalDate;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener, VerificationListener {

    private static final String TAG = CreateAccountActivity.class.getSimpleName();

    private FirebaseAuth mAuth;

    //listener to authentication state changes
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button mCreateUserButton;
    private EditText mNameEditText;
    private EditText mPhoneEditText;
    private EditText mAgeEditText;
    private EditText mGenderEditText;
    private EditText mBloodEditText;

    private ProgressDialog mAuthProgressDialog;

    //listener for OTP callbacks
    private Verification mVerification;

    @Override
    public void onStart()
    {
        super.onStart();

    }

     @Override public void onStop()
     {
         super.onStop();
         if(mAuthListener != null)
         {
             mAuth.removeAuthStateListener(mAuthListener);
         }
         if(mVerification != null)
         {
            // unregisterReceiver();
         }
     }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        mAuth = FirebaseAuth.getInstance();
        createAuthProgressDialog();

        mCreateUserButton = (Button) findViewById(R.id.createUserButton);
        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mPhoneEditText = (EditText) findViewById(R.id.phoneEditText);
        mAgeEditText = (EditText) findViewById(R.id.ageEditText);
        mGenderEditText = (EditText) findViewById(R.id.genderEditText);
        mBloodEditText = (EditText) findViewById(R.id.bloodEditText);

        mCreateUserButton.setOnClickListener(this);

        //listen to authentication events
        createAuthStateListener();

        mAuth.addAuthStateListener(mAuthListener);


    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {

        if (v == mCreateUserButton) {
            createNewUser();
        }
    }

    private boolean isValidPhone(String phone) {
        boolean isGoodPhone =
                (phone != null && android.util.Patterns.PHONE.matcher(phone).matches());
        if (!isGoodPhone) {
            mPhoneEditText.setError("Please enter a valid email address");
            return false;
        }
        return isGoodPhone;
    }

    private boolean isValidName(String name) {
        if (name.equals("")) {
            mNameEditText.setError("Please enter your name");
            return false;
        }
        return true;
    }

    //TODO: add is valid age

    /*private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            mAgeEditText.setError("Please create a password containing at least 6 characters");
            return false;
        } else if (!password.equals(confirmPassword)) {
            mAgeEditText.setError("Passwords do not match");
            return false;
        }
        return true;
    }*/

    private void createNewUser() {
        final String name = mNameEditText.getText().toString().trim();
        final String phone = mPhoneEditText.getText().toString().trim();
        /*String password = mAgeEditText.getText().toString().trim();
        String confirmPassword = mGenderEditText.getText().toString().trim();*/

        boolean validPhone = isValidPhone(phone);
        boolean validName = isValidName(name);
        /*boolean validPassword = isValidPassword(password, confirmPassword);*/
        if (!validPhone || !validName){
            return;
        }

        //TODO: Implement OTP properly, currently only for testing purposes
        mVerification = SendOtpVerification.createSmsVerification(this, "9130643253",this, "91");
        mVerification.initiate(); //sending otp on given number

        //TODO: storing user values when the values are sane, in later version
        //TODO: store values in preferences after OTP is matched
        storeUserData();
        //create an emailAddress out of the phone number
        final String email = phone + Constants.USER_DEFAULT_EMAIL_DOMAIN;

        mAuthProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, Constants.USER_DEFAULT_PASSWORD)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        mAuthProgressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Authentication successful");

                        } else {
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void storeUserData() {
        String name = mNameEditText.getText().toString();
        String phone = mPhoneEditText.getText().toString();
        String age = mAgeEditText.getText().toString();
        String gender = mGenderEditText.getText().toString();
        String blood = mBloodEditText.getText().toString();
        //TODO: fetch uid from server, currently setting it up as the current time
        LocalDate now = new LocalDate();
        String uid = now.toString();
        User userObj = new User(uid, name,age,phone, gender, blood);
        //store user preferences locally
        MySharedPreferences.setUserPreference(Constants.USER_PREF_KEY,userObj, getApplicationContext());
        //TODO: sync this with firebase
        MyFireBaseDatabase.updateUserDatabase(userObj);
    }

    private void createAuthStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

        };

    }

    //Methods for OTP from Msg91
    @Override
    public void onInitiated(String response) {
        Toast.makeText(this, "sent the otp", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitiationFailed(Exception paramException) {

    }

    @Override
    public void onVerified(String response) {

    }

    @Override
    public void onVerificationFailed(Exception paramException) {

    }
}
