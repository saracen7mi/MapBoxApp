package com.example.mapboxapp


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.*
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style


class MainActivity : AppCompatActivity(),
    OnMapReadyCallback, OnLocationClickListener, PermissionsListener,
    OnCameraTrackingChangedListener {

    private var permissionsManager: PermissionsManager? = null
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var locationComponent: LocationComponent? = null
    private var isInTrackingMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.LIGHT
        ) { style -> enableLocationComponent(style) }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {

        if (PermissionsManager.areLocationPermissionsGranted(this)) {


            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .elevation(5f)
                .accuracyAlpha(.6f)
                .accuracyColor(Color.RED)
                .foregroundDrawable(R.drawable.ic_baseline_location_on_24)
                .build()


            locationComponent = mapboxMap!!.locationComponent
            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            locationComponent!!.activateLocationComponent(locationComponentActivationOptions)

            locationComponent!!.isLocationComponentEnabled = true

            locationComponent!!.cameraMode = CameraMode.TRACKING

            locationComponent!!.renderMode = RenderMode.COMPASS

            locationComponent!!.addOnLocationClickListener(this)


            locationComponent!!.addOnCameraTrackingChangedListener(this)
            findViewById<View>(R.id.back_to_camera_tracking_mode).setOnClickListener {
                if (!isInTrackingMode) {
                    isInTrackingMode = true
                    locationComponent!!.cameraMode =
                        CameraMode.TRACKING
                    locationComponent!!.zoomWhileTracking(16.0)
                    Toast.makeText(
                        this, getString(R.string.tracking_enabled),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.tracking_already_enabled),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(this)
        }
    }

    override fun onLocationComponentClick() {
        if (locationComponent!!.lastKnownLocation != null) {
            Toast.makeText(
                this, String.format(
                    getString(R.string.current_location),
                    locationComponent!!.lastKnownLocation!!.latitude,
                    locationComponent!!.lastKnownLocation!!.longitude
                ), Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCameraTrackingDismissed() {
        isInTrackingMode = false
    }

    override fun onCameraTrackingChanged(currentMode: Int) {
        // Empty on purpose
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap!!.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }
}