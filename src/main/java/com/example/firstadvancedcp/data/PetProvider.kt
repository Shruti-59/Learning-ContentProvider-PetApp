package com.example.firstadvancedcp.data

import PetContract
import PetContract.PetEntry
import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log


class PetProvider : ContentProvider() {
    companion object {
        val LOG_TAG = PetProvider::class.java.simpleName

        private const val PETS = 100

        private const val PET_ID = 101

        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        // FOR URI MATCHING
        init {

            sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS)

            sUriMatcher.addURI(
                PetContract.CONTENT_AUTHORITY,
                PetContract.PATH_PETS.toString() + "/#",
                PET_ID
            )
        }
    }

    /** Database helper object  */
    private var mDbHelper: PetDbHelper? = null
    override fun onCreate(): Boolean {
        mDbHelper = PetDbHelper(context)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        // Get readable database
        var selection = selection
        var selectionArgs = selectionArgs
        val database = mDbHelper!!.readableDatabase

        // This cursor will hold the result of the query
        val cursor: Cursor

        // Figure out if the URI matcher can match the URI to a specific code
        val match = sUriMatcher.match(uri)
        when (match) {
            PETS ->
                cursor = database.query(
                    PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder
                )
            PET_ID -> {

                selection = PetEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                cursor = database.query(
                    PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder
                )
            }
            else -> throw IllegalArgumentException("Cannot query unknown URI $uri")
        }

        cursor.setNotificationUri(context!!.contentResolver, uri)

        // Return the cursor
        return cursor
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val match = sUriMatcher.match(uri)
        return when (match) {
            PETS -> insertPet(uri, contentValues)
            else -> throw IllegalArgumentException("Insertion is not supported for $uri")
        }
    }


    private fun insertPet(uri: Uri, values: ContentValues?): Uri? {
        // Check that the name is not null
        val name = values!!.getAsString(PetEntry.COLUMN_PET_NAME)
            ?: throw IllegalArgumentException("Pet requires a name")

        // Check that the gender is valid
        val gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER)
        require(!(gender == null || !PetEntry.isValidGender(gender))) { "Pet requires valid gender" }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        val weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT)
        require(!(weight != null && weight < 0)) { "Pet requires valid weight" }

        // No need to check the breed, any value is valid (including null).

        // Get writeable database
        val database = mDbHelper!!.writableDatabase

        // Insert the new pet with the given values
        val id = database.insert(PetEntry.TABLE_NAME, null, values)
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1L) {
            Log.e(LOG_TAG, "Failed to insert row for $uri")
            return null
        }

        // Notify all listeners that the data has changed for the pet content URI
        context!!.contentResolver.notifyChange(uri, null)

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id)
    }

    override fun update(
        uri: Uri, contentValues: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        val match = sUriMatcher.match(uri)
        return when (match) {
            PETS -> updatePet(uri, contentValues, selection, selectionArgs)
            PET_ID -> {
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                updatePet(uri, contentValues, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Update is not supported for $uri")
        }
    }


    private fun updatePet(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values!!.containsKey(PetEntry.COLUMN_PET_NAME)) {
            val name = values.getAsString(PetEntry.COLUMN_PET_NAME)
                ?: throw IllegalArgumentException("Pet requires a name")
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            val gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER)
            require(!(gender == null || !PetEntry.isValidGender(gender))) { "Pet requires valid gender" }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            val weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT)
            require(!(weight != null && weight < 0)) { "Pet requires valid weight" }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0
        }

        // Otherwise, get writeable database to update the data
        val database = mDbHelper!!.writableDatabase

        // Perform the update on the database and get the number of rows affected
        val rowsUpdated = database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs)

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }

        // Return the number of rows updated
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        // Get writeable database
        var selection = selection
        var selectionArgs = selectionArgs
        val database = mDbHelper!!.writableDatabase

        // Track the number of rows that were deleted
        val rowsDeleted: Int
        val match = sUriMatcher.match(uri)
        when (match) {
            PETS ->                 // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs)
            PET_ID -> {
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Deletion is not supported for $uri")
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }

        // Return the number of rows deleted
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        val match = sUriMatcher.match(uri)
        return when (match) {
            PETS -> PetEntry.CONTENT_LIST_TYPE
            PET_ID -> PetEntry.CONTENT_ITEM_TYPE
            else -> throw IllegalStateException("Unknown URI $uri with match $match")
        }
    }
}