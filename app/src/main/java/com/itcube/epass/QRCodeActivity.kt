package com.itcube.epass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class QRCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()







        override fun onStart() {
            super.onStart()
            val mUser = mAuth!!.currentUser
            val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
            mUserReference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firstName: String = snapshot.child("firstName").value as String
                    val lastName: String = snapshot.child("lastName").value as String
                    tvName!!.text = "$firstName $lastName"
                    tvPosition!!.text = snapshot.child("position").value as String
                    tvBirthday!!.text = snapshot.child("birthday").value as String

                    try {
                        bitmap = TextToImageEncode(mAuth!!.currentUser?.uid.toString())
                        imQR!!.setImageBitmap(bitmap)
                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

        override fun onBackPressed() {
            finishAffinity()
        }

        @Throws(WriterException::class)
        private fun TextToImageEncode(Value: String): Bitmap? {
            val bitMatrix: BitMatrix
            try {
                bitMatrix = MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
                )

            } catch (Illegalargumentexception: IllegalArgumentException) {

                return null
            }

            val bitMatrixWidth = bitMatrix.getWidth()

            val bitMatrixHeight = bitMatrix.getHeight()

            val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

            for (y in 0 until bitMatrixHeight) {
                val offset = y * bitMatrixWidth

                for (x in 0 until bitMatrixWidth) {

                    pixels[offset + x] = if (bitMatrix.get(x, y))
                        resources.getColor(R.color.black)
                    else
                        resources.getColor(R.color.white)
                }
            }
            val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)

            bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
            return bitmap
        }

        companion object {

            val QRcodeWidth = 500
            private val IMAGE_DIRECTORY = "/QRcodeDemonuts"
        }
    }
}