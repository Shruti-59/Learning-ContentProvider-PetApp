package com.example.firstadvancedcp.ui

import PetContract
import PetContract.PetEntry
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.firstadvancedcp.R


class EditorActivity : AppCompatActivity() {

    private var mNameEditText: EditText? = null

    private var mBreedEditText: EditText? = null

    private var mWeightEditText: EditText? = null

    private var mGenderSpinner: Spinner? = null

    private var mGender: Int = PetContract.PetEntry.GENDER_UNKNOWN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        // Find all relevant views that we will need to read user input from

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById<View>(R.id.edit_pet_name) as EditText
        mBreedEditText = findViewById<View>(R.id.edit_pet_breed) as EditText
        mWeightEditText = findViewById<View>(R.id.edit_pet_weight) as EditText
        mGenderSpinner = findViewById<View>(R.id.spinner_gender) as Spinner

        setupSpinner()
    }

    private fun setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        val genderSpinnerAdapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            this,
            R.array.array_gender_options, android.R.layout.simple_spinner_item
        )

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        // Apply the adapter to the spinner
        mGenderSpinner!!.adapter = genderSpinnerAdapter

        // Set the integer mSelected to the constant values
        mGenderSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selection = parent.getItemAtPosition(position) as String
                if (!TextUtils.isEmpty(selection)) {
                    mGender = if (selection == getString(R.string.gender_male)) {
                        PetContract.PetEntry.GENDER_MALE
                    } else if (selection == getString(R.string.gender_female)) {
                        PetContract.PetEntry.GENDER_FEMALE
                    } else {
                        PetContract.PetEntry.GENDER_UNKNOWN
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            override fun onNothingSelected(parent: AdapterView<*>?) {
                mGender = PetContract.PetEntry.GENDER_UNKNOWN
            }
        }
    }

    /**
     * Get user input from editor and save new pet into database.
     */

    private fun insertPet() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        val nameString = mNameEditText!!.text.toString().trim { it <= ' ' }
        val breedString = mBreedEditText!!.text.toString().trim { it <= ' ' }
        val weightString = mWeightEditText!!.text.toString().trim { it <= ' ' }
        val weight = weightString.toInt()

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        val values = ContentValues()
        values.put(PetEntry.COLUMN_PET_NAME, nameString)
        values.put(PetEntry.COLUMN_PET_BREED, breedString)
        values.put(PetEntry.COLUMN_PET_GENDER, mGender)
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight)

        // Insert a new pet into the provider, returning the content URI for the new pet.
        val newUri: Uri? = contentResolver.insert(PetEntry.CONTENT_URI, values)

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(
                this, getString(R.string.editor_insert_pet_failed),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(
                this, getString(R.string.editor_insert_pet_successful),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        menuInflater.inflate(R.menu.menu_editor, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // User clicked on a menu option in the app bar overflow menu
        when (item.itemId) {
            R.id.action_save -> {
                // Save pet to database
                insertPet()
                // Exit activity
                finish()
                return true
            }
            R.id.action_delete ->                 // Do nothing for now
                return true
            android.R.id.home -> {
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}