package com.bignerdranch.android.criminal_intent

import android.app.Application

    // это экземпляр (обьект всего приложения) его Instance
class CriminalIntentApplication: Application() {

    // вызывается системой когда приложение впервые загружается в память.
    override fun onCreate() {
        super.onCreate()

            // иницализируем репозиторий при запуске приложения (жизненного цикла)
        CrimeRepository.initialize(this)
    }



}


/* 6. Получаем контекст всего приложения - информация о жизненном цикле самого приложения что бы
    наш синглтон репозитория был готов со старта приложения.

 */