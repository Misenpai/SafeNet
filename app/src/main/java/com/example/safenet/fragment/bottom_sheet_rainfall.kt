package com.example.safenet.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.safenet.R
import com.example.safenet.RainfallDataListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class bottom_sheet_rainfall : BottomSheetDialogFragment(),RainfallDataListener {

    private var rainfallDataListener: RainfallDataListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet_rainfall, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val submitButton = view.findViewById<Button>(R.id.submit_rainfall)
        val stateNameEditText = view.findViewById<EditText>(R.id.state_name)
        val yearEditText = view.findViewById<EditText>(R.id.year)

        submitButton.setOnClickListener {
            val state = stateNameEditText.text.toString().uppercase()
            val year = yearEditText.text.toString()

            if (state.isNotEmpty() && year.isNotEmpty()) {
                sendDataToServer(state, year.toInt())
            } else {
                Toast.makeText(requireContext(), "Please enter both state and year", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendDataToServer(state: String, year: Int) {
        val url = "https://rainfallpredictionmodel.onrender.com/predict/rainfall"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                // Parse the response and extract the rainfall data
                val rainfallData = parseRainfallData(response)
                rainfallDataListener?.onRainfallDataReceived(rainfallData)
            },
            { error ->
                // Handle the error
                error.printStackTrace()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["state"] = state
                params["year"] = year.toString()
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun parseRainfallData(response: String?): Map<String, Float> {
        return emptyMap()

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RainfallDataListener) {
            rainfallDataListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        rainfallDataListener = null
    }
    override fun onRainfallDataReceived(rainfallData: Map<String, Float>) {
        val rainfallGraphFragment = bottom_sheet_rainfall_graph()
        rainfallGraphFragment.setRainfallData(rainfallData)
        rainfallGraphFragment.show(parentFragmentManager, "RainfallGraphFragment")
    }

}