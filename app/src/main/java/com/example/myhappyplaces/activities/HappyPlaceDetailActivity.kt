package com.example.myhappyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myhappyplaces.R
import com.example.myhappyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.myhappyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.example.myhappyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHappyPlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var happyPlaceDetailModel : HappyPlaceModel? = null
        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            happyPlaceDetailModel = intent.getSerializableExtra(
                    MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }

        if (happyPlaceDetailModel != null){
            binding.toolbarHappyPlaceDetail.title = happyPlaceDetailModel.title
            binding.toolbarHappyPlaceDetail.setNavigationOnClickListener {
                onBackPressed()
            }

            binding.ivPlaceImage.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            binding.tvDescription.text = happyPlaceDetailModel.description
            binding.tvLocation.text = happyPlaceDetailModel.location
        }

    }
}