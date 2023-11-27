package ru.nsu.ccfit.tsd.pinmap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.nsu.ccfit.tsd.pinmap.MapActivity
import ru.nsu.ccfit.tsd.pinmap.PinMarker
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentStartBinding
import ru.nsu.ccfit.tsd.pinmap.pins.PinController

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = findNavController()

        val bsButton = view.findViewById<Button>(R.id.bs_button)
        bsButton.setOnClickListener { controller.navigate(R.id.bottomSheetFragment) }

        setAddMarkerFabClickListener()
        setConfirmMarkerFabClickListener()
        setCancelMarkerFabClickListener()
        setZoomInFabClickListener()
        setZoomOutFabClickListener()

    }

    private fun setZoomInFabClickListener() {
        //todo этой кнопке надо иконку правильную сделать
        binding.zoomInFab.setOnClickListener { view ->
            view.rootView.findViewById<MapView>(R.id.map).controller.zoomIn()
        }
    }

    private fun setZoomOutFabClickListener() {
        //todo этой кнопке надо иконку правильную сделать
        binding.zoomOutFab.setOnClickListener { view ->
            view.rootView.findViewById<MapView>(R.id.map).controller.zoomOut()
        }
    }

    private fun setAddMarkerFabClickListener() {
        binding.addMarkerFab.setOnClickListener { view ->
            view.visibility = View.GONE
            binding.confirmMarkerFab.visibility = View.VISIBLE
            binding.cancelMarkerFab.visibility = View.VISIBLE
            binding.creatingPin.visibility = View.VISIBLE
        }
    }

    private fun setCancelMarkerFabClickListener() {
        binding.cancelMarkerFab.setOnClickListener { view ->
            Toast.makeText(view.context, "Создание маркера отменено!", Toast.LENGTH_SHORT).show()
            binding.addMarkerFab.visibility = View.VISIBLE
            view.visibility = View.GONE
            binding.confirmMarkerFab.visibility = View.GONE
            binding.creatingPin.visibility = View.GONE
        }
    }

    private fun setConfirmMarkerFabClickListener() {
        binding.confirmMarkerFab.setOnClickListener { view ->
            val map = view.rootView.findViewById<MapView>(R.id.map)

            val bundle = Bundle()
            bundle.putBoolean("new", true)
            bundle.putString("name", "Новое воспоминание")
            val geoPoint = map.mapCenter
            bundle.putFloat("latitude", geoPoint.latitude.toFloat())
            bundle.putFloat("longitude", geoPoint.longitude.toFloat())

            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.pinConstructorFragment, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        val pinController = PinController.getController(requireContext().applicationContext)
        val pins = pinController.getAllPins()
        val map = requireView().rootView.findViewById<MapView>(R.id.map)
        val res = this.resources
        val pinImage = ResourcesCompat.getDrawable(res, R.drawable.pin_marker, null)
        map.overlays.forEach {
            map.overlays.remove(it)
        }
        for (pin in pins){
            val marker = PinMarker(map, activity as MapActivity, pin)
            marker.position = GeoPoint(pin.latitude, pin.longitude)
            marker.icon = pinImage
            marker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )
            map.overlays.add(marker)
            map.invalidate()
        }
    }
}
