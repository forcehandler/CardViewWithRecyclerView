package com.example.abhiraj.cardviewwithrecyclerview.signup;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.abhiraj.cardviewwithrecyclerview.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by Abhiraj on 11-02-2017.
 */

public class GoogleSignUp extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleSignInActivity.class.getSimpleName();
    // Context of the calling class
    Context mContext;

    // Sign in button
    private SignInButton mSignInButton;

    // Firebase Authentication object
    private FirebaseAuth mFirebaseAuth;

    // Firebase Authentication Listener
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;

    // Signing options google
    private GoogleSignInOptions gso;

    // google api client
    private GoogleApiClient mGoogleApiClient;

    // Signin constant to check activity result
    private static final int RC_SIGN_IN = 9001;

    public void initializeSignUpButton(Context context, SignInButton signInButton, FirebaseAuth mAuth)
    {
        mContext = context;
        this.mSignInButton = signInButton;
        this.mFirebaseAuth = mAuth;
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setOnClickListener(this);


        // Initializing google sign in option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.default_web_client_id))
                .requestEmail().build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


    }

    // Create sign in intent
    private void signIn()
    {
        // Creating signin dialog intent
        Intent signInIntent = Auth.GoogleSignInApi
                .getSignInIntent(mGoogleApiClient);

        // Starting signin activity with the signin intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    // To handle result from signin dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Check request code
        if(requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi
                    .getSignInResultFromIntent(data);

            // function to handle sign in
            handleSignInResult(result);

        }
    }
    // [START handleSignInResult]
    // Function is called after recieving signin intent from dialog
    private void handleSignInResult(GoogleSignInResult result) {

        // If the login is successful
        if(result.isSuccess())
        {
            // Grab the google account!
            GoogleSignInAccount account = result.getSignInAccount();

            // Authenticate with firebase
            firebaseAuthWithGoogle(account);



            // We can grab image url from GoogleSignInAccount object to display
            // user's image <code> String url = account.getPhotoUrl().toString() </code>
        }

        else // If login fails
        {
            Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
        }
    }
    // [END handleSignInResult]

    // [START firebaseAuthWithGoogle]
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(GoogleSignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
    // [END firebaseAuthWithGoogle]


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
