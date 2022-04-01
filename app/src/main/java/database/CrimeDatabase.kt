package database

import android.app.appsearch.Migrator
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.criminal_intent.Crime

            // Указываем какие сущности использовать Базе и начальную версию
@Database (entities = [Crime::class], version = 2)
@TypeConverters (CrimeTypeConverters::class)

abstract class CrimeDatabase : RoomDatabase() {


    /* Создаем экземпляр Dao , Room будет сам генерировать конкретную реализацию
        (SQL запоросов, функций интерфейса) */

abstract fun crimeDao(): CrimeDao

}

                // Создаем обьект Миграции который будет синглтоном
val migration_1_2 = object : Migration(1,2) {

    // используем параметр SupportSQLiteDatabase, для выполнения любых SQL-команд
    override fun migrate (database: SupportSQLiteDatabase) {
        database.execSQL(
            " ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT '' "
        )       // Команда ALTER TABLE добавляет новый столбец в таблицу
    }
}


/* 2. Создание БД. Аннотация принимает в первом параметре - какие классы-сущности использовать при
создании и управлении таблицами для этой БД.
 Также мы указываем нужные конвертеры данных и обьект для доступа к данным (Dao)
 */

