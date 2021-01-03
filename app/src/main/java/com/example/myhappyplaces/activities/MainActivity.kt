package com.example.myhappyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myhappyplaces.R
import com.example.myhappyplaces.databases.SQLiteDatabaseHandler
import com.example.myhappyplaces.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAddHappyPlace.setOnClickListener{
            val intent = Intent(this, AddHappyPlaceActivity::class.java )
            startActivity(intent)
        }
    }
    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler = SQLiteDatabaseHandler(this)
        val happyPlaceList = dbHandler.getHappyPlacesList()

    }
}