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
import com.example.safenet.DatabaseHelperHeatWave
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
    private lateinit var databaseHelperHeatWave: DatabaseHelperHeatWave
    private lateinit var cityCoordinates: MutableList<Pair<Double, Double>>


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

        databaseHelperHeatWave = DatabaseHelperHeatWave(requireContext())
        databaseHelperHeatWave.initializeDatabase()

        val cursor = databaseHelperHeatWave.db.rawQuery("SELECT latitude, longitude FROM ${databaseHelperHeatWave.TABLE_NAME_HEATWAVE}", null)

        cityCoordinates = mutableListOf()
        while (cursor.moveToNext()) {
            val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"))
            val lon = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
            cityCoordinates.add(Pair(lat, lon))
        }
        for ((lat, lon) in cityCoordinates) {
            Log.d("HeatWaveMainFragment", "City Coordinate: Latitude = $lat, Longitude = $lon")
        }
        cursor.close()


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