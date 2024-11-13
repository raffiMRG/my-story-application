package com.example.mystoryapplication.view.maps_story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.mystoryapplication.R
import com.example.mystoryapplication.data.response.ListStoryItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mystoryapplication.databinding.ActivityMapsStoryBinding
import com.example.mystoryapplication.view.ViewModelFactory
import com.example.mystoryapplication.view.adapter.Adapter
import com.example.mystoryapplication.view.home.ListStoryViewModel
import com.google.android.gms.maps.model.LatLngBounds

class MapsStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsStoryBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private val viewModel by viewModels<MapsStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

//        -6.3465011,106.6892259
        val dicodingSpace = LatLng(-6.3465011, 106.6892259)
        mMap.addMarker(
            MarkerOptions()
                .position(dicodingSpace)
                .title("Unpam")
                .snippet("Batik Kumeli No.50")
        )

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))

        viewModel.getSession().observe(this){ data ->
            viewModel.getStoryModel(data.token)
            viewModel.listEvents.observe(this) { story ->
                story?.forEach {
                    val name = it?.name.toString()
                    val lat = it?.lat.toString()
                    val lon = it?.lon.toString()
                    Log.d("listStory", "nama : $name, \n latitude : $lat, \n longitude : $lon")

                    val latLng = LatLng(it?.lat!!, it.lon!!)
                    mMap.addMarker(MarkerOptions().position(latLng).title(it.name))
//                    boundsBuilder.include(latLng)

//                         BOUNDS FORCE
                    boundsBuilder.include(LatLng(-10.000000, 150.000000))
                    boundsBuilder.include(LatLng(10.000000, 95.000000))

                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            100
                        )
                    )
                }
            }
        }
//        addManyMarker()
    }

//    data class TourismPlace(
//        val name: String,
//        val latitude: Double,
//        val longitude: Double
//    )

//    private fun addManyMarker() {
//        val story = listOf(
////            TourismPlace("Floating Market Lembang", -6.1288277,106.7778008),
////            TourismPlace("Rabbit Town", -0.0746699,109.3558061)
////            TourismPlace("The Great Asia Africa", 37.422092,-122.08392),
//
//            TourismPlace("The Great Asia Africa", 37.422092,-122.08392),
//            TourismPlace("MRG", -6.3465011, 106.6892259),
//            TourismPlace("Floating Market Lembang", -6.8168954,107.6151046),
//            TourismPlace("The Great Asia Africa", -6.8331128,107.6048483),
//            TourismPlace("Rabbit Town", -6.8668408,107.608081),
//            TourismPlace("Alun-Alun Kota Bandung", -6.9218518,107.6025294),
//            TourismPlace("Orchid Forest Cikole", -6.780725, 107.637409),
//        )
//
////        BATAS KASAR INDONESIA
////        -10.000000, 150.000000
////        10.000000, 95.000000
//        story?.forEach { tourism ->
//            val latLng = LatLng(tourism.latitude, tourism.longitude)
////            val latLng = LatLng(tourism?.lat!!, tourism.lon!!)
//            mMap.addMarker(MarkerOptions().position(latLng).title(tourism.name))
//            boundsBuilder.include(latLng)
//
//            // BOUNDS FORCE
////            boundsBuilder.include(LatLng(-10.000000, 150.000000))
////            boundsBuilder.include(LatLng(10.000000, 95.000000))
//
//        }
//
//        val bounds: LatLngBounds = boundsBuilder.build()
//        mMap.animateCamera(
//            CameraUpdateFactory.newLatLngBounds(
//                bounds,
//                resources.displayMetrics.widthPixels,
//                resources.displayMetrics.heightPixels,
//                300
//            )
//        )
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1000))
//    }
}