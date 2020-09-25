package com.michaelmagdy.pokemon

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val locationRequestCode = 100
    var location:Location?= null
    var listOfPokemon = ArrayList<Pokemon>()
    var oldLocation:Location?= null
    var playerPower = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        loadPokemon()
    }

    fun checkPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    locationRequestCode)
            } else {
                getUserLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation(){

        //Toast.makeText(this, "turn on location", Toast.LENGTH_LONG).show()
        var myLocationListener = MyLocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3,
            3f, myLocationListener)
        val myThread = MyThread()
        myThread.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){

            locationRequestCode -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                } else {
                    Toast.makeText(this, "please turn on location", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        /*
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Me")
            .snippet("Here's my location")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ash)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))
         */

    }

    //get user location

    inner class MyLocationListener:LocationListener {

        constructor(){
            location = Location("Start")
            location!!.latitude = 0.0
            location!!.longitude = 0.0
        }

        override fun onLocationChanged(location: Location?) {
           this@MapsActivity.location = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }


    }

    inner class MyThread:Thread {

        constructor():super(){
            oldLocation = Location("Start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0
        }

        override fun run() {

            while (true){

                try {

                    if (oldLocation!!.distanceTo(location) == 0f){
                        continue
                    }
                    oldLocation = location

                    runOnUiThread {

                        //show me
                        mMap.clear()
                        //val sydney = LatLng(-34.0, 151.0)
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(MarkerOptions()
                            .position(sydney)
                            .title("Me")
                            .snippet("Here's my location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ash)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 4f))

                        //show pokemons
                        for (i in 0.. listOfPokemon.size-1){

                            val newPokemon = listOfPokemon[i]
                            if (newPokemon.isCatched == false){
                                val pokemonLocation = LatLng(newPokemon.location!!.latitude,
                                    newPokemon.location!!.longitude)
                                mMap.addMarker(MarkerOptions()
                                    .position(pokemonLocation)
                                    .title(newPokemon.name)
                                    .snippet(newPokemon.desc + " Power : " + newPokemon.power)
                                    .icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!)))

                                if (location!!.distanceTo(newPokemon.location) < 2){
                                    newPokemon.isCatched = true
                                    listOfPokemon[i] = newPokemon
                                    playerPower += newPokemon.power!!
                                    Toast.makeText(applicationContext, "You caught a new pokemon, your power is "
                                    + playerPower, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }

                    Thread.sleep(1000)

                } catch (ex:Exception){

                }
            }
        }
    }

    fun loadPokemon(){

        listOfPokemon.add(
            Pokemon("Bulbasaur", "Earth Pokemon",
            R.drawable.bulbasaur, 55, 35.77, -120.40)
        )
        listOfPokemon.add(Pokemon("Charminder", "Fire Pokemon",
            R.drawable.charmander, 100, 37.79, -122.41))
        listOfPokemon.add(Pokemon("Squirtle", "Water Pokemon",
            R.drawable.squirtle, 90, 39.78, -124.41))
    }
}
