package com.example.safenet.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import bottom_sheet_rainfall
import com.example.safenet.DatabaseHelperFlood
import com.example.safenet.MainActivity
import com.example.safenet.OpenWeatherApiClient
import com.example.safenet.OpenWeatherResponse
import com.example.safenet.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.state.FollowPuckViewportState
import com.mapbox.maps.plugin.viewport.viewport
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class flood_main : Fragment() {

    private lateinit var mapView2: MapView
    private var pointAnnotationManager: PointAnnotationManager? = null
    private lateinit var databaseHelperFlood: DatabaseHelperFlood
    private lateinit var cityCoordinates: MutableList<Pair<Double, Double>>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flood_main, container, false)
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

        val floatingActionButtonFlood = view.findViewById<FloatingActionButton>(R.id.floatingActionButtonFlood)
        floatingActionButtonFlood.setOnClickListener {
            showBottomSheetRainfallFragment()
        }


        databaseHelperFlood = DatabaseHelperFlood(requireContext())
        databaseHelperFlood.initializeDatabase()

        val cursor = databaseHelperFlood.db.rawQuery("SELECT latitude, longitude FROM ${databaseHelperFlood.TABLE_NAME_FLOOD}", null)

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

        mapView2 = view.findViewById(R.id.mapViewFlood)
        mapView2.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ){style->
            markHotCitiesOnMap(style)
        }




        val mapView = MapView(requireContext())
        val viewportPlugin = mapView.viewport
// transition to followPuckViewportState with default transition
        val followPuckViewportState: FollowPuckViewportState = viewportPlugin.makeFollowPuckViewportState(
            FollowPuckViewportStateOptions.Builder()
                .bearing(FollowPuckViewportStateBearing.Constant(0.0))
                .padding(EdgeInsets(200.0 * resources.displayMetrics.density, 0.0, 0.0, 0.0))
                .build()
        )
        viewportPlugin.transitionTo(followPuckViewportState) { success ->
            // the transition has been completed with a flag indicating whether the transition succeeded
        }
    }

    private fun showBottomSheetRainfallFragment() {
        val bottomSheetRainfallFragment = bottom_sheet_rainfall()
        bottomSheetRainfallFragment.show(parentFragmentManager, "bottomSheetRainfallFragment")
    }

    private fun markHotCitiesOnMap(style: Style){
        bitmapFromDrawableRes(requireContext(),R.drawable.icons8_flood_64)?.let { bitmap ->
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
                                val rain = response.body()?.rain?.rainLast1Hour
                                if (rain!=null && rain>0){
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