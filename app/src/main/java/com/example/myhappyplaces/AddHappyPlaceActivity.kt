package com.example.myhappyplaces

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myhappyplaces.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException
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
        binding.tvAddImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date-> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH) ).show()
            }
            //Two options would be provide to user 1) to select a photo from gallery 2)open camera and click photo
            R.id.tv_add_image-> {
                val pictureDialog = AlertDialog.Builder(this@AddHappyPlaceActivity)
                pictureDialog.setTitle("Select Action")
                pictureDialog.setItems(arrayOf("Select Photo from Gallery", "Click photo using camera")){
                    dialogInterface: DialogInterface, i: Int ->
                        when(i) {
                            0 -> choosePhotoFromGallery()
                            1 -> Toast.makeText(this@AddHappyPlaceActivity,
                                    "Camera option coming soon",
                                    Toast.LENGTH_LONG).show()
                        }
                }
                pictureDialog.show()
            }
        }
    }

    private fun updateDate(){
        val format = "dd MMM, yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        binding.etDate.setText(sdf.format(calendar.time).toString())
    }

    private fun choosePhotoFromGallery(){
        //using Dexter for multiple Permissions
        Dexter.withContext(this)
                .withPermissions( android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA)
                .withListener(object:MultiplePermissionsListener{
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        if(p0!!.areAllPermissionsGranted()){
                            Toast.makeText(this@AddHappyPlaceActivity,
                                    "All permissions are granted",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                        showRationaleDialogForPermissions()
                    }
                }).onSameThread().check()
    }
    private fun showRationaleDialogForPermissions(){
        AlertDialog.Builder(this@AddHappyPlaceActivity)
                .setMessage("Looks like you have denied permissions." +
                        " It can be granted in Application Settings")
                .setPositiveButton("Go To Settings"){
                    dialogInterface: DialogInterface, i: Int ->
                    try {
                        //open application settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package",packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }catch (e:IOException){
                        e.printStackTrace()
                    }
                }.setNegativeButton("Cancel"){
                    dialogInterface: DialogInterface, i: Int ->dialogInterface.dismiss()
                }.show()
    }
}

