package com.capstone.plantcare.ui.home

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.plantcare.R
import com.capstone.plantcare.databinding.FragmentHomeBinding
import com.capstone.plantcare.ui.adapter.NewsAdapter
import com.capstone.plantcare.ui.adapter.RecentAdapter
import com.capstone.plantcare.ui.detail.DetailActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var recentAdapter: RecentAdapter
    private lateinit var newsAdapter: NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupFirebase()
        setupRecyclerViewPlant()
        setupRecyclerViewNews()

        viewModel.fetchNewsData()
    }

    private fun setupRecyclerViewNews() {
        newsAdapter = NewsAdapter()

        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupRecyclerViewPlant() {
       recentAdapter = RecentAdapter { historyItem ->
           val intent = Intent(requireContext(), DetailActivity::class.java).apply {
               putExtra("disease", historyItem.disease)
               putExtra("plantType", historyItem.plantType)
               putExtra("probability", historyItem.probability)
               putExtra("imageUri", historyItem.imageUrl)
               putExtra("treatment", historyItem.treatment)
           }
           startActivity(intent)
       }

        binding.rvRecent.apply {
            adapter = recentAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeViewModel() {
        viewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            if (historyList.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "History kosong", Toast.LENGTH_SHORT).show()
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvRecent.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvRecent.visibility = View.VISIBLE
                recentAdapter.submitList(historyList)
            }
        }

        viewModel.newsList.observe(viewLifecycleOwner) { newsList ->
            if (newsList.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Tidak ada berita saat ini.", Toast.LENGTH_SHORT).show()
            } else {
                newsAdapter.submitList(newsList)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isLoading2.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFirebase() {
        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val auth = Firebase.auth
        val user = auth.currentUser

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            viewModel.fetchRecentData(userId)
        } else {
            Toast.makeText(requireContext(), "User Belum Login", Toast.LENGTH_SHORT).show()
        }

        if (user != null) {
            binding.tvUser.text = user.displayName ?: ""
        } else {
            val signInClient = mGoogleSignInClient.signInIntent
            startActivityForResult(signInClient, 123)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}