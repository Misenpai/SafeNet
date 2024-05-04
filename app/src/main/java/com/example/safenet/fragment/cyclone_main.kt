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
import com.example.safenet.DatabaseHelperCyclone
import com.example.safenet.MainActivity
import com.example.safenet.OpenWeatherApiClient
import com.example.safenet.OpenWeatherResponse
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class cyclone_main : Fragment() {

    private lateinit var mapView2:MapView
    private var pointAnnotationManager: PointAnnotationManager? = null
    private lateinit var databaseHelperCyclone: DatabaseHelperCyclone
    private lateinit var cityCoordinates: MutableList<Pair<Double, Double>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cyclone_main, container, false)
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

        databaseHelperCyclone = DatabaseHelperCyclone(requireContext())
        databaseHelperCyclone.initializeDatabase()

        val cursor = databaseHelperCyclone.db.rawQuery("SELECT latitude, longitude FROM ${databaseHelperCyclone.TABLE_NAME_CYCLONE}", null)

        cityCoordinates = mutableListOf()
        while (cursor.moveToNext()) {
            val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"))
            val lon = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
            cityCoordinates.add(Pair(lat, lon))
        }
        for ((lat, lon) in cityCoordinates) {
            Log.d("CycloneMainFragment", "City Coordinate: Latitude = $lat, Longitude = $lon")
        }

        cursor.close()


        mapView2 = view.findViewById(R.id.mapViewCyclone)
        mapView2.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ){style->markHotWindCitiesOnMap(style)}

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
    private fun markHotWindCitiesOnMap(style: Style){
        bitmapFromDrawableRes(requireContext(),R.drawable.redcyclone)?.let{bitmap->
            val annotationApi = mapView2.annotations
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            for ((lat,lon) in cityCoordinates){
                OpenWeatherApiClient.api.getCityWeather(lat,lon,"fef2ed665d05a9d1bfc56ffdbe387d7d")
                    .enqueue(object : Callback<OpenWeatherResponse>{
                        override fun onResponse(
                            call: Call<OpenWeatherResponse>,
                            response: Response<OpenWeatherResponse>
                        ){
                          if (response.isSuccessful){
                              val wind = response.body()?.wind?.windspeed
                              if (wind!= null&&wind>7){
                                  val pointAnnotationOptions:PointAnnotationOptions = PointAnnotationOptions()
                                      .withPoint(Point.fromLngLat(lon,lat))
                                      .withIconImage(bitmap)

                                  pointAnnotationManager?.create(pointAnnotationOptions)
                              }
                          }
                        }

                        override fun onFailure(call:Call<OpenWeatherResponse>, t:Throwable){

                        }
                    })
            }
        }
    }

    private fun bitmapFromDrawableRes(context: Context,@DrawableRes resourseId: Int) = convertDrawableToBitmap(
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
            val bitmap:Bitmap =  Bitmap.createBitmap(
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