package com.itcube.epass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edEmail:EditText = findViewById(R.id.edEmail)
        val edPassword:EditText = findViewById(R.id.edPassword)
        val btnLogin:TextView = findViewById(R.id.textView)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()

        if(mAuth!!.currentUser != null){
            val intent = Intent(this, QRCodeActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            if (edEmail.text.isEmpty() or edPassword.text.isEmpty()){
                Toast.makeText(this, "Поля пустые", Toast.LENGTH_SHORT).show()
            }
            else if (!edEmail.text.contains('@')){
                Toast.makeText(this, "Email неверный", Toast.LENGTH_SHORT).show()
            }
            else{
                email = edEmail.text.toString()
                password = edPassword.text.toString()
                mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("ggg", "signInWithEmail:success")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.e("ggg", "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}