package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.criminal_intent.Crime

            // Указываем какие сущности использовать Базе и начальную версию
@Database (entities = [Crime::class], version = 1)
@TypeConverters (CrimeTypeConverters::class)

abstract class CrimeDatabase : RoomDatabase() {


    /* создаем экземпляр Dao , Room будет сам генерировать конкретную реализацию
        (SQL запоросов, функций интерфейса)
    */
abstract fun crimeDao(): CrimeDao

}




/* 2. Создание БД. Аннотация принимает в первом параметре - какие классы-сущности использовать при
создании и управлении таблицами для этой БД.
 Также мы указываем нужные конвертеры данных и обьект для доступа к данным (Dao)
 */

