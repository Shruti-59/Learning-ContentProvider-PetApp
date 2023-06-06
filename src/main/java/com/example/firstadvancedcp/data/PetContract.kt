import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns


object PetContract {

    const val CONTENT_AUTHORITY = "com.example.firstadvancedcp"


    val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)

    const val PATH_PETS = "pets"

    object PetEntry : BaseColumns {

        val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS)
        const val CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS

        const val CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS


        const val TABLE_NAME = "pets"

        const val _ID = BaseColumns._ID


        const val COLUMN_PET_NAME = "name"

        const val COLUMN_PET_BREED = "breed"

        const val COLUMN_PET_GENDER = "gender"

        const val COLUMN_PET_WEIGHT = "weight"

        const val GENDER_UNKNOWN = 0
        const val GENDER_MALE = 1
        const val GENDER_FEMALE = 2

        fun isValidGender(gender: Int): Boolean {
            return if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                true
            } else false
        }
    }
}

