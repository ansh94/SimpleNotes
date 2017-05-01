package com.example.android.simplenotes.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ANSHDEEP on 28-04-2017.
 */

// A simple utility class which contain methods to access the Shared Preferences.
public class PrefManager {

    Context context;

    public PrefManager(Context context) {
        this.context = context;
    }

    public void saveLoginDetails(String email, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.commit();
    }

    public String getEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }

    public boolean isUserLoggedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
        return isEmailEmpty;
    }

    public void logout() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

    }

}
