package com.example.safenet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.safenet.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class bottom_sheet_earthquake : BottomSheetDialogFragment() {

    private lateinit var latitudeEditText: EditText
    private lateinit var longitudeEditText: EditText
    private lateinit var depthEditText: EditText
    private lateinit var submitButton: Button
    var listener: OnEarthquakeSubmitListener? = null

    interface OnEarthquakeSubmitListener {
        fun onEarthquakeSubmit(latitude: Double, longitude: Double)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_earthquake, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        latitudeEditText = view.findViewById(R.id.latitude_earthquake)
        longitudeEditText = view.findViewById(R.id.longitude_earthquake)
        depthEditText = view.findViewById(R.id.depth_earthquake)
        submitButton = view.findViewById(R.id.submit_earthquake)

        submitButton.setOnClickListener {
            val latitude = latitudeEditText.text.toString().toDoubleOrNull()
            val longitude = longitudeEditText.text.toString().toDoubleOrNull()

            if (latitude != null && longitude != null) {
                listener?.onEarthquakeSubmit(latitude, longitude)
                dismiss() // Dismiss the bottom sheet after submitting the values
            } else {
                // Handle invalid input
            }
        }
    }

}