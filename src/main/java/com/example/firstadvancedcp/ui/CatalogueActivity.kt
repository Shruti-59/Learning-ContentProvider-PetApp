package com.example.firstadvancedcp.ui

import PetContract.PetEntry
import android.app.LoaderManager
import android.content.*

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.firstadvancedcp.R

import com.example.firstadvancedcp.data.PetCursorAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CatalogueActivity : AppCompatActivity(),
    LoaderManager.LoaderCallbacks<Cursor> {
    /** Adapter for the ListView  */
    var mCursorAdapter: PetCursorAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogue)

        // Setup FAB to open EditorActivity
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

        // Find the ListView which will be populated with the pet data
        val petListView = findViewById<View>(R.id.list) as ListView

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        val emptyView = findViewById<View>(R.id.empty_view)
        petListView.emptyView = emptyView

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = PetCursorAdapter(this, null)
        petListView.adapter = mCursorAdapter

        // Setup the item click listener
        petListView.onItemClickListener =
            OnItemClickListener { adapterView, view, position, id -> // Create new intent to go to {@link EditorActivity}
                val intent = Intent(this, EditorActivity::class.java)

                val currentPetUri: Uri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id)

                // Set the URI on the data field of the intent
                intent.data = currentPetUri

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent)
            }

        // Kick off the loader
        loaderManager.initLoader(PET_LOADER, null, this)
    }


    private fun insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        val values = ContentValues()
        values.put(PetEntry.COLUMN_PET_NAME, "Toto")
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier")
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE)
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7)

        val newUri: Uri? = contentResolver.insert(PetEntry.CONTENT_URI, values)
    }


    private fun deleteAllPets() {
        val rowsDeleted = contentResolver.delete(PetEntry.CONTENT_URI, null, null)
        Log.v("CatalogActivity", "$rowsDeleted rows deleted from pet database")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // User clicked on a menu option in the app bar overflow menu
        when (item.itemId) {
            R.id.action_insert_dummy_data -> {
                insertPet()
                return true
            }
            R.id.action_delete_all_entries -> {
                deleteAllPets()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        // Define a projection that specifies the columns from the table we care about.
        val projection = arrayOf(
            PetEntry._ID,
            PetEntry.COLUMN_PET_NAME,
            PetEntry.COLUMN_PET_BREED
        )

        // This loader will execute the ContentProvider's query method on a background thread
        return CursorLoader(
            this,  // Parent activity context
            PetEntry.CONTENT_URI,  // Provider content URI to query
            projection,  // Columns to include in the resulting Cursor
            null,  // No selection clause
            null,  // No selection arguments
            null
        ) // Default sort order
    }


    companion object {
        /** Identifier for the pet data loader  */
        private const val PET_LOADER = 0
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter!!.swapCursor(data as Cursor?)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Callback called when the data needs to be deleted
        mCursorAdapter!!.swapCursor(null)
    }
}