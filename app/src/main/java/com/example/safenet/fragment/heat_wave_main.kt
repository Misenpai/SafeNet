package com.example.safenet.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.example.safenet.MainActivity
import com.example.safenet.OpenWeatherApiClient
import com.example.safenet.OpenWeatherResponse
import com.example.safenet.R
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.logD
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class heat_wave_main : Fragment() {
    private lateinit var mapView2: MapView
    private var pointAnnotationManager: PointAnnotationManager? = null

    private val cityCoordinates = listOf(
        Pair(28.70, 77.10), // Delhi
        Pair(19.07, 72.87), // Mumbai
        Pair(22.57, 88.36), // Kolkata
        Pair(13.08, 80.27), // Chennai
        Pair(23.25, 77.41), // Bhopal
        Pair(17.37, 78.48), // Hyderabad
        Pair(18.52, 73.86), // Pune
        Pair(26.91, 75.79), // Jaipur
        Pair(25.59, 85.14), // Patna
        Pair(30.73, 76.78), // Chandigarh
        Pair(22.72, 75.86), // Indore
        Pair(28.66, 77.23), // Gurgaon
        Pair(21.17, 72.83), // Surat
        Pair(12.97, 77.59), // Bangalore
        Pair(23.03, 72.58), // Ahmedabad
        Pair(18.10, 79.52), // Vijayawada
        Pair(23.18, 79.98), // Jabalpur
        Pair(25.67, 85.31), // Gaya
        Pair(26.46, 80.35), // Kanpur
        Pair(11.00, 76.96) // Kochi
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_heat_wave_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the PermissionsManager instance from the activity
        val permissionsManager = (requireActivity() as MainActivity).permissionsManager

        // Use the PermissionsManager instance as needed
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
        } else {
            permissionsManager.requestLocationPermissions(requireActivity())
        }

        mapView2 = view.findViewById(R.id.mapViewHeat)
        mapView2.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) { style ->
            markHotCitiesOnMap(style)
            markMediumHotCitiesOnMap(style)
            markNoHotCitiesOnMap(style)
        }

    }

    private fun markHotCitiesOnMap(style: Style){
        bitmapFromDrawableRes(requireContext(),R.drawable.redcircle_16)?.let { bitmap ->
            val annotationApi = mapView2.annotations
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            for ((lat, lon) in cityCoordinates) {
                OpenWeatherApiClient.api.getCityWeather(lat, lon, "fef2ed665d05a9d1bfc56ffdbe387d7d")
                    .enqueue(object : Callback<OpenWeatherResponse> {
                        override fun onResponse(
                            call: Call<OpenWeatherResponse>,
                            response: Response<OpenWeatherResponse>
                        ) {
                            if (response.isSuccessful) {
                                val temperature = response.body()?.main?.temperature
                                if (temperature != null && temperature >= 40) {
                                    val pointAnnotationOptions: PointAnnotationOptions =
                                        PointAnnotationOptions()
                                            .withPoint(Point.fromLngLat(lon, lat))
                                            .withIconImage(bitmap)

                                    pointAnnotationManager?.create(pointAnnotationOptions)
                                }
                            }
                        }

                        override fun onFailure(call: Call<OpenWeatherResponse>, t: Throwable) {
                            // Handle failure
                        }
                    })
            }
        }
    }

    private fun markMediumHotCitiesOnMap(style: Style){
        bitmapFromDrawableRes(requireContext(),R.drawable.yellowcircle16x16)?.let { bitmap ->
            val annotationApi = mapView2.annotations
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            for ((lat, lon) in cityCoordinates) {
                OpenWeatherApiClient.api.getCityWeather(lat, lon, "fef2ed665d05a9d1bfc56ffdbe387d7d")
                    .enqueue(object : Callback<OpenWeatherResponse> {
                        override fun onResponse(
                            call: Call<OpenWeatherResponse>,
                            response: Response<OpenWeatherResponse>
                        ) {
                            if (response.isSuccessful) {
                                val temperature = response.body()?.main?.temperature
                                if (temperature != null && temperature >= 25 && temperature < 40) {
                                    val pointAnnotationOptions: PointAnnotationOptions =
                                        PointAnnotationOptions()
                                            .withPoint(Point.fromLngLat(lon, lat))
                                            .withIconImage(bitmap)

                                    pointAnnotationManager?.create(pointAnnotationOptions)
                                }
                            }
                        }

                        override fun onFailure(call: Call<OpenWeatherResponse>, t: Throwable) {
                            // Handle failure
                        }
                    })
            }
        }
    }

    private fun markNoHotCitiesOnMap(style: Style){
        bitmapFromDrawableRes(requireContext(),R.drawable.greencircle16x16)?.let { bitmap ->
            val annotationApi = mapView2.annotations
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            for ((lat, lon) in cityCoordinates) {
                OpenWeatherApiClient.api.getCityWeather(lat, lon, "fef2ed665d05a9d1bfc56ffdbe387d7d")
                    .enqueue(object : Callback<OpenWeatherResponse> {
                        override fun onResponse(
                            call: Call<OpenWeatherResponse>,
                            response: Response<OpenWeatherResponse>
                        ) {
                            if (response.isSuccessful) {
                                val temperature = response.body()?.main?.temperature
                                if (temperature != null && temperature <= 25) {
                                    val pointAnnotationOptions: PointAnnotationOptions =
                                        PointAnnotationOptions()
                                            .withPoint(Point.fromLngLat(lon, lat))
                                            .withIconImage(bitmap)

                                    pointAnnotationManager?.create(pointAnnotationOptions)
                                }
                            }
                        }

                        override fun onFailure(call: Call<OpenWeatherResponse>, t: Throwable) {
                            // Handle failure
                        }
                    })
            }
        }
    }



    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}