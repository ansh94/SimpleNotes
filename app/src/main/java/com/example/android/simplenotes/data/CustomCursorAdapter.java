package com.example.android.simplenotes.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.simplenotes.R;


/**
 * This CustomCursorAdapter creates and binds ViewHolders, that hold the title and description of a note,
 * to a RecyclerView to efficiently display data.
 */
public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.TaskViewHolder> {

    // Class variables for the Cursor that holds note data and the Context
    private Cursor mCursor;
    private Context mContext;


    private final CustomCursorAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface CustomCursorAdapterOnClickHandler {
        void onNoteEditClick(int id, String title, String description);
    }


    /**
     * Constructor for the CustomCursorAdapter that initializes the Context and Click Handler
     */
    public CustomCursorAdapter(Context mContext, CustomCursorAdapterOnClickHandler clickHandler) {
        this.mContext = mContext;
        mClickHandler = clickHandler;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout, parent, false);

        return new TaskViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {

        // Indices for the _id, description, and title columns
        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        int titleIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE);

        // get to the right location in the cursor
        mCursor.moveToPosition(position);

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        final String description = mCursor.getString(descriptionIndex);
        final String title = mCursor.getString(titleIndex);

        //Set values
        holder.itemView.setTag(id);
        holder.taskDescriptionView.setText(description);
        holder.taskTitleView.setText(title);

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickHandler.onNoteEditClick(id, title, description);
            }

        });

    }


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    // Inner class for creating ViewHolders
    class TaskViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and title TextViews
        // and edit ImageButton
        TextView taskDescriptionView;
        TextView taskTitleView;
        ImageButton editButton;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TaskViewHolder(View itemView) {
            super(itemView);

            taskDescriptionView = (TextView) itemView.findViewById(R.id.card_description);
            taskTitleView = (TextView) itemView.findViewById(R.id.card_title);
            editButton = (ImageButton) itemView.findViewById(R.id.edit_button);

        }
    }
}