package com.example.mystoryapplication.view.add_story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.mystoryapplication.R
import com.example.mystoryapplication.data.api.ApiConfig
import com.example.mystoryapplication.data.request.StoryRequest
import com.example.mystoryapplication.data.response.StoryResponse
import com.example.mystoryapplication.databinding.ActivityAddStoryBinding
import com.example.mystoryapplication.view.ViewModelFactory
import com.example.mystoryapplication.view.camerax.CameraActivity
import com.example.mystoryapplication.view.camerax.CameraActivity.Companion.CAMERAX_RESULT
import com.example.mystoryapplication.view.home.ListStoryActivity
import com.example.mystoryapplication.view.main.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import kotlin.properties.Delegates

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat by Delegates.notNull<Float>()
    private var lon by Delegates.notNull<Float>()
    private var addLocation: Boolean = false
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.CAMERA] ?: false -> {
                    // Only approximate location access granted.
//                    allPermissionsGranted()
                }
                else -> {
                    // No location access granted.
                    Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        title = "Add Story"
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setTheme(R.style.Theme_SecondActivity)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCameraX() }
        binding.buttonAdd.setOnClickListener {
            showLoading(true)
            viewModel.getSession().observe(this) {
                uploadImage(it.token)
            }
        }

        binding.addLocation.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                addLocation = true
            } else {
                addLocation = false
            }}

        getMyLastLocation()
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showToast("No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage(token: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            showLoading(true)

            val descRequest: RequestBody
            val latRequest: Float?
            val lonRequest: Float?

            if (addLocation) {
                // Jika `addLocation` true, buat JSON body dengan lat, lon, dan description
//                val storyRequest = StoryRequest(description, lat, lon)
//                val jsonBody = Gson().toJson(storyRequest)
//                jsonBody.toRequestBody("application/json".toMediaType()) // Menggunakan "application/json"

                descRequest = description.toRequestBody("text/plain".toMediaType())
                latRequest = lat
                lonRequest = lon
            } else {
                // Jika `addLocation` false, buat requestBody hanya dengan description
                descRequest = description.toRequestBody("text/plain".toMediaType())
                latRequest = null
                lonRequest = null
            }
//            descRequest = description.toRequestBody("text/plain".toMediaType())

//            val jsonBody = Gson().toJson(data)
//            requestBody = jsonBody.toRequestBody("text/plain".toMediaType())
//            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService(token)
                    val successResponse = apiService.uploadImage(multipartBody, descRequest, latRequest, lonRequest)
                    showToast(successResponse.message)
                    showLoading(false)
                    val intent = Intent(this@AddStoryActivity, ListStoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
                    errorResponse.message?.let { showToast(it) }
                    showLoading(false)
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
//                    showStartMarker(location)
                    lat = location.latitude.toFloat()
                    lon = location.longitude.toFloat()
                    Log.d("Location", "Latitude: $lat, Longitude: $lon")
                } else {
                    Log.d("locationStatus", "permission granted, location null")
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Log.d("locationStatus", "permission deny")
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA
                )
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}