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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.safenet.Earthquake
import com.example.safenet.MainActivity
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.Request as VolleyRequest
import com.android.volley.Response as VolleyResponse


class earthquake_main : Fragment(), bottom_sheet_earthquake.OnEarthquakeSubmitListener {

    private lateinit var  mapView2:MapView
    private var pointAnnotationManager:PointAnnotationManager?=null
    val bottomSheetDialog = bottom_sheet_earthquake()
    private lateinit var fabPlus:FloatingActionButton



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
        bottomSheetDialog.listener = this

        fabPlus = view.findViewById(R.id.earthquake_bottom_sheet_action)

       fabPlus.setOnClickListener{
           bottomSheetDialog.show(childFragmentManager, "earthquake_bottom_sheet")
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
                    val depth = jsonObject.getDouble("depth")

                    val earthquake = Earthquake(latitude, longitude, magnitude, depth)
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

            predictMagnitude(earthquake.latitude, earthquake.longitude, earthquake.depth) { magnitude ->
                pointAnnotationOptions.withTextField(magnitude).withTextOffset(listOf(1.5, 0.0)).withTextSize(12.0)
                pointAnnotationManager?.create(pointAnnotationOptions)
            }
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

    private fun predictMagnitude(latitude: Double, longitude: Double, depth: Double, callback: (String) -> Unit) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "https://earthquakemodel.onrender.com/predict/earthquake"
        val params = HashMap<String, String>()
        params["latitude"] = latitude.toString()
        params["longitude"] = longitude.toString()
        params["depth"] = depth.toString()

        val request = object : StringRequest(
            VolleyRequest.Method.POST, url,
            VolleyResponse.Listener<String> { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val magnitude = jsonObject.getString("Maginitude")
                    callback(magnitude)
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                }
            },
            VolleyResponse.ErrorListener { error ->
                // Handle error
            }
        ) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        queue.add(request)
    }

    override fun onEarthquakeSubmit(latitude: Double, longitude: Double, depth: Double) {
        val earthquake = Earthquake(latitude, longitude, 0.0, depth)
        createMarker(earthquake)
    }



}