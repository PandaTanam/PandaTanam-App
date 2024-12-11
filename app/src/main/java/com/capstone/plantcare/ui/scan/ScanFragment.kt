package com.capstone.plantcare.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.capstone.plantcare.R
import com.capstone.plantcare.databinding.FragmentScanBinding
import com.capstone.plantcare.ui.detail.DetailActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class ScanFragment : Fragment() {
    private var _binding : FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null

    private val viewModel: ScanViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireActivity(), "Permission request Granted", Toast.LENGTH_SHORT).show()
            setupCamera()
        } else {
            Toast.makeText(requireActivity(), "Permission request denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionGranted() = ContextCompat.checkSelfPermission(
        requireActivity(),
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                binding.previewImageView.setImageURI(it)
            }
        }
        
        with(binding) {
            btnCamera.setOnClickListener { setupCamera() }
            btnGallery.setOnClickListener { setupGallery() }
            btnTomato.setOnClickListener { onPlantTypeSelected("tomato") }
            btnMango.setOnClickListener { onPlantTypeSelected("mango") }
            btnChilli.setOnClickListener { onPlantTypeSelected("chili") }
        }
    }

    private fun setupCamera() {
        when {
            allPermissionGranted() -> {
                val uri = getImageUri(requireActivity())
                if (uri != null) {
                    currentImageUri = uri
                    viewModel.setCurrentImageUri(uri)
                    launcherIntentCamera.launch(uri)
                } else {
                    Toast.makeText(requireActivity(), "Failed to create image file", Toast.LENGTH_SHORT).show()
                }
            }
            shouldShowRequestPermissionRationale(REQUIRED_PERMISSION) -> {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            }
            else -> {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission)
            .setMessage(R.string.permission_message)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            binding.previewImageView.setImageURI(viewModel.currentImageUri.value)
        } else {
            currentImageUri = null
        }
    }


    private fun setupGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            viewModel.setCurrentImageUri(uri)
            binding.previewImageView.setImageURI(uri)
        } else {
            Toast.makeText(requireActivity(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onPlantTypeSelected(plantType: String) {
        val auth = Firebase.auth
        val user = auth.currentUser
        val userId = user?.uid

        if (currentImageUri != null && userId != null) {
            binding.progressIndicator.visibility = View.VISIBLE
            Log.d("Selected Plant Type", "Plant Type: $plantType")
            Log.d("User ID", "User ID: $userId")
            viewModel.prepareRequest(requireContext(), currentImageUri, plantType, userId)

            viewModel.uploadResponse.observe(viewLifecycleOwner) { uploadResponse ->
                binding.progressIndicator.visibility = View.GONE
                if (uploadResponse != null) {
                    val intent = Intent(requireActivity(), DetailActivity::class.java).apply {
                        putExtra("plantType", uploadResponse.plantType)
                        putExtra("disease", uploadResponse.disease)
                        putExtra("probability", uploadResponse.probability)
                        putExtra("imageUri", currentImageUri.toString())
                        putExtra("treatment", uploadResponse.treatment)
                    }
                    startActivity(intent)
                    currentImageUri = null
                    binding.previewImageView.setImageResource(R.drawable.baseline_image_24)
                } else {
                    Toast.makeText(requireContext(), "Data tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            }
            viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.progressIndicator.visibility = View.GONE
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Masukkan sebuah gambar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}