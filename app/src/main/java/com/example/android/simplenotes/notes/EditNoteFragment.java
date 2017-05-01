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

/**
 * Created by ANSHDEEP on 29-04-2017.
 */

public class EditNoteFragment extends Fragment {

    private static final String ARGUMENT_NOTE_ID = "noteId";
    private static final String ARGUMENT_NOTE_TITLE = "title";
    private static final String ARGUMENT_NOTE_DESCRIPTION = "description";


    public static EditNoteFragment newInstance(int noteId, String title,
                                               String description) {
        final Bundle args = new Bundle();
        args.putInt(ARGUMENT_NOTE_ID, noteId);
        args.putString(ARGUMENT_NOTE_TITLE, title);
        args.putString(ARGUMENT_NOTE_DESCRIPTION, description);
        final EditNoteFragment fragment = new EditNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_note, container, false);

        final EditText noteTitle = (EditText) view.findViewById(R.id.addnote_title_edit);
        final EditText noteDescription = (EditText) view.findViewById(R.id.addnote_desc_edit);

        final FloatingActionButton fabEdit = (FloatingActionButton) view.findViewById(R.id.addnote_fab_edit);


        final Bundle args = getArguments();
        final String oldTitle = args.getString(ARGUMENT_NOTE_TITLE);
        final String oldDescription = args.getString(ARGUMENT_NOTE_DESCRIPTION);

        noteTitle.setText(oldTitle);
        noteDescription.setText(oldDescription);

        final int id = args.getInt(ARGUMENT_NOTE_ID);

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Reset errors.
                noteTitle.setError(null);
                noteDescription.setError(null);

                boolean cancel = false;
                View focusView = null;


                String newTitle = noteTitle.getText().toString();
                String newDescription = noteDescription.getText().toString();

                // Check if the title or description is empty
                if (TextUtils.isEmpty(newTitle)) {
                    noteTitle.setError(getString(R.string.title_empty));
                    focusView = noteTitle;
                    cancel = true;
                } else if (TextUtils.isEmpty(newDescription)) {
                    noteDescription.setError(getString(R.string.description_empty));
                    focusView = noteDescription;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt to edit the note and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    if (!newTitle.equals(oldTitle) || !newDescription.equals(oldDescription)) {
                        updateNote(newTitle, newDescription, id);
                    } else {
                        Toast.makeText(getActivity(), R.string.note_change_something, Toast.LENGTH_LONG).show();
                    }
                }


            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.edit_note_title));
    }

    private void updateNote(String newTitle, String newDescription, int id) {
        // Build appropriate uri with String row id appended
        String stringId = Integer.toString(id);
        Uri uri = TaskContract.TaskEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();


        // Insert new task data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the task title and description into the ContentValues
        contentValues.put(TaskContract.TaskEntry.COLUMN_TITLE, newTitle);
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, newDescription);


        // Insert the content values via a ContentResolver
        int tasksUpdated = getActivity().getContentResolver().update(uri, contentValues, null, null);

        // Display a Toast if note is updated
        if (tasksUpdated > 0) {
            Toast.makeText(getActivity(), R.string.note_updated, Toast.LENGTH_SHORT).show();
        }

        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }
}
