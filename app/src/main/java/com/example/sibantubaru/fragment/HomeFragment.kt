package com.example.sibantubaru.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sibantubaru.R
import com.example.sibantubaru.ServiceProviderDetailActivity
import com.example.sibantubaru.adapter.ServiceProviderAdapter
import com.example.sibantubaru.model.ServiceProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var serviceProviderAdapter: ServiceProviderAdapter
    private lateinit var serviceProvidersList: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var plumbingServicesChipGroup: ChipGroup

    private var allServiceProviders: List<ServiceProvider> = emptyList()
    private var currentFilteredProviders: List<ServiceProvider> = emptyList()

    private var lastClickedMarker: Marker? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val EXTRA_PROVIDER = "EXTRA_PROVIDER"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        serviceProvidersList = view.findViewById(R.id.serviceProvidersList)
        serviceProviderAdapter = ServiceProviderAdapter(emptyList()) { provider ->
            openDetailActivity(provider)
        }
        serviceProvidersList.layoutManager = LinearLayoutManager(requireContext())
        serviceProvidersList.adapter = serviceProviderAdapter
        searchView = view.findViewById(R.id.searchBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { applyFilters(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { applyFilters(it) }
                return true
            }
        })

        plumbingServicesChipGroup = view.findViewById(R.id.plumbingServicesChipGroup)
        plumbingServicesChipGroup.setOnCheckedStateChangeListener { _, _ ->
            applyFilters(searchView.query.toString())
        }

        allServiceProviders = getDummyServiceProviders()
        currentFilteredProviders = allServiceProviders
        updateRecyclerView(currentFilteredProviders)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    addMarkers(currentFilteredProviders)
                }
            }
    }

    private fun addMarkers(providers: List<ServiceProvider>) {
        mMap.clear()
        providers.forEach { provider ->
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(provider.latitude, provider.longitude))
                    .title(provider.name)
                    .snippet(provider.serviceType)
            )
            marker?.tag = provider
        }
    }

    private fun applyFilters(query: String) {
        val selectedChips = plumbingServicesChipGroup.checkedChipIds

        currentFilteredProviders = allServiceProviders.filter { provider ->
            val chipMatch = selectedChips.isEmpty() || selectedChips.any { chipId ->
                val chipText = requireView().findViewById<Chip>(chipId).text.toString()
                provider.serviceType.contains(chipText, true)
            }
            val queryMatch = query.isEmpty() ||
                    provider.name.contains(query, true) ||
                    provider.serviceType.contains(query, true)
            chipMatch && queryMatch
        }
        updateRecyclerView(currentFilteredProviders)
        addMarkers(currentFilteredProviders)
    }

    private fun updateRecyclerView(providers: List<ServiceProvider>) {
        serviceProviderAdapter.updateList(providers)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val provider = marker.tag as? ServiceProvider
        provider?.let {
            if (lastClickedMarker == marker) {
                // If the same marker is clicked twice, go to detail activity
                openDetailActivity(it)
            } else {
                // Show the preview or info window
                marker.showInfoWindow()
                lastClickedMarker = marker // Store the marker for the next click
            }
        }
        return true
    }

    private fun openDetailActivity(provider: ServiceProvider) {
        val intent = Intent(requireContext(), ServiceProviderDetailActivity::class.java)
        intent.putExtra(EXTRA_PROVIDER, provider)
        startActivity(intent)
    }

    private fun getDummyServiceProviders(): List<ServiceProvider> {
        return listOf(
            ServiceProvider(
                "1", "Pak Ahmad", "Pipe Specialist", "Jl. Merdeka 123",
                4.5f, -7.049500, 110.444100,
                "https://www.vmcdn.ca/f/files/greatwest/images/branded-content-features/home-sweet-home/2023-articles/02-2-plumber.jpg;w=960", "6281339305758",
                "Expert in pipe installation and repairs for home service."
            ),

            ServiceProvider(
                "2", "Pak Budi", "Pipe Specialist", "Jl. Sudirman 456",
                4.2f, -7.052300, 110.447200,
                "https://benjaminfranklinplumbingfortworth.com/wp-content/uploads/2021/06/Essential-Tools-Every-Plumbing-Toolbox-Must-Have-_-Plumber-in-Fort-Worth-TX.jpg", "628987654321",
                "Specialist in repairing leaking pipes, installing water pumps, and consulting on pipe installation designs."
            ),

            ServiceProvider(
                "3", "Bu Siti", "Tile Specialist", "Jl. Harmoni 789",
                4.7f, -7.055000, 110.441800,
                "https://media.istockphoto.com/id/1307614073/photo/female-mason-laying-tiles-on-a-terrace.jpg?s=612x612&w=0&k=20&c=005Prifw1CMDm1bM0biocKIjGkpewhgGdWBsHAmaJnk=", "6282345678",
                "Professional in installing floor tiles, wall tiles, and bathroom renovations for maximum results."
            ),

            ServiceProvider(
                "4", "Pak Rudi", "Tile Specialist", "Jl. Cendana 101",
                4.3f, -7.048700, 110.443500,
                "https://www.flooringhq.com/wp-content/uploads/2016/07/tile-installation.jpg", "6289876543",
                "Years of experience in installing tiles and marble with high precision."
            ),
            ServiceProvider(
                "5", "Mas Joko", "Sink Specialist", "Jl. Mawar 234",
                4.6f, -7.046900, 110.440300,
                "https://img.freepik.com/free-photo/man-adjusting-water-tap-with-wrench_259150-58269.jpg", "6281122334",
                "Expert in sink installation and water channel repairs, ensuring cleanliness and optimal functionality."
            ),
            ServiceProvider(
                "6", "Pak Hadi", "Sink Specialist", "Jl. Melati 567",
                4.4f, -7.050200, 110.446800,
                "https://img.freepik.com/premium-photo/plumber-is-installing-faucet-kitchen-sink_191163-1995.jpg?w=360", "6287766554",
                "Handling clogged sink issues and new installations with fast and reliable service."
            ),
            ServiceProvider(
                "7", "Bu Ani", "Toilet Specialist", "Jl. Teratai 890",
                4.8f, -7.052800, 110.442900,
                "https://img.freepik.com/premium-photo/housewife-washing-disinfecting-toilet-woman-gloves-with-detergent-brush-housekeeping-cleanliness-home-household-duties-service-people-concept_116407-9275.jpg", "6285544332",
                "Expert in installing sitting or squat toilets as well as repairing leaking or clogged toilets."
            ),
            ServiceProvider(
                "8", "Pak Wawan", "Toilet Specialist", "Jl. Kenanga 112",
                4.1f, -7.053600, 110.448200,
                "https://cleaningservicekuwait.com/assets/uploads/media-uploader/toilet-cleaning-service1674783892.jpeg", "6282233445",
                "Providing quick solutions for toilet pipe problems, including repairs and replacements."
            ),
            ServiceProvider(
                "9", "Mas Dodi", "Septic Tank Service", "Jl. Anggrek 345",
                4.9f, -7.050500, 110.445700,
                "https://cdn.careeronestop.org/OccVids/OccupationVideos/47-4071.00.jpg", "6281133557",
                "Fast and eco-friendly septic tank pumping services, available for homes and buildings."
            ),
            ServiceProvider(
                "10", "Pak Anto", "Septic Tank Service", "Jl. Tulip 678",
                4.0f, -7.047800, 110.443900,
                "https://media.licdn.com/dms/image/D4E12AQGNrjg68EByBQ/article-cover_image-shrink_720_1280/0/1689075590910?e=2147483647&v=beta&t=xgy9nkq0isWPngQvzLA10elyvf_FR51RfQulYdwcIzQ", "6287766990",
                "Expert in handling full septic tanks and clogged drains with modern equipment."
            ),
            ServiceProvider(
                "11", "Bu Rina", "Water Heater Specialist", "Jl. Mawar 901",
                4.6f, -7.048300, 110.447400,
                "https://danielcordovaplumbing.com/wp-content/uploads/2011/12/dsniel-water-heater-service.jpg", "6282244668",
                "Installing and repairing water heaters with professional service and quality results."
            ),
            ServiceProvider(
                "12", "Pak Yanto", "Water Heater Specialist", "Jl. Melati 234",
                4.3f, -7.051200, 110.441500,
                "https://gordonac.com/wp-content/uploads/2021/01/water-heater-service.png", "6285599007",
                "Water heater specialist ready to help with installation and maintenance of your equipment."
            )
        )
    }
}