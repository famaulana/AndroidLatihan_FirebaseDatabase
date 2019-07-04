package com.example.androidlatihan15_firebasedb_farhan.Controller

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log.e
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.androidlatihan15_firebasedb_farhan.Adapter.BukuAdapter
import com.example.androidlatihan15_firebasedb_farhan.Adapter.PrefsHelper
import com.example.androidlatihan15_firebasedb_farhan.Model.BukuModel
import com.example.androidlatihan15_firebasedb_farhan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BukuAdapter.FirebaseDataListener {

    lateinit var fAuth: FirebaseAuth
    private var bukuAdapter: BukuAdapter? = null
    private var rcView: RecyclerView? = null
    private var list: MutableList<BukuModel>? = null
    lateinit var dbref: DatabaseReference
    lateinit var helperPrefs: PrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fAuth = FirebaseAuth.getInstance()
        helperPrefs = PrefsHelper(this)

        rcView = findViewById(R.id.recyclerView)
        rcView!!.layoutManager = LinearLayoutManager(this)
        rcView!!.setHasFixedSize(true)

        dbref = FirebaseDatabase.getInstance().getReference("Data Buku/${helperPrefs.getUID()}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                e("TAG_ERROR", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                list = ArrayList<BukuModel>()
                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(BukuModel::class.java)
                    addDataAll!!.setKey(dataSnapshot.key!!)
                    list!!.add(addDataAll!!)
                    bukuAdapter =
                        BukuAdapter(this@MainActivity, list!!)
                }
                rcView!!.adapter = bukuAdapter
            }

        })

        fab.setOnClickListener {
            startActivity(Intent(this, AddData::class.java))
        }

        fab_upload.setOnClickListener {
            startActivity(Intent(this, UploadFileStorage::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflatermenu = menuInflater
        inflatermenu.inflate(R.menu.option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.btn_logout -> {
                fAuth.signOut()
                startActivity(Intent(this, Login::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onUpdateData(buku: BukuModel, position: Int) {
        val datax = buku.getKey()
        val intent = Intent(this, AddData::class.java)
        intent.putExtra("kode", datax)
        startActivity(intent)
    }

    override fun onDeleteData(buku: BukuModel, position: Int) {
        dbref = FirebaseDatabase.getInstance().getReference("Data Buku/${helperPrefs.getUID()}")
        if (dbref != null) {
            dbref.child(buku.getKey()).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Data Removed", Toast.LENGTH_SHORT).show()
                bukuAdapter!!.notifyDataSetChanged()
            }
        }
    }
}
