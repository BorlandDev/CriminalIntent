package com.bignerdranch.android.criminal_intent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.criminal_intent.model.Crime
import java.util.*

// Обьект для взаимодействий с таблицми БД (обновление, чтение, удаление, вставка)

@Dao
interface CrimeDao {

    @Query ("SELECT * FROM crime") // вернет список всех преступлений
    fun getCrimes (): LiveData<List<Crime>>

    @Query ("SELECT * FROM crime WHERE id=(:id)")  // вернет преступление по конкретному UUID
    fun getCrime (id: UUID): LiveData<Crime?>

    @Update
    fun updateCrime (crime: Crime)

    @Insert
    fun addCrime (crime: Crime)
}


/* 4. Определение обьекта доступа к данным. DAO - нтерфейс который содержит
        функции для каждой операции с БД которые мы хотим реализовать.
        Реализацию этих функций (SQL-запросов) Room будет генерироватть сама.
 */

/* Возвращая LiveData мы запускаем запрос в фоновом потоке, по его завершению UI-ый поток будет
уведомлен в основном потоке , когда данные будут готовы .
 */