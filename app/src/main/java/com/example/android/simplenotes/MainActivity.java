package com.example.android.simplenotes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.simplenotes.login.LoginFragment;
import com.example.android.simplenotes.notes.NotesFragment;


public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_layout, new LoginFragment(), "login")
                    .commit();
        }


    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onLoginSuccessful() {
        NotesFragment notesFragment = new NotesFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, notesFragment, "notes")
                .commit();
    }
}
