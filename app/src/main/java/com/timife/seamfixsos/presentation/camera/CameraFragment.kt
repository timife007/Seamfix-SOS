package com.timife.seamfixsos.presentation.camera

import android.Manifest
import android.Manifest.permission.READ_PHONE_STATE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.timife.seamfixsos.R
import com.timife.seamfixsos.databinding.FragmentCameraBinding
import com.timife.seamfixsos.domain.model.LocationItem
import com.timife.seamfixsos.domain.model.SOSItem
import com.timife.seamfixsos.utils.Constants.GPS_REQUEST_CHECK_SETTINGS
import com.timife.seamfixsos.utils.GpsUtil
import com.timife.seamfixsos.utils.Resource
import com.timife.seamfixsos.utils.SOSharedPref
import com.timife.seamfixsos.utils.observeOnce
import com.timife.seamfixsos.worker.SOSWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment() {
    private lateinit var cameraBinding: FragmentCameraBinding
    private val viewModel by viewModels<CameraViewModel>()

    private var isGPSEnabled = false


    @Inject
    lateinit var sharedPref: SOSharedPref


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GpsUtil(requireContext()).turnGPSOn(object : GpsUtil.OnGpsListener {
            override fun gpsStatus(isGPSEnabled: Boolean) {
                this@CameraFragment.isGPSEnabled = isGPSEnabled
            }
        })
    }

    override fun onStart() {
        super.onStart()
        invokeLocationPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cameraBinding = FragmentCameraBinding.inflate(inflater)
        return cameraBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var image: String? = null
        var locationItem : LocationItem?  = null
        val telephonyManager =
            requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        cameraBinding.apply {
            camera.setLifecycleOwner(viewLifecycleOwner)

            snap.setOnClickListener {
                viewModel.fetchLocationLiveData().observeOnce(viewLifecycleOwner) {
                    if (it != null) {
                        locationItem = it
                    }else{
                        Toast.makeText(requireContext(),"Error fetching current location, check internet connection",Toast.LENGTH_SHORT).show()
                    }
                }
                camera.takePicture()
            }
            camera.addCameraListener(object : CameraListener() {
                override fun onPictureTaken(result: PictureResult) {
                    image = result.data.toString()
                }
            })

            send.setOnClickListener {
                if (!hasPhonePermission()) {
                    askPhonePermission()
                }else {
                    val numbers = telephonyManager.emergencyNumberList.values
                    val list = mutableListOf<String>()
                    for (i in numbers){
                        i.forEach {
                            list.add(it.number)
                        }
                    }
                    val sosItem = locationItem?.let { location ->
                        SOSItem(
                            phoneNumbers = list.toSet().toList(),
                            image = image ?: "",
                            locationItem = location
                        )
                    }
                    if(sosItem != null){
                        AlertDialog.Builder(requireContext())
                            .setTitle(getString(R.string.send_sos_req))
                            .setMessage("SOS Details: $sosItem")
                            .setNegativeButton(
                                getString(R.string.no)
                            ) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setPositiveButton(
                                getString(R.string.yes)
                            ) { _, _ ->
                                viewModel.sendSOS(sosItem)
                            }
                            .show()
                    }
                    observeViewModels()
                }
            }
        }
    }
    private fun observeViewModels(){
        viewModel.result.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    Toast.makeText(requireContext(),"SOS successfully sent!",Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(),"Error sending SOS, Please retry",Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading ->{
                    Toast.makeText(requireContext(),"Sending...",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun invokeLocationPermission() {
        when {
            allPermissionsGranted() -> {
            }

            shouldShowRequestPermissionRationale() -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.location_permission))
                    .setMessage(getString(R.string.access_location_message))
                    .setNegativeButton(
                        getString(R.string.no)
                    ) { _, _ -> requireActivity().finish() }
                    .setPositiveButton(
                        getString(R.string.ask_me)
                    ) { _, _ ->
                        requestPermissions(REQUIRED_PERMISSIONS, LOCATION_REQUEST_CODE)
                    }
                    .show()
            }

            !isGPSEnabled -> {
                Snackbar.make(
                    cameraBinding.root,
                    getString(R.string.gps_required_message),
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            else -> {
                requestPermissions(REQUIRED_PERMISSIONS, LOCATION_REQUEST_CODE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_REQUEST_CHECK_SETTINGS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        isGPSEnabled = true
                        invokeLocationPermission()
                    }

                    Activity.RESULT_CANCELED -> {
                        Snackbar.make(
                            cameraBinding.root,
                            getString(R.string.enable_gps),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowRequestPermissionRationale() = REQUIRED_PERMISSIONS.all {
        shouldShowRequestPermissionRationale(it)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            invokeLocationPermission()
        }
    }


    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val LOCATION_REQUEST_CODE = 123
        private const val PERMISSION_REQUEST_CODE = 100
    }


    //Check if contacts permission allowed
    private fun hasPhonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    }
    private fun askPhonePermission(){
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf( READ_PHONE_STATE), PERMISSION_REQUEST_CODE
        )
    }
}