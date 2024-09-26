package com.example.fetoapp

import com.google.firebase.auth.FirebaseAuth

class Utils {

    companion object{

        private val auth = FirebaseAuth.getInstance()

        private var userid : String =""

        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
        const val PROFILE_IMAGE_CAPTURE = 3
        const val PROFILE_IMAGE_PICK = 4

        fun getUidLogged() : String {
            if (auth.currentUser != null){

                userid = auth.currentUser!!.uid!!
            }

            return userid

        }

        fun getTime() : Long{
            val unixTimeStamp: Long = System.currentTimeMillis()/1000

            return unixTimeStamp
        }

    }
}