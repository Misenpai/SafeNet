import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.safenet.R
import com.example.safenet.fragment.bottom_sheet_rainfall_graph
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject

class bottom_sheet_rainfall : BottomSheetDialogFragment() {
    private val TAG = "RainfallFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_rainfall, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val submitButton = view.findViewById<Button>(R.id.submit_rainfall)
        val stateNameEditText = view.findViewById<EditText>(R.id.state_name)
        val yearEditText = view.findViewById<EditText>(R.id.year)

        val url = "https://rainfallpredictionmodel.onrender.com/predict/rainfall"

        submitButton.setOnClickListener {
            val state = stateNameEditText.text.toString().uppercase()
            val year = yearEditText.text.toString()

            if (state.isNotEmpty() && year.isNotEmpty()) {
                val queue = Volley.newRequestQueue(requireActivity())


                val stringRequest = object : StringRequest(
                    Request.Method.POST,
                    url,
                    Response.Listener { response ->
                        try {
                            Log.d(TAG, "Server response: " + response)
                            val jsonResponse = JSONObject(response)
                            val predictedRainfall = jsonResponse.getJSONObject("predicted_rainfall")

                            val rainfallGraphFragment = bottom_sheet_rainfall_graph()
                            val args = Bundle()
                            args.putString("state", state)
                            args.putString("year", year)
                            args.putString("predicted_rainfall", predictedRainfall.toString())
                            rainfallGraphFragment.arguments = args

                            rainfallGraphFragment.show(parentFragmentManager, "RainfallGraphFragment")
                            dismiss()
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(requireContext(), "Volley error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = mutableMapOf<String, String>()
                        params["state"] = state
                        params["year"] = year
                        return params
                    }
                }

                queue.add(stringRequest)
            } else {
                Toast.makeText(requireContext(), "Please enter both state and year", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
