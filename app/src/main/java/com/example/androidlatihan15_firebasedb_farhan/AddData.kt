package com.example.androidlatihan15_firebasedb_farhan

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.add_data.*

class AddData : AppCompatActivity() {

    lateinit var dbRef: DatabaseReference
    lateinit var helperPref: PrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_data)

        helperPref = PrefsHelper(this)

        btn_save.setOnClickListener {
            val nama = et_namaPenulis.text.toString()
            val judul = et_judulBuku.text.toString()
            val tanggal = et_tanggal.text.toString()
            val desc = et_deskripsi.text.toString()

            if (nama.isNotEmpty() || judul.isNotEmpty() || tanggal.isNotEmpty() || desc.isNotEmpty()) {
                saveToFireBase(nama, judul, tanggal, desc)
            } else {
                Toast.makeText(this, "Fill all text", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveToFireBase(nama: String, judul: String, tanggal: String, desc: String) {
        val uidUser = helperPref.getUID()
        val counterID = helperPref.getCounterId()

        dbRef = FirebaseDatabase.getInstance().getReference("Data Buku/$uidUser")
        dbRef.child("$counterID/id").setValue(uidUser)
        dbRef.child("$counterID/namaPenulis").setValue(nama)
        dbRef.child("$counterID/judulBuku").setValue(judul)
        dbRef.child("$counterID/tanggal").setValue(tanggal)
        dbRef.child("$counterID/description").setValue(desc)

        Toast.makeText(this, "Data Berhasil Di Tambah", Toast.LENGTH_SHORT).show()
        helperPref.saveCounterId(counterID + 1)
        onBackPressed()
    }
}