package com.example.firstadvancedcp.data

import PetContract.PetEntry

import android.content.Context
import android.database.Cursor
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.example.firstadvancedcp.R


class PetCursorAdapter

    (context: Context?, c: Cursor?) : CursorAdapter(context, c, 0 /* flags */) {

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
    }


    override fun bindView(view: View, context: Context, cursor: Cursor) {
        // Find individual views that we want to modify in the list item layout
        val nameTextView = view.findViewById(R.id.name) as TextView
        val summaryTextView = view.findViewById(R.id.summary) as TextView

        // Find the columns of pet attributes that we're interested in
        val nameColumnIndex: Int = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME)
        val breedColumnIndex: Int = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED)

        // Read the pet attributes from the Cursor for the current pet
        val petName: String = cursor.getString(nameColumnIndex)
        var petBreed: String = cursor.getString(breedColumnIndex)

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(petBreed)) {
            petBreed = context.getString(R.string.unknown_breed)
        }

        // Update the TextViews with the attributes for the current pet
        nameTextView.text = petName
        summaryTextView.text = petBreed
    }
}