package com.example.android.simplenotes.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.simplenotes.MainActivity;
import com.example.android.simplenotes.R;
import com.example.android.simplenotes.util.PrefManager;

/**
 * Created by ANSHDEEP on 28-04-2017.
 */

public class LoginFragment extends Fragment {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;


    // Mandatory empty constructor
    public LoginFragment() {
    }


    // Define a new interface OnLoginClickListener that triggers a callback in the host activity
    OnLoginClickListener mCallback;

    // OnLoginClickListener interface, calls a method in the host activity named onLoginSuccessful
    public interface OnLoginClickListener {
        void onLoginSuccessful();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.login_title));
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnLoginClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    // Inflates the layout of login fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);


        // Set up the login form.
        mEmailView = (EditText) rootView.findViewById(R.id.email);
        mPasswordView = (EditText) rootView.findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) rootView.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });


        //Here we will check if user is logged out or not
        if (!new PrefManager(getActivity()).isUserLoggedOut()) {
            mCallback.onLoginSuccessful();
        }


        // Return the root view
        return rootView;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // save data in local shared preferences
            saveLoginDetails(email, password);
            mCallback.onLoginSuccessful();
        }
    }


    private void saveLoginDetails(String email, String password) {
        new PrefManager(getActivity()).saveLoginDetails(email, password);
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
