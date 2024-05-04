package com.example.safenet.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.example.safenet.Earthquake
import com.example.safenet.MainActivity
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONException


class earthquake_main : Fragment() {

    private lateinit var  mapView2:MapView
    private var pointAnnotationManager:PointAnnotationManager?=null

    private val cityCoordinates = listOf(
        Pair(26.2124, 127.6809),
        Pair(33.5904, 130.4017),
        Pair(35.6895, 139.6917),
        Pair(26.2124, 127.6809),
        Pair(16.5662, 121.2620),
        Pair(10.3157, 123.8854),
        Pair(7.1907, 125.4553),
        Pair(26.6340, 78.3617),
        Pair(26.5361, 77.0616),
        Pair(33.4996, 126.5312),
        Pair(36.5263, 128.7973),
        Pair(35.8298, 127.1480),
        Pair(23.4241, 113.3622),
        Pair(26.0789, 117.9874),
        Pair(29.1832, 120.0934),
        Pair(19.6959, 109.7453),
        Pair(20.1497, 75.2066),
        Pair(20.0169, 75.8200),
        Pair(20.8880, 76.2591),
        Pair(18.7742, 68.3937),
        Pair(18.7650, 69.0357),
        Pair(21.0466, 107.0448),
        Pair(19.8075, 105.7851),
        Pair(18.8890, 105.6810),
        Pair(25.5000, 90.5000),
        Pair(19.1738, 96.1342),
        Pair(23.7416, 98.0734),
        Pair(20.3165, 87.1086),
        Pair(31.9686, 99.9018),
        Pair(30.9843, 91.9623),
        Pair(32.3547, 89.3985),
        Pair(32.3182, 86.9023),
        Pair(27.9944, 81.7603),
        Pair(35.7596, 79.0193),
        Pair(45.2538, 69.4455),
        Pair(-18.7669, 46.8691),
        Pair(17.1899, 88.4976),
        Pair(18.9712, 72.2852),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_earthquake_main, container, false)
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

        mapView2 = view.findViewById(R.id.mapViewEarthquake)
        mapView2.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ){style->fetchEarthquakeData()}

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

    private fun fetchEarthquakeData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://everyearthquake.p.rapidapi.com/earthquakes?start=1&count=100&type=earthquake&latitude=33.962523&longitude=-118.3706975&radius=1000&units=miles&magnitude=3&intensity=1")
            .get()
            .addHeader("X-RapidAPI-Key", "fa16a211e8mshf1cf291aa9d0cbdp1447edjsn321790df2ee4")
            .addHeader("X-RapidAPI-Host", "everyearthquake.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    // Process the response and create markers on the map
                    processEarthquakeData(responseBody)
                } else {
                    // Handle unsuccessful response
                }
            }
        })
    }

    private fun processEarthquakeData(responseBody: String?) {
        if (responseBody != null) {
            try {
                val jsonArray = JSONArray(responseBody)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val latitude = jsonObject.getDouble("latitude")
                    val longitude = jsonObject.getDouble("longitude")
                    val magnitude = jsonObject.getDouble("magnitude")

                    val earthquake = Earthquake(latitude, longitude, magnitude)
                    createMarker(earthquake)
                }
            } catch (e: JSONException) {
                // Handle JSON parsing error
            }
        }
    }

    private fun createMarker(earthquake: Earthquake) {
        bitmapFromDrawableRes(requireContext(), R.drawable.icons8_earthquake_64)?.let { bitmap ->
            val annotationApi = mapView2.annotations
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(earthquake.longitude, earthquake.latitude))
                .withIconImage(bitmap)

            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourseId: Int) = convertDrawableToBitmap(
        AppCompatResources.getDrawable(context,resourseId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap?{
        if (sourceDrawable == null){
            return null
        }
        return if (sourceDrawable is BitmapDrawable){
            sourceDrawable.bitmap
        }else{
            val constantState = sourceDrawable.constantState?:return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap =  Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            drawable.setBounds(0,0,canvas.width,canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }




}