package com.bignerdranch.android.criminalintent.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.criminalintent.data.Crime
import com.bignerdranch.android.criminalintent.databinding.ListItemCrimeBinding
import java.util.*

class CrimeListAdapter(
    private val onCrimeClicked: (crimeId: UUID) -> Unit
) : ListAdapter<Crime, CrimeListAdapter.CrimeHolder>(CrimesDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimeBinding.inflate(inflater, parent, false)
        return CrimeHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int): Unit =
        holder.bind(getItem(position), onCrimeClicked)

    class CrimeHolder(
        private val binding: ListItemCrimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crime: Crime, onCrimeClicked: (crimeId: UUID) -> Unit) {
            binding.run {
                crimeTitle.text = crime.title
                crimeDate.text = crime.date.toString()

                root.setOnClickListener {
                    onCrimeClicked.invoke(crime.id)
                }
                crimeSolved.isVisible = crime.isSolved
            }
        }
    }

    object CrimesDiffCallback : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean =
            oldItem.id == newItem.id
    }
}


