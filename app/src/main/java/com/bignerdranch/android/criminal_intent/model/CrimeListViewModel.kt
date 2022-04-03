package com.bignerdranch.android.criminal_intent.model

import androidx.lifecycle.ViewModel


class CrimeListViewModel : ViewModel() {

    // получаем экземпляр состояния репозитория
    private val crimeRepository = CrimeRepository.get()

        // запрашиваем данные из репозитория , список преступлений
    val crimeListLiveData = crimeRepository.getCrimes()




    // Реализация функции на панели приложения главного экрана в меню
    fun addCrime (crime: Crime) {
        crimeRepository.addCrime(crime)
    }

}