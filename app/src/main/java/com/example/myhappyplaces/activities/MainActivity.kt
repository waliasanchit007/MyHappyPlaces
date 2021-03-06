package com.example.myhappyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE )
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
        val placesAdapter = HappyPlaceAdapter(happyPlaceList, this)
        binding.rvHappyPlacesList.adapter = placesAdapter

        placesAdapter.setOnClickListener(object : HappyPlaceAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity,
                        HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            }
        }else{
            Log.e("Activity", "No new happy place added")
        }
    }
    companion object{
       private val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        val EXTRA_PLACE_DETAILS = "extra_place_details"
    }
}