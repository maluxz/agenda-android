package com.example.agendasimple
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.provider.BaseColumns._ID

class ContactsDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "Contacts.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "contacts"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSql = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, $COLUMN_PHONE TEXT, $COLUMN_EMAIL TEXT)"
        db?.execSQL(createTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableSql = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableSql)
        onCreate(db)
    }

    fun addContact(contact: Contact): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, contact.name)
        values.put(COLUMN_PHONE, contact.phone)
        values.put(COLUMN_EMAIL, contact.email)
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val contact = Contact(
                    cursor.getLong(cursor.getColumnIndex(_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
                )
                contacts.add(contact)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return contacts
    }

    fun getSpecifiedContacts(cursor: Cursor): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = this.readableDatabase
        //val query = "SELECT * FROM $TABLE_NAME"
        //val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val contact = Contact(
                    cursor.getLong(cursor.getColumnIndex(_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
                )
                contacts.add(contact)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return contacts
    }


    fun updateContact(contact: Contact): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(_ID, contact.id)
        values.put(COLUMN_NAME, contact.name)
        values.put(COLUMN_PHONE, contact.phone)
        values.put(COLUMN_EMAIL, contact.email)
        val rowsAffected = db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(contact.id.toString()))
        db.close()
        return rowsAffected
    }

    fun deleteContact(contact: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(contact.toString()))
        db.close()
    }

    object ContactsContract {
        object ContactEntry : BaseColumns {
            const val TABLE_NAME = "contacts"
            const val COLUMN_NAME_NAME = "name"
            const val COLUMN_NAME_PHONE = "phone"
            const val COLUMN_NAME_EMAIL = "email"
        }
    }

    fun getContact(id: Long): Contact? {
        val db = this.readableDatabase

        val projection = arrayOf(
            COLUMN_ID,
            ContactsContract.ContactEntry.COLUMN_NAME_NAME,
            ContactsContract.ContactEntry.COLUMN_NAME_PHONE,
            ContactsContract.ContactEntry.COLUMN_NAME_EMAIL
        )

        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.query(
            ContactsContract.ContactEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var contact: Contact? = null

        with(cursor) {
            if (moveToFirst()) {
                val name = getString(getColumnIndexOrThrow(ContactsContract.ContactEntry.COLUMN_NAME_NAME))
                val phone = getString(getColumnIndexOrThrow(ContactsContract.ContactEntry.COLUMN_NAME_PHONE))
                val email = getString(getColumnIndexOrThrow(ContactsContract.ContactEntry.COLUMN_NAME_EMAIL))
                contact = Contact(id, name, phone, email)
            }
            close()
        }

        return contact
    }

    fun searchContactsByName(name: String): Cursor {
        val db = this.readableDatabase
        val selection = "${ContactsContract.ContactEntry.COLUMN_NAME_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$name%")
        return db.query(
            ContactsContract.ContactEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            ContactsContract.ContactEntry.COLUMN_NAME_NAME + " ASC"
        )
    }

}
