package com.bignerdranch.android.criminal_intent.database

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class CrimeTypeConverters {


    @TypeConverter  // конвертируем данные для сохранения в базе
    fun fromDate (date: Date?): Long? {
        return date?.time
    }

    @TypeConverter // конвертируем данные из базы обратно в исходный формат
    fun toDate (millisSinceEpoch: Long?): Date? {

        return millisSinceEpoch?.let {
            Date(it)
        }
    }


    @TypeConverter // конвертируем данные для сохранения в базе
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }


    @TypeConverter // конвертируем данные из базы обратно в исходный формат
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

}

/* 3. Учим базу работать (сериализовать) с нашими типами данных, т.к. по дефолту она работает только
с примитивами.
 */