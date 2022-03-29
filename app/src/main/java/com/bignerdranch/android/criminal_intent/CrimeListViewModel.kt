package com.bignerdranch.android.criminal_intent

import androidx.lifecycle.ViewModel



class CrimeListViewModel : ViewModel() {

    // получаем экземпляр состояния репозитория
    private val crimeRepository = CrimeRepository.get()

        // запрашиваем данные из репозитория , список преступлений
    val crimeListLiveData = crimeRepository.getCrimes()





    fun addCrime (crime: Crime) {
        crimeRepository.addCrime(crime)
    }

}