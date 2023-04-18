package cr.ac.menufragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    lateinit var btnActualizar: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync{
                googleMap ->
                map = googleMap
                getLocation()
        }
        btnActualizar = view.findViewById(R.id.myButton)
        // Configurar el botón
        btnActualizar.setOnClickListener {
            actualizarUbicacion() // Llamar a la función para actualizar la ubicación
        }
    }

    private fun actualizarUbicacion() {
        // Generar ubicación aleatoria
        val random = java.util.Random()
        val maxLatitude = 11.2196 // Latitud máxima en Costa Rica
        val minLatitude = 8.0321 // Latitud mínima en Costa Rica
        val maxLongitude = -82.5540 // Longitud máxima en Costa Rica
        val minLongitude = -85.5586 // Longitud mínima en Costa Rica

        currentLatitude += random.nextDouble() * (maxLatitude - minLatitude) + minLatitude
        currentLongitude += random.nextDouble() * (maxLongitude - minLongitude) + minLongitude

        val args = arguments
        val ubicacionActual = args?.getString("Ubicacion actual")
        val currentLatLng = LatLng(currentLatitude, currentLongitude)
        map.addMarker(MarkerOptions().position(currentLatLng).title(ubicacionActual))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                // Ubicación obtenida con éxito
                val args = arguments
                val ubicacionActual = args?.getString("Ubicacion actual")
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(currentLatLng).title(ubicacionActual))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    getLocation()
                }
            } else {
                // Permiso denegado, maneja la situación de acuerdo a tus necesidades
            }
        }
    }
}