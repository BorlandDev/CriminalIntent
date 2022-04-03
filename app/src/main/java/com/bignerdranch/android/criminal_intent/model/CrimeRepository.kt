package com.bignerdranch.android.criminal_intent.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.criminal_intent.database.CrimeDatabase
import com.bignerdranch.android.criminal_intent.database.migration_1_2
import java.io.File
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

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

private const val DATABASE_NAME = "crime-com.bignerdranch.android.criminal_intent.database"

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
        DATABASE_NAME) // Имя базы

   .addMigrations(migration_1_2).build() // Выполняем миграцию


       // Репозиторий вызывает функции через интерфейс crimeDao

                         // храним ссылку на интерфейс DAO
    private val  crimeDao = database.crimeDao()
                         // Функция вернет экземпляр исполнителся указывющий на новый поток
    private val executor = Executors.newSingleThreadExecutor()

                         // получаем ссылку на каталог файловой системы нашего приложения
    private val filesDir = context.applicationContext.filesDir


      /* Как updateCrime так и addCrime оборачивают вызовы в Dao внутри блока execute{ }.
                 Он выталкиват эти операции из основного потока чобы не блокировать работу UI
       */

           // вернет список преступлений
    fun getCrimes (): LiveData<List<Crime>> = crimeDao.getCrimes()
            // вернет конкретное преступление
    fun getCrime (id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

                         // обновит переданное перступление
    fun updateCrime (crime: Crime) {
        executor.execute { crimeDao.updateCrime(crime) }
    }
                // добавит новое преступление
    fun addCrime (crime: Crime) {
        executor.execute { crimeDao.addCrime(crime) }
    }
                // возвращаем фото из указанного каталога файловой системы
    fun getPhotoFile(crime: Crime): File = File(filesDir, crime.photoFileName)




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