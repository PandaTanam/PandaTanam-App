package com.capstone.plantcare.ui.history

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.plantcare.databinding.FragmentHistoryBinding
import com.capstone.plantcare.ui.adapter.PredictAdapter
import com.capstone.plantcare.ui.detail.DetailActivity
import com.google.firebase.auth.FirebaseAuth

class HistoryFragment : Fragment() {
    private var _binding : FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var predictAdapter: PredictAdapter

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFirebase()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            viewModel.fetchHistoryData(userId)
        } else {
            Toast.makeText(requireContext(), "User Belum Login", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        predictAdapter = PredictAdapter { historyItem ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("disease", historyItem.disease)
                putExtra("plantType", historyItem.plantType)
                putExtra("probability", historyItem.probability)
                putExtra("imageUri", historyItem.imageUrl)
                putExtra("treatment", historyItem.treatment)
            }
            startActivity(intent)
        }
        binding.rvHistory.apply {
            adapter = predictAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            if (historyList.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "History kosong", Toast.LENGTH_SHORT).show()
            } else {
                predictAdapter.submitList(historyList)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}