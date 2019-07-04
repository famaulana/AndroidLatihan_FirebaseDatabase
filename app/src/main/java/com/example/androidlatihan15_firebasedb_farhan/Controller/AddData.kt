package com.example.androidlatihan15_firebasedb_farhan.Controller

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.androidlatihan15_firebasedb_farhan.Adapter.PrefsHelper
import com.example.androidlatihan15_firebasedb_farhan.R
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.add_data.*
import kotlinx.android.synthetic.main.upload_image.*
import java.io.IOException
import java.util.*

class AddData : AppCompatActivity() {

    lateinit var dbRef: DatabaseReference
    lateinit var helperPref: PrefsHelper
    var datax: String? = null

    //Upload Image
    val REQUEST_IMAGE = 10002
    val PERMISSION_REQUEST_CODE = 10003
    lateinit var filePathImage : Uri
    var value = 0.0
    lateinit var fStorage : FirebaseStorage
    lateinit var fStrorageRef : StorageReference

    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_data)

        helperPref = PrefsHelper(this)
        fStorage = FirebaseStorage.getInstance()
        fStrorageRef = fStorage.reference

        datax = intent.getStringExtra("kode")
        if (datax != null) {
            showDataUpdate()
        }

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

        image_buku.setOnClickListener {
            when{
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)->{
                    if (ContextCompat.checkSelfPermission(this@AddData, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                    }else{
                        imageChooser()
                    }
                }
                else -> {
                    imageChooser()
                }
            }
        }
    }

    //Image Upload
    private fun imageChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "select image"),REQUEST_IMAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_REQUEST_CODE ->{
                if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this@AddData, "Izin Ditolak!!", Toast.LENGTH_SHORT).show()
                }else{
                    imageChooser()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK){
            return
        }
        when(requestCode){
            REQUEST_IMAGE ->{
                filePathImage = data?.data!!
                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, filePathImage)
                    uploadImage()
                    Glide.with(this).load(bitmap).override(250,250).centerCrop().into(image_buku)
                }catch (x : IOException){
                    x.printStackTrace()
                }
            }
        }
    }

    fun GetFileExtension(uri: Uri) : String?{
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    fun uploadImage(){
        val nameX = UUID.randomUUID().toString()
        val uid = helperPref.getUID()
        val ref = fStrorageRef.child("Image/$uid/${nameX}.${GetFileExtension(filePathImage)}")
        ref.putFile(filePathImage).addOnSuccessListener {
            Toast.makeText(this@AddData, "Success Upload", Toast.LENGTH_SHORT).show()
            progressImgBuku.visibility = View.GONE
            ref.downloadUrl.addOnSuccessListener {
                url = it.toString()
            }
        }.addOnFailureListener {
            Log.e("TAG_ERROR", it.message)
        }.addOnProgressListener {
                taskSnapshot -> value = (100.0*taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount)
            progressImgBuku.visibility = View.VISIBLE
        }
    }

    fun saveToFireBase(nama: String, judul: String, tanggal: String, desc: String) {
        val nameX = UUID.randomUUID().toString()
        val uid = helperPref.getUID()
        val uidUser = helperPref.getUID()
        val counterID: Int
        if (datax != null) {
            counterID = datax!!.toInt()
        } else {
            counterID = helperPref.getCounterId()
        }

        val images = FirebaseStorage.getInstance().getReference("Image/$uid/${nameX}.${GetFileExtension(filePathImage)}")

        dbRef = FirebaseDatabase.getInstance().getReference("Data Buku/$uidUser")
        dbRef.child("$counterID/id").setValue(uidUser)
        dbRef.child("$counterID/namaPenulis").setValue(nama)
        dbRef.child("$counterID/judulBuku").setValue(judul)
        dbRef.child("$counterID/tanggal").setValue(tanggal)
        dbRef.child("$counterID/description").setValue(desc)
        dbRef.child("$counterID/image").setValue(url)

        Toast.makeText(this, "Data Berhasil Di Tambah", Toast.LENGTH_SHORT).show()
        if (datax == null) {
            helperPref.saveCounterId(counterID + 1)
        }
        onBackPressed()
    }

    fun showDataUpdate() {
        dbRef = FirebaseDatabase.getInstance().getReference("Data Buku/${helperPref.getUID()}/${datax}")
        Log.e("Link", dbRef.toString())
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                et_namaPenulis.setText(p0.child("namaPenulis").value.toString())
                et_judulBuku.setText(p0.child("judulBuku").value.toString())
                et_tanggal.setText(p0.child("tanggal").value.toString())
                et_deskripsi.setText(p0.child("description").value.toString())

            }

        })
    }
}