package com.example.agendasimple

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

lateinit var db: ContactsDbHelper
private lateinit var nameEditText: EditText
private lateinit var phoneEditText: EditText
private lateinit var emailEditText: EditText

class AddContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        db = ContactsDbHelper(this)

        nameEditText = findViewById(R.id.name_edit_text)
        phoneEditText = findViewById(R.id.phone_edit_text)
        emailEditText = findViewById(R.id.email_edit_text)

        val addContactButton = findViewById<Button>(R.id.add_contact_button)


        addContactButton.setOnClickListener {
            addContact()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}

private fun addContact() {
    val name = nameEditText.text.toString().trim()
    val phone = phoneEditText.text.toString().trim()
    val email = emailEditText.text.toString().trim()
    if (name.isEmpty()) {
        nameEditText.error = "Please enter a name"
        return
    }
    val contact = Contact(name = name, phone = phone, email = email)
    db.addContact(contact)
    //clearForm()
    //updateContactsList()
}