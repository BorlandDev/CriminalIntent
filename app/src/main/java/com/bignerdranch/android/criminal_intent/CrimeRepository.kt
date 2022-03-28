package com.bignerdranch.android.criminal_intent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import database.CrimeDatabase
import java.lang.IllegalStateException
import java.util.*

/* 5. Класс репозитория инкапсулирует логику для доступа к данным из источника/ков. Он определяет как
захватить и хранить определенный набор данных - локально, в БД или с удаленного сервера. Наш UI код
будет запрашивать все данные из репозитория потому что интерфейсу не важно, как фактически хранятся
или извлекаются данные. Это детали реализации самого репозитория.

Репозиторий существует в единственном экземпляре , благодоря синглтону

Когда вам нужно использовать репозиторий? Что ж, представьте, что пользователь вашего приложения
хочет увидеть свой профиль. Приложение имеет репозиторий, который проверяет Store на наличие
локальной копии профиля пользователя. Если локальная копия отсутствует, репозиторий сверяется
с удаленным Source.

 */

private const val DATABASE_NAME = "crime-database"

                     // контекст - экзмепляр приложения (пока живо приложение - жив обьект)
class CrimeRepository private constructor (context: Context) {

   /* Билдер создает конкретную реализацию нашего абстрактного класса CrimeDatabase. Билдеру нужен контекст
   так как БД обращается к файловой системе. Контекст приложения нужно передавать так как синглтон
   существует дольше чем любой из наших классов activity. Второй парамметр - класс БД которую  Room
   должен создать. Третий - имя файла БД.
    */
    private val database: CrimeDatabase = Room.databaseBuilder (    // храним ссылку на Базу данных

        context.applicationContext, // Для обращения к файловой системе нужен контекст всего приложения
        CrimeDatabase::class.java,  // Ссылка на класс базы которую Room должен создать
        DATABASE_NAME               // Имя базы
   ).build()


       // Репозиторий вызывает функции через интерфейс crimeDao
    private val  crimeDao = database.crimeDao()                   // храним ссылку на интерфейс DAO

           // вернет список преступлений
    fun getCrimes (): LiveData<List<Crime>> = crimeDao.getCrimes()
            // вернет конкретное преступление
    fun getCrime (id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)




    /* Синглтон - его единственный экз, живет пока приложение находится в памяти. Он не подходит для
    долговременного хранения данных, вместо этого он выдает данные о преступлении и дает возможность
    легко передавать эти данные между классами контроллера.
     */
    companion object {
        private var INSTANCE: CrimeRepository? = null

            // инициализация синглтона если он еще не был создан
        fun initialize (context: Context) {
          if (INSTANCE == null) INSTANCE = CrimeRepository(context)
        }

            // обеспечивает доступ к синглтону репозитория
        fun get (): CrimeRepository {
            return INSTANCE ?:
                throw IllegalStateException("CrimeRepository must be initialized")
        }
    }



}