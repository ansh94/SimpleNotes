package com.example.android.simplenotes.notes;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.simplenotes.data.CustomCursorAdapter;
import com.example.android.simplenotes.login.LoginFragment;
import com.example.android.simplenotes.MainActivity;
import com.example.android.simplenotes.R;
import com.example.android.simplenotes.data.TaskContract;
import com.example.android.simplenotes.util.PrefManager;


public class NotesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, CustomCursorAdapter.CustomCursorAdapterOnClickHandler {


    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    // Member variables for the adapter and RecyclerView
    private CustomCursorAdapter mAdapter;
    RecyclerView mRecyclerView;


    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_notes, container, false);


        setHasOptionsMenu(true);


        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewTasks);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CustomCursorAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);


        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Retrieve the id of the task to delete
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = TaskContract.TaskEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // Delete a single row of data using a ContentResolver
                getActivity().getContentResolver().delete(uri, null, null);

                Toast.makeText(getActivity(), R.string.note_deleted, Toast.LENGTH_SHORT).show();

                // Restart the loader to re-query for all tasks after a deletion
                getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, NotesFragment.this);

            }
        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to launch the AddNoteFragment which will replace the
         current fragment and add it to back stack.
         */
        FloatingActionButton fabButton = (FloatingActionButton) rootView.findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root_layout, new AddNoteFragment(), "addnotes")
                        .addToBackStack(null)
                        .commit();

            }
        });

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == R.id.logout) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("Logout");
            alert.setMessage("Are you sure you want to logout?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startLoginFragment();
                    dialog.dismiss();
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Simple Notes");

        // re-queries for all tasks
        getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     * <p>
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(getActivity()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {

                try {
                    return getActivity().getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                            null,
                            "email=?",
                            new String[]{new PrefManager(getActivity()).getEmail()},
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }


    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    // This will start the Login Fragment which will replace the current fragment when logout button
    // is pressed.
    private void startLoginFragment() {
        Log.d("NotesFragment", "startLoginFragment: reached");
        new PrefManager(getActivity()).logout();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, new LoginFragment(), "login")
                .commit();
    }

    // This will start the Edit Note Fragment which will replace the current fragment and add it
    // to the back stack.
    @Override
    public void onNoteEditClick(int id, String title, String description) {
        final EditNoteFragment fragment = EditNoteFragment.newInstance(id, title, description);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, fragment, "editnote")
                .addToBackStack(null)
                .commit();
    }
}
