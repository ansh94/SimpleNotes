package com.example.android.simplenotes.notes;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.simplenotes.MainActivity;
import com.example.android.simplenotes.R;
import com.example.android.simplenotes.data.TaskContract;
import com.example.android.simplenotes.util.PrefManager;

/**
 * Created by ANSHDEEP on 29-04-2017.
 */

public class AddNoteFragment extends Fragment {

    EditText noteTitle;
    EditText noteDescription;


    // Mandatory empty constructor
    public AddNoteFragment() {
    }

    // Inflates the layout of add note fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_add_note, container, false);

        noteTitle = (EditText) rootView.findViewById(R.id.addnote_title);
        noteDescription = (EditText) rootView.findViewById(R.id.addnote_desc);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.addnote_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddTask();
            }
        });

        // Return the root view
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.add_note_title));
    }

    /**
     * onClickAddTask is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onClickAddTask() {

        // Reset errors.
        noteTitle.setError(null);
        noteDescription.setError(null);

        boolean cancel = false;
        View focusView = null;

        String title = noteTitle.getText().toString();
        String description = noteDescription.getText().toString();


        // Check if the title or description is empty
        if (TextUtils.isEmpty(title)) {
            noteTitle.setError(getString(R.string.title_empty));
            focusView = noteTitle;
            cancel = true;
        } else if (TextUtils.isEmpty(description)) {
            noteDescription.setError(getString(R.string.description_empty));
            focusView = noteDescription;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt to add the note and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            String email = new PrefManager(getActivity()).getEmail();

            // Insert new task data via a ContentResolver
            // Create new empty ContentValues object
            ContentValues contentValues = new ContentValues();
            // Put the task title, description and user email into the ContentValues
            contentValues.put(TaskContract.TaskEntry.COLUMN_TITLE, title);
            contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, description);
            contentValues.put(TaskContract.TaskEntry.COLUMN_EMAIL, email);
            // Insert the content values via a ContentResolver
            Uri uri = getActivity().getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

            // Display a Toast if note is added
            if (uri != null) {
                Toast.makeText(getActivity(), R.string.note_added, Toast.LENGTH_SHORT).show();
            }

            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }


    }


}
