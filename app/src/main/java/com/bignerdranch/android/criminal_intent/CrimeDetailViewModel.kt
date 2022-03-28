package com.bignerdranch.android.criminal_intent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CrimeDetailViewModel: ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData <UUID> () // Храним преступление полученное из БД


        // Transformations ........
    var crimeLiveData: LiveData <Crime?> =

        Transformations.switchMap (crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    // определяет какое преступление нужно вывести на экран
    fun loadCrime (crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }

}