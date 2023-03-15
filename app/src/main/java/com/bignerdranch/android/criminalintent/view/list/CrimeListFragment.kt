package com.bignerdranch.android.criminalintent.view.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.criminalintent.R
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeListBinding
import kotlinx.coroutines.launch

class CrimeListFragment : Fragment(R.layout.fragment_crime_list) {

    private val viewModel: CrimeListViewModel by viewModels()

    private var _binding: FragmentCrimeListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentCrimeListBinding is null in ${lifecycle.currentState}"
        }

    private val adapter = CrimeListAdapter {
        findNavController().navigate(
            CrimeListFragmentDirections.actionCrimeListFragmentToCrimeDetailFragment(it)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCrimeListBinding.bind(view)
        binding.run {
            crimeRecyclerView.layoutManager = LinearLayoutManager(context)
            crimeRecyclerView.adapter = adapter

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.crimes.collect {
                        adapter.submitList(it)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}