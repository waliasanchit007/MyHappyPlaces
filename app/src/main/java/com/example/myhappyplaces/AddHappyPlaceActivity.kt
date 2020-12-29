package com.example.myhappyplaces

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import com.example.myhappyplaces.databinding.ActivityAddHappyPlaceBinding
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity() , View.OnClickListener {
    private val calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
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
        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }
        binding.etDate.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date-> DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH) ).show()
        }
    }

    private fun updateDate(){
        val format = "dd MMM, yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        binding.etDate.setText(sdf.format(calendar.time).toString())
    }
}