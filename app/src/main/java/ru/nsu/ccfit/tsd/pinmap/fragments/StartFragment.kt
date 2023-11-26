package ru.nsu.ccfit.tsd.pinmap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import ru.nsu.ccfit.tsd.pinmap.MapActivity
import ru.nsu.ccfit.tsd.pinmap.PinMarker
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentStartBinding
import ru.nsu.ccfit.tsd.pinmap.pins.Pin

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
        binding.zoomInFab.setOnClickListener { view ->
            view.rootView.findViewById<MapView>(R.id.map).controller.zoomIn()
        }
    }

    private fun setZoomOutFabClickListener() {
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

            val geoPoint = map.mapCenter
            val pin = Pin("Новое воспоминание", geoPoint.latitude, geoPoint.longitude)
            val pinMarker = PinMarker(map, activity as MapActivity, pin)//todo это активити это вообще то?
            pinMarker.position = map.mapCenter as GeoPoint? // это на всякий случай чтобы карта не улетела в (0, 0)

            val bundle = Bundle()
            bundle.putBoolean("new", true)
            bundle.putString("name", pin.name)
            bundle.putFloat("latitude", pin.latitude.toFloat())
            bundle.putFloat("longitude", pin.longitude.toFloat())
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.pinConstructorFragment, bundle)
        }
    }
}
