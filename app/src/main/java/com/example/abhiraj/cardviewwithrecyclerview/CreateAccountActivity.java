package com.example.abhiraj.cardviewwithrecyclerview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class CreateAccountActivity extends BaseActivity implements View.OnClickListener, VerificationListener {

    private static final String TAG = CreateAccountActivity.class.getSimpleName();

    private FirebaseAuth mAuth;

    //listener to authentication state changes
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button mCreateUserButton;
    private Button mVerifyUserButton;
    private EditText mOtpEditText;
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
        Log.d(TAG, "in onStart");
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

     @Override
     public void onDestroy()
     {
         mVerification = null;
         super.onDestroy();
     }

    @Override
    public void onPause()
    {
        //mVerification = null;
        super.onPause();
    }

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Enable local caching on Firebase


        mAuth = FirebaseAuth.getInstance();

        createAuthStateListener();
        mVerifyUserButton = (Button) findViewById(R.id.verifyOtpButton);
        mCreateUserButton = (Button) findViewById(R.id.createUserButton);
        mOtpEditText = (EditText) findViewById(R.id.otpEditText);
        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mPhoneEditText = (EditText) findViewById(R.id.phoneEditText);
        mAgeEditText = (EditText) findViewById(R.id.ageEditText);
        mGenderEditText = (EditText) findViewById(R.id.genderEditText);
        mBloodEditText = (EditText) findViewById(R.id.bloodEditText);

        mCreateUserButton.setOnClickListener(this);
        mVerifyUserButton.setOnClickListener(this);


    }

    private void createAuthStateListener()
    {
        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(CreateAccountActivity.this, TemporaryOfferActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                //updateUI(user);
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "in onClick");
        if (v == mCreateUserButton) {
            createNewUser();

        }
        if (v == mVerifyUserButton) {
            if(mOtpEditText.getText().toString().trim().equals("")) {
                Log.d(TAG, mOtpEditText.getText().toString().equals(null) + " " + mOtpEditText.getText().toString());
                mOtpEditText.setError("Please enter your Otp");
            }
            else
            {
                if(mVerification != null) {
                    mVerification.verify(mOtpEditText.getText().toString());
                }
            }
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

    // Create Account with Email and Password

    private void createAnonymousUser(final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);


        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(CreateAccountActivity.this, "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();

                            // If the user is unable to authenticate then sign in the user in the
                            // background using his phone number and default password
                            signIn(email, password);

                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Sign in failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        //hideProgressDialog();

                    }
                });
    }


    private void createNewUser() {
        Log.d(TAG, "in createNewUser");
        final String name = mNameEditText.getText().toString().trim();
        final String phone = mPhoneEditText.getText().toString().trim();

        boolean validPhone = isValidPhone(phone);
        boolean validName = isValidName(name);
        if (!validPhone || !validName){
            return;
        }

        //TODO: Implement OTP properly, currently only for testing purposes
        mVerification = SendOtpVerification.createSmsVerification(this, phone, this, "91", true);
        mVerification.initiate(); //sending otp on given number

        //TODO: storing user values when the values are sane, in later version
        //TODO: store values in preferences after OTP is matched

    }

    private void storeUserData() {
        Log.d(TAG, "in storeUserData");
        String name = mNameEditText.getText().toString();
        String phone = mPhoneEditText.getText().toString();
        String age = mAgeEditText.getText().toString();
        String gender = mGenderEditText.getText().toString();
        String blood = mBloodEditText.getText().toString();
        //TODO: fetch uid from server, currently setting it up as the current time
        LocalDate now = new LocalDate();
        String uid = now.toString();
        User userObj = new User(uid, name, age, phone, gender, blood);
        //store user preferences locally
        MySharedPreferences.setUserPreference(Constants.USER_PREF_KEY, userObj, getApplicationContext());
        //TODO: sync this with firebase
        MyFireBaseDatabase.updateUserDatabase(userObj);

        // TODO: login via firebase
        String phone_email = phone + Constants.USER_DEFAULT_EMAIL_DOMAIN;
        String password = Constants.USER_DEFAULT_PASSWORD;
        createAnonymousUser(phone_email, password);

    }


    //Methods for OTP from Msg91
    @Override
    public void onInitiated(String response) {
        Log.d(TAG, "got on initiated callback from otp");
        Toast.makeText(this, "sent the otp", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitiationFailed(Exception paramException) {
        Log.d(TAG, "got on failed initialization callback from otp");
        Toast.makeText(this, "initiation failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onVerified(String response) {
        Log.d(TAG, "got on verification successful callback from otp");
        Toast.makeText(this, "verification successful", Toast.LENGTH_LONG).show();
        storeUserData();
    }

    @Override
    public void onVerificationFailed(Exception paramException) {
        Log.d(TAG, "got on verification failed callback from otp");
        Toast.makeText(this, "verification failed", Toast.LENGTH_LONG).show();

    }
}
