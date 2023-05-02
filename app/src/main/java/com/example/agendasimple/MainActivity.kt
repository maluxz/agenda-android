package com.example.agendasimple

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.agendasimple.EditContactActivity.Companion.EDIT_CONTACT_REQUEST_CODE


class MainActivity : AppCompatActivity() {

    private lateinit var db: ContactsDbHelper
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var contactsListView: ListView

    private lateinit var searchEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        db = ContactsDbHelper(this)

        //nameEditText = findViewById(R.id.name_edit_text)
        //phoneEditText = findViewById(R.id.phone_edit_text)
        //emailEditText = findViewById(R.id.email_edit_text)


        contactsListView = findViewById(R.id.contacts_list_view)

        //val addContactButton = findViewById<View>(R.id.add_contact_button)
        //addContactButton.setOnClickListener { addContact() }

        val addActivityButton = findViewById<View>(R.id.add_activity_button)
        addActivityButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)

            //startActivity(intent)
            startActivityForResult(intent, EDIT_CONTACT_REQUEST_CODE) //De esta forma al terminar la activity actualiza
        }

        val contacts = db.getAllContacts()
        val contactsListAdapter = ContactsListAdapter(contacts)
        contactsListView.adapter = contactsListAdapter

        contactsListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val contacts = db.getAllContacts()
            val contact = contacts[position]

            val intent = Intent(this, EditContactActivity::class.java)
            intent.putExtra(EditContactActivity.CONTACT_ID_EXTRA, contact.id)

            //startActivity(intent)
            startActivityForResult(intent, EDIT_CONTACT_REQUEST_CODE) //De esta forma al terminar la activity actualiza
        }

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("TAG", "onQueryTextSubmit")
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredContacts = db.searchContactsByName(newText)
                val contacts = db.getSpecifiedContacts(filteredContacts)

                val contactsListAdapter = ContactsListAdapter(contacts)
                contactsListView.adapter = contactsListAdapter
                //adapter.swapCursor(filteredContacts)
                //Log.d("TAG", "$resultadoBusqueda")
                return true
            }
        })

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_CONTACT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // actualizar la lista de contactos
            updateContactsList()
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
        clearForm()
        updateContactsList()
    }


    private fun updateContactsList() {
        val contacts = db.getAllContacts()
        val contactsListAdapter = ContactsListAdapter(contacts)
        contactsListView.adapter = contactsListAdapter
    }

    private fun clearForm() {
        nameEditText.setText("")
        phoneEditText.setText("")
        emailEditText.setText("")
    }




    private inner class ContactsListAdapter(private val contacts: List<Contact>) : BaseAdapter() {

        override fun getCount(): Int {
            return contacts.size
        }

        override fun getItem(position: Int): Any {
            return contacts[position]
        }

        override fun getItemId(position: Int): Long {
            return contacts[position].id
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView
            if (view == null) {
                view = LayoutInflater.from(parent?.context).inflate(R.layout.contact_list_item, parent, false)
            }
            val contact = getItem(position) as Contact
            val nameTextView = view?.findViewById<TextView>(R.id.name_text_view)
            val phoneTextView = view?.findViewById<TextView>(R.id.phone_text_view)
            val emailTextView = view?.findViewById<TextView>(R.id.email_text_view)
            nameTextView?.text = contact.name
            phoneTextView?.text = contact.phone
            emailTextView?.text = contact.email
            return view!!
        }


    }
}
