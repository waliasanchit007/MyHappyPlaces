package com.example.myhappyplaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myhappyplaces.databinding.ActivityAddHappyPlaceBinding

class AddHappyPlaceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddHappyPlaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarAddActivity)

        binding.toolbarAddActivity.setNavigationOnClickListener {
            onBackPressed()
        }
        
    }
}