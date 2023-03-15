package com.bignerdranch.android.criminalintent

import android.app.Application
import com.bignerdranch.android.criminalintent.domain.CrimeRepository

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}