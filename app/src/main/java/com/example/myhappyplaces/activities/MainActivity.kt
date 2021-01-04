package com.example.myhappyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myhappyplaces.R
import com.example.myhappyplaces.adapters.HappyPlaceAdapter
import com.example.myhappyplaces.databases.SQLiteDatabaseHandler
import com.example.myhappyplaces.databinding.ActivityMainBinding
import com.example.myhappyplaces.models.HappyPlaceModel

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
        getHappyPlacesListFromLocalDB()
    }
    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler = SQLiteDatabaseHandler(this)
        val happyPlaceList = dbHandler.getHappyPlacesList()

        if(happyPlaceList.size > 0){
            binding.rvHappyPlacesList.visibility = View.VISIBLE
            binding.tvNoRecordAvailable.visibility = View.GONE
            setupHappyPlaceRecyclerView(happyPlaceList)
        }else{
            binding.rvHappyPlacesList.visibility = View.GONE
            binding.tvNoRecordAvailable.visibility = View.VISIBLE
        }
    }

    private fun setupHappyPlaceRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>){
        binding.rvHappyPlacesList.layoutManager = LinearLayoutManager(this )
        binding.rvHappyPlacesList.setHasFixedSize(true)
        binding.rvHappyPlacesList.adapter = HappyPlaceAdapter(happyPlaceList, this)

    }
}