package com.bignerdranch.android.criminal_intent.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class CrimeDetailViewModel: ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    // Храним преступление полученное из БД возвращенное функцией loadCrime
    private val crimeIdLiveData = MutableLiveData <UUID> ()

    // Transformations ........
    var crimeLiveData: LiveData <Crime?> =

        Transformations.switchMap (crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }






    // определяет какое преступление нужно вывести на экран
    fun loadCrime (crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }

    // сохраняем текст введенный в EditText, в базу данных, обновляя заголовок преступления
    fun saveCrime (crime: Crime) {
        crimeRepository.updateCrime(crime)
    }

    // Выдает информацию о файле для Crime Fragment
    fun getPhotoFile (crime: Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }

}