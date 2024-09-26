@file:Suppress("DEPRECATION")

package com.example.fetoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

    }

    override fun onBackPressed() {
        super.onBackPressed()

        if(supportFragmentManager.backStackEntryCount>0){

            super.onBackPressed()

        } else {

            if(navController.currentDestination?.id == R.id.profileFragment){

                moveTaskToBack(true)

            } else{

                super.onBackPressed()

            }

        }
    }



}