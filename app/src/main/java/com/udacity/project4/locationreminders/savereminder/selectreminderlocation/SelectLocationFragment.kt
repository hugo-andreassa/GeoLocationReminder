package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private val TAG = SelectLocationFragment::class.java.simpleName
    private val REQUEST_FOREGROUND_PERMISSION_REQUEST_CODE = 34

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLocation: LatLng? = null
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        setupGoogleMap()

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setupGoogleMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            childFragmentManager.findFragmentByTag(getString(R.string.map_fragment)) as? SupportMapFragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        setMapStyle(map)

        binding.saveButton.setOnClickListener {
            onLocationSelected()
        }

        enableMyLocation()
        setMapStyle(map)
        setMapClick(map)
    }

    private fun setMapClick(map: GoogleMap) {
        map.setOnMapClickListener { latLng ->
            map.clear()

            setMarker(map, latLng)
        }
    }

    private fun setMarker(map: GoogleMap, latLng: LatLng) {
        map.clear()

        selectedLocation = latLng

        val snippet = String.format(
            Locale.getDefault(),
            "Lat: %1$.5f, Long: %2$.5f",
            latLng.latitude,
            latLng.longitude
        )

        map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(getString(R.string.dropped_pin))
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
    }

    private fun onLocationSelected() {
        if (selectedLocation != null) {
            _viewModel.latitude.value = selectedLocation!!.latitude
            _viewModel.longitude.value = selectedLocation!!.longitude
            _viewModel.reminderSelectedLocationStr.value = String.format(
                Locale.getDefault(),
                "Lat: %1$.2f, Long: %2$.2f",
                selectedLocation!!.latitude,
                selectedLocation!!.longitude
            )
            _viewModel.navigationCommand.postValue(NavigationCommand.Back)
        } else {
            Snackbar.make(
                requireView(),
                "You need to select a location!",
                BaseTransientBottomBar.LENGTH_SHORT
            ).show()
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val style = MapStyleOptions.loadRawResourceStyle(
                requireActivity(),
                R.raw.map_style
            )
            map.setMapStyle(style)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionsGranted() && this::map.isInitialized) {
            map.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
                if (location != null) {

                    if (_viewModel.latitude.value != null) {
                        val latLng =
                            LatLng(_viewModel.latitude.value!!, _viewModel.longitude.value!!)

                        setMarker(map, latLng)

                        val cameraPosition = CameraPosition.fromLatLngZoom(
                            latLng,
                            15f
                        )
                        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                        map.animateCamera(cameraUpdate)
                    } else {
                        val latLng = LatLng(location.latitude, location.longitude)

                        setMarker(map, latLng)

                        val cameraPosition = CameraPosition.fromLatLngZoom(
                            latLng,
                            15f
                        )
                        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                        map.animateCamera(cameraUpdate)
                    }
                }
            }
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")

        if (
            grantResults.isEmpty() ||
            grantResults[0] == PackageManager.PERMISSION_DENIED
        ) {
            _viewModel.showErrorMessage.postValue(getString(R.string.error_maps))
        } else {
            enableMyLocation()
        }
    }

    private fun requestPermissions() {
        if (isPermissionsGranted()) {
            return
        }

        val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = REQUEST_FOREGROUND_PERMISSION_REQUEST_CODE

        requestPermissions(
            permissionsArray,
            resultCode
        )
    }

    private fun isPermissionsGranted(): Boolean {
        return (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ))
    }
}
