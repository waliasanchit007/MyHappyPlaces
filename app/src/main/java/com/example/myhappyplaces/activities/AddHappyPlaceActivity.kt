package com.example.myhappyplaces.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myhappyplaces.R
import com.example.myhappyplaces.databases.SQLiteDatabaseHandler
import com.example.myhappyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.myhappyplaces.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddHappyPlaceActivity : AppCompatActivity() , View.OnClickListener {
    private var mLatitude: Double = 0.0 // A variable which will hold the latitude value.
    private var mLongitude: Double = 0.0 // A variable which will hold the longitude value.

    private var saveImageToInternalStorage : Uri? = null

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
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }
        binding.etDate.setOnClickListener(this)
        binding.tvAddImage.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date -> {
                DatePickerDialog(
                        this@AddHappyPlaceActivity,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
            //Two options would be provide to user 1) to select a photo from gallery 2)open camera and click photo
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this@AddHappyPlaceActivity)
                pictureDialog.setTitle("Select Action")
                pictureDialog.setItems(arrayOf("Select Photo from Gallery", "Click photo using camera")) { dialogInterface: DialogInterface, i: Int ->
                    when (i) {
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }

            R.id.btn_save -> {

                when {
                    binding.etTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                    }
                    binding.etDescription.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT)
                                .show()
                    }
                    binding.etLocation.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT)
                                .show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please add image", Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                        // Assigning all the values to data model class.
                        val happyPlaceModel = HappyPlaceModel(
                                0,
                                binding.etTitle.text.toString(),
                                saveImageToInternalStorage.toString(),
                                binding.etDescription.text.toString(),
                                binding.etDate.text.toString(),
                                binding.etLocation.text.toString(),
                                mLatitude,
                                mLongitude
                        )

                        // Here we initialize the database handler class.
                        val dbHandler = SQLiteDatabaseHandler(this)

                        val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)

                        if (addHappyPlace > 0) {
                            setResult(Activity.RESULT_OK)
                            finish()//finishing activity
                        }
                    }

                }
            }
        }
    }

    private fun takePhotoFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
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
                .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        if (p0!!.areAllPermissionsGranted()) {
                            Toast.makeText(this@AddHappyPlaceActivity,
                                    "All permissions are granted",
                                    Toast.LENGTH_SHORT).show()
                            openImageSelectIntent()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                        showRationaleDialogForPermissions()
                    }
                }).onSameThread().check()
    }

    private fun openImageSelectIntent() {
       val galleryIntent = Intent(Intent.ACTION_PICK,
               MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                if (data != null) {
                    val contentUri = data.data
                    var bitmap: Bitmap? = null
                    if (Build.VERSION.SDK_INT >= 29) {
                        val source = ImageDecoder.createSource(applicationContext.contentResolver, contentUri!!)
                        try {
                            bitmap = ImageDecoder.decodeBitmap(source)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, contentUri!!)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    saveImageToInternalStorage =
                            saveImageToInternalStorage(bitmap!!)
                    Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                    binding.ivPlaceImage.setImageBitmap(bitmap)

                }
            }else if(requestCode == IMAGE_CAPTURE_REQUEST_CODE){
                val imageBitmap = data!!.extras!!.get("data") as Bitmap

                saveImageToInternalStorage =
                        saveImageToInternalStorage(imageBitmap)
                Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")
                binding.ivPlaceImage.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        /**
         * The Mode Private here is
         * File creation mode: the default mode, where the created file can only
         * be accessed by the calling application (or all applications sharing the
         * same user ID).
         */
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.absolutePath)
    }


    private fun showRationaleDialogForPermissions(){
        AlertDialog.Builder(this@AddHappyPlaceActivity)
                .setMessage("Looks like you have denied permissions." +
                        " It can be granted in Application Settings")
                .setPositiveButton("Go To Settings"){ dialogInterface: DialogInterface, i: Int ->
                    try {
                        //open application settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }.setNegativeButton("Cancel"){ dialogInterface: DialogInterface, i: Int ->dialogInterface.dismiss()
                }.show()
    }

    companion object{
        private const val IMAGE_DIRECTORY = "HappyPlaceImages"
        private const val GALLERY_REQUEST_CODE = 1
        private const val IMAGE_CAPTURE_REQUEST_CODE = 2
    }
}

