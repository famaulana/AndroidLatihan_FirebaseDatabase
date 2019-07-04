package com.example.androidlatihan15_firebasedb_farhan.Controller

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log.d
import android.util.Log.e
import android.widget.Toast
import com.example.androidlatihan15_firebasedb_farhan.Adapter.PrefsHelper
import com.example.androidlatihan15_firebasedb_farhan.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.login.*

class Login : AppCompatActivity() {

    //For Request Code
    private val RC_SIGN_IN = 7
    //For Sign In Client
    private lateinit var mGoogleSignIn: GoogleSignInClient
    //For Firebase Authentication
    private lateinit var fAuth: FirebaseAuth

    private lateinit var helperPref: PrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        //Inialitation
        helperPref = PrefsHelper(this)
        fAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        mGoogleSignIn = GoogleSignIn.getClient(this, gso)
        sign_in_google.setOnClickListener {
            signIn()
        }

        btn_signup.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        btn_login.setOnClickListener {
            val email = et_email.text.toString()
            val password = et_password.text.toString()

            if (email.isNotEmpty() || password.isNotEmpty() || !email.equals("") || !password.equals("")) {
                fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java))
                }.addOnFailureListener {
                    Toast.makeText(this, "Your email or password WRONG!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Fill all", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignIn.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        d("FAUTH_LOGIN", "firebaseAuth : ${account.id}")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        fAuth.signInWithCredential(credential).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val user = fAuth.currentUser
                updateUI(user)
            } else {
                Toast.makeText(this, "Login Filed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            helperPref.saveUID(user.uid)
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            e("TAG_ERROR", "user tidak ada")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = fAuth.currentUser
        if (user != null) {
            updateUI(user)
        }
    }

}