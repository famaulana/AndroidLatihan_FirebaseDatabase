package com.example.androidlatihan15_firebasedb_farhan

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log.e
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var fAuth: FirebaseAuth
    private var bukuAdapter: BukuAdapter? = null
    private var rcView: RecyclerView? = null
    private var list: MutableList<BukuModel> = ArrayList<BukuModel>()
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
                for (dataSnapshot in p0.children) {
                    val addDataAll = dataSnapshot.getValue(BukuModel::class.java)
                    list.add(addDataAll!!)
                }
                bukuAdapter = BukuAdapter(applicationContext, list)
                rcView!!.adapter = bukuAdapter
            }

        })

        fab.setOnClickListener {
            startActivity(Intent(this, AddData::class.java))
        }

//        btn_logout.setOnClickListener {
//            fAuth.signOut()
//            onBackPressed()
//        }
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
}
