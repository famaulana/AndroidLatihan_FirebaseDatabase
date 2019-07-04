package com.example.androidlatihan15_firebasedb_farhan.Controller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.androidlatihan15_firebasedb_farhan.Adapter.PrefsHelper
import com.example.androidlatihan15_firebasedb_farhan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.register.*

class Register : AppCompatActivity() {

    lateinit var fAuth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    lateinit var helperPref: PrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        fAuth = FirebaseAuth.getInstance()
        helperPref = PrefsHelper(this)

        btn_register.setOnClickListener {
            val nama = et_nama.text.toString()
            val email = et_email.text.toString()
            val password = et_password.text.toString()
            if (email.isNotEmpty() || password.isNotEmpty() || !email.equals("") || !password.equals("")) {
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveToDatabase(nama, email, password)
                    } else {
                        Toast.makeText(this, "Registration Filed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please Fill all", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToDatabase(nama: String, email: String, password: String) {
        val counterID = helperPref.getCounterId()

        dbRef = FirebaseDatabase.getInstance().getReference("Data User/${fAuth.currentUser?.uid}")
        dbRef.child("userID").setValue(fAuth.currentUser?.uid)
        dbRef.child("namaUser").setValue(nama)
        dbRef.child("Email").setValue(email)
        dbRef.child("Password").setValue(password)

        Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
        helperPref.saveCounterId(counterID + 1)
        onBackPressed()
    }
}