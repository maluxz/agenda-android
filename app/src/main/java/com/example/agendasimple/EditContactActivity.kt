package com.example.agendasimple

import android.app.Activity
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class EditContactActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private lateinit var dbHelper: ContactsDbHelper
    private lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)

        nameEditText = findViewById(R.id.name_edit_text)
        phoneEditText = findViewById(R.id.phone_edit_text)
        emailEditText = findViewById(R.id.email_edit_text)
        saveButton = findViewById(R.id.save_button)
        deleteButton = findViewById(R.id.delete_button)

        dbHelper = ContactsDbHelper(this)

        val contactId = intent.getLongExtra(CONTACT_ID_EXTRA, -1)
        if (contactId == -1.toLong()) {
            finish()
            return
        }

        // Obtener el contacto seleccionado de la base de datos
        contact = dbHelper.getContact(contactId.toLong())!!

        // Mostrar los datos del contacto en los EditText correspondientes
        nameEditText.setText(contact.name)
        phoneEditText.setText(contact.phone)
        emailEditText.setText(contact.email)

        saveButton.setOnClickListener {
            // Actualizar la información del contacto con los datos ingresados
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()

            if (name.isEmpty()) {
                nameEditText.error = "Nombre requerido"
                nameEditText.requestFocus()
                return@setOnClickListener
            }

            dbHelper.updateContact(Contact(contactId.toLong(), name, phone, email))

            // Mostrar un mensaje de éxito y finalizar la actividad
            Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }

        deleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar Contacto")
                .setMessage("¿Seguro que desea eliminar este contacto?")
                .setPositiveButton("Sí") { dialog, which ->
                    dbHelper.deleteContact(contactId)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

    }

    companion object {
        const val CONTACT_ID_EXTRA = "contact_id"
        const val EDIT_CONTACT_REQUEST_CODE = 1
    }

}
