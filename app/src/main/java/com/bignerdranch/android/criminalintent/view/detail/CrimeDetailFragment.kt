package com.bignerdranch.android.criminalintent.view.detail

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bignerdranch.android.criminalintent.R
import com.bignerdranch.android.criminalintent.data.Crime
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class CrimeDetailFragment : Fragment(R.layout.fragment_crime_detail) {

    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentDetailBinding is null in ${lifecycle.currentState}"
        }

    private val args: CrimeDetailFragmentArgs by navArgs()

    private val viewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(crimeId = args.crimeId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCrimeDetailBinding.bind(view)
        binding.run {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                viewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }
            crimeSolved.setOnCheckedChangeListener { _, isCheked ->
                viewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isCheked)
                }
            }
            crimeDate.isEnabled = false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.crime.collect {
                    it?.let { updateUI(it) }
                }
            }
        }
    }

    private fun updateUI(crime: Crime) {
        binding.run {
            if (crimeTitle.text.toString() != crime.title)
                crimeTitle.setText(crime.title)

            crimeDate.text = crime.date.time.toString()
            crimeSolved.isChecked = crime.isSolved
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}