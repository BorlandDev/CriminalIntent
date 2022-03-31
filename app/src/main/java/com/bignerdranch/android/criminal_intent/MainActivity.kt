package com.bignerdranch.android.criminal_intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() , CrimeListFragment.Callbacks {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Находим фрагмент через айдишку контейнера, запрашивая ее у менеджера фрагментов.
                Если фрагмен уже существовал - вернется его экземпляр */
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)


        // создание экземпляра нашего фрагмента (если до этого он не существовал)
        if (currentFragment == null) {
            val fragment = CrimeListFragment.newInstance()

            // создает и закрпляет транзакцию фрагмента
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container , fragment) // добавляет фрагмент в транзакцию
                .commit()
        }
    }





    override fun onCrimeSelected(crimeId: UUID) {

        // передаем информацию о выбраном преступллении в списке
        val fragment = CrimeFragment.newInstance(crimeId)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("Реализация кнопки назад - вернет к предыдущему фрагменту")
            .commit()
    }


}