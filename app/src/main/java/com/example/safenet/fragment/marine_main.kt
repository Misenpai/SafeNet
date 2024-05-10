package com.example.safenet.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.example.safenet.MainActivity
import com.example.safenet.MarineWeatherApi
import com.example.safenet.MarineWeatherResponse
import com.example.safenet.R
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap

class marine_main : Fragment() {

    private lateinit var mapView2: MapView
    private var pointAnnotationManager: PointAnnotationManager? = null
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private val calls = ConcurrentHashMap<String, Call<MarineWeatherResponse>>()


    val coordinates = listOf(
        Pair(54.544587, 10.227487), // Coordinate 1
        Pair(35.0, -122.0), // Off the coast of California, Pacific Ocean
        Pair(42.0, -70.0), // Gulf of Maine, Atlantic Ocean
        Pair(60.0, 10.0), // Norwegian Sea, North Atlantic Ocean
        Pair(35.0, 139.0), // Off the coast of Japan, Pacific Ocean
        Pair(-22.0, 114.0), // Off the coast of Western Australia, Indian Ocean
        Pair(40.0, -72.0), // Off the coast of New York, Atlantic Ocean
        Pair(54.0, -162.0), // Bering Sea, Pacific Ocean
        Pair(5.0, 100.0), // Strait of Malacca, Indian Ocean
        Pair(23.0, -109.0), // Gulf of California, Pacific Ocean
        Pair(48.0, -125.0), // Off the coast of British Columbia, Pacific Ocean
        Pair(-20.0, 115.0), // Off the coast of Western Australia, Indian Ocean
        Pair(50.0, -4.0), // North Sea, Atlantic Ocean
        Pair(-35.0, 150.0), // Off the coast of New South Wales, Australia, Pacific Ocean
        Pair(30.0, -80.0), // Off the coast of Florida, Atlantic Ocean
        Pair(10.0, 125.0), // South China Sea, Pacific Ocean
        Pair(-45.0, 170.0), // Off the coast of New Zealand, Pacific Ocean
        Pair(25.0, -75.0), // Gulf of Mexico, Atlantic Ocean
        Pair(-40.0, 20.0), // Off the coast of South Africa, Indian Ocean
        Pair(60.0, -170.0), // Bering Sea, Pacific Ocean
        Pair(-35.0, -175.0) // Off the coast of New Zealand, Pacific Ocean

    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_marine_main, container, false)
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

        mapView2 = view.findViewById(R.id.mapViewMarine) // Example coordinates
        mapView2.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) { style ->
            fetchAndMarkMarineData(style, coordinates)
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

    private fun fetchAndMarkMarineData(style: Style, coordinates: List<Pair<Double, Double>>) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://marine-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val marineWeatherApi = retrofit.create(MarineWeatherApi::class.java)

        val chunkSize = 5 // Adjust this value based on your requirements
        coordinates.chunked(chunkSize).forEach { chunk ->
            coroutineScope.launch {
                chunk.forEach { (latitude, longitude) ->
                    val call = marineWeatherApi.getMarineWeather(latitude, longitude, "wave_height")
                    calls[getCallKey(latitude, longitude)] = call

                    call.enqueue(object : Callback<MarineWeatherResponse> {
                        override fun onResponse(
                            call: Call<MarineWeatherResponse>,
                            response: Response<MarineWeatherResponse>
                        ) {
                            if (response.isSuccessful) {
                                val marineWeatherResponse = response.body()
                                marineWeatherResponse?.let {
                                    markHMarineCitiesOnMap(style, it, latitude, longitude)
                                }
                            } else {
                                // Handle error case
                            }
                            calls.remove(getCallKey(latitude, longitude))
                        }

                        override fun onFailure(call: Call<MarineWeatherResponse>, t: Throwable) {
                            // Handle failure case
                            calls.remove(getCallKey(latitude, longitude))
                        }
                    })
                }
            }
        }
    }

    private fun getCallKey(latitude: Double, longitude: Double): String {
        return "$latitude,$longitude"
    }

    private fun markHMarineCitiesOnMap(style: Style, response: MarineWeatherResponse, latitude: Double, longitude: Double) {
        bitmapFromDrawableRes(requireContext(), R.drawable.fishing)?.let { bitmap ->
            val annotationApi = mapView2.annotations
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            response.hourly.waveHeight.forEachIndexed { index, waveHeight ->
                if (waveHeight < 5.0) {
                    val point = Point.fromLngLat(longitude, latitude)
                    val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(bitmap)
                    pointAnnotationManager?.create(pointAnnotationOptions)
                }
            }
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) = convertDrawableToBitmap(
        AppCompatResources.getDrawable(context, resourceId)
    )

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
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